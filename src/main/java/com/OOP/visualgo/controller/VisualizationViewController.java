package com.oop.visualgo.controller;

import com.oop.visualgo.model.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.*;

public class VisualizationViewController {

    @FXML
    private Canvas treeCanvas;

    @FXML
    private Pane canvasPane;

    @FXML
    private VBox leftMenu;

    @FXML
    private Button traverseBtn;

    @FXML
    private HBox traverseOptions;

    @FXML
    private Label statusLabel;

    private boolean traverseExpanded = false;
    private String currentTreeType = "BST";

    // Tree data structures
    private BinarySearchTree<Integer> bst;
    private BinaryTree<Integer> binaryTree;
    private GenericTree<Integer> genericTree;
    private RedBlackTree<Integer> rbTree;

    // For highlighting nodes during operations
    private Set<Integer> highlightedNodes = new HashSet<>();
    private Integer searchHighlight = null;

    // For traverse animation
    private Integer currentTraverseHighlight = null;
    private Timeline traverseTimeline;
    private List<Integer> traverseSequence = new ArrayList<>();
    private int traverseIndex = 0;
    private boolean isTraversePaused = false;

    // Tree drawing constants
    private static final double NODE_RADIUS = 25;
    private static final double VERTICAL_GAP = 80;
    private static final double MIN_HORIZONTAL_GAP = 60;

    @FXML
    public void initialize() {
        System.out.println("VisualizationView initialized");
        bst = new BinarySearchTree<>();
        binaryTree = new BinaryTree<>();
        genericTree = new GenericTree<>();
        rbTree = new RedBlackTree<>();

        // Setup canvas to resize with pane
        setupCanvasResizing();
    }

    private void setupCanvasResizing() {
        // Bind canvas size to pane size
        canvasPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            treeCanvas.setWidth(newVal.doubleValue());
            drawTree();
        });
        canvasPane.heightProperty().addListener((obs, oldVal, newVal) -> {
            treeCanvas.setHeight(newVal.doubleValue());
            drawTree();
        });
    }

    public void setTreeType(String treeType) {
        this.currentTreeType = treeType;
        System.out.println("Tree type set to: " + treeType);
        // Clear and initialize appropriate tree
        bst = new BinarySearchTree<>();
        binaryTree = new BinaryTree<>();
        genericTree = new GenericTree<>();
        rbTree = new RedBlackTree<>();

        // Delay drawing to allow canvas to get proper size
        javafx.application.Platform.runLater(this::drawTree);
    }

    // ==================== Left Menu Actions ====================

    @FXML
    private void onCreate() {
        // Show dialog with options
        ChoiceDialog<String> choiceDialog = new ChoiceDialog<>("Random", "Random", "Empty", "Sample");
        choiceDialog.setTitle("Create Tree");
        choiceDialog.setHeaderText("Create " + currentTreeType);
        choiceDialog.setContentText("Choose creation method:");

        Optional<String> choice = choiceDialog.showAndWait();
        choice.ifPresent(method -> {
            switch (method) {
                case "Random":
                    TextInputDialog dialog = new TextInputDialog("7");
                    dialog.setTitle("Create Random Tree");
                    dialog.setHeaderText("Create Random " + currentTreeType);
                    dialog.setContentText("Enter number of nodes (1-15):");

                    Optional<String> result = dialog.showAndWait();
                    result.ifPresent(countStr -> {
                        try {
                            int count = Math.min(15, Math.max(1, Integer.parseInt(countStr)));
                            createRandomTree(count);
                            updateStatus("Created " + currentTreeType + " with " + count + " nodes");
                        } catch (NumberFormatException e) {
                            showError("Invalid Input", "Please enter a valid number.");
                        }
                    });
                    break;
                case "Empty":
                    clearTree();
                    updateStatus("Created empty " + currentTreeType);
                    break;
                case "Sample":
                    createSampleTree();
                    updateStatus("Created sample " + currentTreeType);
                    break;
            }
        });
    }

    @FXML
    private void onSearch() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Search");
        dialog.setHeaderText("Search in " + currentTreeType);
        dialog.setContentText("Enter value to search:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(valueStr -> {
            try {
                int value = Integer.parseInt(valueStr);
                searchHighlight = null; // Reset highlight
                boolean found = searchValue(value);
                if (found) {
                    searchHighlight = value; // Highlight found node
                    updateStatus("Found: " + value);
                    showInfo("Search Result", "Value " + value + " found in the tree!");
                } else {
                    updateStatus("Not found: " + value);
                    showInfo("Search Result", "Value " + value + " not found in the tree.");
                }
                drawTree();

                // Clear highlight after 3 seconds
                new java.util.Timer().schedule(new java.util.TimerTask() {
                    @Override
                    public void run() {
                        javafx.application.Platform.runLater(() -> {
                            searchHighlight = null;
                            drawTree();
                        });
                    }
                }, 3000);
            } catch (NumberFormatException e) {
                showError("Invalid Input", "Please enter a valid number.");
            }
        });
    }

    @FXML
    private void onInsert() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Insert");
        dialog.setHeaderText("Insert into " + currentTreeType);
        dialog.setContentText("Enter value to insert:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(valueStr -> {
            try {
                int value = Integer.parseInt(valueStr);
                insertValue(value);
                updateStatus("Inserted: " + value);
                drawTree();
            } catch (NumberFormatException e) {
                showError("Invalid Input", "Please enter a valid number.");
            }
        });
    }

    @FXML
    private void onRemove() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Remove");
        dialog.setHeaderText("Remove from " + currentTreeType);
        dialog.setContentText("Enter value to remove:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(valueStr -> {
            try {
                int value = Integer.parseInt(valueStr);
                boolean removed = removeValue(value);
                if (removed) {
                    updateStatus("Removed: " + value);
                } else {
                    updateStatus("Value not found: " + value);
                    showInfo("Remove Result", "Value " + value + " not found in the tree.");
                }
                drawTree();
            } catch (NumberFormatException e) {
                showError("Invalid Input", "Please enter a valid number.");
            }
        });
    }

    // ==================== Tree Operations ====================

    private void clearTree() {
        bst = new BinarySearchTree<>();
        binaryTree = new BinaryTree<>();
        genericTree = new GenericTree<>();
        rbTree = new RedBlackTree<>();
        highlightedNodes.clear();
        searchHighlight = null;
        drawTree();
    }

    private void createSampleTree() {
        clearTree();
        switch (currentTreeType) {
            case "BST":
                // Create a balanced BST sample
                int[] bstValues = {50, 30, 70, 20, 40, 60, 80};
                for (int val : bstValues) {
                    bst.insert(val);
                }
                break;
            case "Binary Tree":
                // Create a complete binary tree sample
                int[] btValues = {1, 2, 3, 4, 5, 6, 7};
                for (int val : btValues) {
                    binaryTree.insert(val);
                }
                break;
            case "Generic Tree":
                // Create a generic tree sample
                genericTree.insert(1, null); // root
                genericTree.insert(2, 1);
                genericTree.insert(3, 1);
                genericTree.insert(4, 1);
                genericTree.insert(5, 2);
                genericTree.insert(6, 2);
                genericTree.insert(7, 3);
                break;
            case "Red-Black Tree":
                // Create a Red-Black tree sample
                int[] rbValues = {50, 30, 70, 20, 40, 60, 80};
                for (int val : rbValues) {
                    rbTree.insert(val);
                }
                break;
            default:
                int[] defaultValues = {50, 30, 70, 20, 40, 60, 80};
                for (int val : defaultValues) {
                    bst.insert(val);
                }
        }
        drawTree();
    }

    private void createRandomTree(int count) {
        Random random = new Random();
        Set<Integer> usedValues = new HashSet<>();

        // Clear existing trees
        clearTree();

        for (int i = 0; i < count; i++) {
            int value;
            do {
                value = random.nextInt(99) + 1; // 1-99
            } while (usedValues.contains(value));
            usedValues.add(value);
            insertValue(value);
        }
        drawTree();
    }

    private void insertValue(int value) {
        switch (currentTreeType) {
            case "BST":
                bst.insert(value);
                break;
            case "Binary Tree":
                binaryTree.insert(value);
                break;
            case "Generic Tree":
                if (genericTree.getRoot() == null) {
                    genericTree.insert(value, null); // Insert as root
                } else {
                    // Insert as child of a random existing node using BFS
                    List<GenericTreeNode<Integer>> nodes = genericTree.traverseBFS();
                    if (!nodes.isEmpty()) {
                        Random rand = new Random();
                        GenericTreeNode<Integer> parent = nodes.get(rand.nextInt(nodes.size()));
                        genericTree.insert(value, parent.getValue());
                    }
                }
                break;
            case "Red-Black Tree":
                rbTree.insert(value);
                break;
            default:
                bst.insert(value);
        }
    }

    private boolean searchValue(int value) {
        switch (currentTreeType) {
            case "BST":
                return bst.search(value) != null;
            case "Binary Tree":
                return binaryTree.search(value) != null;
            case "Generic Tree":
                return genericTree.search(value) != null;
            case "Red-Black Tree":
                return rbTree.search(value) != null;
            default:
                return bst.search(value) != null;
        }
    }

    private boolean removeValue(int value) {
        switch (currentTreeType) {
            case "BST":
                return bst.delete(value);
            case "Binary Tree":
                return binaryTree.delete(value);
            case "Generic Tree":
                return genericTree.delete(value);
            case "Red-Black Tree":
                return rbTree.delete(value);
            default:
                return bst.delete(value);
        }
    }

    // ==================== Traverse Toggle ====================

    @FXML
    private void onTraverseToggle() {
        traverseExpanded = !traverseExpanded;
        traverseOptions.setVisible(traverseExpanded);
        traverseOptions.setManaged(traverseExpanded);
        traverseBtn.setText(traverseExpanded ? "Traverse(root) ▲" : "Traverse(root) ▼");
    }

    @FXML
    private void onInorder() {
        updateStatus("Inorder traversal");
        List<Integer> result = new ArrayList<>();

        switch (currentTreeType) {
            case "BST":
                if (bst.getRoot() != null) {
                    inorderTraversal(bst.getRoot(), result);
                }
                break;
            case "Binary Tree":
                if (binaryTree.getRoot() != null) {
                    inorderTraversal(binaryTree.getRoot(), result);
                }
                break;
            case "Generic Tree":
                if (genericTree.getRoot() != null) {
                    // For generic tree, use DFS (similar to preorder)
                    List<GenericTreeNode<Integer>> nodes = genericTree.traverseDFS();
                    for (GenericTreeNode<Integer> node : nodes) {
                        result.add(node.getValue());
                    }
                }
                break;
            case "Red-Black Tree":
                if (rbTree.getRoot() != null) {
                    List<RedBlackTreeNode<Integer>> nodes = rbTree.traverseInOrder();
                    for (RedBlackTreeNode<Integer> node : nodes) {
                        result.add(node.getValue());
                    }
                }
                break;
        }

        // Start animation
        animateTraversal(result, "Inorder Traversal");
    }

    @FXML
    private void onPreorder() {
        updateStatus("Preorder traversal");
        List<Integer> result = new ArrayList<>();

        switch (currentTreeType) {
            case "BST":
                if (bst.getRoot() != null) {
                    preorderTraversal(bst.getRoot(), result);
                }
                break;
            case "Binary Tree":
                if (binaryTree.getRoot() != null) {
                    preorderTraversal(binaryTree.getRoot(), result);
                }
                break;
            case "Generic Tree":
                if (genericTree.getRoot() != null) {
                    List<GenericTreeNode<Integer>> nodes = genericTree.traverseDFS();
                    for (GenericTreeNode<Integer> node : nodes) {
                        result.add(node.getValue());
                    }
                }
                break;
            case "Red-Black Tree":
                if (rbTree.getRoot() != null) {
                    List<RedBlackTreeNode<Integer>> nodes = rbTree.traversePreOrder();
                    for (RedBlackTreeNode<Integer> node : nodes) {
                        result.add(node.getValue());
                    }
                }
                break;
        }

        // Start animation
        animateTraversal(result, "Preorder Traversal");
    }

    @FXML
    private void onPostorder() {
        updateStatus("Postorder traversal");
        List<Integer> result = new ArrayList<>();

        switch (currentTreeType) {
            case "BST":
                if (bst.getRoot() != null) {
                    postorderTraversal(bst.getRoot(), result);
                }
                break;
            case "Binary Tree":
                if (binaryTree.getRoot() != null) {
                    postorderTraversal(binaryTree.getRoot(), result);
                }
                break;
            case "Generic Tree":
                if (genericTree.getRoot() != null) {
                    // For generic tree, use BFS (level order)
                    List<GenericTreeNode<Integer>> nodes = genericTree.traverseBFS();
                    for (GenericTreeNode<Integer> node : nodes) {
                        result.add(node.getValue());
                    }
                }
                break;
            case "Red-Black Tree":
                if (rbTree.getRoot() != null) {
                    List<RedBlackTreeNode<Integer>> nodes = rbTree.traversePostOrder();
                    for (RedBlackTreeNode<Integer> node : nodes) {
                        result.add(node.getValue());
                    }
                }
                break;
        }

        String title = currentTreeType.equals("Generic Tree") ? "BFS Traversal" : "Postorder Traversal";
        // Start animation
        animateTraversal(result, title);
    }

    private void inorderTraversal(BinaryTreeNode<Integer> node, List<Integer> result) {
        if (node == null) return;
        inorderTraversal(node.getLeft(), result);
        result.add(node.getValue());
        inorderTraversal(node.getRight(), result);
    }

    private void preorderTraversal(BinaryTreeNode<Integer> node, List<Integer> result) {
        if (node == null) return;
        result.add(node.getValue());
        preorderTraversal(node.getLeft(), result);
        preorderTraversal(node.getRight(), result);
    }

    private void postorderTraversal(BinaryTreeNode<Integer> node, List<Integer> result) {
        if (node == null) return;
        postorderTraversal(node.getLeft(), result);
        postorderTraversal(node.getRight(), result);
        result.add(node.getValue());
    }

    // ==================== Traverse Animation ====================

    private void animateTraversal(List<Integer> sequence, String traversalName) {
        // Stop any existing animation
        stopTraverseAnimation();

        if (sequence.isEmpty()) {
            showInfo(traversalName, "Tree is empty!");
            return;
        }

        traverseSequence = new ArrayList<>(sequence);
        traverseIndex = 0;
        isTraversePaused = false;

        updateStatus(traversalName + " - Starting animation...");

        traverseTimeline = new Timeline(new KeyFrame(Duration.millis(800), e -> {
            if (traverseIndex < traverseSequence.size()) {
                currentTraverseHighlight = traverseSequence.get(traverseIndex);
                updateStatus(traversalName + " - Visiting: " + currentTraverseHighlight + " (" + (traverseIndex + 1) + "/" + traverseSequence.size() + ")");
                drawTree();
                traverseIndex++;
            } else {
                // Animation finished
                stopTraverseAnimation();
                updateStatus(traversalName + " - Complete!");
                showInfo(traversalName, "Result: " + traverseSequence);
            }
        }));
        traverseTimeline.setCycleCount(Timeline.INDEFINITE);
        traverseTimeline.play();
    }

    private void stopTraverseAnimation() {
        if (traverseTimeline != null) {
            traverseTimeline.stop();
            traverseTimeline = null;
        }
        currentTraverseHighlight = null;
        traverseSequence.clear();
        traverseIndex = 0;
        isTraversePaused = false;
        drawTree();
    }

    // ==================== Playback Controls ====================

    @FXML
    private void onRewind() {
        if (traverseTimeline != null && !traverseSequence.isEmpty()) {
            traverseIndex = 0;
            currentTraverseHighlight = null;
            updateStatus("Rewind - Back to start");
            drawTree();
        } else {
            updateStatus("No animation running");
        }
    }

    @FXML
    private void onStepBack() {
        if (!traverseSequence.isEmpty() && traverseIndex > 0) {
            // Pause if playing
            if (traverseTimeline != null && !isTraversePaused) {
                traverseTimeline.pause();
                isTraversePaused = true;
            }
            traverseIndex--;
            if (traverseIndex > 0) {
                currentTraverseHighlight = traverseSequence.get(traverseIndex - 1);
            } else {
                currentTraverseHighlight = null;
            }
            updateStatus("Step backward - Node " + traverseIndex + "/" + traverseSequence.size());
            drawTree();
        } else {
            updateStatus("Cannot step backward");
        }
    }

    @FXML
    private void onPause() {
        if (traverseTimeline != null && !isTraversePaused) {
            traverseTimeline.pause();
            isTraversePaused = true;
            updateStatus("Paused at node " + traverseIndex + "/" + traverseSequence.size());
        } else {
            updateStatus("No animation to pause");
        }
    }

    @FXML
    private void onPlay() {
        if (traverseTimeline != null) {
            if (isTraversePaused) {
                traverseTimeline.play();
                isTraversePaused = false;
                updateStatus("Resumed animation");
            }
        } else {
            updateStatus("No animation - Select a traversal first");
        }
    }

    @FXML
    private void onFastForward() {
        if (!traverseSequence.isEmpty()) {
            // Stop animation and show final result
            if (traverseTimeline != null) {
                traverseTimeline.stop();
            }
            traverseIndex = traverseSequence.size();
            currentTraverseHighlight = null;
            updateStatus("Fast forward - Complete!");
            drawTree();
            showInfo("Traversal Complete", "Result: " + traverseSequence);
            traverseTimeline = null;
        } else {
            updateStatus("No animation running");
        }
    }

    // ==================== Drawing ====================

    private void drawTree() {
        if (treeCanvas.getWidth() <= 0 || treeCanvas.getHeight() <= 0) {
            return; // Canvas not ready yet
        }

        GraphicsContext gc = treeCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, treeCanvas.getWidth(), treeCanvas.getHeight());

        // Set font for node values
        gc.setFont(Font.font("Arial Bold", 14));
        gc.setTextAlign(TextAlignment.CENTER);

        double startY = 60; // Top padding
        double centerX = treeCanvas.getWidth() / 2;

        switch (currentTreeType) {
            case "BST":
                if (bst.getRoot() != null) {
                    int treeHeight = getTreeHeight(bst.getRoot());
                    double horizontalGap = calculateHorizontalGap(treeHeight);
                    drawBinaryNode(gc, bst.getRoot(), centerX, startY, horizontalGap);
                }
                break;
            case "Binary Tree":
                if (binaryTree.getRoot() != null) {
                    int treeHeight = getTreeHeight(binaryTree.getRoot());
                    double horizontalGap = calculateHorizontalGap(treeHeight);
                    drawBinaryNode(gc, binaryTree.getRoot(), centerX, startY, horizontalGap);
                }
                break;
            case "Generic Tree":
                if (genericTree.getRoot() != null) {
                    drawGenericTree(gc);
                }
                break;
            case "Red-Black Tree":
                if (rbTree.getRoot() != null) {
                    int treeHeight = getRBTreeHeight(rbTree.getRoot());
                    double horizontalGap = calculateHorizontalGap(treeHeight);
                    drawRBNode(gc, rbTree.getRoot(), centerX, startY, horizontalGap);
                }
                break;
        }
    }

    private int getTreeHeight(BinaryTreeNode<Integer> node) {
        if (node == null) return 0;
        return 1 + Math.max(getTreeHeight(node.getLeft()), getTreeHeight(node.getRight()));
    }

    private int getRBTreeHeight(RedBlackTreeNode<Integer> node) {
        if (node == null) return 0;
        return 1 + Math.max(getRBTreeHeight(node.getLeft()), getRBTreeHeight(node.getRight()));
    }

    private double calculateHorizontalGap(int treeHeight) {
        // Calculate gap based on canvas width and tree height
        double canvasWidth = treeCanvas.getWidth();
        // For each level, we need 2^level nodes to fit
        // Gap at level 0 should be canvasWidth/4 to center tree
        double baseGap = canvasWidth / 4;
        
        // Adjust based on tree height to prevent overlap
        if (treeHeight <= 3) {
            return baseGap;
        } else if (treeHeight <= 5) {
            return baseGap * 0.9;
        } else {
            return Math.max(MIN_HORIZONTAL_GAP, baseGap * 0.8);
        }
    }

    // Draw Red-Black Tree node
    private void drawRBNode(GraphicsContext gc, RedBlackTreeNode<Integer> node, double x, double y, double hGap) {
        if (node == null) return;

        // Ensure node stays within canvas bounds
        x = Math.max(NODE_RADIUS + 10, Math.min(treeCanvas.getWidth() - NODE_RADIUS - 10, x));

        // Draw edges first (behind nodes)
        gc.setStroke(Color.web("#888888"));
        gc.setLineWidth(2.5);

        double nextHGap = Math.max(MIN_HORIZONTAL_GAP / 2, hGap / 2);

        if (node.getLeft() != null) {
            double childX = x - hGap;
            double childY = y + VERTICAL_GAP;
            gc.strokeLine(x, y + NODE_RADIUS, childX, childY - NODE_RADIUS);
        }
        if (node.getRight() != null) {
            double childX = x + hGap;
            double childY = y + VERTICAL_GAP;
            gc.strokeLine(x, y + NODE_RADIUS, childX, childY - NODE_RADIUS);
        }

        // Determine node color based on Red-Black tree color
        Color fillColor;
        Color strokeColor;

        // Highlight current traverse node (highest priority - bright yellow/orange)
        if (currentTraverseHighlight != null && node.getValue().equals(currentTraverseHighlight)) {
            fillColor = Color.web("#ffeb3b"); // Bright yellow for current traverse
            strokeColor = Color.web("#f57f17");
        }
        // Highlight search result
        else if (searchHighlight != null && node.getValue().equals(searchHighlight)) {
            fillColor = Color.web("#ff9800"); // Orange for found
            strokeColor = Color.web("#e65100");
        }
        else if (node.isRed()) {
            fillColor = Color.web("#e53935"); // Red
            strokeColor = Color.web("#b71c1c");
        } else {
            fillColor = Color.web("#424242"); // Dark gray (easier to see than pure black)
            strokeColor = Color.web("#212121");
        }

        // Draw node circle with shadow effect
        gc.setFill(Color.rgb(0, 0, 0, 0.2));
        gc.fillOval(x - NODE_RADIUS + 3, y - NODE_RADIUS + 3, NODE_RADIUS * 2, NODE_RADIUS * 2);
        
        gc.setFill(fillColor);
        gc.fillOval(x - NODE_RADIUS, y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);
        gc.setStroke(strokeColor);
        gc.setLineWidth(3);
        gc.strokeOval(x - NODE_RADIUS, y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);

        // Draw value
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 14));
        gc.fillText(String.valueOf(node.getValue()), x, y + 5);

        // Draw children with reduced horizontal gap
        drawRBNode(gc, node.getLeft(), x - hGap, y + VERTICAL_GAP, nextHGap);
        drawRBNode(gc, node.getRight(), x + hGap, y + VERTICAL_GAP, nextHGap);
    }

    private void drawBinaryNode(GraphicsContext gc, BinaryTreeNode<Integer> node, double x, double y, double hGap) {
        if (node == null) return;

        double nextHGap = Math.max(MIN_HORIZONTAL_GAP / 2, hGap / 2);

        // Draw edges first (behind nodes)
        gc.setStroke(Color.web("#888888"));
        gc.setLineWidth(2.5);

        if (node.getLeft() != null) {
            double childX = x - hGap;
            double childY = y + VERTICAL_GAP;
            gc.strokeLine(x, y + NODE_RADIUS, childX, childY - NODE_RADIUS);
        }
        if (node.getRight() != null) {
            double childX = x + hGap;
            double childY = y + VERTICAL_GAP;
            gc.strokeLine(x, y + NODE_RADIUS, childX, childY - NODE_RADIUS);
        }

        // Determine node color
        Color fillColor = Color.web("#4caf50"); // Default green
        Color strokeColor = Color.web("#2e7d32");

        // Highlight current traverse node (highest priority - bright yellow/orange)
        if (currentTraverseHighlight != null && node.getValue().equals(currentTraverseHighlight)) {
            fillColor = Color.web("#ffeb3b"); // Bright yellow for current traverse
            strokeColor = Color.web("#f57f17");
        }
        // Highlight search result
        else if (searchHighlight != null && node.getValue().equals(searchHighlight)) {
            fillColor = Color.web("#ff9800"); // Orange for found
            strokeColor = Color.web("#e65100");
        }
        // Check if node is in highlighted set
        else if (highlightedNodes.contains(node.getValue())) {
            fillColor = Color.web("#2196f3"); // Blue for highlighted path
            strokeColor = Color.web("#1565c0");
        }

        // Draw node circle with shadow effect
        gc.setFill(Color.rgb(0, 0, 0, 0.2));
        gc.fillOval(x - NODE_RADIUS + 3, y - NODE_RADIUS + 3, NODE_RADIUS * 2, NODE_RADIUS * 2);

        gc.setFill(fillColor);
        gc.fillOval(x - NODE_RADIUS, y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);
        gc.setStroke(strokeColor);
        gc.setLineWidth(3);
        gc.strokeOval(x - NODE_RADIUS, y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);

        // Draw value
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 14));
        gc.fillText(String.valueOf(node.getValue()), x, y + 5);

        // Draw children with reduced horizontal gap
        drawBinaryNode(gc, node.getLeft(), x - hGap, y + VERTICAL_GAP, nextHGap);
        drawBinaryNode(gc, node.getRight(), x + hGap, y + VERTICAL_GAP, nextHGap);
    }

    private void drawGenericTree(GraphicsContext gc) {
        if (genericTree.getRoot() == null) return;

        // Calculate positions for generic tree using BFS
        Map<GenericTreeNode<Integer>, double[]> positions = new HashMap<>();
        calculateGenericTreePositions(genericTree.getRoot(), positions);

        // Draw edges first
        gc.setStroke(Color.web("#888888"));
        gc.setLineWidth(2.5);
        drawGenericTreeEdges(gc, genericTree.getRoot(), positions);

        // Draw nodes
        for (Map.Entry<GenericTreeNode<Integer>, double[]> entry : positions.entrySet()) {
            GenericTreeNode<Integer> node = entry.getKey();
            double[] pos = entry.getValue();
            drawGenericNode(gc, node, pos[0], pos[1]);
        }
    }

    private void calculateGenericTreePositions(GenericTreeNode<Integer> root, Map<GenericTreeNode<Integer>, double[]> positions) {
        if (root == null) return;

        // Use level-order traversal to assign positions
        Queue<GenericTreeNode<Integer>> queue = new LinkedList<>();
        Queue<Integer> levels = new LinkedList<>();
        Map<Integer, List<GenericTreeNode<Integer>>> levelNodes = new HashMap<>();

        queue.offer(root);
        levels.offer(0);

        while (!queue.isEmpty()) {
            GenericTreeNode<Integer> node = queue.poll();
            int level = levels.poll();

            levelNodes.computeIfAbsent(level, k -> new ArrayList<>()).add(node);

            for (GenericTreeNode<Integer> child : node.getChildren()) {
                queue.offer(child);
                levels.offer(level + 1);
            }
        }

        // Assign x positions based on level and order
        double startY = 60;
        for (Map.Entry<Integer, List<GenericTreeNode<Integer>>> entry : levelNodes.entrySet()) {
            int level = entry.getKey();
            List<GenericTreeNode<Integer>> nodes = entry.getValue();
            double y = startY + level * VERTICAL_GAP;
            double totalWidth = treeCanvas.getWidth() - 100;
            double spacing = totalWidth / (nodes.size() + 1);

            for (int i = 0; i < nodes.size(); i++) {
                double x = 50 + spacing * (i + 1);
                positions.put(nodes.get(i), new double[]{x, y});
            }
        }
    }

    private void drawGenericTreeEdges(GraphicsContext gc, GenericTreeNode<Integer> node, Map<GenericTreeNode<Integer>, double[]> positions) {
        if (node == null) return;

        double[] parentPos = positions.get(node);
        if (parentPos == null) return;

        for (GenericTreeNode<Integer> child : node.getChildren()) {
            double[] childPos = positions.get(child);
            if (childPos != null) {
                gc.strokeLine(parentPos[0], parentPos[1] + NODE_RADIUS, childPos[0], childPos[1] - NODE_RADIUS);
            }
            drawGenericTreeEdges(gc, child, positions);
        }
    }

    private void drawGenericNode(GraphicsContext gc, GenericTreeNode<Integer> node, double x, double y) {
        // Determine node color
        Color fillColor = Color.web("#9c27b0"); // Purple for generic tree
        Color strokeColor = Color.web("#6a1b9a");

        // Highlight current traverse node (highest priority - bright yellow/orange)
        if (currentTraverseHighlight != null && node.getValue().equals(currentTraverseHighlight)) {
            fillColor = Color.web("#ffeb3b"); // Bright yellow for current traverse
            strokeColor = Color.web("#f57f17");
        }
        // Highlight search result
        else if (searchHighlight != null && node.getValue().equals(searchHighlight)) {
            fillColor = Color.web("#ff9800"); // Orange for found
            strokeColor = Color.web("#e65100");
        }

        // Draw node circle with shadow effect
        gc.setFill(Color.rgb(0, 0, 0, 0.2));
        gc.fillOval(x - NODE_RADIUS + 3, y - NODE_RADIUS + 3, NODE_RADIUS * 2, NODE_RADIUS * 2);

        gc.setFill(fillColor);
        gc.fillOval(x - NODE_RADIUS, y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);
        gc.setStroke(strokeColor);
        gc.setLineWidth(3);
        gc.strokeOval(x - NODE_RADIUS, y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);

        // Draw value
        gc.setFill(Color.WHITE);
        gc.fillText(String.valueOf(node.getValue()), x, y + 5);
    }

    // ==================== Playback Controls ====================

    @FXML
    private void onStepBackward() {
        updateStatus("Step backward");
    }

    @FXML
    private void onPrevious() {
        updateStatus("Previous step");
    }

    @FXML
    private void onPlayPause() {
        updateStatus("Play/Pause");
    }

    @FXML
    private void onNext() {
        updateStatus("Next step");
    }

    @FXML
    private void onStepForward() {
        updateStatus("Step forward");
    }

    // ==================== Navigation ====================

    @FXML
    private void onBackToMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/oop/visualgo/main-menu.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) treeCanvas.getScene().getWindow();

            // Lưu trạng thái fullscreen/maximized hiện tại
            boolean wasFullScreen = stage.isFullScreen();
            boolean wasMaximized = stage.isMaximized();

            // Tạo scene mới với kích thước hiện tại của stage
            Scene newScene = new Scene(root, stage.getWidth(), stage.getHeight());
            stage.setScene(newScene);
            stage.setTitle("VisualGo - Main Menu");

            // Khôi phục trạng thái fullscreen/maximized
            if (wasFullScreen) {
                stage.setFullScreen(true);
            } else if (wasMaximized) {
                stage.setMaximized(true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ==================== Helper Methods ====================

    private void updateStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
        }
        System.out.println("Status: " + message);
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}


package com.oop.visualgo.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class RedBlackTree<T extends Comparable<T>> {
    private RedBlackTreeNode<T> root;

    public RedBlackTreeNode<T> getRoot() {
        return root;
    }

    public void setRoot(RedBlackTreeNode<T> root) {
        this.root = root;
    }

    // ==================== Insert ====================
    public void insert(T value) {
        RedBlackTreeNode<T> newNode = new RedBlackTreeNode<>(value);
        
        if (root == null) {
            root = newNode;
            root.setBlack(); // Root is always black
            return;
        }

        // BST insert
        RedBlackTreeNode<T> current = root;
        RedBlackTreeNode<T> parent = null;

        while (current != null) {
            parent = current;
            int cmp = value.compareTo(current.getValue());
            if (cmp < 0) {
                current = current.getLeft();
            } else {
                current = current.getRight();
            }
        }

        newNode.setParent(parent);
        if (value.compareTo(parent.getValue()) < 0) {
            parent.setLeft(newNode);
        } else {
            parent.setRight(newNode);
        }

        // Fix Red-Black tree properties
        fixInsert(newNode);
    }

    private void fixInsert(RedBlackTreeNode<T> node) {
        while (node != root && node.getParent().isRed()) {
            RedBlackTreeNode<T> parent = node.getParent();
            RedBlackTreeNode<T> grandparent = node.getGrandparent();
            RedBlackTreeNode<T> uncle = node.getUncle();

            if (grandparent == null) break;

            if (parent == grandparent.getLeft()) {
                // Parent is left child
                if (uncle != null && uncle.isRed()) {
                    // Case 1: Uncle is red - recolor
                    parent.setBlack();
                    uncle.setBlack();
                    grandparent.setRed();
                    node = grandparent;
                } else {
                    // Case 2: Uncle is black
                    if (node == parent.getRight()) {
                        // Case 2a: Node is right child - left rotate
                        node = parent;
                        rotateLeft(node);
                        parent = node.getParent();
                    }
                    // Case 2b: Node is left child - right rotate
                    parent.setBlack();
                    grandparent.setRed();
                    rotateRight(grandparent);
                }
            } else {
                // Parent is right child (mirror cases)
                if (uncle != null && uncle.isRed()) {
                    // Case 1: Uncle is red - recolor
                    parent.setBlack();
                    uncle.setBlack();
                    grandparent.setRed();
                    node = grandparent;
                } else {
                    // Case 2: Uncle is black
                    if (node == parent.getLeft()) {
                        // Case 2a: Node is left child - right rotate
                        node = parent;
                        rotateRight(node);
                        parent = node.getParent();
                    }
                    // Case 2b: Node is right child - left rotate
                    parent.setBlack();
                    grandparent.setRed();
                    rotateLeft(grandparent);
                }
            }
        }
        root.setBlack(); // Root is always black
    }

    // ==================== Rotations ====================
    private void rotateLeft(RedBlackTreeNode<T> node) {
        RedBlackTreeNode<T> rightChild = node.getRight();
        node.setRight(rightChild.getLeft());

        if (rightChild.getLeft() != null) {
            rightChild.getLeft().setParent(node);
        }

        rightChild.setParent(node.getParent());

        if (node.getParent() == null) {
            root = rightChild;
        } else if (node == node.getParent().getLeft()) {
            node.getParent().setLeft(rightChild);
        } else {
            node.getParent().setRight(rightChild);
        }

        rightChild.setLeft(node);
        node.setParent(rightChild);
    }

    private void rotateRight(RedBlackTreeNode<T> node) {
        RedBlackTreeNode<T> leftChild = node.getLeft();
        node.setLeft(leftChild.getRight());

        if (leftChild.getRight() != null) {
            leftChild.getRight().setParent(node);
        }

        leftChild.setParent(node.getParent());

        if (node.getParent() == null) {
            root = leftChild;
        } else if (node == node.getParent().getRight()) {
            node.getParent().setRight(leftChild);
        } else {
            node.getParent().setLeft(leftChild);
        }

        leftChild.setRight(node);
        node.setParent(leftChild);
    }

    // ==================== Search ====================
    public RedBlackTreeNode<T> search(T value) {
        return searchRecursive(root, value);
    }

    private RedBlackTreeNode<T> searchRecursive(RedBlackTreeNode<T> node, T value) {
        if (node == null) return null;
        
        int cmp = value.compareTo(node.getValue());
        if (cmp == 0) {
            return node;
        } else if (cmp < 0) {
            return searchRecursive(node.getLeft(), value);
        } else {
            return searchRecursive(node.getRight(), value);
        }
    }

    // ==================== Delete ====================
    public boolean delete(T value) {
        RedBlackTreeNode<T> node = search(value);
        if (node == null) return false;

        deleteNode(node);
        return true;
    }

    private void deleteNode(RedBlackTreeNode<T> node) {
        RedBlackTreeNode<T> replacement;
        RedBlackTreeNode<T> child;

        // Find replacement node
        if (node.getLeft() == null || node.getRight() == null) {
            replacement = node;
        } else {
            replacement = findMin(node.getRight());
        }

        // Get child of replacement
        if (replacement.getLeft() != null) {
            child = replacement.getLeft();
        } else {
            child = replacement.getRight();
        }

        // Remove replacement from tree
        if (child != null) {
            child.setParent(replacement.getParent());
        }

        if (replacement.getParent() == null) {
            root = child;
        } else if (replacement == replacement.getParent().getLeft()) {
            replacement.getParent().setLeft(child);
        } else {
            replacement.getParent().setRight(child);
        }

        // Copy replacement value to node
        if (replacement != node) {
            node.setValue(replacement.getValue());
        }

        // Fix RB tree if needed
        if (replacement.isBlack() && child != null) {
            fixDelete(child);
        }

        if (root != null) {
            root.setBlack();
        }
    }

    private void fixDelete(RedBlackTreeNode<T> node) {
        while (node != root && (node == null || node.isBlack())) {
            if (node == null) break;
            
            RedBlackTreeNode<T> sibling = node.getSibling();
            RedBlackTreeNode<T> parent = node.getParent();

            if (parent == null) break;

            if (node == parent.getLeft()) {
                if (sibling != null && sibling.isRed()) {
                    sibling.setBlack();
                    parent.setRed();
                    rotateLeft(parent);
                    sibling = parent.getRight();
                }

                if (sibling == null || 
                    ((sibling.getLeft() == null || sibling.getLeft().isBlack()) &&
                     (sibling.getRight() == null || sibling.getRight().isBlack()))) {
                    if (sibling != null) sibling.setRed();
                    node = parent;
                } else {
                    if (sibling.getRight() == null || sibling.getRight().isBlack()) {
                        if (sibling.getLeft() != null) sibling.getLeft().setBlack();
                        sibling.setRed();
                        rotateRight(sibling);
                        sibling = parent.getRight();
                    }
                    if (sibling != null) {
                        sibling.setColor(parent.getColor());
                        sibling.getRight().setBlack();
                    }
                    parent.setBlack();
                    rotateLeft(parent);
                    node = root;
                }
            } else {
                // Mirror cases for right child
                if (sibling != null && sibling.isRed()) {
                    sibling.setBlack();
                    parent.setRed();
                    rotateRight(parent);
                    sibling = parent.getLeft();
                }

                if (sibling == null ||
                    ((sibling.getRight() == null || sibling.getRight().isBlack()) &&
                     (sibling.getLeft() == null || sibling.getLeft().isBlack()))) {
                    if (sibling != null) sibling.setRed();
                    node = parent;
                } else {
                    if (sibling.getLeft() == null || sibling.getLeft().isBlack()) {
                        if (sibling.getRight() != null) sibling.getRight().setBlack();
                        sibling.setRed();
                        rotateLeft(sibling);
                        sibling = parent.getLeft();
                    }
                    if (sibling != null) {
                        sibling.setColor(parent.getColor());
                        sibling.getLeft().setBlack();
                    }
                    parent.setBlack();
                    rotateRight(parent);
                    node = root;
                }
            }
        }
        if (node != null) node.setBlack();
    }

    private RedBlackTreeNode<T> findMin(RedBlackTreeNode<T> node) {
        while (node.getLeft() != null) {
            node = node.getLeft();
        }
        return node;
    }

    // ==================== Traversals ====================
    public List<RedBlackTreeNode<T>> traverseInOrder() {
        List<RedBlackTreeNode<T>> result = new ArrayList<>();
        inOrderRecursive(root, result);
        return result;
    }

    private void inOrderRecursive(RedBlackTreeNode<T> node, List<RedBlackTreeNode<T>> result) {
        if (node == null) return;
        inOrderRecursive(node.getLeft(), result);
        result.add(node);
        inOrderRecursive(node.getRight(), result);
    }

    public List<RedBlackTreeNode<T>> traversePreOrder() {
        List<RedBlackTreeNode<T>> result = new ArrayList<>();
        preOrderRecursive(root, result);
        return result;
    }

    private void preOrderRecursive(RedBlackTreeNode<T> node, List<RedBlackTreeNode<T>> result) {
        if (node == null) return;
        result.add(node);
        preOrderRecursive(node.getLeft(), result);
        preOrderRecursive(node.getRight(), result);
    }

    public List<RedBlackTreeNode<T>> traversePostOrder() {
        List<RedBlackTreeNode<T>> result = new ArrayList<>();
        postOrderRecursive(root, result);
        return result;
    }

    private void postOrderRecursive(RedBlackTreeNode<T> node, List<RedBlackTreeNode<T>> result) {
        if (node == null) return;
        postOrderRecursive(node.getLeft(), result);
        postOrderRecursive(node.getRight(), result);
        result.add(node);
    }

    public List<RedBlackTreeNode<T>> traverseBFS() {
        List<RedBlackTreeNode<T>> result = new ArrayList<>();
        if (root == null) return result;

        Queue<RedBlackTreeNode<T>> queue = new LinkedList<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            RedBlackTreeNode<T> node = queue.poll();
            result.add(node);
            if (node.getLeft() != null) queue.offer(node.getLeft());
            if (node.getRight() != null) queue.offer(node.getRight());
        }
        return result;
    }
}


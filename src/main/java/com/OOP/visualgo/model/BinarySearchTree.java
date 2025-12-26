package com.oop.visualgo.model;

public class BinarySearchTree<T extends Comparable<T>> extends BinaryTree<T> {
    @Override
    public void insert(T value) {
        BinaryTreeNode<T> newNode = new BinaryTreeNode<>(value);
        if (getRoot() == null) {
            setRoot(newNode);
            return;
        }
        insertRecursive(getRoot(), newNode);
    }
    private void insertRecursive(BinaryTreeNode<T> current, BinaryTreeNode<T> newNode) {
        // Nếu value < current -> đi sang trái
        if (newNode.getValue().compareTo(current.getValue()) < 0) {
            if (current.getLeft() == null) {
                current.setLeft(newNode);
                newNode.setParent(current);
            } else {
                insertRecursive(current.getLeft(), newNode);
            }
        }
        // Nếu value >= current -> đi sang phải
        else {
            if (current.getRight() == null) {
                current.setRight(newNode);
                newNode.setParent(current);
            } else {
                insertRecursive(current.getRight(), newNode);
            }
        }
    }

    @Override
    public BinaryTreeNode<T> search(T value) {
        return searchBST(getRoot(), value);
    }

    private BinaryTreeNode<T> searchBST(BinaryTreeNode<T> node, T value) {
        if (node == null) return null;
        int cmp = value.compareTo(node.getValue());
        if (cmp == 0) {
            return node;
        } else if (cmp < 0) {
            return searchBST(node.getLeft(), value);
        } else {
            return searchBST(node.getRight(), value);
        }
    }

    @Override
    public boolean delete(T value) {
        if (getRoot() == null) return false;
        BinaryTreeNode<T> deleteNode = search(value);
        if (deleteNode == null) return false;

        deleteNode(deleteNode);
        return true;
    }

    private void deleteNode(BinaryTreeNode<T> node) {
        // TH1: Node là lá
        if (node.isLeaf()) {
            if (node == getRoot()) {
                setRoot(null);
            } else {
                BinaryTreeNode<T> parent = node.getParent();
                if (parent.getLeft() == node) {
                    parent.setLeft(null);
                } else {
                    parent.setRight(null);
                }
            }
        }
        // TH2: Node có 1 con
        else if (node.getLeft() == null || node.getRight() == null) {
            BinaryTreeNode<T> child = (node.getLeft() != null) ? node.getLeft() : node.getRight();
            if (node == getRoot()) {
                setRoot(child);
                child.setParent(null);
            } else {
                BinaryTreeNode<T> parent = node.getParent();
                if (parent.getLeft() == node) {
                    parent.setLeft(child);
                } else {
                    parent.setRight(child);
                }
                child.setParent(parent);
            }
        }
        // TH3: Node có 2 con -> thay bằng node nhỏ nhất của con phải || node lớn nhất của con trái
        else {
            BinaryTreeNode<T> successor = findMin(node.getRight());
            node.setValue(successor.getValue());
            deleteNode(successor);
        }
    }

    // Tìm node nhỏ nhất trong subtree
    public BinaryTreeNode<T> findMin(BinaryTreeNode<T> node) {
        while (node.getLeft() != null) {
            node = node.getLeft();
        }
        return node;
    }

    // Tìm node lớn nhất trong subtree
    public BinaryTreeNode<T> findMax(BinaryTreeNode<T> node) {
        while (node.getRight() != null) {
            node = node.getRight();
        }
        return node;
    }

    // Tìm node nhỏ nhất trong cây
    public BinaryTreeNode<T> findMin() {
        if (getRoot() == null) return null;
        return findMin(getRoot());
    }

    // Tìm node lớn nhất trong cây
    public BinaryTreeNode<T> findMax() {
        if (getRoot() == null) return null;
        return findMax(getRoot());
    }

    // Duyệt In-order (Left -> Root -> Right)
    public java.util.List<BinaryTreeNode<T>> traverseInOrder() {
        java.util.List<BinaryTreeNode<T>> result = new java.util.ArrayList<>();
        inOrderRecursive(getRoot(), result);
        return result;
    }

    private void inOrderRecursive(BinaryTreeNode<T> node, java.util.List<BinaryTreeNode<T>> result) {
        if (node == null) return;
        inOrderRecursive(node.getLeft(), result);
        result.add(node);
        inOrderRecursive(node.getRight(), result);
    }
}

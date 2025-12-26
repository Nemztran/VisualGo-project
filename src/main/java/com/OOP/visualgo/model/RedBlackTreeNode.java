package com.oop.visualgo.model;

public class RedBlackTreeNode<T extends Comparable<T>> implements TreeNode<T> {
    public static final boolean RED = true;
    public static final boolean BLACK = false;

    private T value;
    private RedBlackTreeNode<T> parent;
    private RedBlackTreeNode<T> left;
    private RedBlackTreeNode<T> right;
    private boolean color; // true = RED, false = BLACK

    public RedBlackTreeNode(T value) {
        this.value = value;
        this.color = RED; // New nodes are always RED
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public RedBlackTreeNode<T> getParent() {
        return parent;
    }

    @Override
    public void setParent(TreeNode<T> parent) {
        this.parent = (RedBlackTreeNode<T>) parent;
    }

    public RedBlackTreeNode<T> getLeft() {
        return left;
    }

    public void setLeft(RedBlackTreeNode<T> left) {
        this.left = left;
    }

    public RedBlackTreeNode<T> getRight() {
        return right;
    }

    public void setRight(RedBlackTreeNode<T> right) {
        this.right = right;
    }

    public boolean isRed() {
        return color == RED;
    }

    public boolean isBlack() {
        return color == BLACK;
    }

    public boolean getColor() {
        return color;
    }

    public void setColor(boolean color) {
        this.color = color;
    }

    public void setRed() {
        this.color = RED;
    }

    public void setBlack() {
        this.color = BLACK;
    }

    public boolean isLeaf() {
        return left == null && right == null;
    }

    public RedBlackTreeNode<T> getGrandparent() {
        if (parent != null) {
            return parent.getParent();
        }
        return null;
    }

    public RedBlackTreeNode<T> getUncle() {
        RedBlackTreeNode<T> grandparent = getGrandparent();
        if (grandparent == null) {
            return null;
        }
        if (parent == grandparent.getLeft()) {
            return grandparent.getRight();
        } else {
            return grandparent.getLeft();
        }
    }

    public RedBlackTreeNode<T> getSibling() {
        if (parent == null) {
            return null;
        }
        if (this == parent.getLeft()) {
            return parent.getRight();
        } else {
            return parent.getLeft();
        }
    }
}


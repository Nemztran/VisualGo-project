package com.oop.visualgo.model;

public class BinaryTreeNode<T> implements TreeNode<T> {
    private T value;
    private BinaryTreeNode<T> parent;
    private BinaryTreeNode<T> left;
    private BinaryTreeNode<T> right;
    public BinaryTreeNode (T value) {
        this.value = value;
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
    public BinaryTreeNode<T> getParent() {
        return parent;
    }
    @Override
    public void setParent(TreeNode<T> parent) {
        this.parent = (BinaryTreeNode<T>) parent;
    }
    public BinaryTreeNode<T> getLeft() {
        return left;
    }
    public void setLeft(BinaryTreeNode<T> left) {
        this.left = left;
    }
    public BinaryTreeNode<T> getRight() {
        return right;
    }
    public void setRight(BinaryTreeNode<T> right) {
        this.right = right;
    }
    public boolean isLeaf() {
        return (left == null) && (right == null);
    }
}

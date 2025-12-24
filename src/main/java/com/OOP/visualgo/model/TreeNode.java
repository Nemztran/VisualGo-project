package com.oop.visualgo.model;
public interface TreeNode<T> {
    T getValue();
    void setValue(T value);
    TreeNode<T> getParent();
    void setParent(TreeNode<T> parent);
}


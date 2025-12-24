package com.oop.visualgo.model;
import java.util.ArrayList;
import java.util.List;
// Generic type in Java
public class GenericTreeNode<T> implements TreeNode<T> {
    private T value;
    private GenericTreeNode<T> parent;
    private List<GenericTreeNode<T>> children;
    public GenericTreeNode(T value) {
        this.value = value;
        this.children = new ArrayList<>();
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
    public GenericTreeNode<T> getParent() {
        return parent;
    }
    @Override
    public void setParent(TreeNode<T> parent) {
        this.parent = (GenericTreeNode<T>) parent;
    }
    public List<GenericTreeNode<T>> getChildren() {
        return children;
    }
    public void addChild(GenericTreeNode<T> child) {
        if (child == null) return;
        children.add(child);
        child.setParent(this);
    }
    public void removeChild(GenericTreeNode<T> child) {
        if (children.remove(child)) {
            child.setParent(null);
        }
    }
    public boolean isLeaf() {
        return children.isEmpty();
    }

}

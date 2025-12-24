package com.oop.visualgo.model;
import java.util.*;
public class GenericTree<T> implements Tree<T, GenericTreeNode<T>> {
    private GenericTreeNode<T> root;
    @Override
    public GenericTreeNode<T> getRoot() {
        return root;
    }
    @Override
    public void setRoot(GenericTreeNode<T> root) {
        this.root = root;
    }
    @Override
    public void insert(T value, T parentValue) {
        GenericTreeNode<T> newNode = new GenericTreeNode<>(value);
        if (root == null) {
            root = newNode;
            return;
        }
        GenericTreeNode<T> parent = search(parentValue);
        if (parent != null) {
            parent.addChild(newNode);
        }
    }
    @Override
    public boolean delete(T value) {
        if (root == null) return false;
        if (root.getValue().equals(value)) {
            root = null;
            return false;
        }
        GenericTreeNode<T> deleteNode = search(value);
        if (deleteNode != null && deleteNode.getParent() != null) {
            deleteNode.getParent().removeChild(deleteNode);
            return true;
        }
        return false;
    }
    @Override
    public boolean update(T oldValue, T newValue) {
        GenericTreeNode<T> oldNode = search(oldValue);
        if (oldNode != null) {
            oldNode.setValue(newValue);
            return true;
        }
        return false;
    }
    @Override
    public GenericTreeNode<T> search(T value) {
        return searchRecursive(root, value);
    }
    public GenericTreeNode<T> searchRecursive(GenericTreeNode<T> node, T value) {
        if (node == null) return null;
        if (node.getValue().equals(value)) {
            return node;
        }
        for (GenericTreeNode<T> child : node.getChildren()) {
            GenericTreeNode<T> res = searchRecursive(child, value);
            if (res != null) return res;
        }
        return null;
    }
    @Override
    public List<GenericTreeNode<T>> traverseDFS() {
        List<GenericTreeNode<T>> res = new ArrayList<>();
        traverseDFSRecurive(root, res);
        return res;
    }
    private void traverseDFSRecurive(GenericTreeNode<T> node, List<GenericTreeNode<T>> res) {
        if (node == null) return ;
        res.add(node);
        for (GenericTreeNode<T> child : node.getChildren()) {
            traverseDFSRecurive(child, res);
        }
    }
    @Override
    public List<GenericTreeNode<T>> traverseBFS() {
        List<GenericTreeNode<T>> res = new ArrayList<>();
        if (root == null) return res;

        Queue<GenericTreeNode<T>> queue = new LinkedList<>();
        queue.offer(root);
        while (!queue.isEmpty()) {
            GenericTreeNode<T> node = queue.poll();
            res.add(node);
            for (GenericTreeNode<T> child : node.getChildren()) {
                queue.offer(child);
            }
        }
        return res;
    }
    @Override
    public void clear() {
        root = null;
    }

}

package com.oop.visualgo.model;
import java.util.List;
public interface Tree<T, N extends TreeNode<T>> {
    N getRoot();
    void setRoot(N root);
    void insert(T value, T parentValue);
    boolean delete(T value);
    boolean update(T oldValue, T newValue);
    N search(T value);
    List<N> traverseDFS();
    List<N> traverseBFS();
    void clear();
}


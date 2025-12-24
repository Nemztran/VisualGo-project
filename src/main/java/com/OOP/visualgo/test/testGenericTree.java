package com.oop.visualgo.test;

import com.oop.visualgo.model.GenericTree;
import com.oop.visualgo.model.GenericTreeNode;

import java.util.List;

public class testGenericTree {
    public testGenericTree() {
        GenericTree<Integer> tree = new GenericTree<>();
        GenericTreeNode<Integer> root = new GenericTreeNode<>(12);
        tree.setRoot(root);
        tree.insert(1, 12);
        tree.insert(2, 1);
        tree.insert(3, 12);
        tree.insert(4, 3);
        System.out.println(tree.delete(5));
        tree.update(12, 36);
        List<GenericTreeNode<Integer>> bfsResult = tree.traverseBFS();
        for (GenericTreeNode<Integer> child : bfsResult) {
            System.out.println(child.getValue());
        }
    }
    public static void main(String[] args) {
        new testGenericTree();
    }
}

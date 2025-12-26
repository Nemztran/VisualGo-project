package com.oop.visualgo.test;

import com.oop.visualgo.model.BinaryTree;
import com.oop.visualgo.model.BinaryTreeNode;

import java.util.List;

public class testBinaryTree {
    public static void main (String[] args) {
        BinaryTreeNode<Integer> root = new BinaryTreeNode<>(18);
        BinaryTree<Integer> tree = new BinaryTree<>();
        tree.setRoot(root);
        tree.insert(30);
        tree.insert(31);
        tree.insert(32);
        tree.insert(33);
        tree.insert(34);
        tree.delete(32);
        List<BinaryTreeNode<Integer>> res = tree.traverseDFS();
        for (BinaryTreeNode<Integer> node : res) {
            System.out.println(node.getValue());
        }
        System.out.println(root.isLeaf());
    }

}

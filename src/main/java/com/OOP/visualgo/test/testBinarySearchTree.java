package com.oop.visualgo.test;

import com.oop.visualgo.model.BinarySearchTree;
import com.oop.visualgo.model.BinaryTreeNode;

import java.util.List;

public class testBinarySearchTree {
    public static void main(String[] args) {
        BinarySearchTree<Integer> bst = new BinarySearchTree<>();
        bst.insert(50);
        bst.insert(30);
        bst.insert(70);
        bst.insert(20);
        bst.insert(40);
        bst.insert(60);
        bst.insert(80);
        // inorder traverse
        List<BinaryTreeNode<Integer>> inOrder = bst.traverseInOrder();
        for (BinaryTreeNode<Integer> node : inOrder) {
            System.out.print(node.getValue() + " ");
        }
        System.out.println("================");
        // traverse BFS
        List<BinaryTreeNode<Integer>> bfs = bst.traverseBFS();
        for (BinaryTreeNode<Integer> node : bfs) {
            System.out.print(node.getValue() + " ");
        }
        System.out.println("================");
        // search
        BinaryTreeNode<Integer> found = bst.search(40);
        System.out.println(found == null);
        System.out.println("================");
        BinaryTreeNode<Integer> found_ = bst.search(100);
        System.out.println(found_ == null);
        System.out.println("================");
        // find min, max
        System.out.println(bst.findMin().getValue());
        System.out.println("================");
        System.out.println(bst.findMax().getValue());
        System.out.println("================");
        //delete node lá
        bst.delete(20);
        for (BinaryTreeNode<Integer> node : bst.traverseInOrder()) {
            System.out.print(node.getValue() + " ");
        }
        System.out.println("================");
        // delete node 1 con
        bst.delete(30);
        for (BinaryTreeNode<Integer> node : bst.traverseInOrder()) {
            System.out.print(node.getValue() + " ");
        }
        System.out.println("================");
        // delete node có 2 con
        bst.delete(70);
        for (BinaryTreeNode<Integer> node : bst.traverseInOrder()) {
            System.out.print(node.getValue() + " ");
        }
        System.out.println("================");
        // delete root
        bst.delete(50);
        for (BinaryTreeNode<Integer> node : bst.traverseInOrder()) {
            System.out.print(node.getValue() + " ");
        }
        System.out.println("================");

        System.out.println(bst.getRoot().getValue());


    }
}


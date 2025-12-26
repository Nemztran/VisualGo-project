package com.oop.visualgo.model;

import java.util.*;

public class BinaryTree<T> implements Tree<T, BinaryTreeNode<T>> {
       private BinaryTreeNode<T> root;
       @Override
       public BinaryTreeNode<T> getRoot() {
           return root;
       }
       @Override
       public void setRoot(BinaryTreeNode<T> root) {
           this.root = root;
       }
       @Override
       // check con trái -> con phải -> insert
       public void insert(T value, T parentValue) {
           BinaryTreeNode<T> newNode = new BinaryTreeNode<>(value);
           if (root == null) {
               root = newNode;
               return;
           }
           BinaryTreeNode<T> parent = search(parentValue);
           if (parent.getLeft() == null) {
               parent.setLeft(newNode);
               newNode.setParent(parent);
           }
           else if (parent.getRight() == null) {
               parent.setRight(newNode);
               newNode.setParent(parent);
           }
       }
     //  Tự động tìm node đầu tiên chưa có đủ 2 con để thêm vào
       public void insert(T value) {
           BinaryTreeNode<T> newNode = new BinaryTreeNode<>(value);
           if (root == null) {
               root = newNode;
               return;
           }
           // Dùng BFS để tìm node đầu tiên chưa có đủ 2 con
           Queue<BinaryTreeNode<T>> queue = new LinkedList<>();
           queue.offer(root);
           while (!queue.isEmpty()) {
               BinaryTreeNode<T> current = queue.poll();
               // Nếu chưa có con trái -> thêm vào bên trái
               if (current.getLeft() == null) {
                   current.setLeft(newNode);
                   newNode.setParent(current);
                   return;
               } else {
                   queue.offer(current.getLeft());
               }
               // Nếu chưa có con phải -> thêm vào bên phải
               if (current.getRight() == null) {
                   current.setRight(newNode);
                   newNode.setParent(current);
                   return;
               } else {
                   queue.offer(current.getRight());
               }
           }
       }
       @Override
       public boolean delete(T value) {
           if (root == null) return false;

           // Tìm node cần xóa và node cuối cùng (rightmost, deepest node)
           BinaryTreeNode<T> deleteNode = null;
           BinaryTreeNode<T> lastNode = null;
           BinaryTreeNode<T> lastParent = null;

           Queue<BinaryTreeNode<T>> queue = new LinkedList<>();
           queue.offer(root);

           while (!queue.isEmpty()) {
               BinaryTreeNode<T> current = queue.poll();

               if (current.getValue().equals(value)) {
                   deleteNode = current;
               }

               // Track parent of last node
               if (current.getLeft() != null) {
                   lastParent = current;
                   queue.offer(current.getLeft());
               }
               if (current.getRight() != null) {
                   lastParent = current;
                   queue.offer(current.getRight());
               }

               lastNode = current;
           }

           if (deleteNode == null) return false;

           // Nếu cây chỉ có 1 node (root)
           if (lastNode == root && deleteNode == root) {
               root = null;
               return true;
           }

           // Thay thế giá trị của node bị xóa bằng giá trị của node cuối cùng
           deleteNode.setValue(lastNode.getValue());

           // Xóa node cuối cùng
           if (lastParent != null) {
               if (lastParent.getRight() == lastNode) {
                   lastParent.setRight(null);
               } else if (lastParent.getLeft() == lastNode) {
                   lastParent.setLeft(null);
               }
           }

           return true;
       }
       @Override
       public boolean update(T oldValue, T newValue) {
           BinaryTreeNode<T> oldNode = search(oldValue);
           if (oldNode != null) {
               oldNode.setValue(newValue);
               return true;
           }
           return false;
       }
       @Override
       public BinaryTreeNode<T> search(T value) {
           return searchRecursive(root, value);
       }
       public BinaryTreeNode<T> searchRecursive(BinaryTreeNode<T> node, T value) {
           if (node == null) return null;
           if (node.getValue().equals(value)) {
               return node;
           }
           BinaryTreeNode<T> leftFound = searchRecursive(node.getLeft(), value);
           if (leftFound != null) return leftFound;
           return searchRecursive(node.getRight(), value);
       }
       @Override
       public List<BinaryTreeNode<T>> traverseDFS() {
           List<BinaryTreeNode<T>> res = new ArrayList<>();
           if (root == null) {
               return res;
           }
           Stack<BinaryTreeNode<T>> st = new Stack<>();
           st.push(root);
           while (!st.isEmpty()) {
               BinaryTreeNode<T> cur = st.pop();
               res.add(cur);
               if (cur.getRight() != null) {
                   st.push(cur.getRight());
               }
               if (cur.getLeft() != null) {
                   st.push(cur.getLeft());
               }
           }
           return res;
       }
       @Override
       public List<BinaryTreeNode<T>> traverseBFS() {
           List<BinaryTreeNode<T>> res = new ArrayList<>();
           if (root == null) return res;

           Queue<BinaryTreeNode<T>> queue = new LinkedList<>();
           queue.offer(root);
           while (!queue.isEmpty()) {
               BinaryTreeNode<T> node = queue.poll();
               res.add(node);
               if (node.getLeft() != null) {
                   queue.offer(node.getLeft());
               }
               if (node.getRight() != null) {
                   queue.offer(node.getRight());
               }
           }
           return res;
       }
       @Override
       public void clear() {
           root = null;
       }


}

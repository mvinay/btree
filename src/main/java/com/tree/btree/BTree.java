package com.tree.btree;

import com.tree.Data;
import com.tree.ITree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Created by vinay.madhusudhan on 01/12/15.
 */

public class BTree implements ITree {

    class Node {

        private ArrayList<Data> keys;
        private ArrayList<Node> pointers;
        Node parent;

        public Node() {
            setKeys(new ArrayList<>());
            setPointers(new ArrayList<>());
            parent = null;
        }

        public boolean isFull() {
            return getKeys().size() >= size;
        }

        public boolean isLeaf() {
            return getPointers().isEmpty();
        }

        public void addDataToLeaf(Data data) {
            assert !isFull() : "Node is full " + toString();
            assert isLeaf() : "Node is not leaf " + toString();
            getKeys().add(data);
            getKeys().sort((data1, other) -> data1.compare(other));
        }

        /*
        *   Adds the extra data to the node.
        *   Note: This should only be called while splitting.
         */
        public void addExtraData(Data data) {
            assert isLeaf() : "Node is not leaf " + toString();
            getKeys().add(data);
            getKeys().sort((data1, other) -> data1.compare(other));
        }

        @Override
        public String toString() {
            return "Node{" +
                    "keys=" + getKeys() +
                    ", pointers=" + getPointers() +
                    '}';
        }

        public String getName() {
            final StringBuffer name = new StringBuffer();

            for (int i = 0; i < getKeys().size(); ++i) {
                Data curr = getKeys().get(i);
                name.append("<f" + i + "> " + curr + " | ");
            }
            return name.substring(0, name.length() - 3).toString();
        }

        public String getId() {
            final StringBuffer id = new StringBuffer();
            getKeys().stream().forEachOrdered((data) -> id.append(data.toString()));
            id.append(hashCode());
            return id.toString();
        }

        public void updateParentForPointers() {
            getPointers().stream().forEach((node) -> node.parent = this);
        }

        public ArrayList<Data> getKeys() {
            return keys;
        }

        public void setKeys(ArrayList<Data> keys) {
            this.keys = (keys);
        }

        public ArrayList<Node> getPointers() {
            return pointers;
        }

        public void setPointers(ArrayList<Node> pointers) {
            this.pointers = pointers;
            updateParentForPointers();
        }
    }


    private Node root;
    private final int size;

    public BTree(int size) {
        root = new Node();
        this.size = size;
    }

    /* split the node into two halves and insert the new node to the parent.
     */
    private void split(Node node, Data data, Node left, Node right) {
        assert node.isFull() : "Node is not full " + node;

        // If the node is root, assign the new root and return.
        if (node == root) {
            root.setKeys(getNewList(Arrays.asList(data)));
            root.setPointers(new ArrayList<>(Arrays.asList(left, right)));
            return;
        }

        // If it is not root, add the node to the right position in the parent.
        Node parent = node.parent;
        assert parent != null : "parent is null " + node;

        // Get the position of the child in the parent.
        int pointerPos = getNodePtrPosInParent(parent, node);
        boolean isParentFull = parent.isFull();

        // Put the data to the correct position in the parent, even if it is full!
        parent.getKeys().add(pointerPos, data);
        parent.getPointers().set(pointerPos, left);
        parent.getPointers().add(pointerPos + 1, right);
        left.parent = parent;
        right.parent = parent;

        // If the parent is overloaded, do not split. We are done.
        if (!isParentFull) {
            return;
        }

        int nodeSize = parent.getKeys().size();
        left = getNewSplitNode(parent, true);
        right = getNewSplitNode(parent, false);
        data = parent.getKeys().get(nodeSize / 2);
        split(parent, data, left, right);
    }

    private ArrayList getNewList(List other) {
        return new ArrayList<>(other);
    }

    private Node getNewSplitNode(Node node, boolean isLeft) {
        Node newNode = new Node();
        int nodeSize = node.getKeys().size();

        int startIndex, endIndex;
        int startPtrIndex, endPtrIndex;
        if (isLeft) {
            startIndex = startPtrIndex = 0;
            endIndex = nodeSize / 2;
            endPtrIndex = nodeSize / 2 + 1;
        } else {
            startIndex = startPtrIndex = nodeSize / 2 + 1;
            endIndex = nodeSize;
            endPtrIndex = nodeSize + 1;
        }

        newNode.setKeys(getNewList(node.getKeys().subList(startIndex, endIndex)));
        if (!node.isLeaf()) {
            newNode.setPointers(getNewList(node.getPointers().subList(startPtrIndex, endPtrIndex)));
        }
        return newNode;
    }

    private int getNodePtrPosInParent(Node parent, Node node) {
        for (int i = 0; i < parent.getPointers().size(); ++i) {
            if (node == parent.getPointers().get(i)) {
                return i;
            }
        }
        assert false : "Could not find the node in the parent";
        return -1;
    }

    public void add(Data data) {
        Node node = getNodeToInsert(data);

        if (node.isFull()) {
            node.addExtraData(data);

            // split the node into two halves.
            Node left = getNewSplitNode(node, true);
            Node right = getNewSplitNode(node, false);

            // Extract the middle node and keep it ready to be attached.
            int nodeSize = node.getKeys().size();
            data = node.getKeys().get(nodeSize / 2);

            // split the nodes into two halves and update the tree.
            split(node, data, left, right);
        } else {
            node.addDataToLeaf(data);
        }
        dumpTree(data);
    }


    private int getPositionForKey(Node node, Object key) {
        for (int i = 0; i < node.getKeys().size(); ++i) {
            Data curr = node.getKeys().get(i);
            if (curr.compare(key) > 0) {
                return i;
            }
        }
        // It is the right most node.
        return node.getKeys().size();
    }


    /**
     * @param node
     * @param key
     * @return Node which has/ should have the data.
     */
    private Node searchNodeToInsert(Node node, Object key) {

        if (node == null) {
            return null;
        }

        if (node.isLeaf()) {
            return node;
        }

        int index = getPositionForKey(node, key);
        return searchNodeToInsert(node.getPointers().get(index), key);

    }

    /**
     * @param node
     * @param key
     * @return Node which has/ should have the data.
     */
    private Data getData(Node node, Object key) {

        if (node == null) {
            return null;
        }

        if (node.isLeaf()) {
            return getDataInNode(node, key);
        }

        int index = getPositionForKey(node, key);
        Data data = getDataInNode(node, key);
        if (data != null) {
            return data;
        }

        return getData(node.getPointers().get(index), key);
    }

    /**
     * @param node
     * @param key
     * @return The Data object; if present
     */
    private Data getDataInNode(Node node, Object key) {
        Optional<Data> data = node.getKeys().stream().filter(obj -> (obj.compare(key) == 0)).findFirst();
        if (data.isPresent()) {
            return data.get();
        }
        return null;
    }


    private Node getNodeToInsert(Data data) {
        return searchNodeToInsert(root, data.key());
    }

    public Data get(Object key) {
        return getData(root, key);
    }

    public Data delete(Object key) {
        assert false : "Not yet implemented!!!";
        return null;
    }

    public void dump(Object key) {
        Data data = get(key);
        System.out.println("Data = " + data);
    }

    public void dumpTree(Data data) {
        GraphVizPrinter.printTree(root, data);
    }
}

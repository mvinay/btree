package com.tree.btree.test;

import com.tree.Data;
import com.tree.btree.BTree;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by vinay.madhusudhan on 01/12/15.
 */
public class BTreeTest {

    /*
    * A Data which stores integer.
     */
    class IntData implements Data<Integer> {

        private Integer id;

        public IntData(Integer value) {
            id = value;
        }

        @Override
        public int compare(Data<Integer> other) {
            return compare(other.key());
        }

        @Override
        public int compare(Object key) {
            assert key instanceof Integer;
            return id.compareTo((Integer) key);
        }

        @Override
        public Integer key() {
            return id;
        }

        @Override
        public String toString() {
            return id.toString();
        }
    }

    @Test
    public void testBTree() {

        // Tree with order 3 keys and 4 fan-outs
        BTree tree = new BTree(3);

        ArrayList<Integer> insertList = new ArrayList<>();

        // Find some random numbers.
        for (int i = 100; i >= 0; --i) {
            insertList.add(Math.abs(new Random().nextInt(10000)));
        }

        // insert the random data.
        insertList.stream().forEach((curr) -> tree.add(new IntData(curr)));

        // search for all the inserted data.
        insertList.stream().forEach((curr) -> Assert.assertNotNull(tree.get(curr)));

        // prints the tree to dotfile.
        tree.dumpTree();
    }
}

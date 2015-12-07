package com.tree;

/**
 * Created by vinay.madhusudhan on 01/12/15.
 */
public interface ITree {

    void add(Data data);

    Data get(Object key);

    Data delete(Object key);

    void dump(Object key);

    void dumpTree();
}

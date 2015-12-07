package com.tree;

/**
 * Created by vinay.madhusudhan on 01/12/15.
 */
public interface Data<T> {

    /*
    *   0 - equals
    *   1 - curr object is greater
    *   -1 - other is greater
     */
    int compare(final Data<T> other);

    /*
    * compare with another key.
    *
    */
    int compare(final Object key);

    /*
    * Returns the key. It should not be null.
     */
    T key();

}

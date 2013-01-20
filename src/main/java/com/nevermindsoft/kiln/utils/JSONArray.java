package com.nevermindsoft.kiln.utils;

import java.util.ArrayList;

/**
 * User: rcracel
 * Date: 1/20/13
 * Time: 6:31 PM
 */
public class JSONArray<E> extends ArrayList<E> {

    /**
     * Returns a string representation of this collection.  The string
     * representation consists of a list of the collection's elements in the
     * order they are returned by its iterator, enclosed in square brackets
     * (<tt>"[]"</tt>).  Adjacent elements are separated by the characters
     * <tt>", "</tt> (comma and space).  Elements are converted to strings as
     * by {@link String#valueOf(Object)}.
     *
     * @return a string representation of this collection
     */
    @Override
    public String toString() {
        return JSONUtils.array2json(this);
    }
}

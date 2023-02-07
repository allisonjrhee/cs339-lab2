package simpledb.storage;

import simpledb.common.Type;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * TupleDesc describes the schema of a tuple.
 */
public class TupleDesc implements Serializable {

    /**
     * A help class to facilitate organizing the information of each field
     */
    public static class TDItem implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * The type of the field
         */
        public final Type fieldType;

        /**
         * The name of the field
         */
        public final String fieldName;

        public TDItem(Type t, String n) {
            this.fieldName = n;
            this.fieldType = t;
        }

        public String toString() {
            return fieldName + "(" + fieldType + ")";
        }

    }

    private TDItem[] fields;

    /**
     * @return An iterator which iterates over all the field TDItems
     *         that are included in this TupleDesc
     */
    public Iterator<TDItem> iterator() {
        // TODO: some code goes here
        return null;
    }

    private static final long serialVersionUID = 1L;

    /**
     * Create a new TupleDesc with typeAr.length fields with fields of the
     * specified types, with associated named fields.
     *
     * @param typeAr  array specifying the number of and types of fields in this
     *                TupleDesc. It must contain at least one entry.
     * @param fieldAr array specifying the names of the fields. Note that names may
     *                be null.
     */
    public TupleDesc(Type[] typeAr, String[] fieldAr) {
        // TODO: some code goes here
        TDItem[] cfields = new TDItem[typeAr.length];
        for (int i = 0; i < typeAr.length; i++){
            cfields[i] = new TDItem(typeAr[i], fieldAr[i]);
        }
        this.fields = cfields;
    }

    /**
     * Constructor. Create a new tuple desc with typeAr.length fields with
     * fields of the specified types, with anonymous (unnamed) fields.
     *
     * @param typeAr array specifying the number of and types of fields in this
     *               TupleDesc. It must contain at least one entry.
     */
    public TupleDesc(Type[] typeAr) {
        // TODO: some code goes here
        TDItem[] cfields = new TDItem[typeAr.length];
        for (int i = 0; i < typeAr.length; i++){
            cfields[i] = new TDItem(typeAr[i], null);
        }
        this.fields = cfields;
    }

    /**
     * @return the number of fields in this TupleDesc
     */
    public int numFields() {
        // TODO: some code goes here
        return this.fields.length;
    }

    /**
     * Gets the (possibly null) field name of the ith field of this TupleDesc.
     *
     * @param i index of the field name to return. It must be a valid index.
     * @return the name of the ith field
     * @throws NoSuchElementException if i is not a valid field reference.
     */
    public String getFieldName(int i) throws NoSuchElementException {
        // TODO: some code goes here
        if (i >= fields.length){
            throw new NoSuchElementException();
        }
        TDItem tup = fields[i];
        return tup.fieldName;
    }

    /**
     * Gets the type of the ith field of this TupleDesc.
     *
     * @param i The index of the field to get the type of. It must be a valid
     *          index.
     * @return the type of the ith field
     * @throws NoSuchElementException if i is not a valid field reference.
     */
    public Type getFieldType(int i) throws NoSuchElementException {
        // TODO: some code goes here
        if (i >= fields.length){
            throw new NoSuchElementException();
        }
        TDItem tup = fields[i];
        return tup.fieldType;
    }

    /**
     * Find the index of the field with a given name.
     *
     * @param name name of the field.
     * @return the index of the field that is first to have the given name.
     * @throws NoSuchElementException if no field with a matching name is found.
     */
    public int indexForFieldName(String name) throws NoSuchElementException {
        // TODO: some code goes here
        for (int i = 0; i < fields.length; i++){
            if (fields[i].fieldName != null && fields[i].fieldName.equals(name)) {
                return i;
            }
        }
        throw new NoSuchElementException();
    }

    /**
     * @return The size (in bytes) of tuples corresponding to this TupleDesc.
     *         Note that tuples from a given TupleDesc are of a fixed size.
     */
    public int getSize() {
        // TODO: some code goes here
        int total = 0;
        for (int i = 0; i<fields.length;i++) {
            TDItem tup = fields[i];
            int currsize = tup.fieldType.getLen();
            total += currsize;
        }
        return total;
    }

    /**
     * Merge two TupleDescs into one, with td1.numFields + td2.numFields fields,
     * with the first td1.numFields coming from td1 and the remaining from td2.
     *
     * @param td1 The TupleDesc with the first fields of the new TupleDesc
     * @param td2 The TupleDesc with the last fields of the TupleDesc
     * @return the new TupleDesc
     */
    public static TupleDesc merge(TupleDesc td1, TupleDesc td2) {
        // TODO: some code goes here
        int newlen = td1.numFields() + td2.numFields();
        int firstlen = td1.numFields();
        // have to use TupleDesc which takes array of types and array of fields
        Type[] types = new Type[newlen];
        String[] names = new String[newlen];

        for (int i = 0; i < newlen; i++){
            if (i < firstlen){
                types[i] = td1.getFieldType(i);
                names[i] = td1.getFieldName(i);
            } else {
                types[i] = td2.getFieldType(i-firstlen);
                names[i] = td2.getFieldName(i-firstlen);
            }
        }

        TupleDesc merged = new TupleDesc(types, names);
        return merged;
    }

    /**
     * Compares the specified object with this TupleDesc for equality. Two
     * TupleDescs are considered equal if they have the same number of items
     * and if the i-th type in this TupleDesc is equal to the i-th type in o
     * for every i.
     *
     * @param o the Object to be compared for equality with this TupleDesc.
     * @return true if the object is equal to this TupleDesc.
     */

    public boolean equals(Object o) {
        // TODO: some code goes here
        /* does this work???????
        if (o.equals(this)) {
            return true;
        } else {
            return false;
        } */

        //System.out.println(this.toString()); this is just to test toString!!

        if (o==null){
            return false;
        }
        if (o.getClass() != TupleDesc.class) {
            return false;
        }
        // same num of fields
        if (((TupleDesc) o).numFields() != this.numFields()){
            return false;
            }
        // each field type is the same
        for (int i = 0; i<this.numFields();i++) {
            Class oclass = ((TupleDesc) o).getFieldType(i).getClass();
            Class thisclass = this.getFieldType(i).getClass();
            if (oclass != thisclass){
                return false;}}
        return true;

    }

    public int hashCode() {
        // If you want to use TupleDesc as keys for HashMap, implement this so
        // that equal objects have equals hashCode() results
        throw new UnsupportedOperationException("unimplemented");
    }

    /**
     * Returns a String describing this descriptor. It should be of the form
     * "fieldType[0](fieldName[0]), ..., fieldType[M](fieldName[M])", although
     * the exact format does not matter.
     *
     * @return String describing this descriptor.
     */
    public String toString() {
        String result = "";
        for (int i = 0; i < this.numFields(); i++){
            if (i == (this.numFields()-1)){
                result = result + this.getFieldType(i) + "(" + this.getFieldName(i) + ")";
            } else {
                result = result + this.getFieldType(i) + "(" + this.getFieldName(i) + "),";
            }
        }
        return result;
    }
}

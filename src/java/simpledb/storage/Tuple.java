package simpledb.storage;

import simpledb.common.Type;

import java.io.Serializable;
import java.util.*;

/**
 * Tuple maintains information about the contents of a tuple. Tuples have a
 * specified schema specified by a TupleDesc object and contain Field objects
 * with the data for each field.
 */
public class Tuple implements Serializable {

    private static final long serialVersionUID = 1L;

    private RecordId rid;

    private Field[] tuple;

    private TupleDesc tupledesc;

    /**
     * Create a new tuple with the specified schema (type).
     *
     * @param td the schema of this tuple. It must be a valid TupleDesc
     *           instance with at least one field.
     */
    public Tuple(TupleDesc td) {
        this.tupledesc = td;
        this.rid = null;
        this.tuple = new Field[td.numFields()];
    }

    /**
     * @return The TupleDesc representing the schema of this tuple.
     */
    public TupleDesc getTupleDesc() {
        // TODO: some code goes here
        return this.tupledesc;
    }

    /**
     * @return The RecordId representing the location of this tuple on disk. May
     *         be null.
     */
    public RecordId getRecordId() {
        // TODO: some code goes here
        return this.rid;
    }

    /**
     * Set the RecordId information for this tuple.
     *
     * @param rid the new RecordId for this tuple.
     */
    public void setRecordId(RecordId rid) {
        // TODO: some code goes here
        this.rid = rid;
    }

    /**
     * Change the value of the ith field of this tuple.
     *
     * @param i index of the field to change. It must be a valid index.
     * @param f new value for the field.
     */
    public void setField(int i, Field f) {
        // TODO: some code goes here
        Type currtype = this.tupledesc.getFieldType(i);
        if (i < this.tupledesc.numFields()){
            if (currtype == f.getType()) {
                this.tuple[i] = f;
            }
        }
    }

    /**
     * @param i field index to return. Must be a valid index.
     * @return the value of the ith field, or null if it has not been set.
     */
    public Field getField(int i) {
        // TODO: some code goes here
        if (i < this.tupledesc.numFields()){
            if (this.tuple[i] != null){
                return this.tuple[i];
            } else {
                return null;
            }
        }
        return null;
    }

    /**
     * Returns the contents of this Tuple as a string. Note that to pass the
     * system tests, the format needs to be as follows:
     * <p>
     * column1\tcolumn2\tcolumn3\t...\tcolumnN
     * <p>
     * where \t is any whitespace (except a newline)
     */
    public String toString() {
        // TODO: some code goes here
        String result = "";
        for (int i = 0; i < this.tupledesc.numFields(); i++){
            if (i == (this.tupledesc.numFields()-1)) {
                if (this.tuple[i] == null) {
                    result = result + "null";
                } else {
                    result = result + this.tuple[i];
                }
            } else {
                if (this.tuple[i] == null) {
                    result = result + "null" + "  ";
                } else {
                    result = result + this.tuple[i] + "  ";
                }
            }
        }
        return result;
//        throw new UnsupportedOperationException("Implement this");
    }

    /**
     * @return An iterator which iterates over all the fields of this tuple
     */
    public Iterator<Field> fields() {
        // TODO: some code goes here
        ArrayList<Field> arr = new ArrayList<Field>(this.tuple.length);
        for (int i = 0; i < this.tuple.length; i++){
            arr.add(this.tuple[i]);
        }
        Iterator<Field> it = arr.iterator();
        return it;
    }

    /**
     * reset the TupleDesc of this tuple (only affecting the TupleDesc)
     */
    public void resetTupleDesc(TupleDesc td) {
        // TODO: some code goes here
        this.tupledesc = td;
    }
}

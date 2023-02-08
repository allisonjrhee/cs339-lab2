package simpledb.storage;

import simpledb.common.Database;
import simpledb.common.DbException;
import simpledb.common.Debug;
import simpledb.common.Permissions;
import simpledb.transaction.TransactionAbortedException;
import simpledb.transaction.TransactionId;

import java.io.*;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * HeapFile is an implementation of a DbFile that stores a collection of tuples
 * in no particular order. Tuples are stored on pages, each of which is a fixed
 * size, and the file is simply a collection of those pages. HeapFile works
 * closely with HeapPage. The format of HeapPages is described in the HeapPage
 * constructor.
 *
 * @author Sam Madden
 * @see HeapPage#HeapPage
 */
public class HeapFile implements DbFile {

    /**
     * Constructs a heap file backed by the specified file.
     *
     * @param f the file that stores the on-disk backing store for this heap
     * file.
     */

    private File file;
    private TupleDesc td;

    public HeapFile(File f, TupleDesc td) {
        // TODO: some code goes here
        this.file = f;
        this.td = td;

    }

    /**
     * Returns the File backing this HeapFile on disk.
     *
     * @return the File backing this HeapFile on disk.
     */
    public File getFile() {
        // TODO: some code goes here
        return this.file;
    }

    /**
     * Returns an ID uniquely identifying this HeapFile. Implementation note:
     * you will need to generate this tableid somewhere to ensure that each
     * HeapFile has a "unique id," and that you always return the same value for
     * a particular HeapFile. We suggest hashing the absolute file name of the
     * file underlying the heapfile, i.e. f.getAbsoluteFile().hashCode().
     *
     * @return an ID uniquely identifying this HeapFile.
     */
    public int getId() {
        // TODO: some code goes here
        return this.file.getAbsoluteFile().hashCode();
    }

    /**
     * Returns the TupleDesc of the table stored in this DbFile.
     *
     * @return TupleDesc of this DbFile.
     */
    public TupleDesc getTupleDesc() {
        return this.td;
    }

    // see DbFile.java for javadocs
    public Page readPage(PageId pid) {
        // TODO: some code goes here
        int pageNum = pid.getPageNumber();
        byte[] pageData = new byte[BufferPool.getPageSize()];
        try {
            FileInputStream fin = new FileInputStream(file);
            BufferedInputStream bin = new BufferedInputStream(fin);

            bin.read(pageData, pageNum * BufferPool.getPageSize(), BufferPool.getPageSize());
            HeapPageId hpid = new HeapPageId(pid.getTableId(), pageNum);
            return new HeapPage(hpid, pageData);
        } catch (IOException e) {
            System.out.println("Error: " + e);
            return null;
        }
    }

    // see DbFile.java for javadocs
    public void writePage(Page page) throws IOException {
        // TODO: some code goes here
        // not necessary for lab1
    }

    /**
     * Returns the number of pages in this HeapFile.
     */
    public int numPages() {
        // TODO: some code goes here
        // file is object arranged into set of pages, so file length / page size?
        return (int) Math.floor(this.file.length() / BufferPool.getPageSize());
    }

    // see DbFile.java for javadocs
    public List<Page> insertTuple(TransactionId tid, Tuple t)
            throws DbException, IOException, TransactionAbortedException {
        // TODO: some code goes here
        return null;
        // not necessary for lab1
    }

    // see DbFile.java for javadocs
    public List<Page> deleteTuple(TransactionId tid, Tuple t) throws DbException,
            TransactionAbortedException {
        // TODO: some code goes here
        return null;
        // not necessary for lab1
    }

    // see DbFile.java for javadocs

    private class HeapIterator extends AbstractDbFileIterator {
        /**
         * You will also need to implement the `HeapFile.iterator()` method,
         * which should iterate through the tuples of each page in the
         * HeapFile. The iterator must use the `BufferPool.getPage()` method
         * to access pages in the `HeapFile`. This method loads the page into
         * the buffer pool and will eventually be used (in a later lab) to implement
         * locking-based concurrency control and recovery. Do not load
         * the entire table into memory on the open() call -- this will cause an out
         * of memory error for very large tables.
         */
        public HeapPage currPage;

        private int currPageNo;

        public int getCurrPageNo() {
            return currPageNo;
        }

        private Tuple next = null;
        private Iterator<Tuple> it; //DbFileIterator should iterate through the tuples of each page in the HeapFile

        private TransactionId tid; //because DbFileIterator takes in a tid
        private int tableId;
        private BufferPool dbBUfferPool;
        private boolean isOpen;

        public HeapIterator(TransactionId tid) {
            this.tid = tid;
            this.currPageNo = 0;
            this.tableId = getId();
            this.dbBUfferPool = Database.getBufferPool();
        }

        public void open() throws TransactionAbortedException, DbException {
            //need a page id to call BufferPool.getpage(), use HeapPageID?
            //use this code to read other pages as well
            this.isOpen = true;
            HeapPageId hpid = new HeapPageId(this.tableId, this.currPageNo);
            currPage = (HeapPage) (dbBUfferPool.getPage(this.tid, hpid, Permissions.READ_ONLY));
//            while (hasNext()) { //need to define hasNext separately?
//                readNext();
//            }
            this.it = currPage.iterator(); //iterate over the tuples in the page, already have iterator for that in HeapPage

        }

        @Override
        public boolean hasNext() throws DbException, TransactionAbortedException {
            if (!isOpen) {
                return false;
            }
            if (it.hasNext() != true) { //the tuple iterator does not have a next
                //we should move to the next page
                int maxpages = numPages();
                if (currPageNo + 1 < maxpages) {
                    currPageNo++;
                    HeapPageId hpid = new HeapPageId(tableId, currPageNo);
                    currPage = (HeapPage) (dbBUfferPool.getPage(tid, hpid, Permissions.READ_ONLY));
                    it = currPage.iterator();
                    if (it.hasNext() == true) {
                        return true;
                    }
                } else {
                    return false;
                }
            }
            return true;
        }

        @Override
        public void close() {
            // Ensures that a future call to next() will fail
            isOpen = false;
        }

        @Override
        public void rewind() throws DbException, TransactionAbortedException {
            close();
            this.currPageNo = 0;
            open();
        }

        @Override
        protected Tuple readNext() throws DbException, TransactionAbortedException {
            if (isOpen) {
                if (hasNext()) {
                    next = it.next();
                    return next;
                }
            }
            throw new TransactionAbortedException();
        }

        @Override
        public Tuple next() throws DbException, TransactionAbortedException,
                NoSuchElementException {
            //if
            if (hasNext() == true) {
                next = readNext();
                return next;
            } else {
                throw new NoSuchElementException();
            }
        }
    }

    public DbFileIterator iterator(TransactionId tid) {
        // TODO: some code goes here
        return new HeapIterator(tid);
//        HeapIterator it = new HeapIterator(tid);
//        for (int i=it.getCurrPageNo(); i <= numPages(); i++) {
//            //Having this throw dbException might change API?
//            it.open();
//        }
//        return it;
    }
}


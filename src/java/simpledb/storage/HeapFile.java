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
        //pageNum checks
        if (pageNum < 0 || pageNum > this.numPages()){
            throw new IllegalArgumentException("invalid pid");
        }
        byte[] pageData = new byte[BufferPool.getPageSize()];
        try {
            RandomAccessFile raf = new RandomAccessFile(this.file, "r");
            /**
             * The pages will be allocated sequentially in the file.
             * Thus when we open the file, we want to seek to the offset
             * <page size> * <page_no> to read in the right page.
             */
            int offset = BufferPool.getPageSize() * pid.getPageNumber();
            raf.seek(offset);
            raf.read(pageData);
            //returns a page so we need to construct a new page
            HeapPageId hpid = new HeapPageId(pid.getTableId(), pid.getPageNumber());
            Page pageread = new HeapPage(hpid, pageData);
            raf.close();
            return pageread;
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        return null;

        /* using bufferedinputstream
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
         */
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
        private Iterator<Tuple> it; //DbFileIterator should iterate through the tuples of each page in the HeapFile

        private TransactionId tid; //because DbFileIterator takes in a tid

        private HeapFile hf;
        private boolean isOpen;

        public HeapIterator(HeapFile hf, TransactionId tid) {
            this.tid = tid;
            this.hf = hf;
            this.isOpen = false;
            this.currPageNo = 0;
        }

        public void open() throws TransactionAbortedException, DbException {
            //need a page id to call BufferPool.getpage(), use HeapPageID?
            //use this code to read other pages as well
            this.isOpen = true;
            HeapPageId hpId = new HeapPageId(hf.getId(), this.currPageNo);
            this.currPage = (HeapPage) Database.getBufferPool().getPage(tid, hpId, Permissions.READ_ONLY);
            it = this.currPage.iterator();

//            while (hasNext()) { //need to define hasNext separately?
//                readNext();
//            }
            // it = currPage.iterator(); //iterate over the tuples in the page, already have iterator for that in HeapPage
        }

        @Override
        public void close() {
            super.close();
            // Ensures that a future call to next() will fail
            it = null;
            currPageNo = 0;
            this.isOpen = false;
        }

        @Override
        public void rewind() throws DbException, TransactionAbortedException {
            close();
            open();
        }

        @Override
        protected Tuple readNext() throws DbException, TransactionAbortedException {
            if (!this.isOpen) { return null;}

//            HeapPageId hpId = new HeapPageId(hf.getId(), currPageNo);
//            currPage = (HeapPage) Database.getBufferPool().getPage(tid, hpId, Permissions.READ_ONLY);
//            it = currPage.iterator();

            //iterator has a next
            if (it.hasNext()) {return it.next();}

//            System.out.println("number of pages total = " + hf.numPages());

            while (currPageNo < hf.numPages()) {
//                System.out.println("curr page number = " + currPageNo);
                if (!it.hasNext()) {
//                    System.out.println("it has no next!");
                    currPageNo++;
//                    System.out.println("page number is now " + currPageNo);
                    if (currPageNo >= hf.numPages()){
//                        System.out.println("returning a null...");
                        return null;
                    } else {
                        HeapPageId hpId = new HeapPageId(hf.getId(), currPageNo);
                        currPage = (HeapPage) Database.getBufferPool().getPage(tid, hpId, Permissions.READ_ONLY);
                        it = currPage.iterator();
//                        System.out.println("looping again...");
                    }
                } else {
//                    System.out.println("found a next, returning it...");
                    return it.next();
                }
            }
//            System.out.println("returning a null...");
            return null;
        }
    }

    public DbFileIterator iterator(TransactionId tid) {
        // TODO: some code goes here
        return new HeapIterator(this, tid);
//        HeapIterator it = new HeapIterator(tid);
//        for (int i=it.getCurrPageNo(); i <= numPages(); i++) {
//            //Having this throw dbException might change API?
//            it.open();
//        }
//        return it;
    }
}


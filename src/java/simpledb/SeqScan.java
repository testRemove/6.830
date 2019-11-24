package simpledb;

import java.io.File;
import java.util.*;

import sun.net.www.protocol.http.HttpURLConnection.TunnelState;

/**
 * SeqScan is an implementation of a sequential scan access method that reads
 * each tuple of a table in no particular order (e.g., as they are laid out on
 * disk).
 */
public class SeqScan implements OpIterator {
	private TransactionId tId;
	private int tableId;
	private String tableAlias;
	private DbFile dbFile;
	private DbFileIterator iterator;

    private static final long serialVersionUID = 1L;

    /**
     * Creates a sequential scan over the specified table as a part of the
     * specified transaction.
     *
     * @param tid
     *            The transaction this scan is running as a part of.
     * @param tableid
     *            the table to scan.
     * @param tableAlias
     *            the alias of this table (needed by the parser); the returned
     *            tupleDesc should have fields with name tableAlias.fieldName
     *            (note: this class is not responsible for handling a case where
     *            tableAlias or fieldName are null. It shouldn't crash if they
     *            are, but the resulting name can be null.fieldName,
     *            tableAlias.null, or null.null).
     */
    public SeqScan(TransactionId tid, int tableid, String tableAlias) {
        // some code goes here
    	this.tId = tid;
    	this.tableId = tableid;
    	this.tableAlias = tableAlias;
    	this.dbFile = null; // 在open()再打开。
    	this.iterator = null;
    }
    
    public SeqScan(TransactionId tid, int tableId) {
        this(tid, tableId, Database.getCatalog().getTableName(tableId));
    }

    /**
     * @return
     *       return the table name of the table the operator scans. This should
     *       be the actual name of the table in the catalog of the database
     * */
    public String getTableName() {
        return Database.getCatalog().getTableName(tableId);
    }

    /**
     * @return Return the alias of the table this operator scans.
     * */
    public String getAlias()
    {
        // some code goes here
        return tableAlias;
    }

    /**
     * Reset the tableid, and tableAlias of this operator.
     * @param tableid
     *            the table to scan.
     * @param tableAlias
     *            the alias of this table (needed by the parser); the returned
     *            tupleDesc should have fields with name tableAlias.fieldName
     *            (note: this class is not responsible for handling a case where
     *            tableAlias or fieldName are null. It shouldn't crash if they
     *            are, but the resulting name can be null.fieldName,
     *            tableAlias.null, or null.null).
     */
    public void reset(int tableid, String tableAlias) {
        // some code goes here
    	this.tableId = tableid;
    	this.tableAlias = tableAlias;
    }

    public void open() throws DbException, TransactionAbortedException {
        // some code goes here
    	dbFile = Database.getCatalog().getDatabaseFile(tableId);
    	iterator = dbFile.iterator(tId);
    	iterator.open();
    }

    /**
     * Returns the TupleDesc with field names from the underlying HeapFile,
     * prefixed with the tableAlias string from the constructor. This prefix
     * becomes useful when joining tables containing a field(s) with the same
     * name.  The alias and name should be separated with a "." character
     * (e.g., "alias.fieldName").
     *
     * @return the TupleDesc with field names from the underlying HeapFile,
     *         prefixed with the tableAlias string from the constructor.
     */
    public TupleDesc getTupleDesc() {
        // some code goes here
    	// 没有检查this.dbFile是否不为null，即caller是否先调用了open()。
        TupleDesc tupleDesc = dbFile.getTupleDesc();
        Type[] typeAr = new Type[tupleDesc.numFields()];
        String[] fieldAr = new String[tupleDesc.numFields()];
        for (int i = 0; i < tupleDesc.numFields(); i++) {
        	typeAr[i] = tupleDesc.getFieldType(i);
			fieldAr[i] = tableAlias + "." + tupleDesc.getFieldName(i);
		}
		return new TupleDesc(typeAr, fieldAr);
    }

    public boolean hasNext() throws TransactionAbortedException, DbException {
        // some code goes here
    	if (dbFile==null || iterator==null)
    		throw new IllegalStateException();
        return iterator.hasNext();
    }

    public Tuple next() throws NoSuchElementException,
            TransactionAbortedException, DbException {
        // some code goes here
    	if (dbFile==null || iterator==null)
    		throw new IllegalStateException();
        return iterator.next();
    }

    public void close() {
        // some code goes here
    	iterator.close();
    }

    public void rewind() throws DbException, NoSuchElementException,
            TransactionAbortedException {
        // some code goes here
    	iterator.rewind();
    }
}

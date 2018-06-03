package com.example.ccojo.udacitybookstore.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.ccojo.udacitybookstore.data.BookStoreContract.BookEntry;

/**
 * Created by ccojo on 6/3/2018.
 */

public class BookDbHelper extends SQLiteOpenHelper {

    // Tag for log messages
    private static final String TAG = BookDbHelper.class.getName();

    // Name of database
    private static final String DATABASE_NAME = "bookstore";

    // Version of database, increment if schema is changed
    private static final int DATABASE_VERSION = 1;

    // Constants for SQL
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ", ";
    private static final String PRIMARY_KEY = " PRIMARY KEY";
    private static final String AUTOINCREMENT = " AUTOINCREMENT";
    private static final String DEFAULT = " DEFAULT";
    private static final String NOT_NULL = " NOT NULL";

    // SQL drop table
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + BookEntry.TABLE_NAME;

    // SQL Create Books Table
    private static final String SQL_CREATE_BOOKS_TABLE =
            "CREATE TABLE " + BookEntry.TABLE_NAME + " (" +
                    BookEntry._ID + INTEGER_TYPE + PRIMARY_KEY + AUTOINCREMENT + COMMA_SEP +
                    BookEntry.COLUMN_PRODUCT_NAME + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                    BookEntry.COLUMN_AUTHOR + TEXT_TYPE + COMMA_SEP +
                    BookEntry.COLUMN_GENRE + INTEGER_TYPE + DEFAULT + " 0" + COMMA_SEP +
                    BookEntry.COLUMN_PRICE + INTEGER_TYPE + NOT_NULL + COMMA_SEP +
                    BookEntry.COLUMN_QUANTITY + INTEGER_TYPE + NOT_NULL + DEFAULT + " 0" + COMMA_SEP +
                    BookEntry.COLUMN_SUPPLIER_NAME + TEXT_TYPE + COMMA_SEP +
                    BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER + TEXT_TYPE + COMMA_SEP +
                    BookEntry.COLUMN_FORMAT + INTEGER_TYPE + DEFAULT + " 0" + COMMA_SEP +
                    BookEntry.COLUMN_LANGUAGE + TEXT_TYPE + COMMA_SEP +
                    BookEntry.COLUMN_PRINT_TYPE + INTEGER_TYPE + NOT_NULL + DEFAULT + " 0" +
                    ");";

    // Constructor - cursor factory null
    public BookDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate: Create Table: " + SQL_CREATE_BOOKS_TABLE);
        db.execSQL(SQL_CREATE_BOOKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}

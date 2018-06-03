package com.example.ccojo.udacitybookstore.data;

import android.provider.BaseColumns;

/**
 * Created by ccojo on 6/3/2018.
 */

public class BookStoreContract {

    private BookStoreContract() {}

    public class BookEntry implements BaseColumns {

        // name of the table
        public static final String TABLE_NAME = "books";

        // columns including ID
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_PRODUCT_NAME = "Product_Name";
        public static final String COLUMN_AUTHOR = "Author";
        public static final String COLUMN_GENRE = "Genre";
        public static final String COLUMN_PRICE = "Price";
        public static final String COLUMN_QUANTITY = "Quantity";
        public static final String COLUMN_SUPPLIER_NAME = "Supplier_Name";
        public static final String COLUMN_SUPPLIER_PHONE_NUMBER = "Supplier_Phone_Number";
        public static final String COLUMN_FORMAT = "Format";
        public static final String COLUMN_PRINT_TYPE = "Print_Type";
        public static final String COLUMN_LANGUAGE = "Language";

        // constants used as values in column genre
        public static final int GENRE_UNKNOWN = 0;
        public static final int GENRE_OTHER = 1;
        public static final int GENRE_CRIME_MISTERY = 2;
        public static final int GENRE_HORROR = 3;
        public static final int GENRE_FANTASY = 4;
        public static final int GENRE_CHILDREN = 5;
        public static final int GENRE_ADULT = 6;
        public static final int GENRE_LITERARY_FICTION = 7;
        public static final int GENRE_HISTORICAL_FICTION = 8;
        public static final int GENRE_SCIENCE_FICTION = 9;
        public static final int GENRE_HISTORY = 10;

        // constants used as values in column format
        public static final int FORMAT_UNKNOWN = 0;
        public static final int FORMAT_PAPERBACK = 1;
        public static final int FORMAT_HARDCOVER = 2;
        public static final int FORMAT_EBOOK = 3;

        // constants used as values in column print type
        public static final int PRINT_UNKNOWN = 0;
        public static final int PRINT_BLACK_AND_WHITE = 1;
        public static final int PRINT_COLOR = 2;
    }
}

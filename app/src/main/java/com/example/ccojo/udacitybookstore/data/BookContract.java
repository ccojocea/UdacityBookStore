package com.example.ccojo.udacitybookstore.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by ccojo on 6/3/2018.
 */

public final class BookContract {

    public static final String CONTENT_AUTHORITY = "com.example.ccojo.udacitybookstore";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_BOOKS = "books";

    // Prevent internal instantiation by throwing an AssertionError
    private BookContract() {
        throw new AssertionError("No instances for you!");
    }

    /**
     * Inner class that defines constant values for the books database table.
     * Each entry in the table represents a single book.
     */
    public static final class BookEntry implements BaseColumns {

        /** The MIME type of the {@link #CONTENT_URI} for a list of books */
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        /** The MIME type of the {@link #CONTENT_URI} for a single book */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        /** The content URI to access the book data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);

        /** Name of database table for books */
        public static final String TABLE_NAME = "books";

        /** columns including ID */
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

        public static boolean isValidGenre(int genre) {
            return genre == GENRE_UNKNOWN || genre == GENRE_OTHER || genre == GENRE_CRIME_MISTERY ||
                   genre == GENRE_HORROR || genre == GENRE_FANTASY || genre == GENRE_CHILDREN  ||
                   genre == GENRE_ADULT || genre == GENRE_LITERARY_FICTION || genre == GENRE_HISTORICAL_FICTION ||
                   genre == GENRE_SCIENCE_FICTION || genre == GENRE_HISTORY;
        }

        // constants used as values in column format
        public static final int FORMAT_UNKNOWN = 0;
        public static final int FORMAT_PAPERBACK = 1;
        public static final int FORMAT_HARDCOVER = 2;
        public static final int FORMAT_EBOOK = 3;

        public static boolean isValidFormat(int format) {
            return format == FORMAT_UNKNOWN || format == FORMAT_PAPERBACK || format == FORMAT_HARDCOVER || format == FORMAT_EBOOK;
        }

        // constants used as values in column print type
        public static final int PRINT_UNKNOWN = 0;
        public static final int PRINT_BLACK_AND_WHITE = 1;
        public static final int PRINT_COLOR = 2;

        public static boolean isValidPrint(int print) {
            return print == PRINT_UNKNOWN || print == PRINT_BLACK_AND_WHITE || print == PRINT_COLOR;
        }
    }
}

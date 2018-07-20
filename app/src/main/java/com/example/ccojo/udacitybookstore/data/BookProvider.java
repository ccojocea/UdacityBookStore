package com.example.ccojo.udacitybookstore.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.ccojo.udacitybookstore.R;
import com.example.ccojo.udacitybookstore.data.BookContract.BookEntry;

public class BookProvider extends ContentProvider {

    /**
     * Constant for LOG messages
     */
    private static final String LOG_TAG = BookProvider.class.getSimpleName();
    /**
     * URI matcher code for the content URI for the books table
     */
    private static final int BOOKS = 100;
    /**
     * URI matcher code for the content URI for a single book in the books table
     */
    private static final int BOOK_ID = 101;
    /**
     * UriMatcher object to match a content URI to a corresponding code.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    /** Static initialiser */
    static {
        /** Sets the integer value for multiple rows in the books table to 100. No wildcard is used in the match */
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS, BOOKS);

        /** Sets the integer value for a single row in the books table to 101. The "#" wildcard is used */
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS + "/#", BOOK_ID);
    }

    /**
     * BookDbHelper for access to the books database
     */
    private BookDbHelper mDbHelper;

    private static void validateData(ContentValues values) {

        if (values.containsKey(BookEntry.COLUMN_PRODUCT_NAME)) {
            // Check that the name is not null
            String name = values.getAsString(BookEntry.COLUMN_PRODUCT_NAME);
            if (name == null || name.equals("")) {
                throw new IllegalArgumentException(Resources.getSystem().getString(R.string.book_name_missing));
            }
        }

        if (values.containsKey(BookEntry.COLUMN_FORMAT)) {
            // Check that the format is valid
            Integer format = values.getAsInteger(BookEntry.COLUMN_FORMAT);
            if (format == null || !BookEntry.isValidFormat(format)) {
                throw new IllegalArgumentException(Resources.getSystem().getString(R.string.book_format_invalid));
            }
        }

        if (values.containsKey(BookEntry.COLUMN_PRINT_TYPE)) {
            // Check that the print type is valid
            Integer print = values.getAsInteger(BookEntry.COLUMN_PRINT_TYPE);
            if (print == null || !BookEntry.isValidPrint(print)) {
                throw new IllegalArgumentException(Resources.getSystem().getString(R.string.invalid_book_print));
            }
        }

        if (values.containsKey(BookEntry.COLUMN_GENRE)) {
            // Check that the genre is valid
            Integer genre = values.getAsInteger(BookEntry.COLUMN_GENRE);
            if (genre == null || !BookEntry.isValidGenre(genre)) {
                throw new IllegalArgumentException(Resources.getSystem().getString(R.string.invalid_book_genre));
            }
        }

        if (values.containsKey(BookEntry.COLUMN_PRICE)) {
            // Check that the price is provided, and check that it's greater than or equal to 0
            Integer price = values.getAsInteger(BookEntry.COLUMN_PRICE);
            if (price == null || price < 0) {
                throw new IllegalArgumentException(Resources.getSystem().getString(R.string.book_price_positive));
            }
        }

        if (values.containsKey(BookEntry.COLUMN_QUANTITY)) {
            // Check that if the quantity is provided, it's greater than or equal to 0
            Integer quantity = values.getAsInteger(BookEntry.COLUMN_QUANTITY);
            if (quantity != null && quantity < 0) {
                throw new IllegalArgumentException(Resources.getSystem().getString(R.string.book_quantity_positive));
            }
        }

        // No need to check the author, any value is valid (including null).
        // No need to check the language, any value is valid (including null).
        // No need to check the supplier name, any value is valid (including null).
        // No need to check the supplier phone, any value is valid (including null).
    }

    /**
     * Initialise the provider and the database helper object
     */
    @Override
    public boolean onCreate() {
        mDbHelper = new BookDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // Cursor to hold the result of the query
        Cursor cursor;

        // Use the URI Matcher to match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                cursor = database.query(BookEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case BOOK_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(BookEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new IllegalArgumentException(getContext().getString(R.string.query_unknown_URI) + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        // Use the URI Matcher to match the URI to a specific code
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return insertBook(uri, values);
            default:
                throw new IllegalArgumentException(getContext().getString(R.string.insertion_not_supported) + uri);
        }
    }

    private Uri insertBook(Uri uri, ContentValues values) {

        // Validate values provided. Will throw an exception if error is found
        validateData(values);

        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert new Book with the given values
        long id = database.insert(BookEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, getContext().getString(R.string.failed_to_insert_row) + uri);
            return null;
        }

        // Return the new URI with the ID appended at the end and notify for the change
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues
     */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int rowsUpdated;

        // Use the URI Matcher to match the URI to a specific code
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                rowsUpdated = updateBook(uri, values, selection, selectionArgs);
                break;
            case BOOK_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                rowsUpdated = updateBook(uri, values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException(getContext().getString(R.string.update_not_supported) + uri);
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    private int updateBook(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Validate values provided. Will throw an exception if error is found
        validateData(values);

        // Get the data repository in write mode
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(BookEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, notify all listeners that the data at the given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        // Get writable database;
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;

        // Use the URI Matcher to match the URI to a specific code
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                // Delete all the rows that match the selection and the selection args
                rowsDeleted = database.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BOOK_ID:
                // Delete a single row given by the ID in the URI
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException(getContext().getString(R.string.deletion_not_supported) + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return rowsDeleted;
    }

    /**
     * Returns the MIME type of the data for the content URI
     */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        // Use the URI Matcher to match the URI to a specific code
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case BOOKS:
                return BookEntry.CONTENT_LIST_TYPE;
            case BOOK_ID:
                return BookEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}

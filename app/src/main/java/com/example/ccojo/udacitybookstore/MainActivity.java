package com.example.ccojo.udacitybookstore;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.ccojo.udacitybookstore.data.BookDbHelper;
import com.example.ccojo.udacitybookstore.data.BookStoreContract.BookEntry;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    // Tag for log messages
    private static final String TAG = MainActivity.class.getName();

    // Views
    private TextView displayTextView;
    private Button insertButton;
    private Button deleteButton;
    private ScrollView scrollView;

    // Database SQLiteOpenHelper
    private BookDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the database helper object
        mDbHelper = new BookDbHelper(this);

        displayTextView = findViewById(R.id.displayTextView);
        insertButton = findViewById(R.id.button);
        deleteButton = findViewById(R.id.button2);
        scrollView = findViewById(R.id.scrollView);

        // set the button to add some data in the database
        insertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertData();
            }
        });

        // set the button to delete data from the database
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteData();
            }
        });

        displayDatabaseInfo(queryData());
    }

    /**
     * Insert into database.
     */
    private void insertData() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Generate random number between 1 and 10
        Random rand = new Random();
        int max = 6;
        int min = 1;
        int random = rand.nextInt((max - min) + 1) + min;

        String productName;
        String authorName = "Unknown";
        int genre = BookEntry.GENRE_UNKNOWN;
        int price = 0;
        int quantity = 0;
        String supplierName = "";
        String supplierPhone = "";
        int printType = BookEntry.PRINT_UNKNOWN;
        String language = "English";
        int format = BookEntry.FORMAT_UNKNOWN;

        switch (random) {
            case 1:
                productName = "A Tale of Two Cities";
                authorName = "Charles Dickens";
                genre = BookEntry.GENRE_HISTORICAL_FICTION;
                price = 999;
                quantity = 15;
                supplierName = "Amazon";
                supplierPhone = "123123891273";
                printType = BookEntry.PRINT_COLOR;
                format = BookEntry.FORMAT_HARDCOVER;
                break;
            case 2:
                productName = "Harry Potter";
                authorName = "J.K. Rowlins";
                genre = BookEntry.GENRE_CHILDREN;
                price = 1999;
                quantity = 20;
                supplierName = "Amazon";
                supplierPhone = "123123891273";
                printType = BookEntry.PRINT_COLOR;
                format = BookEntry.FORMAT_EBOOK;
                break;
            case 3:
                productName = "The Lord of the Rings";
                authorName = "J.R.R. Tolkien";
                genre = BookEntry.GENRE_FANTASY;
                price = 1950;
                quantity = 30;
                supplierName = "Powell’s Books";
                supplierPhone = "123123891273";
                printType = BookEntry.PRINT_COLOR;
                format = BookEntry.FORMAT_PAPERBACK;
                break;
            case 4:
                productName = "And Then There Were None";
                authorName = "Agatha Christie";
                genre = BookEntry.GENRE_CRIME_MISTERY;
                price = 1299;
                quantity = 40;
                supplierName = "Books-A-Million";
                supplierPhone = "12312391273";
                printType = BookEntry.PRINT_BLACK_AND_WHITE;
                format = BookEntry.FORMAT_HARDCOVER;
                break;
            case 5:
                productName = "The Catcher in the Rye";
                authorName = "J. D. Salinger";
                genre = BookEntry.GENRE_OTHER;
                price = 2550;
                quantity = 50;
                supplierName = "Alibris";
                supplierPhone = "123123891273";
                printType = BookEntry.PRINT_COLOR;
                format = BookEntry.FORMAT_PAPERBACK;
                break;
            default:
                productName = "Random Book";
                supplierName = "Unknown";
                supplierPhone = "Unknown";
        }

        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_PRODUCT_NAME, productName);
        values.put(BookEntry.COLUMN_AUTHOR, authorName);
        values.put(BookEntry.COLUMN_GENRE, genre);
        values.put(BookEntry.COLUMN_PRICE, price);
        values.put(BookEntry.COLUMN_QUANTITY, quantity);
        values.put(BookEntry.COLUMN_SUPPLIER_NAME, supplierName);
        values.put(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER, supplierPhone);
        values.put(BookEntry.COLUMN_PRINT_TYPE, printType);
        values.put(BookEntry.COLUMN_LANGUAGE, language);
        values.put(BookEntry.COLUMN_FORMAT, format);

        db.insert(BookEntry.TABLE_NAME, null, values);

        displayDatabaseInfo(queryData());
    }

    /**
     * Query the database.
     */
    private Cursor queryData() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // array of columns to be returned
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_PRODUCT_NAME,
                BookEntry.COLUMN_AUTHOR,
                BookEntry.COLUMN_GENRE,
                BookEntry.COLUMN_PRICE,
                BookEntry.COLUMN_QUANTITY,
                BookEntry.COLUMN_SUPPLIER_NAME,
                BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER,
                BookEntry.COLUMN_PRINT_TYPE,
                BookEntry.COLUMN_LANGUAGE,
                BookEntry.COLUMN_FORMAT
        };

        return db.query(BookEntry.TABLE_NAME, projection, null, null, null, null, null, null);
    }

    /**
     * Delete all data from the database
     */
    private void deleteData() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long result = db.delete(BookEntry.TABLE_NAME, null, null);
        Log.d(TAG, "deleteData: " + result);

        displayDatabaseInfo(queryData());
    }

    /**
     * Display database information in the available TextView:
     * Row count
     * Each record
     * @param c
     */
    public void displayDatabaseInfo(Cursor c) {
        if (c == null) {
            return;
        }

        try {
            displayTextView.setText("Number of books in the database table: " + c.getCount());

            int idColumnIndex = c.getColumnIndex(BookEntry._ID);
            int nameColumnIndex = c.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME);
            int authorColumnIndex = c.getColumnIndex(BookEntry.COLUMN_AUTHOR);
            int genreColumnIndex = c.getColumnIndex(BookEntry.COLUMN_GENRE);
            int priceColumnIndex = c.getColumnIndex(BookEntry.COLUMN_PRICE);
            int quantityColumnIndex = c.getColumnIndex(BookEntry.COLUMN_QUANTITY);
            int supplierNameColumnIndex = c.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = c.getColumnIndex(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
            int printTypeColumnIndex = c.getColumnIndex(BookEntry.COLUMN_PRINT_TYPE);
            int languageColumnIndex = c.getColumnIndex(BookEntry.COLUMN_LANGUAGE);
            int formatColumnIndex = c.getColumnIndex(BookEntry.COLUMN_FORMAT);

            if (c.getCount() > 0) {
                displayTextView.append("\n\n");
                displayTextView.append(
                        BookEntry._ID + " - " +
                        BookEntry.COLUMN_PRODUCT_NAME + " - " +
                        BookEntry.COLUMN_AUTHOR + " - " +
                        BookEntry.COLUMN_GENRE + " - " +
                        BookEntry.COLUMN_PRICE + " - " +
                        BookEntry.COLUMN_QUANTITY + " - " +
                        BookEntry.COLUMN_SUPPLIER_NAME + " - " +
                        BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER
                );
            }

            while (c.moveToNext()) {
                int id = c.getInt(idColumnIndex);
                String name = c.getString(nameColumnIndex);
                String author = c.getString(authorColumnIndex);
                int genre = c.getInt(genreColumnIndex);
                long price = c.getInt(priceColumnIndex) / 100;
                int quantity = c.getInt(quantityColumnIndex);
                String supplierName = c.getString(supplierNameColumnIndex);
                String supplierPhone = c.getString(supplierPhoneColumnIndex);

                displayTextView.append("\n" + id + " - " + name + " - " + author + " - " + genre + " - " + price + " - " + quantity + " - " + supplierName + " - " + supplierPhone);
            }
        } finally {
            // Close the cursor when done reading from it to release its resources
            c.close();
        }

        scrollView.fullScroll(View.FOCUS_DOWN);
    }
}

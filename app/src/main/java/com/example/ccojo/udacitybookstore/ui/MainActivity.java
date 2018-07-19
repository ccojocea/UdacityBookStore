package com.example.ccojo.udacitybookstore.ui;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.ccojo.udacitybookstore.adapters.BookCursorAdapter;
import com.example.ccojo.udacitybookstore.R;
import com.example.ccojo.udacitybookstore.data.BookContract.BookEntry;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    // Tag for log messages
    private static final String LOG_TAG = MainActivity.class.getName();

    // Views
    private ListView bookListView;
    private View emptyView;

    // Adapter used to display the list's data
    BookCursorAdapter mCursorAdapter;

    // Constant for the cursor loader
    private static final int BOOK_LOADER = 0;

    // Book rows to be retrieved
    static final String[] PROJECTION = {
        BookEntry._ID,
        BookEntry.COLUMN_PRODUCT_NAME,
        BookEntry.COLUMN_AUTHOR,
        BookEntry.COLUMN_PRICE,
        BookEntry.COLUMN_QUANTITY
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialise views
        bookListView = findViewById(R.id.list_view);
        emptyView = findViewById(R.id.empty_view);

        // Set the empty view on the listview so it only shows when the list has 0 items
        bookListView.setEmptyView(emptyView);

        // Setup the item click listener so each item from the listview opens in the Editor Activity
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Content URI which represents the specific book that was clicked on
                Uri uri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);

                // Create the new intent to go to the Editor Activity
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);

                // Set the Uri on the data field of the intent
                intent.setData(uri);

                // Launch the editor activity
                startActivity(intent);
            }
        });

        // Setup the floating action button to open the editor activity in add mode
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // Create an empty adapter used to display the loaded data
        // This will be updated in onLoadFinished()
        mCursorAdapter = new BookCursorAdapter(this, null);

        // Prepare the loader. Reconnect with an existing one or start a new one
        getLoaderManager().initLoader(BOOK_LOADER, null, this);

        // Attach the cursor adapter to the list view
        bookListView.setAdapter(mCursorAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_main.xml file
        // This adds the gears icon to the app bar
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_setup) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    // Called when a new Loader needs to be created
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Create and return a cursor loader that will take care of creating a cursor for the data being displayed.
        return new CursorLoader(this, BookEntry.CONTENT_URI, PROJECTION, null, null, null);
    }

    // Called when a previously created loader has finished loading
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Swap the new cursor in (Old cursor is closed by the framework)
        mCursorAdapter.swapCursor(data);
    }

    // Called when a previously created loader is reset, making the data unavailable
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last cursor provided to onLoadFinished() is about to be closed
        mCursorAdapter.swapCursor(null);
    }
}

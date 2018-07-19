package com.example.ccojo.udacitybookstore.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.ccojo.udacitybookstore.R;

import static com.example.ccojo.udacitybookstore.data.BookContract.*;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    // Tag for log messages
    private static final String TAG = EditorActivity.class.getName();

    // Constant for phone call permission
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;

    // Constant for the cursor loader
    private static final int BOOK_LOADER = 0;

    // Uri
    private Uri mBookUri;

    // Boolean used after user tries to save an item
    private boolean hasPressedSave = false;

    // OnTouchListener - if the user touches a view, implying that they modified the view, change the
    // boolean to true
    private boolean mBookHasChanged = false;
    private final View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mBookHasChanged = true;
            // Return false (Android will pass the event down to other views, which could be under this view)
            // Return true = The press is taken care of, tell Android to forget it.
            return false;
        }
    };

    // OnTouchListener for spinners, to close the Text Input
    private final View.OnTouchListener mTouchListenerSpinners = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mBookHasChanged = true;

            // Hide the keyboard if a spinner is touched
            hideKeyboard(EditorActivity.this);

            return false;
        }
    };

    // Book rows to be retrieved
    private static final String[] PROJECTION = {
            BookEntry._ID,
            BookEntry.COLUMN_PRODUCT_NAME,
            BookEntry.COLUMN_AUTHOR,
            BookEntry.COLUMN_PRICE,
            BookEntry.COLUMN_QUANTITY,
            BookEntry.COLUMN_PRINT_TYPE,
            BookEntry.COLUMN_FORMAT,
            BookEntry.COLUMN_GENRE,
            BookEntry.COLUMN_SUPPLIER_NAME,
            BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER,
            BookEntry.COLUMN_LANGUAGE
    };

    // Views
    private EditText mNameEditText;
    private EditText mAuthorEditText;
    private EditText mPriceEditText;
    private EditText mQuantityEditText;
    private EditText mSupplierNameEditText;
    private EditText mSupplierPhoneEditText;
    private EditText mLanguageEditText;
    private Spinner mPrintTypeSpinner;
    private Spinner mFormatSpinner;
    private Spinner mGenreSpinner;
    private Button mPlus;
    private Button mMinus;
    private Button mOrderButton;

    // Genre, Print and Format
    private int mGenre = 0;
    private int mPrint = 0;
    private int mFormat = 0;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Find all the relevant views that we need to read user input from
        mNameEditText = findViewById(R.id.edit_book_name);
        mAuthorEditText = findViewById(R.id.edit_book_author);
        mPriceEditText = findViewById(R.id.edit_book_price);
        mQuantityEditText = findViewById(R.id.edit_book_quantity);
        mSupplierNameEditText = findViewById(R.id.edit_supplier_name);
        mSupplierPhoneEditText = findViewById(R.id.edit_supplier_phone);
        mLanguageEditText = findViewById(R.id.edit_book_language);
        mPrintTypeSpinner = findViewById(R.id.spinner_print);
        mFormatSpinner = findViewById(R.id.spinner_format);
        mGenreSpinner = findViewById(R.id.spinner_genre);
        mPlus = findViewById(R.id.plus_button);
        mMinus = findViewById(R.id.minus_button);
        mOrderButton = findViewById(R.id.order_book_button);

        // Set the onTouchListener on all views
        mNameEditText.setOnTouchListener(mTouchListener);
        mAuthorEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierNameEditText.setOnTouchListener(mTouchListener);
        mSupplierPhoneEditText.setOnTouchListener(mTouchListener);
        mLanguageEditText.setOnTouchListener(mTouchListener);
        mPrintTypeSpinner.setOnTouchListener(mTouchListenerSpinners);
        mFormatSpinner.setOnTouchListener(mTouchListenerSpinners);
        mGenreSpinner.setOnTouchListener(mTouchListenerSpinners);
        mPlus.setOnTouchListener(mTouchListener);
        mMinus.setOnTouchListener(mTouchListener);

        // Setup the 3 dropdown spinners that allow the user to select genre, print and format for the book
        setupSpinners();

        // Get intent and uri from data attached to the intent
        mBookUri = getIntent().getData();

        if (mBookUri != null) {
            setTitle(getString(R.string.editor_activity_title_edit_book));

            // Prepare the loader. Either reconnect with one or start a new one
            getLoaderManager().initLoader(BOOK_LOADER, null, this);
        } else {
            // Invalidate the options menu to hide the delete menu option
            invalidateOptionsMenu();

            setTitle(R.string.editor_activity_title_add_book);
        }

        mPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increaseQuantity();
            }
        });

        mMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decreaseQuantity();
            }
        });

        mOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSupplierPhoneEditText.getText() != null && !mSupplierPhoneEditText.getText().toString().equals("")) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(getString(R.string.tel_uri_string) + mSupplierPhoneEditText.getText().toString()));

                    if (checkForPhonePermission()) {
                        startActivity(intent);
                    } else {
                        Toast.makeText(EditorActivity.this, R.string.call_permission_wait, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    // Setup the 3 dropdown spinners that allow the user to select genre, print and format for the book
    private void setupSpinners() {
        // Create adapters for the spinners
        ArrayAdapter genreSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_genre_options, android.R.layout.simple_spinner_item);
        ArrayAdapter formatSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_format_options, android.R.layout.simple_spinner_item);
        ArrayAdapter printSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_print_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genreSpinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        formatSpinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        printSpinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        // Apply the adapters to the spinners
        mGenreSpinner.setAdapter(genreSpinnerAdapter);
        mFormatSpinner.setAdapter(formatSpinnerAdapter);
        mPrintTypeSpinner.setAdapter(printSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mGenreSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.genre_unknown))) {
                        mGenre = BookEntry.GENRE_UNKNOWN;
                    } else if (selection.equals(getString(R.string.genre_other))) {
                        mGenre = BookEntry.GENRE_OTHER;
                    } else if (selection.equals(getString(R.string.genre_crime_mistery))) {
                        mGenre = BookEntry.GENRE_CRIME_MISTERY;
                    } else if (selection.equals(getString(R.string.genre_horror))) {
                        mGenre = BookEntry.GENRE_HORROR;
                    } else if (selection.equals(getString(R.string.genre_fantasy))) {
                        mGenre = BookEntry.GENRE_FANTASY;
                    } else if (selection.equals(getString(R.string.genre_children))) {
                        mGenre = BookEntry.GENRE_CHILDREN;
                    } else if (selection.equals(getString(R.string.genre_adult))) {
                        mGenre = BookEntry.GENRE_ADULT;
                    } else if (selection.equals(getString(R.string.genre_literary_fiction))) {
                        mGenre = BookEntry.GENRE_LITERARY_FICTION;
                    } else if (selection.equals(getString(R.string.genre_historical_fiction))) {
                        mGenre = BookEntry.GENRE_HISTORICAL_FICTION;
                    } else if (selection.equals(getString(R.string.genre_science_fiction))){
                        mGenre = BookEntry.GENRE_SCIENCE_FICTION;
                    } else {
                        mGenre = BookEntry.GENRE_HISTORY;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGenre = 0;
            }
        });
        mFormatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.format_unknown))) {
                        mFormat = BookEntry.FORMAT_UNKNOWN;
                    } else if (selection.equals(getString(R.string.format_paperback))) {
                        mFormat = BookEntry.FORMAT_PAPERBACK;
                    } else if (selection.equals(getString(R.string.format_hardcover))) {
                        mFormat = BookEntry.FORMAT_HARDCOVER;
                    } else {
                        mFormat = BookEntry.FORMAT_EBOOK;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mFormat = 0;
            }
        });
        mPrintTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.print_unknown))) {
                        mPrint = BookEntry.PRINT_UNKNOWN;
                    } else if (selection.equals(getString(R.string.print_black_and_white))) {
                        mPrint = BookEntry.PRINT_BLACK_AND_WHITE;
                    } else {
                        mPrint = BookEntry.PRINT_COLOR;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mPrint = 0;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                //Save book to the database
                try {
                    // disable the save button so the user doesn't press it multiple times
                    hasPressedSave = true;
                    invalidateOptionsMenu();

                    // try to save the book
                    saveBook();
                } catch (IllegalArgumentException e) {
                    // If an error is caught, make sure the button is enabled again
                    hasPressedSave = false;
                    invalidateOptionsMenu();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    return false;
                }

                // close the activity
                finish();

                return true;

            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Show confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                // If the book hasn't changed, continue with navigation
                if (!mBookHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Hide or show the delete option in editor activity as needed (Add or Edit book)(called after invalidateOptionsMenu())
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        // If this is a new Book, hide the "Delete" menu item.
        if (mBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }

        // If user pressed save, disable the save button
        if (hasPressedSave) {
            MenuItem menuItem = menu.findItem(R.id.action_save);
            menuItem.setEnabled(false);
        }

        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Create and return a cursor loader that will take care of creating a cursor for the data being displayed.
        return new CursorLoader(this, mBookUri, PROJECTION, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()) {
            String name = data.getString(data.getColumnIndexOrThrow(BookEntry.COLUMN_PRODUCT_NAME));
            mNameEditText.setText(name);

            String author = data.getString(data.getColumnIndexOrThrow(BookEntry.COLUMN_AUTHOR));
            mAuthorEditText.setText(author);

            int genre = data.getInt(data.getColumnIndexOrThrow(BookEntry.COLUMN_GENRE));
            mGenreSpinner.setSelection(genre);

            int format = data.getInt(data.getColumnIndexOrThrow(BookEntry.COLUMN_FORMAT));
            mFormatSpinner.setSelection(format);

            int print = data.getInt(data.getColumnIndexOrThrow(BookEntry.COLUMN_PRINT_TYPE));
            mPrintTypeSpinner.setSelection(print);

            String supplier = data.getString(data.getColumnIndexOrThrow(BookEntry.COLUMN_SUPPLIER_NAME));
            mSupplierNameEditText.setText(supplier);

            String supplierPhone = data.getString(data.getColumnIndexOrThrow(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER));
            mSupplierPhoneEditText.setText(supplierPhone);

            int price = data.getInt(data.getColumnIndexOrThrow(BookEntry.COLUMN_PRICE));
            mPriceEditText.setText(String.valueOf(price));

            int quantity = data.getInt(data.getColumnIndexOrThrow(BookEntry.COLUMN_QUANTITY));
            mQuantityEditText.setText(String.valueOf(quantity));

            String language = data.getString(data.getColumnIndexOrThrow(BookEntry.COLUMN_LANGUAGE));
            mLanguageEditText.setText(language);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mAuthorEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mSupplierNameEditText.setText("");
        mSupplierPhoneEditText.setText("");
        mLanguageEditText.setText("");
        mPrintTypeSpinner.setSelection(BookEntry.PRINT_UNKNOWN);
        mFormatSpinner.setSelection(BookEntry.FORMAT_UNKNOWN);
        mGenreSpinner.setSelection(BookEntry.GENRE_UNKNOWN);
    }

    @Override
    public void onBackPressed() {
        // If the book wasn't changed, continue with handling back button press
        if (!mBookHasChanged) {
            super.onBackPressed();
            NavUtils.navigateUpFromSameTask(this);
            return;
        }

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog();
    }

    private void showUnsavedChangesDialog () {
        // Create an AlertDialog.Builder and set the message and click listeners for positive/negative buttons
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_message);
        builder.setPositiveButton(R.string.dialog_discard, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked "Discard", close the current activity
                finish();
            }
        });
        builder.setNegativeButton(R.string.dialog_keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message and click listeners for the positive and negative buttons on the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.editor_activity_delete_book);
        builder.setPositiveButton(R.string.editor_activity_delete_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked the "Delete" button, delete the book
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.editor_activity_cancel_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked the "cancel" button so dismiss the dialog and continue editing the book
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the Alert Dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // Get user input from the editor and save new book into database (or update if in edit mode)
    private void saveBook() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        // Create a content values object where column names are the keys and book attributes are the values
        ContentValues values = new ContentValues();
        String name = mNameEditText.getText().toString().trim();
        values.put(BookEntry.COLUMN_PRODUCT_NAME, name);
        values.put(BookEntry.COLUMN_AUTHOR, mAuthorEditText.getText().toString().trim());
        values.put(BookEntry.COLUMN_PRICE, mPriceEditText.getText().toString().trim());
        values.put(BookEntry.COLUMN_QUANTITY, mQuantityEditText.getText().toString().trim());
        values.put(BookEntry.COLUMN_SUPPLIER_NAME, mSupplierNameEditText.getText().toString().trim());
        values.put(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER, mSupplierPhoneEditText.getText().toString().trim());
        values.put(BookEntry.COLUMN_LANGUAGE, mLanguageEditText.getText().toString().trim());
        values.put(BookEntry.COLUMN_GENRE, mGenre);
        values.put(BookEntry.COLUMN_FORMAT, mFormat);
        values.put(BookEntry.COLUMN_PRINT_TYPE, mPrint);

        // Check if in edit or add mode
        if (mBookUri != null) {
            // In edit mode

            // Update the book using the mBookUri (no selection / selectionArgs necessary)
            int mRowsUpdated = getContentResolver().update(mBookUri, values, null, null);

            // Show a toast message based on result
            if (mRowsUpdated != 0) {
                // The update was successful and we can display a toast
                Toast.makeText(this, R.string.editor_activitity_book_update, Toast.LENGTH_SHORT).show();
            } else {
                // If no rows were affected, then there was an error with the update
                Toast.makeText(this, R.string.editor_activity_error_update, Toast.LENGTH_SHORT).show();
            }
        } else {
            // In add new mode

            // Insert a new book using the content values and content uri
            Uri resultUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful
            if (resultUri != null) {
                // The insertion was successful
                Toast.makeText(this, R.string.editor_activity_insert_success + name, Toast.LENGTH_SHORT).show();
            } else {
                // The insertion failed
                Toast.makeText(this, R.string.editor_activity_insert_error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    // delete the selected book
    private void deleteBook() {
        // Perform the delete if this is an existing pet
        if (mBookUri != null) {
            // Call the content resolver to delete the pet at the given URI
            // Pass in null for selection and args because mBookUri already identifies the book
            int mRowsDeleted = getContentResolver().delete(mBookUri, null, null);
            if (mRowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, R.string.editor_delete_book_failed, Toast.LENGTH_SHORT).show();
            } else {
                // The delete was successful
                Toast.makeText(this, R.string.editor_delete_book_success, Toast.LENGTH_SHORT).show();
            }

            // Close the activity
            finish();
        }
    }

    // Helper method to hide the keyboard
    private static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        // Find the currently focused view, so we can grab the correct window token from it
        View view = activity.getCurrentFocus();
        // If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }

        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void decreaseQuantity() {
        if (mQuantityEditText.getText() != null && !mQuantityEditText.getText().toString().equals("")) {
            int currentQuantity = Integer.valueOf(mQuantityEditText.getText().toString());
            if (currentQuantity > 0) {
                currentQuantity--;
            } else {
                Toast.makeText(this, R.string.decrease_quantity_error, Toast.LENGTH_SHORT).show();
            }
            mQuantityEditText.setText(String.valueOf(currentQuantity));
        } else {
            mQuantityEditText.setText("0");
        }
    }

    private void increaseQuantity() {
        if (mQuantityEditText.getText() != null && !mQuantityEditText.getText().toString().equals("")) {
            int currentQuantity = Integer.valueOf(mQuantityEditText.getText().toString());
            currentQuantity++;
            mQuantityEditText.setText(String.valueOf(currentQuantity));
        } else {
            mQuantityEditText.setText("1");
        }
    }

    // check for call permissions
    private boolean checkForPhonePermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST_CALL_PHONE);
            return false;
        } else {
            return true;
        }
    }

    // if phone permissions are granted after a button press, place the call after the permission check
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(getString(R.string.tel_uri_string) + mSupplierPhoneEditText.getText().toString()));
            startActivity(intent);
        }
    }
}

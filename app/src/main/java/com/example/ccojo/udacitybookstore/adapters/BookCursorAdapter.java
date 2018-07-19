package com.example.ccojo.udacitybookstore.adapters;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ccojo.udacitybookstore.R;
import com.example.ccojo.udacitybookstore.data.BookContract.BookEntry;
import com.example.ccojo.udacitybookstore.ui.MainActivity;

public class BookCursorAdapter extends CursorAdapter{

    private static final String TAG = BookCursorAdapter.class.getSimpleName();

    public BookCursorAdapter (Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    // The newView method is used to inflate a new view and return it
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.book_list_item, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        // Find views to populate in inflated template
        TextView tvBookName = view.findViewById(R.id.tv_book_name);
        TextView tvBookAuthor = view.findViewById(R.id.tv_book_author);
        TextView tvBookQuantity = view.findViewById(R.id.tv_book_quantity);
        TextView tvBookPrice = view.findViewById(R.id.tv_book_price);

        // Extract properties from the cursor
        String name = cursor.getString(cursor.getColumnIndexOrThrow(BookEntry.COLUMN_PRODUCT_NAME));
        String author = cursor.getString(cursor.getColumnIndexOrThrow(BookEntry.COLUMN_AUTHOR));
        final int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(BookEntry.COLUMN_QUANTITY));
        int price = cursor.getInt(cursor.getColumnIndexOrThrow(BookEntry.COLUMN_PRICE));

        final int id = cursor.getInt(cursor.getColumnIndexOrThrow(BookEntry._ID));

        if (TextUtils.isEmpty(author)) {
            author = context.getString(R.string.unknown_author);
        }

        // Add listener on button to decrease quantity
        Button saleButton = view.findViewById(R.id.button_sale);
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create uri for current book
                Uri mBookUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);

                // Don't try to update if the quantity would be reduced under 0
                if (quantity == 0) {
                    Toast.makeText(context, R.string.cursor_adapter_negative_quantity_warning, Toast.LENGTH_SHORT).show();
                    return;
                }

                // Put the updated quantity inside a ContentValues object
                ContentValues values = new ContentValues();
                values.put(BookEntry.COLUMN_QUANTITY, quantity - 1);

                // Update the item
                int mRowsUpdated = context.getContentResolver().update(mBookUri, values, null, null);

                // Check if the update was successful and display a Toast
                if (mRowsUpdated != 0) {
                    Toast.makeText(context, R.string.cursor_adapter_book_sold, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, R.string.cursor_adapter_error_selling_book, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set dark or light "theme"
        if (MainActivity.THEME.equals(context.getString(R.string.settings_default_theme_value_dark))) {
            tvBookName.setTextColor(context.getResources().getColor(R.color.colorWhite));
        } else {
            tvBookName.setTextColor(context.getResources().getColor(R.color.list_item_name_color));
        }

        // Populate fields with extracted properties
        tvBookName.setText(name);
        tvBookAuthor.setText(author);
        tvBookPrice.setText(String.valueOf(price));
        tvBookQuantity.setText(String.valueOf(quantity));
    }
}

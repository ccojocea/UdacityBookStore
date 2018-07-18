package com.example.ccojo.udacitybookstore;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.ccojo.udacitybookstore.data.BookContract;

public class BookCursorAdapter extends CursorAdapter{

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
    public void bindView(View view, Context context, Cursor cursor) {
        // Find views to populate in inflated template
        TextView tvBookName = view.findViewById(R.id.tv_book_name);
        TextView tvBookAuthor = view.findViewById(R.id.tv_book_author);
        TextView tvBookQuantity = view.findViewById(R.id.tv_book_quantity);
        TextView tvBookPrice = view.findViewById(R.id.tv_book_price);

        // Extract properties from the cursor
        String name = cursor.getString(cursor.getColumnIndexOrThrow(BookContract.BookEntry.COLUMN_PRODUCT_NAME));
        String author = cursor.getString(cursor.getColumnIndexOrThrow(BookContract.BookEntry.COLUMN_AUTHOR));
        int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(BookContract.BookEntry.COLUMN_QUANTITY));
        int price = cursor.getInt(cursor.getColumnIndexOrThrow(BookContract.BookEntry.COLUMN_PRICE));

        if (TextUtils.isEmpty(author)) {
            author = context.getString(R.string.unknown_author);
        }

        // Populate fields with extracted properties
        tvBookName.setText(name);
        tvBookAuthor.setText(author);
        tvBookPrice.setText(String.valueOf(price));
        tvBookQuantity.setText(String.valueOf(quantity));
    }
}

package com.example.ccojo.udacitybookstore.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.ccojo.udacitybookstore.R;
import com.example.ccojo.udacitybookstore.data.BookContract;

public class SettingsActivity extends AppCompatActivity {

    // Tag for log messages
    private static final String TAG = SettingsActivity.class.getSimpleName();

    // Views
    private Button mDeleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mDeleteButton = findViewById(R.id.button_erase_database);
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Disable the button
                mDeleteButton.setEnabled(false);

                // Show confirmation dialog
                showDeleteConfirmationDialog();
            }
        });
    }

    // Apply settings after pressing the back button as well
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        NavUtils.navigateUpFromSameTask(this);
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message and click listeners for the positive and negative buttons on the dialog
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.settings_activity_delete_everything);
        builder.setPositiveButton(R.string.settings_activity_delete_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // User clicked the "Delete" button, delete the books
                int rowsDeleted = getContentResolver().delete(BookContract.BookEntry.CONTENT_URI, null, null);
                if (rowsDeleted != 0) {
                    Toast.makeText(getApplicationContext(), R.string.main_activity_data_deleted_success, Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });
        builder.setNegativeButton(R.string.editor_activity_cancel_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Enable the button
                mDeleteButton.setEnabled(true);

                // User clicked the "cancel" button so dismiss the dialog
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the Alert Dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public static class BooksPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener, SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            sharedPreferences.registerOnSharedPreferenceChangeListener(this);

            // show summary under preference label
            Preference itemsPerPage = findPreference(getString(R.string.settings_default_theme_key));
            bindPreferenceSummaryToValue(itemsPerPage);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String stringValue = newValue.toString();

            if (preference.getKey().equals(getString(R.string.settings_default_theme_key))) {
                if (stringValue.equals(getString(R.string.settings_default_theme_value_dark))) {
                    Toast.makeText(getActivity().getApplicationContext(), R.string.dark_theme_selected, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), R.string.light_theme_selected, Toast.LENGTH_SHORT).show();
                }
            }

            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if (prefIndex >= 0) {
                    CharSequence[] labels = listPreference.getEntries();
                    preference.setSummary(labels[prefIndex]);
                }
            }

            return true;
        }

        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String preferenceString = preferences.getString(preference.getKey(), "");
            onPreferenceChange(preference, preferenceString);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        }
    }
}

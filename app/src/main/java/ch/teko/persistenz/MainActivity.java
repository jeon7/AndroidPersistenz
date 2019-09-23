package ch.teko.persistenz;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MainActivity";
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private static final int PERMISSIONS_REQUEST_WRITE_CONTACTS = 101;
    private EditText editTextNumberContacts;
    private SharedPreferences sp;
    private static final String keyNumberContacts = "numberContacts";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        editTextNumberContacts = findViewById(R.id.editText_number_contacts);
        String spLastValue = sp.getString(keyNumberContacts, "0");
        // show last user saved number
        editTextNumberContacts.setText(spLastValue);

        // hide keyboard when not focused on editText_new_item
        editTextNumberContacts.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        requestContactsPermission();
    }

    public void onButtonShowClicked(View view) {
        editTextNumberContacts.clearFocus();

        String userInput = editTextNumberContacts.getText().toString();

        // if user input has new value
        if (sp.getString(keyNumberContacts, "0") != userInput) {
            SharedPreferences.Editor editor;
            editor = sp.edit();
            editor.putString(keyNumberContacts, userInput);
            editor.commit();
        }

        // todo:
        getContacts();
        //  showContacts()

        TableLayout tableLayout = findViewById(R.id.tableLayoutContacts);
        // clear table
        if (tableLayout.getChildCount() != 0) {
            tableLayout.removeAllViews();
        }

        // test code
        String spNumberContacts = sp.getString(keyNumberContacts, "0");
        TableRow tableRow = new TableRow(this);
        TextView tv = new TextView(this);
        tv.setText(spNumberContacts);
        tableRow.addView(tv);
        tableLayout.addView(tableRow);
    }

    private void getContacts() {

        ContentResolver resolver = getContentResolver();
        Uri contactsUri = ContactsContract.Contacts.CONTENT_URI;

        String[] projection = {ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.LOOKUP_KEY};
        String contactsSelection = ContactsContract.Contacts.DISPLAY_NAME + " NOTNULL";
        String[] selectionArgs = null;
        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";

        Cursor cursor = resolver.query(contactsUri, projection, contactsSelection, selectionArgs, sortOrder);

        int indexId = cursor.getColumnIndex(ContactsContract.Contacts._ID);
        int indexName = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
        int indexKey = cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY);

//        Uri phoneUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
//        String phoneSelection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?";
//        int indexHasPhoneNumber = cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);

        while (cursor.moveToNext()) {
            String id = cursor.getString(indexId);
            String name = cursor.getString(indexName);
            String key = cursor.getString(indexKey);

            Log.i(LOG_TAG, "id: " + id);
            Log.i(LOG_TAG, "name: " + name);
            Log.i(LOG_TAG, "key: " + key);
        }
        cursor.close();
    }

    private void requestContactsPermission() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{Manifest.permission.READ_CONTACTS},
                    PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.WRITE_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{Manifest.permission.WRITE_CONTACTS},
                    PERMISSIONS_REQUEST_WRITE_CONTACTS);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
            } else {
                Toast.makeText(this, "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}

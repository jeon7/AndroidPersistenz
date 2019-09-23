package ch.teko.persistenz;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

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

        TableLayout tableLayout = findViewById(R.id.tableLayoutContacts);
        // clear table
        if (tableLayout.getChildCount() != 0) {
            tableLayout.removeAllViews();
        }

        // todo: getContacts(), showContacts()

        // test code
        String spNumberContacts = sp.getString(keyNumberContacts, "0");
        TableRow tableRow = new TableRow(this);
        TextView tv = new TextView(this);
        tv.setText(spNumberContacts);
        tableRow.addView(tv);
        tableLayout.addView(tableRow);
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}

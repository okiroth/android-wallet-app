package com.palebluepagos.androidwallet.accounts;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.palebluepagos.androidwallet.HomeActivity;
import com.palebluepagos.androidwallet.R;
import com.palebluepagos.androidwallet.models.Bank;
import com.palebluepagos.androidwallet.user.UserData;
import com.palebluepagos.androidwallet.utilities.Codes;

/**
 * Created by ivan on 3/28/15.
 */
public class AddNewBankActivity extends ActionBarActivity {

    private int selectedBank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bank_account);
        getSupportActionBar().setElevation(0f);

        Spinner spinner = (Spinner) findViewById(R.id.banks_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.banks_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedBank = position;

                if (position == 1) {
                    findViewById(R.id.add_bank_aacount_galicia).setVisibility(View.GONE);
                    findViewById(R.id.add_bank_aacount_bbva).setVisibility(View.VISIBLE);
                    findViewById(R.id.add_bank_aacount_done).setVisibility(View.VISIBLE);

                } else if (position == 2) {
                    findViewById(R.id.add_bank_aacount_galicia).setVisibility(View.VISIBLE);
                    findViewById(R.id.add_bank_aacount_bbva).setVisibility(View.GONE);
                    findViewById(R.id.add_bank_aacount_done).setVisibility(View.VISIBLE);

                } else {
                    findViewById(R.id.add_bank_aacount_done).setVisibility(View.GONE);
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void addNewBank(View view) {
        if (selectedBank == 1) {
            String username = ((EditText) findViewById(R.id.bbva_username_edittext)).getText().toString();
            String password = ((EditText) findViewById(R.id.bbva_password_edittext)).getText().toString();
            String pin8 = ((EditText) findViewById(R.id.bbva_pin8_edittext)).getText().toString();

            Bank bbva = new Bank(Codes.BBVA_BANK_CODE);
            bbva.setPassword(password);
            bbva.setUsername(username);
            bbva.passwordInternet = pin8;

            bbva.save();
        }

        if (selectedBank == 2) {
            UserData user = new UserData(this);
            String password = ((EditText) findViewById(R.id.galica_password_edittext)).getText().toString();

            Bank galicia = new Bank(Codes.GALICIA_BANK_CODE);
            galicia.setUsername(user.getCurrentUser().getDni());
            galicia.setPassword(password);

            galicia.save();
        }

        startActivity(new Intent(this, HomeActivity.class));
    }

}

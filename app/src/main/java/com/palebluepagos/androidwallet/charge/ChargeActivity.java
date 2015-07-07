package com.palebluepagos.androidwallet.charge;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.palebluepagos.androidwallet.R;
import com.palebluepagos.androidwallet.user.UserData;
import com.palebluepagos.androidwallet.utilities.AnimationHelper;
import com.palebluepagos.androidwallet.utilities.numpad.NumPadFragment;

public class ChargeActivity extends ActionBarActivity {

    private static final String TAG_CHARGE_NUMPAD       = "TAG_CHARGE_NUMPAD";
    private static final String TAG_CHARGE_ACCOUNT_LIST = "TAG_CHARGE_ACCOUNT_LIST";
    private static final int    MAX_DISPLAY_DESC        = 30;

    private String                     savedDesc;
    private AccountsListChargeFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge);

        getSupportActionBar().setElevation(0f);

        UserData userData = new UserData(this);
        this.savedDesc = userData.getDefaultChargeDesc();
        EditText editText = (EditText) findViewById(R.id.charge_desc_text);
        editText.setText(savedDesc);
        this.trimDisplayDesc();

        TextView amount = (TextView) findViewById(R.id.charge_detail_amount);

        amount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimationHelper.showToBottom(findViewById(R.id.numpad_container));
                AnimationHelper.hideToTop(findViewById(R.id.charge_details_choose_list));
                findViewById(R.id.charge_desc_container).setVisibility(View.GONE);
                findViewById(R.id.charge_accounts_title).setVisibility(View.GONE);
            }
        });

        if (savedInstanceState == null) {
            NumPadFragment numpad = new NumPadFragment();
            numpad.setTextView(amount);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.numpad_container, numpad, TAG_CHARGE_NUMPAD)
                    .commit();

            fragment = new AccountsListChargeFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.charge_details_choose_list, fragment, TAG_CHARGE_ACCOUNT_LIST)
                    .commit();
        }
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    public void editDesc(View view) {
        findViewById(R.id.charge_description_section).setVisibility(View.VISIBLE);
        EditText editText = (EditText) findViewById(R.id.charge_desc_text);
        editText.requestFocus();

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(view.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
    }

    public void acceptNewDesc(View view) {
        // Hide keyboard and section
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        findViewById(R.id.charge_description_section).setVisibility(View.GONE);

        EditText editText = (EditText) findViewById(R.id.charge_desc_text);
        savedDesc = editText.getText().toString();
        this.trimDisplayDesc();
    }

    public void shareQrData(View view) {
        Intent sendIntent = new Intent();

        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, fragment.getCharge().getChargeShareUri());

        sendIntent.setType("text/plain");

        startActivity(Intent.createChooser(sendIntent, "Como quieres compartir?"));
    }

    private void trimDisplayDesc() {
        TextView descDisplay = (TextView) findViewById(R.id.charge_desc_display);
        if (savedDesc.length() > MAX_DISPLAY_DESC) {
            descDisplay.setText(savedDesc.substring(0, MAX_DISPLAY_DESC) + "...");
        } else {
            descDisplay.setText(savedDesc);
        }
    }

    public void cancelNewDesc(View view) {
        // Hide keyboard and section
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        findViewById(R.id.charge_description_section).setVisibility(View.GONE);

        EditText editText = (EditText) findViewById(R.id.charge_desc_text);
        editText.setText(savedDesc);
    }
}

package com.palebluepagos.androidwallet.pay;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.palebluepagos.androidwallet.HomeActivity;
import com.palebluepagos.androidwallet.R;
import com.palebluepagos.androidwallet.models.Charge;

public class PayActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_details);
        getSupportActionBar().setElevation(0f);

        String qrData = getIntent().getStringExtra(getString(R.string.qr_encoded_data));
        Charge charge = new Charge(qrData);

        // Set info
        TextView amount = (TextView) findViewById(R.id.pay_detail_amount);
        TextView desc = (TextView) findViewById(R.id.pay_detail_desc);
        amount.setText("$ " + charge.getAmount());
        desc.setText(charge.getDesc());

        // Add accounts to pay from
        AccountsListPayFragment fragment = new AccountsListPayFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.payment_details_choose_list, fragment)
                .commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_payment_details, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent home = new Intent(this, HomeActivity.class);
        startActivity(home);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

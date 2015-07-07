/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.palebluepagos.androidwallet;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.palebluepagos.androidwallet.accounts.AccountsListFragment;
import com.palebluepagos.androidwallet.accounts.AddNewBankActivity;
import com.palebluepagos.androidwallet.accounts.BanksListFragment;
import com.palebluepagos.androidwallet.accounts.LeftMenuFragment;
import com.palebluepagos.androidwallet.accounts.data.AccountsDataHandler;
import com.palebluepagos.androidwallet.charge.ChargeActivity;
import com.palebluepagos.androidwallet.charge.ChargeDataContract;
import com.palebluepagos.androidwallet.charge.ChargesListFragment;
import com.palebluepagos.androidwallet.models.Charge;
import com.palebluepagos.androidwallet.pay.ReadQrActivity;


public class HomeActivity extends ActionBarActivity {

    public static final boolean superUser = false;
    private static final String TAG_HOME_BANKS_LIST = "TAG_HOME_BANKS_LIST";
    private static final String TAG_LEFT_MENU       = "TAG_LEFT_MENU";
    // Left Menu
    private DrawerLayout          mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private AccountsListFragment accountsListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        LeftMenuFragment menu = new LeftMenuFragment();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.left_drawer, menu, TAG_LEFT_MENU)
                .commit();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.banks_list_container, new BanksListFragment(), TAG_HOME_BANKS_LIST)
                .commit();

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close);

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setElevation(0f);

        // Create a Tabs View
        CustomPagerAdapter mCustomPagerAdapter = new CustomPagerAdapter(getSupportFragmentManager());
        ViewPager mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mCustomPagerAdapter);

        this.checkOriginIntent();
    }

    private void checkOriginIntent() {
        String newPayment = getIntent().getStringExtra(getString(R.string.register_payment_ok));

        if (newPayment != null && newPayment.length() > 3) {
            String id = Charge.getChargeIdFromPhone(newPayment);
            Charge charge = Charge.findById(Charge.class, Long.valueOf(id));

            if (charge != null) {
                charge.setStatus(ChargeDataContract.PAYED_UNVERIFYED);
                charge.save();

                new AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setTitle("Te han enviado $" + charge.getAmount())
                        .setMessage("El dinero estará disponible en tu cuenta en las próximas 24hs")
                        .setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .show();
            }
        }

        /* Add here all other intents that go to Home screen */
    }

    public void leftMenuAddNewBank(View view) {
        startActivity(new Intent(this, AddNewBankActivity.class));
    }

    public void leftMenuLockWallet(View view) {
        MainActivity.PIN_WAS_SET = false;
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == android.R.id.home) {
            if (mDrawerLayout.isDrawerVisible(GravityCompat.START)) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
            } else {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        }

        if (item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        if (item.getItemId() == R.id.action_refresh) {
            accountsListFragment.accountsDataHandler.updateAccounts(AccountsDataHandler.FORCE_REFERSH,
                    accountsListFragment.rootView);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    /**
     * Starts a new PaymentActivity
     *
     * @param view The button pressed
     */
    public void startPaymentActivity(View view) {
        startActivity(new Intent(this, ReadQrActivity.class));
    }

    /**
     * Starts a new PaymentActivity
     *
     * @param view The button pressed
     */
    public void startChargeActivity(View view) {
        startActivity(new Intent(this, ChargeActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MainActivity.PIN_WAS_SET = false;
        finish();
    }

    @Override
    protected void onResume() {
        if (accountsListFragment != null) accountsListFragment.updateAccounts();
        super.onResume();
    }

    class CustomPagerAdapter extends FragmentPagerAdapter {

        public CustomPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = new TopButtonsFragment();
                    break;

                case 1:
                    fragment = new AccountsListFragment();
                    accountsListFragment = (AccountsListFragment) fragment;
                    break;

                case 2:
                    fragment = new ChargesListFragment();
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "BILLETERA";

                case 1:
                    return "CUENTAS";

                case 2:
                    return "INGRESOS";
            }
            return "null";
        }
    }
}
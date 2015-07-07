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

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;

import com.palebluepagos.androidwallet.pay.PayActivity;
import com.palebluepagos.androidwallet.user.UserData;
import com.palebluepagos.androidwallet.user.UserDataActivity;
import com.palebluepagos.androidwallet.utilities.numpad.NumPadPinFragment;
import com.palebluepagos.androidwallet.utilities.numpad.NumpadCallbackDone;


public class MainActivity extends ActionBarActivity {

    public static boolean PIN_WAS_SET = false;

    UserData userData;
    TextView pin;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        userData = new UserData(this);

        if (userData.hasLockPin() == false || userData.hasCuit() == false) {
            startActivity(new Intent(this, UserDataActivity.class));
            return;
        }

        this.checkPinIsSet();
    }

    private void checkPinIsSet() {
        if (PIN_WAS_SET == false) {
            setContentView(R.layout.activity_ask_pin);

            pin = (TextView) findViewById(R.id.general_pin_display);
            final NumPadPinFragment numpad = new NumPadPinFragment();
            numpad.setTextView(pin);

            numpad.setNumpadCallback(new NumpadCallbackDone() {

                @Override
                public void execute(View view) {
                    if (userData.verifyLockPin(numpad.storedValue)) {
                        PIN_WAS_SET = true;
                        afterPin();
                    } else {
                        pin.setText("");
                        numpad.storedValue = "";
                    }
                }

            });

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.numpad_container, numpad, "TAG_NUMPAD_GENERAL_PIN")
                    .commit();
        } else {
            this.afterPin();
        }
    }

    private void afterPin() {
        Intent incoming = getIntent();
        String action = incoming.getAction();

        Intent out = null;

        if (action != null && action.equals(Intent.ACTION_VIEW)) {
            String path = incoming.getData().getPath();
            String data = incoming.getData().getQueryParameter("e");

            switch (path) {
                case "/qr/":
                    out = new Intent(this, PayActivity.class);
                    out.putExtra(getString(R.string.qr_encoded_data), data);
                    break;

                case "/p/":
                    out = new Intent(this, HomeActivity.class);
                    out.putExtra(getString(R.string.register_payment_ok), data);
                    break;
            }

        } else {
            out = new Intent(this, HomeActivity.class);
        }

        startActivity(out);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}
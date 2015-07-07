/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.palebluepagos.androidwallet.utilities.numpad;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.palebluepagos.androidwallet.R;
import com.palebluepagos.androidwallet.utilities.AnimationHelper;

/**
 * Encapsulates fetching the forecast and displaying it as a {@link android.widget.ListView} layout.
 */
public class NumPadPinFragment extends Fragment {

    public final static int PIN_LENGTH = 4;
    public  NumpadCallbackDone callbackDone;
    public String storedValue = "";
    private TextView           textView;

    public NumPadPinFragment() {
    }

    public void setTextView(TextView textView) {
        this.textView = textView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        int layout = R.layout.fragment_num_pad_pin;

        View rootView = inflater.inflate(layout, container, false);

        View.OnClickListener numpadListener = new NumpadListener();
        rootView.findViewById(R.id.numpad_0).setOnClickListener(numpadListener);
        rootView.findViewById(R.id.numpad_1).setOnClickListener(numpadListener);
        rootView.findViewById(R.id.numpad_2).setOnClickListener(numpadListener);
        rootView.findViewById(R.id.numpad_3).setOnClickListener(numpadListener);
        rootView.findViewById(R.id.numpad_4).setOnClickListener(numpadListener);
        rootView.findViewById(R.id.numpad_5).setOnClickListener(numpadListener);
        rootView.findViewById(R.id.numpad_6).setOnClickListener(numpadListener);
        rootView.findViewById(R.id.numpad_7).setOnClickListener(numpadListener);
        rootView.findViewById(R.id.numpad_8).setOnClickListener(numpadListener);
        rootView.findViewById(R.id.numpad_9).setOnClickListener(numpadListener);
        rootView.findViewById(R.id.numpad_errase).setOnClickListener(numpadListener);

        return rootView;
    }

    public NumpadCallbackDone getNumpadCallback() {
        if (this.callbackDone == null) {
            return new NumpadCallbackDone() {
                @Override
                public void execute(View view) {
                    AnimationHelper.hideToTop(view.findViewById(R.id.numpad_container));
                    AnimationHelper.showToBottom(view.findViewById(R.id.charge_details_choose_list));
                    view.findViewById(R.id.charge_desc_container).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.charge_accounts_title).setVisibility(View.VISIBLE);
                }
            };
        } else {
            return this.callbackDone;
        }
    }

    public void setNumpadCallback(NumpadCallbackDone numpadCallback) {
        this.callbackDone = numpadCallback;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private class NumpadListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.numpad_errase:
                    int length = textView.getText().length();
                    if (length > 0) {
                        String text = textView.getText().toString();
                        textView.setText(text.substring(0, text.length() - 1));
                        storedValue = storedValue.substring(0, text.length() - 1);
                    }
                    break;

                default:
                    Button button = (Button) v;
                    String s = button.getText().toString();
                    textView.append(getActivity().getString(R.string.password_dot));
                    storedValue += s;

                    if (storedValue.length() == PIN_LENGTH) {
                        getNumpadCallback().execute(v.getRootView());
                    }
            }
        }
    }
}
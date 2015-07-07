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
package com.palebluepagos.androidwallet.pay;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.palebluepagos.androidwallet.R;
import com.palebluepagos.androidwallet.accounts.AccountsListAdapter;
import com.palebluepagos.androidwallet.accounts.data.AccountsDataHandler;
import com.palebluepagos.androidwallet.connection.RequestResult;
import com.palebluepagos.androidwallet.connection.RequestsCallback;
import com.palebluepagos.androidwallet.models.Account;
import com.palebluepagos.androidwallet.pay.controller.AfterPaymentHandler;
import com.palebluepagos.androidwallet.pay.controller.BanksPayer;
import com.palebluepagos.androidwallet.utilities.AnimationHelper;

/**
 * Encapsulates fetching the forecast and displaying it as a {@link android.widget.ListView} layout.
 */
public class AccountsListPayFragment extends Fragment implements RequestsCallback {

    public ListView listView;
    private AccountsListAdapter mAccountsListAdapter;
    private AccountsDataHandler accountsDataHandler;

    public AccountsListPayFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        mAccountsListAdapter = new AccountsListAdapter(getActivity());
        accountsDataHandler = new AccountsDataHandler(getActivity(), mAccountsListAdapter);

        View rootView = inflater.inflate(R.layout.fragment_bank_list, container, false);

        listView = (ListView) rootView.findViewById(R.id.accouts_listview);
        listView.setAdapter(mAccountsListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Account account = (Account) mAccountsListAdapter.getItem(position);
                makeTransfer(account);
            }

        });

        this.accountsDataHandler.updateAccountsDataUI();

        return rootView;
    }

    private void makeTransfer(Account account) {
        // Hide list with animation
        AnimationHelper.hideToTop(getActivity().findViewById(R.id.payment_details_choose_list));

        // Hide the List title
        getActivity().findViewById(R.id.pay_accounts_title).setVisibility(View.GONE);

        // Show operation text label
        getActivity().findViewById(R.id.payment_operation_progress_text).setVisibility(View.VISIBLE);

        // Change wording
        ((TextView) getActivity().findViewById(R.id.payment_operation_progress_text))
                .setText(getString(R.string.pay_in_progress));

        // Make loading animation visible
        getActivity().findViewById(R.id.payment_operation_progress_loading).setVisibility(View.VISIBLE);

        String qrData = getActivity().getIntent().getStringExtra(getString(R.string.qr_encoded_data));

        BanksPayer payer = new BanksPayer(getActivity());
        payer.pay(this, account, qrData);
    }

    @Override
    public void doOnComplete(RequestResult result) {
        AfterPaymentHandler afterPaymentHandler = new AfterPaymentHandler(result.getCharge(), getActivity());
        afterPaymentHandler.resolveStatus(result.getStatus());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

}
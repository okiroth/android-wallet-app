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
package com.palebluepagos.androidwallet.charge;

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
import com.palebluepagos.androidwallet.models.Account;
import com.palebluepagos.androidwallet.models.Charge;
import com.palebluepagos.androidwallet.user.UserData;
import com.palebluepagos.androidwallet.utilities.AnimationHelper;
import com.palebluepagos.androidwallet.utilities.QrHelper;

/**
 * Encapsulates fetching the forecast and displaying it as a {@link android.widget.ListView} layout.
 */
public class AccountsListChargeFragment extends Fragment {

    public ListView listView;
    private AccountsListAdapter mAccountsListAdapter;
    private AccountsDataHandler accountsDataHandler;
    private Charge charge;

    public AccountsListChargeFragment() {
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
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Account account = (Account) mAccountsListAdapter.getItem(position);
                createNewCharge(account);
            }
        });

        this.accountsDataHandler.updateAccountsDataUI();

        return rootView;
    }

    public void createNewCharge(Account account) {
        // Disable Edits
        TextView amount = (TextView) getActivity().findViewById(R.id.charge_detail_amount);
        amount.setEnabled(false);

        getActivity().findViewById(R.id.edition_pencil_icon).setVisibility(View.GONE);

        TextView desc = (TextView) getActivity().findViewById(R.id.charge_desc_text);

        // Hide list with animation
        AnimationHelper.hideToTop(getActivity().findViewById(R.id.charge_details_choose_list));

        // Hide the List title
        getActivity().findViewById(R.id.charge_accounts_title).setVisibility(View.GONE);

        this.setCharge(new Charge());

        this.getCharge().setAmount(amount.getText().toString());
        this.getCharge().setDesc(desc.getText().toString());
        this.getCharge().setCbu(account.cbu);
        this.getCharge().setBankCode(account.bankcode);

        this.getCharge().setType(ChargeDataContract.ONE_TIME_CHARGE);
        this.getCharge().setStatus(ChargeDataContract.PENDING);
        this.getCharge().setDate(System.currentTimeMillis());

        UserData userData = new UserData(getActivity());
        this.getCharge().setCuit(userData.getCurrentUser().getCuit());
        this.getCharge().setPhone(userData.getCurrentUser().getPhone());

        this.getCharge().save();

        Long id = charge.getId();
        this.getCharge().setUid(String.valueOf(id));

        this.getCharge().save();

        QrHelper qrHelper = new QrHelper(getActivity(), this.getCharge());
        qrHelper.createQrCode();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public Charge getCharge() {
        return charge;
    }

    public void setCharge(Charge charge) {
        this.charge = charge;
    }


}
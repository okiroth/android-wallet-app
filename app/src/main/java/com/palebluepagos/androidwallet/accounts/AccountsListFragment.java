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
package com.palebluepagos.androidwallet.accounts;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.palebluepagos.androidwallet.R;
import com.palebluepagos.androidwallet.accounts.data.AccountsDataHandler;

/**
 * Encapsulates fetching the forecast and displaying it as a {@link ListView} layout.
 */
public class AccountsListFragment extends Fragment {

    public AccountsListAdapter adapter;
    public AccountsDataHandler accountsDataHandler;

    public ListView listView;
    public View     rootView;

    public AccountsListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new AccountsListAdapter(getActivity());
        accountsDataHandler = new AccountsDataHandler(getActivity(), adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_bank_list, container, false);

        listView = (ListView) rootView.findViewById(R.id.accouts_listview);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

            }
        });

        this.updateAccounts();

        return rootView;
    }

    public void updateAccounts() {
        this.accountsDataHandler.updateAccounts(AccountsDataHandler.DONT_FORCE_REFERSH, rootView);
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
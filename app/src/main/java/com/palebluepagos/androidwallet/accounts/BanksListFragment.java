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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.palebluepagos.androidwallet.HomeActivity;
import com.palebluepagos.androidwallet.R;
import com.palebluepagos.androidwallet.models.Account;
import com.palebluepagos.androidwallet.models.Bank;
import com.palebluepagos.androidwallet.utilities.Utility;

import java.util.List;

/**
 * Encapsulates fetching the forecast and displaying it as a {@link android.widget.ListView} layout.
 */
public class BanksListFragment extends Fragment {

    public BankCustomAdapter adapter;

    public ListView listView;
    public View     rootView;

    public BanksListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new BankCustomAdapter(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_bank_list, container, false);

        listView = (ListView) rootView.findViewById(R.id.accouts_listview);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                final Bank bank = (Bank) adapter.getItem(position);

                new AlertDialog.Builder(getActivity())
                        .setIcon(android.R.drawable.ic_delete)
                        .setTitle(R.string.delete_bank)
                        .setMessage("Desea eliminar el banco " + Utility.getBankNameByCode(bank.getCode()) + "?")
                        .setPositiveButton("SI", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                bank.delete();

                                List<Account> byBank = Account.findByBank(bank.getCode());
                                for (Account account : byBank) {
                                    account.delete();
                                }

                                startActivity(new Intent(getActivity(), HomeActivity.class));
                            }

                        })
                        .setNegativeButton("NO", null)
                        .show();
            }
        });

        this.updateBankList();

        return rootView;
    }

    private void updateBankList() {
        List<Bank> list = Bank.listAll(Bank.class);
        adapter.clear();
        adapter.addAll(list);
        adapter.notifyDataSetChanged();
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
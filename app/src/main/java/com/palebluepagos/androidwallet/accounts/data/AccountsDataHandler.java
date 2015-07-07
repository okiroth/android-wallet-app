package com.palebluepagos.androidwallet.accounts.data;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.palebluepagos.androidwallet.R;
import com.palebluepagos.androidwallet.accounts.AccountsListAdapter;
import com.palebluepagos.androidwallet.accounts.AddNewBankActivity;
import com.palebluepagos.androidwallet.connection.BaseConnectionHandler;
import com.palebluepagos.androidwallet.connection.RequestResult;
import com.palebluepagos.androidwallet.connection.RequestsCallback;
import com.palebluepagos.androidwallet.connection.banks.BbvaConnectionHandler;
import com.palebluepagos.androidwallet.connection.banks.GaliciaConnectionHandler;
import com.palebluepagos.androidwallet.models.Account;
import com.palebluepagos.androidwallet.models.Bank;
import com.palebluepagos.androidwallet.utilities.AnimationHelper;
import com.palebluepagos.androidwallet.utilities.Codes;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by ivan on 3/20/15.
 */
public class AccountsDataHandler implements RequestsCallback {
    public final static boolean FORCE_REFERSH      = true;
    public final static boolean DONT_FORCE_REFERSH = false;

    private AccountsListAdapter    adapter;
    private List<BaseDataProvider> activeBanks;

    private View    fragmentContainer;
    private Context context;

    private int finished = 0;

    public AccountsDataHandler(Context context, AccountsListAdapter listAdapter) {
        this.adapter = listAdapter;
        this.context = context;

        this.activeBanks = new LinkedList<>();

        List<Bank> banks = Bank.listAll(Bank.class);
        for (Bank bank : banks) {
            BaseDataProvider provider = null;
            switch (bank.getCode()) {
                case Codes.BBVA_BANK_CODE:
                    provider = new BaseDataProvider(new BbvaConnectionHandler(context));
                    break;

                case Codes.GALICIA_BANK_CODE:
                    provider = new BaseDataProvider(new GaliciaConnectionHandler(context));
                    break;
            }
            activeBanks.add(provider);
        }
    }

    /**
     * Gets new data either from DB or internet
     * and notifies the adapter
     */
    public void updateAccounts(boolean force, View view) {
        if (this.activeBanks.size() == 0) {
            context.startActivity(new Intent(context, AddNewBankActivity.class));
            return;
        }

        this.fragmentContainer = view;
        finished = 0;

        this.updateAccountsDataUI();

        this.showLoadingAnimation();
        for (BaseDataProvider provider : this.activeBanks) {
            provider.injectAccountsDataUI(this, force);
        }
    }

    @Override
    public void doOnComplete(RequestResult result) {

        switch (result.getStatus()) {
            case BaseConnectionHandler.LOGIN_FAIL:
            case BaseConnectionHandler.ERROR_CONNECTING_URL:

                Toast toast = Toast.makeText(context, "Error al conectar con " + result.getName(), Toast.LENGTH_LONG);
                toast.show();

                break;

            default:
                // do nothing
        }

        finished++;
        this.finshedUpdatingAllAccounts();
    }

    private void showLoadingAnimation() {
        AnimationHelper.showToBottom(fragmentContainer.findViewById(R.id.fetching_data_home_screen_animation));
    }

    private void finshedUpdatingAllAccounts() {
        if (finished == activeBanks.size()) {
            AnimationHelper.hideToTop(fragmentContainer.findViewById(R.id.fetching_data_home_screen_animation));
            this.updateAccountsDataUI();
        }
    }

    public void updateAccountsDataUI() {
        List<Account> accounts = Account.listAll(Account.class);
        adapter.clear();
        adapter.addAll(accounts);
        adapter.notifyDataSetChanged();
    }

}

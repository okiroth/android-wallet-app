package com.palebluepagos.androidwallet.pay.controller;

import android.content.Context;

import com.palebluepagos.androidwallet.connection.BaseConnectionHandler;
import com.palebluepagos.androidwallet.connection.RequestsCallback;
import com.palebluepagos.androidwallet.connection.banks.BbvaConnectionHandler;
import com.palebluepagos.androidwallet.connection.banks.GaliciaConnectionHandler;
import com.palebluepagos.androidwallet.models.Account;
import com.palebluepagos.androidwallet.models.Charge;
import com.palebluepagos.androidwallet.utilities.Codes;

/**
 * Created by ivan on 3/27/15.
 */
public class BanksPayer {

    public static Context payContext;

    public BanksPayer(Context context) {
        this.payContext = context;
    }

    public void pay(RequestsCallback callback, Account origin, String data) {

        Charge destiny = new Charge();
        destiny.setDataFromEncoded(data);
        destiny.setCurrency(Codes.CURRENCY_ARS);

        BaseConnectionHandler handler = null;

        switch (origin.bankcode) {
            case Codes.BBVA_BANK_CODE:
                handler = new BbvaConnectionHandler(this.payContext);
                break;

            case Codes.GALICIA_BANK_CODE:
                handler = new GaliciaConnectionHandler(this.payContext);
                break;
        }

//        handler = new DummyConnectionHandler(payContext);

        handler.transferMoney(callback, origin, destiny);
    }


}

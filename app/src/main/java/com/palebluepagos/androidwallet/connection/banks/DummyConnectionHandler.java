package com.palebluepagos.androidwallet.connection.banks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.palebluepagos.androidwallet.connection.BaseConnectionHandler;
import com.palebluepagos.androidwallet.connection.RequestResult;
import com.palebluepagos.androidwallet.connection.RequestsCallback;
import com.palebluepagos.androidwallet.models.Account;
import com.palebluepagos.androidwallet.models.Charge;

/**
 * Created by ivan on 3/20/15.
 */
public class DummyConnectionHandler extends BaseConnectionHandler {


    public DummyConnectionHandler(Context context) {
        super(context);
        BANK_NAME = "Dummy Bank";
    }

    @Override
    public void fetchAccountsNewData(RequestsCallback callback) {
        this.callback = callback;
        new LogIn().execute();
    }

    @Override
    public void transferMoney(RequestsCallback callback, Account origin, Charge charge) {
        this.callback = callback;
        this.origin = origin;
        this.charge = charge;

        new TransferMoney().execute();
    }

    @Override
    public void doAfterAskCoordenadas(String result) {
        new AfterCoordenadas().execute();
    }

    private class LogIn extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return LOGIN_OK;
        }

        @Override
        protected void onPostExecute(String s) {
            callback.doOnComplete(new RequestResult(s, BANK_NAME));
        }
    }

    private class TransferMoney extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            coordenadasHandler.coordenadasKey[0] = "F6";
            coordenadasHandler.coordenadasKey[1] = "I2";

            return TRANSFERENCE_OK;
        }

        @Override
        protected void onPostExecute(String s) {
            // if has coords, then ask, or go callback
            coordenadasHandler.askCoordenadas();
        }
    }

    private class AfterCoordenadas extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Log.d("COORD", coordenadasHandler.coordenadasKey[0] + ": " + coordenadasHandler.coordenadasValues[0]);
            Log.d("COORD", coordenadasHandler.coordenadasKey[1] + ": " + coordenadasHandler.coordenadasValues[1]);

            return TRANSFERENCE_OK;
        }

        @Override
        protected void onPostExecute(String s) {
            RequestResult req = new RequestResult(s, BANK_NAME);
            req.setCharge(charge);
            callback.doOnComplete(req);
        }
    }

}

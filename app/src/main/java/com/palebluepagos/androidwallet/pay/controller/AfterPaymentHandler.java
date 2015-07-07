package com.palebluepagos.androidwallet.pay.controller;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;

import com.palebluepagos.androidwallet.HomeActivity;
import com.palebluepagos.androidwallet.R;
import com.palebluepagos.androidwallet.connection.BaseConnectionHandler;
import com.palebluepagos.androidwallet.models.Charge;
import com.palebluepagos.androidwallet.utilities.Utility;

/**
 * Created by ivan on 4/9/15.
 */
public class AfterPaymentHandler {

    private Context context;
    private Charge  charge;

    public AfterPaymentHandler(Charge charge, Context context) {
        this.charge = charge;
        this.context = context;
    }

    public void resolveStatus(String status) {
        if (status == null) return;

        switch (status) {
            case BaseConnectionHandler.TRANSFERENCE_OK:
                this.paymentPositive();
                break;

            case BaseConnectionHandler.NO_MONEY:
                this.noMoney();
                break;

            case BaseConnectionHandler.TRANSFERENCE_FAIL:
                this.paymentNegative();
                break;

            default:
                Log.d("AUX", "unkwon: " + status);
                // do nothing
        }
    }


    private void paymentPositive() {
        this.sendTextMessage();

        new AlertDialog.Builder(this.getContext())
                .setCancelable(false)
                .setTitle("Pago realizado con éxito")
                .setMessage("El dinero se descontará de su cuenta en las próximas 24hs")
                .setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        goHome();
                    }
                })
                .show();
    }


    private void noMoney() {
        new AlertDialog.Builder(this.getContext())
                .setCancelable(false)
                .setTitle("Fondos insuficientes")
                .setMessage(this.getContext().getString(R.string.error_payment))
                .setPositiveButton("CERRAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Go to Pay again, or show acconunts list again
                        goHome();
                    }
                })
                .show();
    }

    private void paymentNegative() {
        new AlertDialog.Builder(this.getContext())
                .setCancelable(false)
                .setTitle("Pago no pudo ser realizado")
                .setMessage(this.getContext().getString(R.string.error_payment))
                .setPositiveButton("CERRAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Go to Pay again, or show acconunts list again
                        goHome();
                    }
                })
                .show();
    }

    public void sendTextMessage() {
        if (this.getCharge().getPhone().length() < Utility.MIN_PHONE_LENGTH) return;

        String smsBody = this.getCharge().getEncryptedPhone();

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(this.getCharge().getPhone(),
                    null,
                    smsBody,
                    null,
                    null);

        } catch (Exception ex) {
//            Toast.makeText(this.getContext(), "Your sms has failed...", Toast.LENGTH_LONG).show();
//            ex.printStackTrace();
        }

    }

    private void goHome() {
        Intent intent = new Intent(this.getContext(), HomeActivity.class);
        this.getContext().startActivity(intent);
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Charge getCharge() {
        return charge;
    }

    public void setCharge(Charge charge) {
        this.charge = charge;
    }
}

package com.palebluepagos.androidwallet.charge;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.palebluepagos.androidwallet.R;
import com.palebluepagos.androidwallet.models.Charge;
import com.palebluepagos.androidwallet.utilities.TimeUtils;
import com.palebluepagos.androidwallet.utilities.Utility;

public class ChargeDialog extends Dialog implements View.OnClickListener {

    public Context activity;
    public Dialog  dialog;
    public Button  close, share;

    private Charge charge;

    public ChargeDialog(Context uiThread, Charge charge) {
        super(uiThread);
        this.activity = uiThread;
        this.charge = charge;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_charge);

        TextView date = (TextView) findViewById(R.id.charge_date);
        date.setText(TimeUtils.getFullDate(this.getCharge().getDate()));

        TextView desc = (TextView) findViewById(R.id.charge_desc);
        desc.setText(this.getCharge().getDesc());
        TextView amount = (TextView) findViewById(R.id.charge_amount);
        amount.setText("$ " + this.getCharge().getAmount());


        TextView status = (TextView) findViewById(R.id.charge_status);
        status.setText(Utility.getStatusName(this.getCharge().getStatus()));
        ImageView icon = (ImageView) findViewById(R.id.charge_status_icon);
        icon.setImageResource(Utility.getStatusIcon(this.getCharge().getStatus()));

        TextView destiny = (TextView) findViewById(R.id.charge_destiny_bank);
        destiny.setText(Utility.getBankNameByCode(this.getCharge().getBankCode()));

        close = (Button) findViewById(R.id.btn_close);
        share = (Button) findViewById(R.id.btn_share);
        close.setOnClickListener(this);
        share.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_close:
                break;

            case R.id.btn_share:
                this.shareCharge();
                break;
        }
        dismiss();
    }

    private void shareCharge() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, charge.getChargeShareUri());
        sendIntent.setType("text/plain");
        CharSequence title = "Como quieres compartir?";
        this.activity.startActivity(Intent.createChooser(sendIntent, title));
    }

    public Charge getCharge() {
        return charge;
    }

    public void setCharge(Charge charge) {
        this.charge = charge;
    }
}

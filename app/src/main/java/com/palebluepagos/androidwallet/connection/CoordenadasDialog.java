package com.palebluepagos.androidwallet.connection;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.palebluepagos.androidwallet.R;

public class CoordenadasDialog extends Dialog implements android.view.View.OnClickListener {

    public Context activity;
    public Dialog  dialog;
    public Button  yes;

    private String key0, key1;

    private CoordenadasHandler coordenadasHandler;

    public CoordenadasDialog(Context uiThread, CoordenadasHandler coordenadasHandler,
                             String key0, String key1) {
        super(uiThread);
        this.activity = uiThread;
        this.coordenadasHandler = coordenadasHandler;
        this.key0 = key0;
        this.key1 = key1;
    }

    public EditText getEditText0() {
        return (EditText) findViewById(R.id.coordenanda0_edittext);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_coordinates);

        TextView key0Text = (TextView) findViewById(R.id.coordenanda0_key);
        key0Text.setText(this.key0 + ":");

        TextView key1Text = (TextView) findViewById(R.id.coordenanda1_key);
        key1Text.setText(this.key1 + ":");

        yes = (Button) findViewById(R.id.btn_yes);
        yes.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_yes:

                EditText val0Text = (EditText) findViewById(R.id.coordenanda0_edittext);
                EditText val1Text = (EditText) findViewById(R.id.coordenanda1_edittext);

                String val0 = val0Text.getText().toString();
                String val1 = val1Text.getText().toString();

                if (val0.length() != 2) {
                    val0Text.requestFocus();
                    return;
                }

                if (val1.length() != 2) {
                    val0Text.requestFocus();
                    return;
                }

                this.coordenadasHandler.onAccept(val0, val1);

                break;
            default:
                break;
        }
        dismiss();
    }
}

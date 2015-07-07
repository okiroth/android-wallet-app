package com.palebluepagos.androidwallet.user;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.palebluepagos.androidwallet.HomeActivity;
import com.palebluepagos.androidwallet.R;
import com.palebluepagos.androidwallet.connection.BaseConnectionHandler;
import com.palebluepagos.androidwallet.connection.RequestResult;
import com.palebluepagos.androidwallet.connection.RequestsCallback;
import com.palebluepagos.androidwallet.models.Account;
import com.palebluepagos.androidwallet.models.Bank;
import com.palebluepagos.androidwallet.models.UserModel;
import com.palebluepagos.androidwallet.utilities.AnimationHelper;

import java.util.List;


public class UserDataActivity extends ActionBarActivity implements RequestsCallback {

    EditText cuit;
    EditText pin;
    EditText phone;
    private int width = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_data);
        getSupportActionBar().hide();

        cuit = (EditText) findViewById(R.id.userdata_cuit_edittext);
        pin = (EditText) findViewById(R.id.userdata_pin_edittext);
        phone = (EditText) findViewById(R.id.userdata_phone_edittext);
    }

    public void saveUserdata(View v) {

        UserModel userdata;
        List<UserModel> list = UserModel.listAll(UserModel.class);

        if (list.size() == 1) {
            userdata = list.get(0);
        } else {
            userdata = new UserModel();
        }

        userdata.setCuit(cuit.getText().toString().replaceAll("-", "").trim());
        userdata.setLockPin(pin.getText().toString());
        userdata.setPhone(phone.getText().toString());
        userdata.save();

        startActivity(new Intent(this, HomeActivity.class));
    }


    public void goCuit(View view) {
        width = findViewById(R.id.new_user_data_step0).getWidth();

        // No validation
        AnimationHelper.hideToLeft(findViewById(R.id.new_user_data_step0));
        AnimationHelper.showFromRight(findViewById(R.id.new_user_data_step_cuit), width);
    }

    public void goPhone(View view) {

        if (cuit.getText().length() < UserData.CUIT_MIN_LENGTH) {
            Toast.makeText(this, "El CUIT o CUIL debe tener 10 o 11 números", Toast.LENGTH_LONG).show();
        } else {
            AnimationHelper.hideToLeft(findViewById(R.id.new_user_data_step_cuit));
            AnimationHelper.showFromRight(findViewById(R.id.new_user_data_step_phone), width);
        }

    }

    public void goPin(View view) {

        if (cuit.getText().length() < UserData.CUIT_MIN_LENGTH) {
            Toast.makeText(this, "El celular debe tener 10 o 11 números", Toast.LENGTH_LONG).show();
        } else {
            AnimationHelper.hideToLeft(findViewById(R.id.new_user_data_step_phone));
            AnimationHelper.showFromRight(findViewById(R.id.new_user_data_step_pin), width);
        }

    }

    public void doneUserSteps(View view) {

        if (pin.getText().length() != UserData.PIN_LENGTH) {
            Toast.makeText(this, "El PIN debe tener 4 dígitos", Toast.LENGTH_LONG).show();
        } else {
            AnimationHelper.hideToLeft(findViewById(R.id.new_user_data_step_pin));
            AnimationHelper.showFromRight(findViewById(R.id.new_user_data_step_loading), width);

            this.saveUserdata(view);
        }

    }


    @Override
    public void doOnComplete(RequestResult result) {
        if (result.getStatus().equals(BaseConnectionHandler.LOGIN_FAIL)) {
            new AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setTitle("Error al crear cuenta")
                    .setMessage("Se ha producido un error, por favor intenta nuevamente.")
                    .setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AnimationHelper.hideToLeft(findViewById(R.id.new_user_data_step_loading));
                            AnimationHelper.showFromRight(findViewById(R.id.new_user_data_step0), width);

                            Bank.deleteAll(Bank.class);
                            Account.deleteAll(Account.class);
                            UserModel.deleteAll(UserModel.class);
                        }
                    })
                    .show();
        } else {
            final Activity _this = this;
            new AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setTitle("Listo!")
                    .setMessage("Te damos una alegre bienvenida a la billetera del siglo XXI.")
                    .setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(_this, HomeActivity.class));
                        }
                    })
                    .show();
        }
    }
}

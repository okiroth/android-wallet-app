package com.palebluepagos.androidwallet.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.palebluepagos.androidwallet.R;
import com.palebluepagos.androidwallet.models.UserModel;

import java.util.List;

/**
 * Created by ivan on 3/26/15.
 */
public class UserData {

    public final static int CUIT_MIN_LENGTH = 10;
    public final static int PIN_LENGTH      = 4;
    public final static String EMPTY_STRING = "";
    // TODO delete this
    protected SharedPreferences preferences;
    private Context   context;
    private UserModel currentUser;

    public UserData(Context context) {
        this.context = context;
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);

        List<UserModel> list = UserModel.listAll(UserModel.class);
        if (list.size() == 1) {
            this.currentUser = list.get(0);
        }
    }


    public UserModel getCurrentUser() {
        return this.currentUser;
    }


    public boolean hasLockPin() {
        if (this.currentUser != null && this.currentUser.getLockPin() != null) {
            return this.currentUser.getLockPin().length() == PIN_LENGTH;
        }
        return false;
    }


    public boolean hasCuit() {
        if (this.currentUser != null && this.currentUser.getCuit() != null) {
            return this.currentUser.getCuit().length() >= CUIT_MIN_LENGTH;
        }
        return false;
    }


    public boolean verifyLockPin(String pin) {
        return this.currentUser.getLockPin().equals(pin);
    }


    public String getDefaultChargeDesc() {
        return this.preferences.getString(context.getString(R.string.prefskey_user_charge_desc), EMPTY_STRING);
    }

}

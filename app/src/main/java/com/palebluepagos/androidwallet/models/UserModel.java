package com.palebluepagos.androidwallet.models;

import com.orm.SugarRecord;

/**
 * Created by ivan on 3/28/15.
 */
public class UserModel extends SugarRecord<UserModel> {

    private String cuit;
    private String dni;
    private String lockPin;
    private String phone;

    public UserModel() {
    }

    private void parseDni() {
        this.dni = cuit.substring(2, cuit.length() - 1);
    }

    public String getLockPin() {
        return this.lockPin;
    }

    // TODO encrypt the PIN
    public void setLockPin(String pin) {
        this.lockPin = pin;
    }

    public String getCuit() {
        return this.cuit;
    }

    public void setCuit(String cuit) {
        this.cuit = cuit;
        this.parseDni();
    }

    public String getDni() {
        return this.dni;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}

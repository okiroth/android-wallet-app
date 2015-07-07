package com.palebluepagos.androidwallet.models;

import com.palebluepagos.androidwallet.utilities.Utility;
import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

/**
 * Created by ivan on 3/28/15.
 */
public class Bank extends SugarRecord<Bank> {

    // Pagomiscuentas
    public String passwordInternet;
    private int    code;
    private String username;
    private String password;

    public Bank() {

    }

    public Bank(int code) {
        this.setCode(code);
    }

    public static Bank findByCode(int code) {
        List<Bank> list = Select.from(Bank.class)
                .where(Condition.prop("code").eq(code))
                .list();

        if (list.size() != 1) {
            return null;
        } else {
            return list.get(0);
        }
    }

    public String getName() {
        return Utility.getBankNameByCode(this.getCode());
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}

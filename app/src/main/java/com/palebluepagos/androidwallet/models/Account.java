package com.palebluepagos.androidwallet.models;

import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

/**
 * Created by ivan on 3/20/15.
 */
public class Account extends SugarRecord<Account> {

    public long milli;

    public String  cbu;
    public String  accountnumber;
    public String  accounttype;
    public String  currency;
    public String  amount;
    public Integer bankcode;
    public Integer pageorder;

    public Account() {
    }

    public Account(int pageOrder) {
        this.pageorder = pageOrder;
    }

    public static Account findByCbu(String cbu) {
        List<Account> list = Select.from(Account.class)
                .where(Condition.prop("cbu").eq(cbu))
                .list();

        if (list.size() != 1) {
            return null;
        } else {
            return list.get(0);
        }
    }

    public static List<Account> findByBank(int code) {
        List<Account> list = Select.from(Account.class)
                .where(Condition.prop("bankcode").eq(code))
                .list();

        return list;
    }


    @Override
    public String toString() {
        String out = "";

        out += this.accounttype + "|";
        out += this.accountnumber + "|";
        out += this.currency + "|";
        out += this.bankcode + "|";
        out += this.amount + "|";
        out += this.pageorder + "|";
        out += this.cbu + "|";

        return out;
    }
}


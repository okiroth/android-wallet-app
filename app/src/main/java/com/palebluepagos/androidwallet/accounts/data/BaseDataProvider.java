package com.palebluepagos.androidwallet.accounts.data;

import com.palebluepagos.androidwallet.connection.BaseConnectionHandler;
import com.palebluepagos.androidwallet.connection.RequestResult;
import com.palebluepagos.androidwallet.connection.RequestsCallback;
import com.palebluepagos.androidwallet.models.Account;
import com.palebluepagos.androidwallet.utilities.Utility;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

/**
 * Created by ivan on 3/28/15.
 */
public class BaseDataProvider {

    public final static String ACCOUNTS_IN_DB = "ACCOUNTS DATA ALREADY IN DB";

    private BaseConnectionHandler handler;

    public BaseDataProvider(BaseConnectionHandler handler) {
        this.setHandler(handler);
    }

    public void injectAccountsDataUI(RequestsCallback callback, boolean force) {
        List<Account> accounts = Select.from(Account.class)
                .where(Condition.prop("bankcode").eq(this.getHandler().BANK_CODE))
                .list();

        long currentTime = System.currentTimeMillis();
        long accountLastUpdate = 0;

        if (accounts.size() != 0) accountLastUpdate = accounts.get(0).milli;

        if ((currentTime - accountLastUpdate) > Utility.REFRESH_TIME || force) {
            this.getHandler().fetchAccountsNewData(callback);

        } else {
            callback.doOnComplete(new RequestResult(ACCOUNTS_IN_DB, this.getHandler().BANK_NAME));
        }
    }

    public BaseConnectionHandler getHandler() {
        return handler;
    }

    public void setHandler(BaseConnectionHandler handler) {
        this.handler = handler;
    }
}

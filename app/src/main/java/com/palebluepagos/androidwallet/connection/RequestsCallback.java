package com.palebluepagos.androidwallet.connection;

/**
 * Created by ivan on 3/20/15.
 */
public interface RequestsCallback {

    public static final String PAYMENT_RESULT_STATUS_KEY = "PAYMENT_RESULT_STATUS_KEY";

    public void doOnComplete(RequestResult result);
}

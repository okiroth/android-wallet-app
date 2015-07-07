package com.palebluepagos.androidwallet.connection;

import com.palebluepagos.androidwallet.models.Charge;

/**
 * Created by ivan on 3/30/15.
 */
public class RequestResult {

    private String status;
    private String name;
    private Charge charge;

    public RequestResult(String status, String name) {
        this.status = status;
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Charge getCharge() {
        return charge;
    }

    public void setCharge(Charge charge) {
        this.charge = charge;
    }
}

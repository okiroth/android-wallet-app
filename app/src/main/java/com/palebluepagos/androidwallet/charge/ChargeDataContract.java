package com.palebluepagos.androidwallet.charge;

/**
 * Created by ivan on 3/23/15.
 */
public class ChargeDataContract {

    // XXXX | asdasdasd| asdasda| |assadasd |

    public final static String PAYMENT_DATA_DIVIEDR = "\\|";

    public final static int PAYMENT_DATA_ATTRIBUTES = 8;

    public final static int CBU_POSITION       = 0;
    public final static int CUIT_POSITION      = 1;
    public final static int DESCRIPTION_POS    = 2;
    public final static int MONEY_POSITION     = 3;
    public final static int BANK_CODE_POSITION = 4;
    public final static int TYPE_POSITION      = 5;
    public final static int UID_POSITION       = 6;
    public final static int PHONE_POSITION     = 7;


    // Charges types
    public static final int ONE_TIME_CHARGE = 1;
    public static final int INFINITE_CHARGE = 2;


    // Charges status
    public static final int PENDING          = 0;
    public static final int PAYED_UNVERIFYED = 1;
    public static final int ACREDITED        = 2;

}

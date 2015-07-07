package com.palebluepagos.androidwallet.models;

import android.net.Uri;
import android.util.Log;

import com.palebluepagos.androidwallet.charge.ChargeDataContract;
import com.palebluepagos.androidwallet.utilities.Codes;
import com.palebluepagos.androidwallet.utilities.SimpleCrypto;
import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by ivan on 3/20/15.
 */
public class Charge extends SugarRecord<Charge> {

    public static final  String CHARGE_DOMAIN = "http://qr.cashpagos.com";
    private static final String EXTRA_STRING  = "EXTRA_STRING_NO_PURPOSE";
    private String currency;
    private String amount;
    private String cbu;
    private String desc;
    private String cuit;

    private long   date;
    private String uid;

    private int bankCode;
    private int type;
    private int status;

    private String phone;

    public Charge() {
    }

    /**
     * Constructs a PaymentDestiny from a QR encrypted string
     *
     * @param enctyptedString this
     */
    public Charge(String enctyptedString) {
        this.setDataFromEncoded(enctyptedString);
    }

    public static String getChargeIdFromPhone(String data) {
        Log.d("UNDECO", data);
        String decoded = SimpleCrypto.decrypt(data, SimpleCrypto.SEED_DEFAULT);
        Log.d("DECO", decoded);
        return decoded.replace(EXTRA_STRING, "");
    }

    public static List<Charge> findByStatus(int status) {
        List<Charge> list = Select.from(Charge.class)
                .where(Condition.prop("status").eq(status))
                .orderBy("date DESC")
                .list();
        return list;
    }

    public static List<Charge> findAll() {
        List<Charge> list = Select.from(Charge.class)
                .orderBy("date DESC")
                .list();
        return list;
    }

    public String getEncryptedPhone() {
        try {

            String data = EXTRA_STRING + this.getUid();
            String encoded = URLEncoder.encode(SimpleCrypto.encrypt(data, SimpleCrypto.SEED_DEFAULT), "utf-8");

            return CHARGE_DOMAIN + "/p/?e=" + encoded;

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getChargeShareUri() {
        String data = null;
        try {
            data = URLEncoder.encode(this.getEncrypted(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Uri qrDataUri = Uri.parse(CHARGE_DOMAIN + "/qr/?e=" + data);
        return qrDataUri.toString();
    }

    public String getEncrypted() {
        String[] arr = new String[ChargeDataContract.PAYMENT_DATA_ATTRIBUTES];

        arr[ChargeDataContract.MONEY_POSITION] = this.amount;
        arr[ChargeDataContract.DESCRIPTION_POS] = this.desc;
        arr[ChargeDataContract.CBU_POSITION] = this.cbu;
        arr[ChargeDataContract.CUIT_POSITION] = this.cuit;
        arr[ChargeDataContract.BANK_CODE_POSITION] = String.valueOf(this.bankCode);
        arr[ChargeDataContract.TYPE_POSITION] = String.valueOf(this.type);
        arr[ChargeDataContract.UID_POSITION] = this.uid;
        arr[ChargeDataContract.PHONE_POSITION] = this.phone;

        String encodedStr = "";
        for (String attr : arr) {
            encodedStr += attr + "|";
        }

        return SimpleCrypto.encrypt(encodedStr, SimpleCrypto.SEED_DEFAULT);
    }

    public void setDataFromEncoded(String encodedData) {
        String decoded = SimpleCrypto.decrypt(encodedData, SimpleCrypto.SEED_DEFAULT);
        String[] data = decoded.split(ChargeDataContract.PAYMENT_DATA_DIVIEDR);

        this.setAmount(data[ChargeDataContract.MONEY_POSITION]);
        this.setCbu(data[ChargeDataContract.CBU_POSITION]);
        this.setCuit(data[ChargeDataContract.CUIT_POSITION]);
        this.setDesc(data[ChargeDataContract.DESCRIPTION_POS]);
        this.setUid(data[ChargeDataContract.UID_POSITION]);
        this.setPhone(data[ChargeDataContract.PHONE_POSITION]);

        this.setBankCode(Integer.parseInt(data[ChargeDataContract.BANK_CODE_POSITION]));
        this.setType(Integer.parseInt(data[ChargeDataContract.TYPE_POSITION]));
    }

    /**
     * Strips the DNI from the CUIT nn-XX.XXX.XXX-n
     *
     * @return the DNI as string
     */
    public String getDni() {
        return cuit.substring(2, cuit.length() - 1);
    }

    /**
     * Returns the first two digits from teh CUIT
     *
     * @return Two digit numbre string
     */
    public String getCuitPre() {
        return cuit.substring(0, 2);
    }

    /**
     * Returns the last digit form teh CUIT
     *
     * @return One digit number string
     */
    public String getCuitPost() {
        return cuit.substring(cuit.length() - 1);
    }

    public String getAmountMain() {
        return this.amount.split("\\.")[0];
    }

    public String getAmountCents() {
        return this.amount.split("\\.")[1];
    }

    public String getAmount() {
        return this.amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDesc() {
        if (desc == null || desc.length() == 0) {
//            Log.d("PAYMENET", "Desc was never set!");
            return "cash";
        }
        return this.desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCbu() {
        return cbu;
    }

    public void setCbu(String cbu) {
        this.cbu = cbu;
    }

    public String getCuit() {
        return cuit;
    }

    public void setCuit(String cuit) {
        this.cuit = cuit;
    }

    public String getCurrency() {
        if (currency == null || currency.length() == 0) {
//            Log.d("PAYMENET", "Currency was never set!");
            return Codes.CURRENCY_ARS;
        }
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Override
    public String toString() {
        String out = "";

        out += this.cbu + "|";
        out += this.amount + "|";
        out += this.currency + "|";
        out += this.desc + "|";
        out += this.cuit + "|";

        return out;
    }

    public int getBankCode() {
        return bankCode;
    }

    public void setBankCode(int bankCode) {
        this.bankCode = bankCode;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}


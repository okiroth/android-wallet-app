package com.palebluepagos.androidwallet.connection.banks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.palebluepagos.androidwallet.connection.BaseConnectionHandler;
import com.palebluepagos.androidwallet.connection.RequestResult;
import com.palebluepagos.androidwallet.connection.RequestsCallback;
import com.palebluepagos.androidwallet.connection.parser.BbvaParser;
import com.palebluepagos.androidwallet.models.Account;
import com.palebluepagos.androidwallet.models.Bank;
import com.palebluepagos.androidwallet.models.Charge;
import com.palebluepagos.androidwallet.utilities.Codes;
import com.palebluepagos.androidwallet.utilities.Utility;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles the connection with the backend for the BBVA bank
 * <p/>
 * Created by ivan on 3/20/15.
 */
public class BbvaConnectionHandler extends BaseConnectionHandler {

    // URLs without Login
    public static final String MAIN_REFERER   = "https://hb.bbv.com.ar/fnet/index.jsp";
    public static final String URL_INITIAL    = "https://www.bbvafrances.com.ar/";
    public static final String URL_HOME       = "https://www.bbvafrances.com.ar/tlal/jsp/ar/esp/home/index.jsp";
    public static final String URL_LOGIN_FORM = "https://hb.bbv.com.ar/fnet/mod/login/loginNew.do";

    // URLs with Login
    public static final String URL_HOMEBANKING_INDEX  = "https://hb.bbv.com.ar/fnet/index.jsp";
    public static final String URL_HOMEBANKING_LOGOUT = "https://hb.bbv.com.ar/fnet/logout.jsp";


    private final static int STEP_GET_DATA = 1;
    private final static int STEP_TRANSFER = 2;

    public BbvaConnectionHandler(Context context) {
        super(context);
        this.bankData = Bank.findByCode(Codes.BBVA_BANK_CODE);
        BANK_NAME = this.bankData.getName();
        BANK_CODE = this.bankData.getCode();
    }


    @Override
    public void fetchAccountsNewData(RequestsCallback callback) {
        this.callback = callback;
        DO_AFTER_LOGIN = STEP_GET_DATA;
        new LogInBbva().execute();
    }

    @Override
    public void transferMoney(RequestsCallback callback, Account origin, Charge destiny) {
        this.callback = callback;
        this.origin = origin;
        this.charge = destiny;

        DO_AFTER_LOGIN = STEP_TRANSFER;
        new LogInBbva().execute();
    }

    @Override
    public void doAfterAskCoordenadas(String result) {
        // not needed for now
    }

    private class LogInBbva extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            redirect = NO_REDIRECT;

            getUrl(URL_INITIAL, EMPTY_REFERER);
            if (hasRedirect()) getUrl(redirect, EMPTY_REFERER);
            getUrl(URL_HOME, redirect);

            // First send only DNI
            List<NameValuePair> sendDni = new ArrayList<>();
            sendDni.add(new BasicNameValuePair("tipodoc", "DNI"));
            sendDni.add(new BasicNameValuePair("nrodoc", userData.getCurrentUser().getDni()));
            sendDni.add(new BasicNameValuePair("tipodoc", ""));
            sendDni.add(new BasicNameValuePair("nrodoc", ""));

            postUrl(URL_LOGIN_FORM + "?method=status", URL_HOME, Utility.pairsToString(sendDni));

            // Extra calls to simulate browser behaviour //TODO make this more dynamic
            getUrl("https://hb.bbv.com.ar/fnet/ajax/jquery-ui/1.10.2/css/smoothness/jquery-ui-1.10.2.custom.css", MAIN_REFERER);
            getUrl("https://hb.bbv.com.ar/fnet/html/bbva/loginNuevo.css", MAIN_REFERER);

            // Now send the user/pass
            List<NameValuePair> sendUser = new ArrayList<>();
            sendUser.add(new BasicNameValuePair("usuario", bankData.getUsername()));
            sendUser.add(new BasicNameValuePair("clave", bankData.getPassword()));

            postUrl(URL_LOGIN_FORM + "?method=claveDigital", URL_LOGIN_FORM + "?method=status",
                    Utility.pairsToString(sendUser));

            String indexPage = getUrl(URL_HOMEBANKING_INDEX, URL_LOGIN_FORM + "?method=status");

            // The best way to check login is parsing the webpage //TODO think about this
            if (BbvaParser.parseAccountsData(indexPage).equals(BbvaParser.ERROR_PARSING)) {
                return LOGIN_FAIL;
            } else {
                return LOGIN_OK;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals(LOGIN_FAIL)) {
                new Logout().execute();

                if (loginsCount++ < MAX_RETRIES) {
                    new LogInBbva().execute();
                } else {
                    callback.doOnComplete(new RequestResult(result, BANK_NAME));
                }
                return;
            }
            loginsCount = 0;

            if (DO_AFTER_LOGIN == STEP_GET_DATA) {
                new GetAccountsData().execute();

            } else if (DO_AFTER_LOGIN == STEP_TRANSFER) {
                new TransferMoney().execute();

            } else {
                new Logout().execute();
                callback.doOnComplete(new RequestResult(result, BANK_NAME));
            }
        }
    }

    /**
     * After a succesful LOG_IN, then go to the index
     * page of the homebanking
     */
    private class GetAccountsData extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                String indexPage = getUrl(URL_HOMEBANKING_INDEX, URL_LOGIN_FORM + "?method=status");
                BbvaParser.parseAccountsData(indexPage);

                List<String> links = BbvaParser.parseAccountDetailLinks(indexPage);
                int pageOrder = 0;
                for (String link : links) {

                    // enter the details account, session stored following link
                    getUrl("https://hb.bbv.com.ar/fnet/mod/cuentas/cuentasMovimientosJson.do"
                            + link + "&_=" + Utility.GetMilliseconds(), MAIN_REFERER);

                    // ask CBU
                    String cbuPage = getUrl("https://hb.bbv.com.ar/fnet/mod/cuentas/consultaCBUCuentaSD.do?method=getCBUCuenta&_="
                            + Utility.GetMilliseconds(), MAIN_REFERER);

                    BbvaParser.parseCBU(cbuPage, pageOrder);
                    pageOrder++;
                }

                return CONNECTING_URL_OK;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return ERROR_CONNECTING_URL;
        }

        @Override
        protected void onPostExecute(String result) {
            new Logout().execute();
            callback.doOnComplete(new RequestResult(result, BANK_NAME));
        }
    }


    private class TransferMoney extends AsyncTask<String, Void, String> {
        // Click en "Transferencias" (left menu)
        String STEP_11_TRANSFERENCIA_MENU = "https://hb.bbv.com.ar/fnet/fnetMenuTabs.do?mod=transf&tab=1&_=";
        String STEP_12_TRANSFERENCIA_MENU = "https://hb.bbv.com.ar/fnet/mod/bbva/NL-publicidadDeHueco.do";
        String STEP_13_TRANSFERENCIA_MENU = "https://hb.bbv.com.ar/fnet/mod/transf/transfPropiasSD.do?_=";
        String STEP_14_TRANSFERENCIA_MENU = "https://hb.bbv.com.ar/fnet/mod/transf/js/async/transfPropias-1-3.js?";

        // Click en "Cuentas otro Banco - diferida"
        String STEP_22_TRANSFERENCIA_OTROS_BANCOS = "https://hb.bbv.com.ar/fnet/mod/transf/transfOtrosIndSD.do?target=";
        String STEP_23_TRANSFERENCIA_OTROS_BANCOS = "https://hb.bbv.com.ar/fnet/mod/transf/js/async/transfOtros-1-3.js?_=";
        String STEP_24_TRANSFERENCIA_OTROS_BANCOS = "https://hb.bbv.com.ar/fnet/mod/transf/js/async/transfAnteriores.js?_=";

        @Override
        protected String doInBackground(String... params) {
            try {
                String getTransPage = getUrl(STEP_11_TRANSFERENCIA_MENU + Utility.GetMilliseconds(), MAIN_REFERER);
                String targetNumber = BbvaParser.parseTargetNumber(getTransPage);

                int count = 0;
                while (targetNumber.equals(BbvaParser.ERROR_PARSING)) {
                    getTransPage = getUrl(STEP_11_TRANSFERENCIA_MENU + Utility.GetMilliseconds(), MAIN_REFERER);
                    targetNumber = BbvaParser.parseTargetNumber(getTransPage);
                    count++;

                    if (count > 20) {
                        Log.d("FAILED", getTransPage);
                        return "FAILED";
                    }
                }

                // Extra
                postUrl(STEP_12_TRANSFERENCIA_MENU, MAIN_REFERER, "");

                getUrl(STEP_13_TRANSFERENCIA_MENU + Utility.GetMilliseconds(), MAIN_REFERER);
                getUrl(STEP_14_TRANSFERENCIA_MENU + Utility.GetMilliseconds(), MAIN_REFERER);

                // Extra
                getUrl("https://hb.bbv.com.ar/fnet/images/bbva/sp.gif", MAIN_REFERER);
                getUrl("https://hb.bbv.com.ar/fnet/images/sprite-gray-x16.png", MAIN_REFERER);

                getUrl(STEP_22_TRANSFERENCIA_OTROS_BANCOS + targetNumber + "&_" + Utility.GetMilliseconds(), MAIN_REFERER);
                getUrl(STEP_23_TRANSFERENCIA_OTROS_BANCOS + Utility.GetMilliseconds(), MAIN_REFERER);
                getUrl(STEP_24_TRANSFERENCIA_OTROS_BANCOS + Utility.GetMilliseconds(), MAIN_REFERER);

                ////////////////////////////////////////////////////////////////////////////////////////////////////////
                // MAKE TRANSFER
                List<NameValuePair> transfData = new ArrayList<>();

                //Origin
                String currency = origin.currency; //Pesos
                String account = origin.pageorder + ""; // Caja de ahorro
                String money = charge.getAmount();
                String concept_code = "008-VARIOS++++";
                String concept = charge.getDesc();
                String sameOwnerDestiny = "N";
                String isCheckingAccount = "N";
                //Destino
                String cbu_1 = charge.getCbu().substring(0, 8);
                String cbu_2 = charge.getCbu().substring(8);

                transfData.add(new BasicNameValuePair("email", ""));
                transfData.add(new BasicNameValuePair("email2", ""));
                transfData.add(new BasicNameValuePair("alias", ""));
                transfData.add(new BasicNameValuePair("detalle", ""));
                transfData.add(new BasicNameValuePair("moneda", currency));
                transfData.add(new BasicNameValuePair("ctaorigen", account));
                transfData.add(new BasicNameValuePair("ctadestino0", cbu_1));
                transfData.add(new BasicNameValuePair("ctadestino1", cbu_2));
                transfData.add(new BasicNameValuePair("cuitdes", charge.getCuit()));
                transfData.add(new BasicNameValuePair("codconcepto", concept_code));
                transfData.add(new BasicNameValuePair("concepto", concept));
                transfData.add(new BasicNameValuePair("importe", money));
                transfData.add(new BasicNameValuePair("titu", sameOwnerDestiny));
                transfData.add(new BasicNameValuePair("tiesctacctu", isCheckingAccount));

                String aux = Utility.pairsToString(transfData);
                String postTransPage = postUrl(STEP_22_TRANSFERENCIA_OTROS_BANCOS + targetNumber, MAIN_REFERER, aux);
                ////////////////////////////////////////////////////////////////////////////////////////////////////////

                String urlReturn = BbvaParser.parseUrlReturn(postTransPage);
                String urlVolver = BbvaParser.parseUrlVolver(postTransPage);

                if (urlReturn.equals(BbvaParser.ERROR_PARSING)) {
                    targetNumber = BbvaParser.parseTargetNumber(postTransPage);
                    postTransPage = postUrl(STEP_22_TRANSFERENCIA_OTROS_BANCOS + BbvaParser.parseTargetNumber(postTransPage),
                            MAIN_REFERER, aux);
                    urlReturn = BbvaParser.parseUrlReturn(postTransPage);
                    urlVolver = BbvaParser.parseUrlVolver(postTransPage);
                }

                if (urlReturn.equals(BbvaParser.ERROR_PARSING)) {
                    Log.d("DEBUG", postTransPage);
                    return TRANSFERENCE_FAIL;
                }

                getUrl("https://hb.bbv.com.ar/fnet/mod/login/js/async/pin8YFactorValidacion.js?_=" + Utility.GetMilliseconds(), MAIN_REFERER);

                transfData.add(new BasicNameValuePair("pinban", bankData.passwordInternet));
                transfData.add(new BasicNameValuePair("usaTCoord", "S"));
                transfData.add(new BasicNameValuePair("urlReturn", urlVolver));
                transfData.add(new BasicNameValuePair("pasos", "2-3"));
                transfData.add(new BasicNameValuePair("urlVolver", ""));
                transfData.add(new BasicNameValuePair("target", targetNumber));
                transfData.add(new BasicNameValuePair("urlVolver", urlVolver));

                postUrl("https://hb.bbv.com.ar/fnet/mod/login/verificarPin8YFactorValidacion.do?ts=" + Utility.GetMilliseconds(),
                        MAIN_REFERER, Utility.pairsToString(transfData));

                postUrl("https://hb.bbv.com.ar/fnet" + urlReturn,
                        MAIN_REFERER, Utility.pairsToString(transfData));

                return TRANSFERENCE_OK;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return TRANSFERENCE_FAIL;
        }

        @Override
        protected void onPostExecute(String result) {
            new Logout().execute();

            RequestResult req = new RequestResult(result, BANK_NAME);
            req.setCharge(charge);
            callback.doOnComplete(req);
        }
    }

    private class Logout extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            return getUrl(URL_HOMEBANKING_LOGOUT, MAIN_REFERER);
        }
    }


}

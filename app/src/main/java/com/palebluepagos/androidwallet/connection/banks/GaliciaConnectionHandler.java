package com.palebluepagos.androidwallet.connection.banks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.palebluepagos.androidwallet.connection.BaseConnectionHandler;
import com.palebluepagos.androidwallet.connection.RequestResult;
import com.palebluepagos.androidwallet.connection.RequestsCallback;
import com.palebluepagos.androidwallet.connection.parser.GaliciaParser;
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
 * Created by ivan on 3/20/15.
 */
public class GaliciaConnectionHandler extends BaseConnectionHandler {

    private static final String HOST = "https://wsec01.bancogalicia.com.ar";

    // URLs without Login
    public static final String MAIN_REFERER = HOST + "/scripts/homebanking/Principal.asp";

    // URLs with Login
    public static final String URL_HOMEBANKING_INDEX  = HOST + "/scripts/homebanking/Principal.asp";
    public static final String URL_HOMEBANKING_LOGOUT = HOST + "/scripts/homebanking/Logout.asp?Valor=1&tipoUsuario=REAL";
    public static final String LEFT_MENU_REFERER      = HOST + "/scripts/homebanking/leftFrame.asp?IdOrigen=Login&IdOpMenu=0";

    private final static int STEP_TRANSFER = 2;

    // Needed only for Galicia Bank
    public String coordenandasPage = "";

    public GaliciaConnectionHandler(Context context) {
        super(context);
        this.bankData = Bank.findByCode(Codes.GALICIA_BANK_CODE);
        BANK_NAME = this.bankData.getName();
        BANK_CODE = this.bankData.getCode();
    }

    @Override
    public void fetchAccountsNewData(RequestsCallback callback) {
        this.callback = callback;
        new LogInParseDataGalicia().execute();
    }

    @Override
    public void transferMoney(RequestsCallback callback, Account origin, Charge destiny) {
        this.callback = callback;
        this.origin = origin;
        this.charge = destiny;

        DO_AFTER_LOGIN = STEP_TRANSFER;
        new LogInParseDataGalicia().execute();
    }

    @Override
    public void doAfterAskCoordenadas(String result) {
        new SendCoordenadas().execute();
    }


    private class LogInParseDataGalicia extends AsyncTask<String, Void, String> {
        private static final String URL_VERISIGN = "https://seal.verisign.com/getseal?host_name=WSEC01.BANCOGALICIA.COM.AR&size=S&use_flash=YES&use_transparent=YES&lang=es";        private static final String URL_LOGIN_PAGE          = HOST + "/scripts/homebanking/baselogin.asp";

        @Override
        protected String doInBackground(String... params) {
            try {
                getUrl(HOST + "/scripts/homebanking/GalHBlogin.asp", MAIN_REFERER);
                getUrl(HOST + "/scripts/homebanking/include/JavaScriptUtiles/flashobject.js", MAIN_REFERER);
                getUrl(HOST + "/scripts/homebanking/include/JavaScriptUtiles/Objects.js", MAIN_REFERER);
                getUrl(HOST + "/scripts/homebanking/include/JavaScriptUtiles/Cookie.js", MAIN_REFERER);
                getUrl(HOST + "/scripts/homebanking/include/JavaScriptUtiles/random.js", MAIN_REFERER);
                getUrl(HOST + "/scripts/homebanking/include/JavaScriptUtiles/class.div.js", MAIN_REFERER);
                getUrl(HOST + "/scripts/homebanking/include/JavaScriptUtiles/mootoolsV3.js", MAIN_REFERER);
                getUrl(HOST + "/scripts/homebanking/include/JavaScriptUtiles/Navegacion.js", MAIN_REFERER);
                getUrl(HOST + "/scripts/homebanking/include/JavaScriptUtiles/DefaultElements.js", MAIN_REFERER);
                getUrl(HOST + "/scripts/homebanking/include/JavaScriptUtiles/EncryptPin.js", MAIN_REFERER);
                getUrl(HOST + "/homebanking/DefaultNewNetscapeStyles.css", MAIN_REFERER);
                getUrl(URL_VERISIGN, MAIN_REFERER);

                String loginPage = getUrl(URL_LOGIN_PAGE, MAIN_REFERER);

                String encPin = GaliciaParser.getEncPin(loginPage, bankData.getPassword());
                String dni = userData.getCurrentUser().getDni();

                List<NameValuePair> loginData = new ArrayList<>();
                loginData.add(new BasicNameValuePair("IdOpcionMenu", ""));
                loginData.add(new BasicNameValuePair("AccDirDatos", "AccDir"));
                loginData.add(new BasicNameValuePair("PinEn", encPin));
                loginData.add(new BasicNameValuePair("Documento", dni));
                loginData.add(new BasicNameValuePair("txtPin", ""));
                loginData.add(new BasicNameValuePair("cmbAccDir", "0"));

                postUrl(URL_LOGIN_FORM, URL_LOGIN_PAGE, Utility.pairsToString(loginData));

                if (getRedirect().contains("Error")) return LOGIN_FAIL;

                // index page is mostly blank
                getUrl(URL_HOMEBANKING_INDEX, URL_LOGIN_PAGE);

                // here is the info about accounts
                String resumenPage = getUrl(URL_HOMEBANKING_RESUMEN, MAIN_REFERER);
                GaliciaParser.parseAccountsData(resumenPage);

                // cbu page contains all CBUs
                String cbuPage = getUrl(URL_CBU_PAGE, MAIN_REFERER);
                GaliciaParser.parseCBU(cbuPage);

                return LOGIN_OK;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return LOGIN_FAIL;
        }        private static final String URL_LOGIN_FORM          = HOST + "/scripts/homebanking/Registracion.asp";

        @Override
        protected void onPostExecute(String result) {
            if (result.equals(LOGIN_FAIL)) {
                new Logout().execute(); // in case there is an open session

                if (loginsCount++ < MAX_RETRIES) {
                    Log.d("GALICIA", "Retry login: " + loginsCount);
                    new LogInParseDataGalicia().execute();
                } else {
                    callback.doOnComplete(new RequestResult(result, BANK_NAME));
                }
                return;
            }
            loginsCount = 0;

            if (DO_AFTER_LOGIN == STEP_TRANSFER) {
                new TransferMoney().execute();

            } else {
                new Logout().execute();
                callback.doOnComplete(new RequestResult(result, BANK_NAME));
            }
        }        private static final String URL_HOMEBANKING_RESUMEN = HOST + "/scripts/homebanking/consultivo/Resumen.asp";
        private static final String URL_CBU_PAGE            = HOST + "/scripts/homebanking/consultivo/ConsultaCBU.asp";









    }


    private class TransferMoney extends AsyncTask<String, Void, String> {

        private boolean askCoordenadas = false;        private String URL_MENU_OPERAICONES     = HOST + "/scripts/homebanking/Portadas/portadaOperaciones.asp";

        @Override
        protected String doInBackground(String... params) {
            try {

                // Click top menu OPERACIONES
                getUrl(URL_MENU_OPERAICONES, MAIN_REFERER);

                // Click left menu Otros Bancos
                getUrl(URL_MENU_OTROS_BANCOS, LEFT_MENU_REFERER);

                if (getRedirect().contains("Desbloqueo1")) return TRANSFERENCE_FAIL;

                ////////////////////////////////////////////////////////////////
                List<NameValuePair> data = new ArrayList<>();
                data.add(new BasicNameValuePair("hiIDCuenta", "" + (origin.pageorder + 1))); //index start in 1
                data.add(new BasicNameValuePair("hiImporte", "" + charge.getAmount()));
                data.add(new BasicNameValuePair("hiMotivoTransferencia", charge.getDesc()));
                data.add(new BasicNameValuePair("hiReferenciaOperacion", "VAR" + charge.getDesc()));
                data.add(new BasicNameValuePair("hiCBUBanco", charge.getCbu().substring(0, 8)));
                data.add(new BasicNameValuePair("hiCBUNumero", charge.getCbu().substring(8)));
                data.add(new BasicNameValuePair("hiCtaCreditoMoneda", charge.getCurrency()));
                data.add(new BasicNameValuePair("hiBeneficiarioNombre", "."));
                data.add(new BasicNameValuePair("hiBeneficiarioTipoDoc", "CUIT"));
                data.add(new BasicNameValuePair("hiBeneficiarioNroDoc", ""));
                data.add(new BasicNameValuePair("hiBeneficiarioNroDoc1", charge.getDni()));
                data.add(new BasicNameValuePair("hiPrim2NumCuil", charge.getCuitPre()));
                data.add(new BasicNameValuePair("hiUltNumCuil", charge.getCuitPost()));
                data.add(new BasicNameValuePair("hiTipoMovimiento", "2"));
                data.add(new BasicNameValuePair("hFecha", ""));
                data.add(new BasicNameValuePair("hTitulo", ""));
                data.add(new BasicNameValuePair("hEnviaMail", ""));
                data.add(new BasicNameValuePair("hEMail", ""));
                data.add(new BasicNameValuePair("CmbCuentaDebito", "2"));
                data.add(new BasicNameValuePair("selTipoMovimiento", "2"));
                data.add(new BasicNameValuePair("TxtImporte1", charge.getAmountMain()));
                data.add(new BasicNameValuePair("TxtImporte2", charge.getAmountCents()));
                data.add(new BasicNameValuePair("CmbConceptoTranferencia", "VAR"));
                data.add(new BasicNameValuePair("TxtMotivoTransferencia", charge.getDesc()));
                data.add(new BasicNameValuePair("hiNroOperFrecuente", ""));
                data.add(new BasicNameValuePair("hiNroOperFrecuente", ""));
                data.add(new BasicNameValuePair("TxtCBUBanco", charge.getCbu().substring(0, 8)));
                data.add(new BasicNameValuePair("TxtCBUNumero", charge.getCbu().substring(8)));
                data.add(new BasicNameValuePair("CmbMonedaCBU", "1"));
                data.add(new BasicNameValuePair("TxtBeneficiarioNombre", "."));
                data.add(new BasicNameValuePair("CmbBeneficiarioTipoDoc", "CUIT"));
                data.add(new BasicNameValuePair("Prim2NumCuil", charge.getCuitPre()));
                data.add(new BasicNameValuePair("TxtBeneficiarioNroDoc1", charge.getDni()));
                data.add(new BasicNameValuePair("UltNumCuil", charge.getCuitPost()));
                data.add(new BasicNameValuePair("hiMailBeneficiario", ""));
                data.add(new BasicNameValuePair("hiFechaCalendar1", ""));
                data.add(new BasicNameValuePair("txtTitulo", ""));
                data.add(new BasicNameValuePair("cmbEmail", "mail@mail.com"));
                data.add(new BasicNameValuePair("bAltaMail", "0"));

                // Click Preparar Transferencia
                postUrl(URL_PREPARE_TRANSFERENCE, URL_MENU_OTROS_BANCOS, Utility.pairsToString(data));

                Log.d("AUX", Utility.pairsToString(data));

                List<NameValuePair> data2 = new ArrayList<>();
                data.add(new BasicNameValuePair("hiMailBeneficiario", ""));
                data.add(new BasicNameValuePair("NroCtaOperacionTokenSMS", charge.getCbu().substring(8)));
                data.add(new BasicNameValuePair("ImporteOperacionTokenSMS", charge.getAmount().replace("\\.", ",")));
                data.add(new BasicNameValuePair("MonedaOperacionTokenSMS", "03"));

                // Click confirmar
                postUrl(URL_TRANSFERENCE_FORM, URL_PREPARE_TRANSFERENCE, Utility.pairsToString(data2));

                if (hasRedirect() && getRedirect().contains("SolicitudCoordenadas")) {
                    askCoordenadas = true;
                    coordenandasPage = getUrl(HOST + getRedirect(), URL_PREPARE_TRANSFERENCE);

                    GaliciaParser.parseCoordenadas(
                            coordenandasPage,
                            coordenadasHandler.card,
                            coordenadasHandler.coordenadasKey);

                    if (coordenadasHandler.coordenadasKey[0] == null || coordenadasHandler.coordenadasKey[0].length() == 0) {
                        return TRANSFERENCE_FAIL;
                    }
                } else {
//                    Log.d("NO COORDINADAS REDIRECT", "last redirect: " + getRedirect());
                }

                return TRANSFERENCE_OK;

            } catch (Exception e) {
                e.printStackTrace();
            }

            return TRANSFERENCE_FAIL;
        }        private String URL_MENU_OTROS_BANCOS    = HOST + "/scripts/homebanking/transaccional/TransferenciaTercerosCoelsa1.asp";

        @Override
        protected void onPostExecute(String result) {
            if (askCoordenadas) {
                coordenadasHandler.askCoordenadas();
            } else {
                new Logout().execute();
                callback.doOnComplete(new RequestResult(result, BANK_NAME));
            }
        }        private String URL_PREPARE_TRANSFERENCE = HOST + "/scripts/homebanking/transaccional/TransferenciaTercerosCoelsa2.asp";
        private String URL_TRANSFERENCE_FORM    = HOST + "/scripts/homebanking/transaccional/TransferenciaTercerosCoelsa3.asp";






    }

    private class SendCoordenadas extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                String[] pivots = GaliciaParser.parsePivots(coordenandasPage);
                coordenadasHandler.card = pivots[2];

                String encCoord_1 = Utility.Encode(
                        Long.parseLong(pivots[0]), Long.parseLong(pivots[1]),
                        coordenadasHandler.coordenadasValues[0]);

                String encCoord_2 = Utility.Encode(
                        Long.parseLong(pivots[0]), Long.parseLong(pivots[1]),
                        coordenadasHandler.coordenadasValues[1]);

                String letter_1 = (coordenadasHandler.coordenadasKey[0].split("")[1]);
                String letter_2 = (coordenadasHandler.coordenadasKey[1].split("")[1]);

                String number_1 = (coordenadasHandler.coordenadasKey[0].split("")[2]);
                String number_2 = (coordenadasHandler.coordenadasKey[1].split("")[2]);

                List<NameValuePair> coords = new ArrayList<>();
                coords.add(new BasicNameValuePair("txtCoordenada1", ""));
                coords.add(new BasicNameValuePair("txtCoordenada2", ""));
                coords.add(new BasicNameValuePair("hiCoordenada1", encCoord_1));
                coords.add(new BasicNameValuePair("hiCoordenada2", encCoord_2));
                coords.add(new BasicNameValuePair("hiColumna1", letter_1));
                coords.add(new BasicNameValuePair("hiColumna2", letter_2));
                coords.add(new BasicNameValuePair("hiFila1", number_1));
                coords.add(new BasicNameValuePair("hiFila2", number_2));
                coords.add(new BasicNameValuePair("hiTipoOperacion", "T"));
                coords.add(new BasicNameValuePair("hiCantCoordDisp", "081"));
                coords.add(new BasicNameValuePair("hiNroSerie", coordenadasHandler.card));
                coords.add(new BasicNameValuePair("hiContexto", ""));

                postUrl(HOST + "/scripts/homebanking/anexos/SolicitudCoordenadas2.asp",
                        getRedirect(), Utility.pairsToString(coords));

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

package com.palebluepagos.androidwallet.connection;

import android.content.Context;
import android.util.Log;

import com.palebluepagos.androidwallet.models.Account;
import com.palebluepagos.androidwallet.models.Bank;
import com.palebluepagos.androidwallet.models.Charge;
import com.palebluepagos.androidwallet.pay.controller.BanksPayer;
import com.palebluepagos.androidwallet.user.UserData;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by ivan on 3/20/15.
 */
public abstract class BaseConnectionHandler {

    // LOGs
    public static final String EMPTY_RESPONSE       = "EMPTY RESPONSE";
    public static final String ERROR_CONNECTING_URL = "ERROR CONNECTING URL";
    public static final String CONNECTING_URL_OK    = "CONNECTING OK";
    public static final String TRANSFERENCE_OK      = "TRANSFERENCE DONE OK";
    public static final String TRANSFERENCE_FAIL    = "TRANSFERENCE FAIL";
    public static final String NO_REDIRECT          = "NO REDIERCT";
    public static final String LOGIN_OK             = "LOGIN SUCCESSFUL";
    public static final String LOGIN_FAIL           = "LOGIN FAILED";
    public static final String NO_MONEY             = "NO MONEY";
    //
    public static boolean DEBUG         = false;
    public static String  EMPTY_REFERER = "";
    public String BANK_NAME;
    public int    BANK_CODE;
    protected int loginsCount = 0;
    protected int MAX_RETRIES = 0;
    protected int DO_AFTER_LOGIN;
    // For transferences
    protected Account origin;
    protected Charge  charge;
    protected CoordenadasHandler coordenadasHandler;
    protected UserData userData;
    protected Bank     bankData;

    // Connection
    protected CookieStore      cookieStore;
    protected HttpContext      httpContext;
    protected String           redirect;
    protected RequestsCallback callback;

    public BaseConnectionHandler(Context context) {
        cookieStore = new BasicCookieStore();
        httpContext = new BasicHttpContext();
        userData = new UserData(context);
        coordenadasHandler = new CoordenadasHandler(BanksPayer.payContext, this);
    }

    public abstract void doAfterAskCoordenadas(String result);

    public abstract void fetchAccountsNewData(RequestsCallback callback);

    public abstract void transferMoney(RequestsCallback callback, Account origin, Charge charge);

    /**
     * Makes a PUT request to the desired URL
     *
     * @param address    the address (URL) to call
     * @param referer    the referer for the HEADER, can be EMPTY_REFERER
     * @param dataToSend the data to be added to the PUT request as a String
     * @return the response BODY as a String
     */
    public String postUrl(String address, String referer, String dataToSend) {
        this.setRedirect(NO_REDIRECT);
        try {
            URL url = new URL(address);

            HttpURLConnection request;
            if (address.startsWith("https")) {
                request = (HttpsURLConnection) url.openConnection();
            } else {
                request = (HttpURLConnection) url.openConnection();
            }

            request.setInstanceFollowRedirects(false);
            request.setRequestMethod("POST");

            request.setDoOutput(true);
            request.setDoInput(true);
            request.setUseCaches(false);
            request.setAllowUserInteraction(false);

            this.prepareHeader(request, cookieStore, referer);

            DataOutputStream dataout = new DataOutputStream(request.getOutputStream());
            dataout.writeBytes(dataToSend);

            return handleResponse("POST", address, request);

        } catch (Exception e) {
//            e.printStackTrace();
        }

        return ERROR_CONNECTING_URL;
    }


    /**
     * Makes a GET request to the desired URL
     *
     * @param address the address (URL) to call
     * @param referer the referer for the HEADER, can be EMPTY_REFERER
     * @return the response BODY as a String
     */
    public String getUrl(String address, String referer) {
        this.setRedirect(NO_REDIRECT);

        try {
            URL url = new URL(address);

            HttpURLConnection request;
            if (address.startsWith("https")) {
                request = (HttpsURLConnection) url.openConnection();
            } else {
                request = (HttpURLConnection) url.openConnection();
            }

            request.setInstanceFollowRedirects(false);
            request.setRequestMethod("GET");

            request.setDoInput(true);
            request.setUseCaches(false);
            request.setAllowUserInteraction(false);

            this.prepareHeader(request, cookieStore, referer);

            return handleResponse("GET", address, request);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ERROR_CONNECTING_URL;
    }


    /**
     * Reads the InputStream and transforms it into an String to be parsed.
     * Also, sets the Location
     *
     * @param method  the used method (GET, PUT)
     * @param url     the URL called
     * @param request the @HttpsURLConnection object used
     * @return the reponse BODY
     */
    public String handleResponse(String method, String url, HttpURLConnection request) {

        this.setRedirect(request.getHeaderField("Location"));
        this.addCookiesInStore(cookieStore, request);

//        try {
//            Log.d(method, url + " | " + request.getResponseCode() + " " + request.getResponseMessage());
//        } catch (IOException e) {
//        }

        String response = EMPTY_RESPONSE;
        try {
            if (request.getResponseCode() == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(request.getInputStream()));
                String inputLine;
                response = "";
                while ((inputLine = in.readLine()) != null) {
                    response += inputLine + "\n";
                }
                in.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }


    /**
     * Iterates through the reponse header and saves the cookies
     * in a CookieStore
     *
     * @param cookieStore
     * @param request
     */
    private void addCookiesInStore(CookieStore cookieStore, HttpURLConnection request) {
        for (int i = 0; i < request.getHeaderFields().size(); i++) {
            String key = request.getHeaderFieldKey(i);
            if (DEBUG) {
                Log.d("HEADER", key + ": " + request.getHeaderField(i));
            }
            if (key != null && key.equals("Set-Cookie")) {
                String header = request.getHeaderField(i);
                String[] cookie = header.split("=");
                cookieStore.addCookie(new BasicClientCookie(cookie[0], cookie[1]));
            }
        }
    }

    /**
     * Validates if redirect is empty or null
     *
     * @return
     */
    public boolean hasRedirect() {
        if (redirect == null || redirect.equals("")) {
            return false;
        }
        return true;
    }

    public String getRedirect() {
        return this.redirect;
    }

    /**
     * Stores the Location header response in a varible
     * to be used if needed
     *
     * @param location the Location from the HEADER response
     */
    public void setRedirect(String location) {
        if (location != null) {
            redirect = location;
//            if(!location.equals(NO_REDIRECT)) Log.d("REDIRECT", location);
        }
    }

    /**
     * Add the HEADER properties to the @HttpsURLConnection to simulate
     * the call from a regular browser
     *
     * @param request     the @HttpsURLConnection object to be used
     * @param cookieStore the current session @CookieStore
     * @param referer     a String referer to be placed in the HEADER
     */
    public void prepareHeader(HttpURLConnection request, CookieStore cookieStore, String referer) {
        String cookies = "";
        for (Cookie elem : cookieStore.getCookies()) {
            cookies += elem.getName() + "=" + elem.getValue() + "; ";
        }

        request.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        request.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.115 Safari/537.36");
        request.setRequestProperty("Referer", referer);

        request.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");

        request.setRequestProperty("Cache-Control", "no-cache");
        request.setRequestProperty("Pragma", "no-cache");
        request.setRequestProperty("Connection", "keep-alive");
        request.setRequestProperty("Cookie", cookies);
    }

}














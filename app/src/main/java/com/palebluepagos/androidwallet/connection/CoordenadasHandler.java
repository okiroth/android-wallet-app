package com.palebluepagos.androidwallet.connection;

import android.content.Context;

/**
 * Created by ivan on 3/31/15.
 */
public class CoordenadasHandler {

    public static final String COORDENADAS_OK = "COORDENADAS_OK";
    public String   card              = "";
    public String[] coordenadasKey    = new String[2];
    public String[] coordenadasValues = new String[2];
    private Context               uiThread;
    private BaseConnectionHandler connectionHandler;

    public CoordenadasHandler(Context uiThread, BaseConnectionHandler connectionHandler) {
        this.setUiThread(uiThread);
        this.setConnectionHandler(connectionHandler);
    }

    public void askCoordenadas() {
        CoordenadasDialog dialog = this.getDialog();
        dialog.setCancelable(false);
        dialog.show();

        dialog.getEditText0().requestFocus();
    }

    private CoordenadasDialog getDialog() {
        CoordenadasDialog dialog = new CoordenadasDialog(this.getUiThread(), this,
                this.coordenadasKey[0], this.coordenadasKey[1]);
        return dialog;
    }

    public void onAccept(String val0, String val1) {
        this.coordenadasValues[0] = val0;
        this.coordenadasValues[1] = val1;

        connectionHandler.doAfterAskCoordenadas(COORDENADAS_OK);
    }

    public Context getUiThread() {
        return uiThread;
    }

    public void setUiThread(Context uiThread) {
        this.uiThread = uiThread;
    }

    public BaseConnectionHandler getConnectionHandler() {
        return connectionHandler;
    }

    public void setConnectionHandler(BaseConnectionHandler connectionHandler) {
        this.connectionHandler = connectionHandler;
    }
}

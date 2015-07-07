package com.palebluepagos.androidwallet.utilities;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;

import com.palebluepagos.androidwallet.R;
import com.palebluepagos.androidwallet.charge.ChargeDataContract;

import org.apache.http.NameValuePair;

import java.util.Date;
import java.util.List;

public class Utility {

    public final static String CASH_MAIN_CBU  = "";
    public final static String CASH_MAIN_CUIT = "";

    public static final int  MIN_PHONE_LENGTH = 5;
    public static       long REFRESH_TIME     = 5 * 60 * 1000;

    /**
     * Helper method to provide the icon resource id according to the bank code
     *
     * @param bankCode
     * @return resource id for the corresponding icon. -1 if no relation is found.
     */
    public static int getBankIconByCode(int bankCode) {
        switch (bankCode) {
            case Codes.BBVA_BANK_CODE:
                return R.drawable.ic_bbva;

            case Codes.GALICIA_BANK_CODE:
                return R.drawable.ic_galicia;

            default:
                return 0;
        }
    }

    public static Bitmap getCircularBitmapWithWhiteBorder(Bitmap bitmap, int borderWidth) {
        if (bitmap == null || bitmap.isRecycled()) {
            return null;
        }

        final int width = bitmap.getWidth() + borderWidth;
        final int height = bitmap.getHeight() + borderWidth;

        Bitmap canvasBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(shader);

        Canvas canvas = new Canvas(canvasBitmap);
        float radius = width > height ? ((float) height) / 2f : ((float) width) / 2f;
        canvas.drawCircle(width / 2, height / 2, radius, paint);
        paint.setShader(null);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.LTGRAY);
        paint.setStrokeWidth(borderWidth);
        canvas.drawCircle(width / 2, height / 2, radius - borderWidth / 2, paint);
        return canvasBitmap;
    }


    public static String pairsToString(List<NameValuePair> pairs) {
        String output = "";
        for (NameValuePair elem : pairs) {
            output += elem.getName() + "=" + elem.getValue() + "&";
        }
        return output;
    }

    public static String GetMilliseconds() {
        return (new Date()).getTime() + "";
    }

    public static String currSign(int bank, String code) {
        switch (bank) {
            case Codes.BBVA_BANK_CODE:
                if (code.equals(Codes.CURRENCY_ARS)) return "$";

            case Codes.GALICIA_BANK_CODE:
                if (code.equals(Codes.GALICIA_CURRENCY_ARS)) return "$";

            default:
                return "";
        }
    }

    public static String getBankNameByCode(int code) {
        switch (code) {
            case Codes.BBVA_BANK_CODE:
                return "BBVA Banco Frances";

            case Codes.GALICIA_BANK_CODE:
                return "Banco Galicia";

        }

        return "Codigo de banco: " + code;
    }


    /////////////////////////////////////////////////////////////////////////////////////
    /////////////////////// GALICIA WEBSITE METHODS /////////////////////////////////////

    public static String Encode(long E, long N, String oTxtVisiblePin) {
        String encoded_field = "";
        String original_field = oTxtVisiblePin;

        long Enc = E;
        long Mod = N;

        // loop through all single letter of field
        for (int i = 0; i <= original_field.length() - 1; i++) {
            // charCodeAt gives the ASC value of character in position i
            encoded_field = encoded_field + Utility.Multiply((Character.codePointAt(original_field, i)), Enc, Mod) + ",";
        }

        //oTxtVisiblePin.value = "";
        //alert(encoded_field);

        return encoded_field;
    }

    public static long Multiply(long x, long p, long m) {
        long y = 1;
        double i = 0;

        for (i = p; i > 0; i--) {
            while ((i / 2) == (Math.floor((i / 2)))) {
                x = Utility.ModFunction((x * x), m);
                i = (i / 2);
            }
            y = Utility.ModFunction((x * y), m);
        }

        return y;
    }

    public static long ModFunction(long Main, long Modulus) {
        long aux = (Main - ((long) Math.floor((Main / Modulus)) * Modulus));
        return aux;
    }
    /////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////


    public static int getStatusIcon(int status) {
        switch (status) {

            case ChargeDataContract.PENDING:
                return R.drawable.ic_clock;

            case ChargeDataContract.PAYED_UNVERIFYED:
//                return R.drawable.ic_moving_truck; // TODO verify with the Homebanking
                return R.drawable.ic_check_round;

            case ChargeDataContract.ACREDITED:
                return R.drawable.ic_check_round;

            default:
                return R.drawable.ic_clock;
        }
    }

    public static String getStatusName(int status) {
        switch (status) {
            case ChargeDataContract.PENDING:
                return "Pendiente";

            case ChargeDataContract.ACREDITED:
                return "Acreditado";

            case ChargeDataContract.PAYED_UNVERIFYED:
                return "Pagado";
        }

        return "";
    }
}

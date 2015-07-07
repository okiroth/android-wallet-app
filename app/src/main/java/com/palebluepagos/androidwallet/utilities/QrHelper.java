package com.palebluepagos.androidwallet.utilities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.palebluepagos.androidwallet.R;
import com.palebluepagos.androidwallet.models.Charge;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by ivan on 4/13/15.
 */
public class QrHelper {

    private Activity context;
    private Charge   charge;

    public QrHelper(Activity uiThread, Charge charge) {
        this.setUiThread(uiThread);
        this.setCharge(charge);
    }


    public void createQrCode() {
        new WriteQrCode().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public Bitmap createBitmap() {
        WindowManager manager = (WindowManager) getUiThread().getSystemService(getUiThread().WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int width = point.x;
        int height = point.y;
        int smallerDimension = width < height ? width : height;
        smallerDimension = smallerDimension * 3 / 4;

        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix matrix = writer.encode(getCharge().getEncrypted(),
                    BarcodeFormat.QR_CODE,
                    smallerDimension,
                    smallerDimension);

            Bitmap bitmap = toBitmap(matrix);

                /* Not using the saved file for now, maybe we will never save a file  */

            return bitmap;

        } catch (WriterException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Writes the given Matrix on a new Bitmap object.
     *
     * @param matrix the matrix to write.
     * @return the new {@link Bitmap}-object.
     */
    public Bitmap toBitmap(BitMatrix matrix) {
        int height = matrix.getHeight();
        int width = matrix.getWidth();

        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bmp.setPixel(x, y, matrix.get(x, y) ? Color.BLACK : Color.WHITE);
            }
        }

        return bmp;
    }

    public String storeBitmap(Bitmap bitmap) {
        File sdcard = Environment.getExternalStorageDirectory();
        File myFile = null;
        FileOutputStream out = null;
        try {
            myFile = new File(sdcard, "qrcode.jpg");
            out = new FileOutputStream(myFile);
            boolean success = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            if (success) {
                Log.d("QR", "File saved");
            } else {
                Log.d("QR", "File NOT saved");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (out != null) try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return myFile.getAbsolutePath();
    }

    public Charge getCharge() {
        return charge;
    }

    public void setCharge(Charge charge) {
        this.charge = charge;
    }

    public Activity getUiThread() {
        return context;
    }

    public void setUiThread(Activity context) {
        this.context = context;
    }

    private class WriteQrCode extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {

            //Find screen size
            WindowManager manager = (WindowManager) getUiThread().getSystemService(getUiThread().WINDOW_SERVICE);
            Display display = manager.getDefaultDisplay();
            Point point = new Point();
            display.getSize(point);
            int width = point.x;
            int height = point.y;
            int smallerDimension = width < height ? width : height;
            smallerDimension = smallerDimension * 3 / 4;

            QRCodeWriter writer = new QRCodeWriter();
            try {
                BitMatrix matrix = writer.encode(getCharge().getEncrypted(),
                        BarcodeFormat.QR_CODE,
                        smallerDimension,
                        smallerDimension);

                Bitmap bitmap = toBitmap(matrix);

                /* Not using the saved file for now, maybe we will never save a file  */

                return bitmap;

            } catch (WriterException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            ImageView jpgView = (ImageView) getUiThread().findViewById(R.id.charge_new_qr_code);
            jpgView.setImageBitmap(bitmap);
            getUiThread().findViewById(R.id.share_qr_button).setVisibility(View.VISIBLE);
        }
    }
}

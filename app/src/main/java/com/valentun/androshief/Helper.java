package com.valentun.androshief;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import java.io.ByteArrayOutputStream;

public class Helper {

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

//    ----------Methods, working with the images-----------------------------

    public static RoundedBitmapDrawable RoundBitmap(Resources res, Bitmap bmp) {
        RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(res, bmp);
        drawable.setCircular(true);
        return  drawable;
    }

    public static RoundedBitmapDrawable getCroppedBitMap(Bitmap bitmap, DisplayMetrics dm, Resources res) {
        Bitmap bmp = ThumbnailUtils.extractThumbnail(bitmap, (int) (80 * dm.density), (int) (80 * dm.density));
        return RoundBitmap(res, bmp);
    }

    public static String encodeBitMap(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return encoded;
    }

    public static Bitmap decodeBitMap(String stringImage) {
        byte[] decodedBytes = Base64.decode(stringImage, 0);
        Bitmap bmp = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        return bmp;
    }

    //-------------------------------------------------------------------------

    public static DisplayMetrics getDisplayMetrics(Activity activity) {
        WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();


        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        return dm;
    }
}

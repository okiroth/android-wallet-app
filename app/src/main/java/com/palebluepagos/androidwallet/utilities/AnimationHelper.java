package com.palebluepagos.androidwallet.utilities;

import android.view.View;
import android.view.animation.TranslateAnimation;

/**
 * Created by ivan on 3/23/15.
 */
public class AnimationHelper {

    // To animate view slide out from top to bottom
    public static void hideToBottom(View view) {
        view.bringToFront();
        TranslateAnimation animate = new TranslateAnimation(0, 0, 0, view.getHeight());
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        view.setVisibility(View.GONE);
    }

    // To animate view slide out from bottom to top
    public static void hideToTop(View view) {
        view.bringToFront();
        TranslateAnimation animate = new TranslateAnimation(0, 0, 0, -view.getHeight());
        animate.setDuration(500);
        animate.setFillAfter(false);
        view.startAnimation(animate);
        view.setVisibility(View.GONE);
    }

    public static void showToBottom(View view) {
        TranslateAnimation animate = new TranslateAnimation(0, 0, -view.getHeight(), 0);
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        view.setVisibility(View.VISIBLE);
    }

    public static void hideToLeft(View view) {
        view.bringToFront();
        TranslateAnimation animate = new TranslateAnimation(0, -view.getWidth(), 0, 0);
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        view.setVisibility(View.GONE);
    }

    public static void showFromRight(View view, int width) {
        view.bringToFront();
        TranslateAnimation animate = new TranslateAnimation(-width, 0, 0, 0);
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        view.setVisibility(View.VISIBLE);
    }

}

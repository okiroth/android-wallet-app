package com.palebluepagos.androidwallet.connection.parser;

import android.text.Html;

/**
 * Created by ivan on 3/20/15.
 */
public class BaseParser {

    public static String getInnerHtml(String html) {
        return String.valueOf(Html.fromHtml(html));
    }

}

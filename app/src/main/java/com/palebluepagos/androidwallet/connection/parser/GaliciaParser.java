package com.palebluepagos.androidwallet.connection.parser;

import android.text.Html;
import android.util.Log;

import com.palebluepagos.androidwallet.models.Account;
import com.palebluepagos.androidwallet.utilities.Codes;
import com.palebluepagos.androidwallet.utilities.Utility;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

/**
 * Created by ivan on 3/20/15.
 */
public class GaliciaParser extends BaseParser {

    public static final  String ERROR_PARSING = "ERROR GALICIA PARSE";
    private static final String PARSE_OK      = "PARSE_OK";


    public static String getEncPin(String body, String pin) {
        String aux = body.substring(body.indexOf("document.form1,"));
        aux = aux.substring(15, aux.indexOf(",document.form1.txtPin"));

        String[] tokens = aux.split(",");

        return Utility.Encode(Long.parseLong(tokens[0]), Long.parseLong(tokens[1]), pin);
    }


    /**
     * Parses the accounts from the home banking index page
     * and saves to DB
     *
     * @param data: resumen page
     */
    public static String parseAccountsData(String data) {
        try {

            String tableId = "id=\"tableCuentasBancarias\"";
            String endTable = "</table>";
            String rowTag = "<tr";

            String table = data.substring(data.indexOf(tableId));
            table = table.substring(0, table.indexOf(endTable));

            String[] tableArr = table.split(rowTag);

            int INFO_STARTS_IN_ROW = 2;

            int POSITION_TYPE = 2;
            int POSITION_NUMBER = 5;
            int POSITION_AMOUNT = 8;

            for (int i = INFO_STARTS_IN_ROW; i < tableArr.length; i++) {
                String[] tr = tableArr[i].split("\n");

                String accountNumber = getInnerHtml(tr[POSITION_NUMBER]);
                String accountType = getInnerHtml(tr[POSITION_TYPE]);

                List<Account> list = Select.from(Account.class)
                        .where(Condition.prop("bankcode").eq(Codes.GALICIA_BANK_CODE))
                        .where(Condition.prop("accountnumber").eq(accountNumber))
                        .list();

                Account account;
                if (list.size() != 1) {
                    account = new Account(i - INFO_STARTS_IN_ROW); // pageOrder starts at 0
                } else {
                    account = list.get(0);
                }

                if (accountType.contains(Codes.GALICIA_CRURENCY_USD_SIGN) == false) {
                    if (accountType.contains(" en")) accountType = accountType.replace(" en ", "");
                    if (accountType.contains("$")) accountType = accountType.replace("$", "");

                    account.accounttype = accountType.trim();
                    account.accountnumber = accountNumber;
                    account.amount = getInnerHtml(tr[POSITION_AMOUNT]);
                    account.bankcode = Codes.GALICIA_BANK_CODE;
                    account.currency = Codes.GALICIA_CURRENCY_ARS;

                    account.milli = System.currentTimeMillis();
                    account.save();
                }
            }

            return PARSE_OK;

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("PARSER", data);
        }

        return ERROR_PARSING;
    }

    /**
     * Useful for a single line of html data
     * Ex: <p><h1>Here is the text you will get</h1></p>
     *
     * @param html single line
     * @return String
     */
    public static String getInnerHtml(String html) {
        return String.valueOf(Html.fromHtml(html));
    }


    public static String parseCBU(String cbuPage) {
        List<Account> accounts = Select.from(Account.class)
                .where(Condition.prop("bankcode").eq(Codes.GALICIA_BANK_CODE))
                .list();

        String[] arr = cbuPage.split("\n");

        try {
            for (Account account : accounts) {
                for (int i = 0; i < arr.length; i++) {
                    String line = arr[i];
                    if (line.indexOf(account.accountnumber) != -1) {
                        String cbu = getInnerHtml(arr[i + 1]);
                        cbu = cbu.replace("&nbsp;", "").replace("-", "").replaceAll(" ", "");
                        account.cbu = cbu.substring(0, 22);

                        String regex = "\\d+";
                        if (account.cbu.matches(regex) && account.cbu.length() == 22) {
                            account.save();
                        } else {
                            return ERROR_PARSING;
                        }
                    }
                }
            }

            return PARSE_OK;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ERROR_PARSING;
    }


    public static String parseCoordenadas(String page, String card, String[] coordenadasKeys) {
        String lastDigitsUniqueRecon = "...";

        if (page.indexOf(lastDigitsUniqueRecon) < 0) return ERROR_PARSING;

        card = page.substring(page.indexOf(lastDigitsUniqueRecon) + 3,
                page.indexOf(lastDigitsUniqueRecon) + 7);

        String coordsRecon = "align=\"right\"><span class=\"redBoldFont2\">";
        String coord_1 = (page.split(coordsRecon)[1]).substring(0, 2);
        String coord_2 = (page.split(coordsRecon)[2]).substring(0, 2);

        coordenadasKeys[0] = coord_1;
        coordenadasKeys[1] = coord_2;

        return PARSE_OK;
    }

    public static String[] parsePivots(String data) {
        String anchor1 = "hiCoordenada1.value=Encode(document.form1,";
        String anchor2 = "document.form1.txtCoordenada1)";
        int h1Start = data.indexOf(anchor1);
        int h1Stop = data.indexOf(anchor2);

        String h1 = data.substring(h1Start + anchor1.length(), h1Stop - 1);

        int h2Start = data.indexOf("hiCoordenada2.value=Encode(document.form1,");
        int h2Stop = data.indexOf("document.form1.txtCoordenada2)");

        String h2 = data.substring(h2Start + anchor1.length(), h2Stop - 1);

        String[] pivots = new String[3];

        pivots[0] = h1.split(",")[0];
        pivots[1] = h1.split(",")[1];
        pivots[2] = h2;

        return pivots;
    }
}

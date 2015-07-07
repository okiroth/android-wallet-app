package com.palebluepagos.androidwallet.connection.parser;

import android.util.Log;

import com.palebluepagos.androidwallet.models.Account;
import com.palebluepagos.androidwallet.utilities.Codes;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by ivan on 3/20/15.
 */
public class BbvaParser extends BaseParser {

    public static final  String ERROR_PARSING = "ERROR BBVA PARSE";
    private static final String PARSE_OK      = "PARSE_OK";

    /**
     * Parses the accounts from the home banking index page
     * and saves to DB
     *
     * @param indexPage
     */
    public static String parseAccountsData(String indexPage) {
        try {
            String tableElementId = "id=\"dataCuenta";

            String tableElementStartKey = "<tbody>";
            String tableElementEndKey = "<script";

            String tableStr = indexPage.substring(indexPage.indexOf(tableElementId));
            tableStr = tableStr.substring(tableStr.indexOf(tableElementStartKey) + tableElementStartKey.length(),
                    tableStr.indexOf(tableElementEndKey)).trim();
            String tableRowKey = "<tr";
            String[] tableArr = tableStr.split(tableRowKey);

            String newLine = "\n";

            int INFO_STARTS_IN_ROW = 1;

            int POSITION_TYPE = 1;
            int POSITION_CURRENCY = 2;
            int POSITION_NUMBER = 3;
            int POSITION_AMOUNT = 8;

            for (int i = INFO_STARTS_IN_ROW; i < tableArr.length; i++) {
                String[] accountArr = tableArr[i].split(newLine);

                String accountNumber = getInnerHtml(accountArr[POSITION_NUMBER]);
                String accountCurrency = getInnerHtml(accountArr[POSITION_CURRENCY]);

                List<Account> list = Select.from(Account.class)
                        .where(Condition.prop("bankcode").eq(Codes.BBVA_BANK_CODE))
                        .where(Condition.prop("accountnumber").eq(accountNumber))
                        .list();

                Account account;
                if (list.size() != 1) {
                    account = new Account(i - INFO_STARTS_IN_ROW); // pageOrder starts at 0
                } else {
                    account = list.get(0);
                }

                if (accountCurrency.equals(Codes.CRURENCY_PESOS_SIGN)) {
                    account.bankcode = Codes.BBVA_BANK_CODE;
                    account.currency = Codes.CURRENCY_ARS;
                    account.accountnumber = accountNumber;
                    account.amount = getInnerHtml(accountArr[POSITION_AMOUNT]);
                    account.accounttype = getInnerHtml(accountArr[POSITION_TYPE]);

                    account.milli = System.currentTimeMillis();
                    account.save();
                }
            }
        } catch (Exception e) {
            return ERROR_PARSING;
        }

        return PARSE_OK;
    }


    /**
     * Each CBU has to be parsed from a diferent page,
     * in the index page those links are clearly displayed
     *
     * @param indexPage the index page
     * @return the List of links for each account
     */
    public static List<String> parseAccountDetailLinks(String indexPage) {
        List<String> links = new LinkedList<>();

        String accountDetailUrl = "cuentasMovimientosJson.do";
        String variableToFind = "?method";

        String[] dataArr = indexPage.split(accountDetailUrl);

        for (int i = 0; i < dataArr.length; i++) {
            String line = dataArr[i];
            if (line.indexOf(variableToFind) == 0) {
                line = line.substring(0, line.indexOf("'"));
                links.add(line);
            }
        }

        return links;
    }


    /**
     * Parses the CBU from the accounts detail page
     * also suses the pageOrder to save that CBU with its
     * account.
     *
     * @param cbuPage
     * @param pageOrder
     * @return
     */
    public static String parseCBU(String cbuPage, int pageOrder) {
        int cbuSize = 22;
        int count = 0;
        int pos = 0;
        String cbu = "";

        for (char c : cbuPage.toCharArray()) {
            if (c >= '0' && c <= '9') {
                count++;
                cbu += c;
            } else {
                count = 0;
                cbu = "";
            }
            pos++;

            if (count == cbuSize) break;
        }

        // no CBU was found
        if (pos == cbuPage.length()) return ERROR_PARSING;


        List<Account> list = Select.from(Account.class)
                .where(Condition.prop("bankcode").eq(Codes.BBVA_BANK_CODE))
                .where(Condition.prop("pageorder").eq(pageOrder))
                .list();

        if (list.size() == 1) {
            list.get(0).cbu = cbu;
            list.get(0).save();
        }

        return PARSE_OK;
    }


    public static String parseTargetNumber(String data) {
        try {
            String key = "transfOtrosIndSD.do?target=";
            String[] dataArr = data.split(">");
            for (int i = 0; i < dataArr.length; i++) {
                String line = dataArr[i].trim();

                if (line.indexOf(key) != -1) {
                    String aux = line.substring(line.indexOf(key) + key.length());
                    int point = aux.contains("'") ? aux.indexOf("'") : aux.indexOf("\"");
                    String target = aux.substring(0, point);
                    return target;
                }
            }
        } catch (Exception e) {
            Log.d(ERROR_PARSING, "targetNumber: " + e.getMessage());
        }

        return ERROR_PARSING;
    }

    public static String parseUrlReturn(String data) {
        try {
            String aux = data.substring(data.indexOf("\"urlReturn\""));
            return aux.substring(aux.indexOf("value=") + 7, aux.indexOf(">") - 1);
        } catch (Exception e) {
            Log.d(ERROR_PARSING, "urlReturn: " + e.getMessage());
        }

        return ERROR_PARSING;
    }

    public static String parseUrlVolver(String data) {
        try {
            String aux = data.substring(data.indexOf("\"urlVolver\""));
            return aux.substring(aux.indexOf("value=") + 7, aux.indexOf(">") - 1);
        } catch (Exception e) {
            Log.d(ERROR_PARSING, "urlVolver: " + e.getMessage());
        }

        return ERROR_PARSING;
    }


}

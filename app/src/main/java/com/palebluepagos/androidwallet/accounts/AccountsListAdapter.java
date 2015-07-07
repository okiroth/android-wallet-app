package com.palebluepagos.androidwallet.accounts;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.palebluepagos.androidwallet.R;
import com.palebluepagos.androidwallet.models.Account;
import com.palebluepagos.androidwallet.utilities.Utility;

/**
 * {@link AccountsListAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class AccountsListAdapter extends ArrayAdapter {

    private Context context;

    // Init always with an empty list, use callbacks to update
    public AccountsListAdapter(Context context) {
        super(context, 0);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Account account = (Account) getItem(position);

        ViewHolder viewHolder; // view lookup cache stored in tag

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_account, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Populate the data into the template view using the data object
        viewHolder.amount.setText(Utility.currSign(account.bankcode, account.currency) + " " + account.amount);
        viewHolder.name.setText(account.accounttype + " | " + account.accountnumber);

        int img = Utility.getBankIconByCode(account.bankcode);
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), img);
        Bitmap bm = Utility.getCircularBitmapWithWhiteBorder(icon, 2);

        viewHolder.icon.setImageBitmap(bm);

        // Return the completed view to render on screen
        return convertView;
    }

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        public final ImageView icon;
        public final TextView  amount;
        public final TextView  name;

        public ViewHolder(View view) {
            icon = (ImageView) view.findViewById(R.id.list_item_bank_icon);
            amount = (TextView) view.findViewById(R.id.list_item_account_amount);
            name = (TextView) view.findViewById(R.id.list_item_account_name);
        }
    }
}
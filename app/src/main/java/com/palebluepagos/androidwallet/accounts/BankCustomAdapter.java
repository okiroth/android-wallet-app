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
import com.palebluepagos.androidwallet.models.Bank;
import com.palebluepagos.androidwallet.utilities.Utility;

/**
 * {@link com.palebluepagos.androidwallet.accounts.BankCustomAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class BankCustomAdapter extends ArrayAdapter {

    private Context context;

    // Init always with an empty list, use callbacks to update
    public BankCustomAdapter(Context context) {
        super(context, 0);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Bank bank = (Bank) getItem(position);

        ViewHolder viewHolder; // view lookup cache stored in tag

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_bank, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.name.setText(Utility.getBankNameByCode(bank.getCode()));

        int img = Utility.getBankIconByCode(bank.getCode());
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), img);
        Bitmap bm = Utility.getCircularBitmapWithWhiteBorder(icon, 2);

        viewHolder.icon.setImageBitmap(bm);

        return convertView;
    }

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        public final ImageView icon;
        public final ImageView trash;
        public final TextView  name;

        public ViewHolder(View view) {
            icon = (ImageView) view.findViewById(R.id.list_item_bank_logo);
            trash = (ImageView) view.findViewById(R.id.ic_remove_bank);
            name = (TextView) view.findViewById(R.id.list_item_bank_name);
        }
    }
}
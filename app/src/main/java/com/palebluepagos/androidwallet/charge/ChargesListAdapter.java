package com.palebluepagos.androidwallet.charge;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.palebluepagos.androidwallet.R;
import com.palebluepagos.androidwallet.models.Charge;
import com.palebluepagos.androidwallet.utilities.TimeUtils;
import com.palebluepagos.androidwallet.utilities.Utility;

public class ChargesListAdapter extends ArrayAdapter {

    private Context context;

    // Init always with an empty list, use callbacks to update
    public ChargesListAdapter(Context context) {
        super(context, 0);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Charge charge = (Charge) getItem(position);

        ViewHolder viewHolder; // view lookup cache stored in tag

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_charge, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.amount.setText("$ " + charge.getAmount());
        viewHolder.desc.setText(charge.getDesc());

        viewHolder.ago.setText(TimeUtils.getSimpleDate(charge.getDate()));

        viewHolder.status.setText(Utility.getStatusName(charge.getStatus()));
        viewHolder.icon.setImageResource(Utility.getStatusIcon(charge.getStatus()));

        return convertView;
    }

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        public final ImageView icon;
        public final TextView  amount;
        public final TextView  desc;
        public final TextView  status;
        public final TextView  ago;

        public ViewHolder(View view) {
            icon = (ImageView) view.findViewById(R.id.list_item_status_icon);
            amount = (TextView) view.findViewById(R.id.list_item_charge_amount);
            desc = (TextView) view.findViewById(R.id.list_item_charge_desc);
            status = (TextView) view.findViewById(R.id.list_item_charge_status);
            ago = (TextView) view.findViewById(R.id.list_item_charge_time_ago);
        }
    }
}
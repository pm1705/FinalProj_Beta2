package com.example.finalproj_beta2;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CostumeAdapter extends BaseAdapter {

    /**
     * @author		Paz Malul <malul.paz@gmail.com>

     * A costume adapter for the request lists.
     */

    Context context;
    ArrayList<String> nameList, symbols, copies, settings, due_dates;
    ArrayList<Boolean> urgent;
    LayoutInflater inflater;

    public CostumeAdapter(Context applicationContext, ArrayList<String> nameList, ArrayList<String> symbols, ArrayList<String> copies, ArrayList<String> settings, ArrayList<String> due_dates, ArrayList<Boolean> urgent) {
        this.context = applicationContext;
        this.nameList = nameList;
        this.symbols = symbols;
        this.copies = copies;
        this.settings = settings;
        this.due_dates = due_dates;
        this.urgent = urgent;
        inflater = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return nameList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.custom_lv_request, null);

        TextView name = (TextView) view.findViewById(R.id.tV);
        TextView copy = (TextView) view.findViewById(R.id.copies);
        TextView setting = (TextView) view.findViewById(R.id.setting);
        TextView due_date = (TextView) view.findViewById(R.id.due_date);
        ImageView symbol = (ImageView) view.findViewById(R.id.iV);
        LinearLayout req_back = (LinearLayout) view.findViewById(R.id.req_back);

        name.setText(nameList.get(i));
        copy.setText(copies.get(i));
        setting.setText(settings.get(i));
        due_date.setText(due_dates.get(i));

        if (urgent.get(i)){
            due_date.setTextColor(Color.parseColor("#535353"));
            due_date.setAllCaps(true);
            due_date.setTypeface(null, Typeface.BOLD);
            due_date.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        }

        Picasso.get().load(symbols.get(i)).into(symbol);
        return view;
    }
}


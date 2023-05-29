package com.example.finalproj_beta2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CostumeAdapterUser extends BaseAdapter {

    /**
     * @author		Paz Malul <malul.paz@gmail.com>

     * A costume adapter for the user lists.
     */

    Context context;
    ArrayList<String> cityList;
    ArrayList<String> symbols;
    LayoutInflater inflter;

    public CostumeAdapterUser(Context applicationContext, ArrayList<String> cityList, ArrayList<String> symbols) {
        this.context = applicationContext;
        this.cityList = cityList;
        this.symbols = symbols;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return cityList.size();
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
        view = inflter.inflate(R.layout.costume_user_layout, null);
        TextView city = (TextView) view.findViewById(R.id.tV);
        ImageView symbol = (ImageView) view.findViewById(R.id.iV);
        city.setText(cityList.get(i));
        Picasso.get().load(symbols.get(i)).into(symbol);
        System.out.println(city.getText());
        return view;
    }
}

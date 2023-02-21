package com.example.howtheymove;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomeBaseAdaptor extends BaseAdapter{

    Context context;
    String listIcon[];
    int listImage[];
    LayoutInflater inflater;

    public CustomeBaseAdaptor(Context ctx, String listIcon[], int[] listImage){
        this.context = ctx;
        this.listIcon = listIcon;
        this.listImage = listImage;
        inflater = LayoutInflater.from(ctx);
    }

    @Override
    public int getCount() {
        return listIcon.length;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.activity_custom_list, null);
        TextView textView = (TextView) convertView.findViewById(R.id.textView);
        ImageView iconImg = (ImageView)  convertView.findViewById(R.id.imageView);
        textView.setText(listIcon[position]);
        iconImg.setImageResource(listImage[position]);
        return convertView;
    }
}

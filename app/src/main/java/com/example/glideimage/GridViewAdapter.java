package com.example.glideimage;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class GridViewAdapter extends BaseAdapter {
    private final ArrayList<Pics> picList;
    private final LayoutInflater inflater;

    public GridViewAdapter(Context context, ArrayList<Pics> data) {
        super();
        this.picList = data;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return picList.size();
    }

    @Override
    public Object getItem(int position) {
        return picList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.pic_item, null);
            holder.imageView = convertView.findViewById(R.id.image);
            holder.border = convertView.findViewById(R.id.border);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(200, 200);
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        if (picList.get(position).bd)
            holder.border.setBackgroundColor(Color.parseColor("#888888"));
        else
            holder.border.setBackgroundColor(Color.parseColor("#E8E8E8"));
        holder.imageView.setLayoutParams(layoutParams);
        holder.imageView.setImageBitmap(picList.get(position).bitmap);
        return convertView;
    }

    public static final class ViewHolder {
        public ImageView imageView;
        public LinearLayout border;
    }
}

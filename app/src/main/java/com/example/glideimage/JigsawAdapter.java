package com.example.glideimage;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class JigsawAdapter extends BaseAdapter {
    private final ArrayList<JigsawItem> data;
    private final LayoutInflater inflater;
    private final int type;

    public JigsawAdapter(Context context, ArrayList<JigsawItem> data, int type) {
        this.data = data;
        this.type = type;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View covertView, ViewGroup parent) {
        ViewHolder holder;
        if (covertView == null) {
            holder = new ViewHolder();
            covertView = inflater.inflate(R.layout.jigsaw_item, null);
            holder.imageView = covertView.findViewById(R.id.image);
            covertView.setTag(holder);
        } else {
            holder = (ViewHolder) covertView.getTag();
        }
        holder.imageView.setLayoutParams(new LinearLayout.LayoutParams(840 / type, 840 / type));
        holder.imageView.setImageBitmap(data.get(position).itemBitmap);
        return covertView;
    }

    public static final class ViewHolder {
        public ImageView imageView;
    }

    public void update(int index, GridView gv) {
        View view = gv.getChildAt(index);
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.imageView = view.findViewById(R.id.image);
        holder.imageView.setImageBitmap(data.get(index).itemBitmap);
    }

    public void update2(int index, GridView gv, Bitmap blankBitmap) {
        View view = gv.getChildAt(index);
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.imageView = view.findViewById(R.id.image);
        holder.imageView.setImageBitmap(blankBitmap);
    }
}

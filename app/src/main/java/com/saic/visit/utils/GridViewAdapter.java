package com.saic.visit.utils;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.saic.visit.R;

import java.util.List;

/**
 * Created by 1 on 2017/3/15.
 */

public class GridViewAdapter extends BaseAdapter {

    private List<String> items;
    private Context context;

    public GridViewAdapter(List<String> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_gridview_content, null);
            holder.value = (ImageView) convertView.findViewById(R.id.lastImageShow);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        // BitmapFactory.Options options = new BitmapFactory.Options();
        //options.inSampleSize = 1;//直接设置它的压缩率，表示1/4
        // holder.value.setImageBitmap(BitmapFactory.decodeFile(items.get(position), options));
        holder.value.setImageURI(Uri.parse(items.get(position)));
        // AlxImageLoader alxImageLoader = new AlxImageLoader(context);
        // alxImageLoader.setAsyncBitmapFromSD(items.get(position),holder.value,200,true,true,false);
        // Picasso.with(context).load(new File(items.get(position))).into(holder.value);
        return convertView;
    }

    class ViewHolder {
        private ImageView value;
    }
}

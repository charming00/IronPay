package com.charming.ironpay.card;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.charming.ironpay.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by cm on 16/3/3.
 */
public class CardlistAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
//    private String[] nameStrings= {"a", "b"};


    private List<Map<String, Object>> mData;

    CardlistAdapter(Context context, List<Map<String, Object>> contentList) {
        mData = contentList;
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
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
            convertView = mLayoutInflater.inflate(R.layout.vlist, null);
            holder.img = (ImageView) convertView.findViewById(R.id.img);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.img.setBackgroundResource((Integer) mData.get(position).get("img"));
        holder.title.setText((String) mData.get(position).get("title"));

        return convertView;
    }

    void refeshData(List<Map<String, Object>> contentList) {
        mData = contentList;
        notifyDataSetChanged();
    }

    class ViewHolder {
        TextView title;
        ImageView img;
    }
}

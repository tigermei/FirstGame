package com.example.firstgame;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class BlockAdapter extends BaseAdapter {
    private Context context;

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    public  class ViewHolder{
        public TextView colorTxt;
    }

    class ItemData{
        private String itemRes;
        public ItemData(String itemRes){
            this.itemRes = itemRes;
        }
    }
}

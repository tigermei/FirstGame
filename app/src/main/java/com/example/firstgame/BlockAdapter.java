package com.example.firstgame;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class BlockAdapter extends BaseAdapter {
    private Context context;

    private List<ItemData> dataList = new ArrayList<ItemData>();

    public BlockAdapter(ItemData []list, Context context){
        this.context = context;
        for(ItemData item:list){
            dataList.add(item);
        }
    }

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
        ViewHolder view = null;
        if(null == convertView){
            view = new ViewHolder();

            convertView = (TextView) LayoutInflater.from(this.context).inflate(R.layout.block_item, null);
            view.colorTxt = convertView.findViewById(R.id.item);

            convertView.setTag(view);
        } else {
            view = (ViewHolder) convertView.getTag();
        }

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

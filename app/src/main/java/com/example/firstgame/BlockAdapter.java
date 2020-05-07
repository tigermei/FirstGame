package com.example.firstgame;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class BlockAdapter extends BaseAdapter {
    public final static String tag = "BlockAdapter";
    private Context context;

    //GridView每个item的数据
    private List<ItemData> dataList = new ArrayList<ItemData>();
    //移动的图形每个item的数据
    private List<ItemData> moveShapeDataList = new ArrayList<ItemData>();
    private int nLeftPosition = (GameActivity.BLOCK_COLUMN_NUM/2)-1;
    private int nTopPosition = 0;

    public BlockAdapter(ItemData []list, Context context){
        super();
        this.context = context;
        for(ItemData item:list){
            dataList.add(item);
        }
    }

    @Override
    public int getCount() {
        if(null != dataList){
            return dataList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if(null != dataList){
            return dataList.get(position);
        }
        return  null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder view = null;
        if(null == convertView){
            view = new ViewHolder();

            convertView = LayoutInflater.from(this.context).inflate(R.layout.block_item, null);
            view.colorTxt = convertView.findViewById(R.id.item);

            convertView.setTag(view);
        } else {
            view = (ViewHolder) convertView.getTag();
        }

        ItemData itemData = dataList.get(position);
        if(!itemData.bEmpty){
            //
            //@TODO 这里填充颜色
            view.colorTxt.setBackground(context.getResources().getDrawable(itemData.itemResId));
        }

        return convertView;
    }

    public  class ViewHolder{
        public TextView colorTxt;
    }

    static class ItemData{
        private int itemResId;
        private boolean bEmpty;
        public ItemData(int itemRes, boolean bEmpty){
            this.itemResId = itemRes;
            this.bEmpty = bEmpty;
        }
    }

    public List<ItemData> getListData(){
        return dataList;
    }

    public void onDestroy(){
        if(null != dataList){
            dataList.clear();
            dataList = null;
        }
    }

    public boolean onStart(){
        if(!moveShapeDataList.isEmpty()){
            Log.e(tag, "game has started, why start again?");
            return false;
        }

        for(int i = 0; i < GameActivity.MINI_BLOCK_COLUMN_NUM; ++i){
            for(int j = 0; j < GameActivity.MINI_BLOCK_ROW_NUM; ++j){

            }
        }

        return true;
    }

}

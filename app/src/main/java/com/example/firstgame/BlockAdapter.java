package com.example.firstgame;

import android.content.ClipData;
import android.content.Context;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;
import android.widget.Toast;


public class BlockAdapter extends BaseAdapter {
    public final static String tag = "BlockAdapter";
    private Context context;

    //GridView每个item的数据
    private List<ItemData> dataList = new ArrayList<ItemData>();
    //中间移动的图形
    private MovingShape movingShape = new MovingShape();
    //定时的任务
    Timer timer = null;
    //保存的分数
    int nScore = 0;

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

        ItemData itemData = null;
        if(!movingShape.isDataListEmpty()){
            itemData = movingShape.getData(position);
        }

        if(null == itemData){
            itemData = dataList.get(position);
        }

        if(!itemData.bEmpty){
            view.colorTxt.setBackground(context.getResources().getDrawable(itemData.itemResId));
        } else {
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

        stopTimer();
    }

    public boolean start(){
        if(!movingShape.isDataListEmpty()){
            Log.e(tag, "game has started, why start again?");
            return false;
        }

        movingShape.restart();
        startTimer();
        notifyDataSetChanged();
        return true;
    }

    public boolean pause(){
        if(!movingShape.isDataListEmpty()){
            Log.e(tag, "game has started, why start again?");
            return false;
        }


        return true;
    }

    public boolean end(){
        if(!movingShape.isDataListEmpty()){
            Log.e(tag, "game has started, why start again?");
            return false;
        }

        stopTimer();

        return true;
    }

    private void saveCurrentShape() {
        //1.保存当前移动的shape到大方格列表中
        for(int j = movingShape.nTopPosition; j < movingShape.nTopPosition + GameActivity.MINI_BLOCK_ROW_NUM; ++j){
            if(j < 0 || GameActivity.BLOCK_ROW_NUM <= j ){
                //如果移动后有方格超出了界面之外，那么这个显然就是非法了，
                Log.e(tag, "error!!, j:"+j);
                continue;
            }
            for(int i = movingShape.nLeftPosition; i < movingShape.nLeftPosition + GameActivity.MINI_BLOCK_COLUMN_NUM; ++i){
                int nRow = j - movingShape.nTopPosition;
                int nColumn = i - movingShape.nLeftPosition;
                ItemData movingShapeData = movingShape.getData(nRow, nColumn);
                if(i < 0 || GameActivity.BLOCK_COLUMN_NUM <= i){
                    //如果移动后有方格超出了界面之外，那么这个显然就是非法了
                    Log.e(tag, "error!!, j:"+j+" i:"+i);
                    continue;
                }

                if(null != movingShapeData && !movingShapeData.bEmpty){
                    //
                    ItemData itemData = dataList.get(j*GameActivity.BLOCK_COLUMN_NUM+i);
                    if(null != itemData && !itemData.bEmpty){
                        Log.e(tag, "error!! 居然没有为空！");
                        //TODO
                        //throw new Exception("程序出现了不可饶恕的错误！");
                    }
                    dataList.set(j*GameActivity.BLOCK_COLUMN_NUM+i, movingShapeData);
                }

            }
        }

        movingShape.clear();
        //2.检查是否可以消掉某一行
        for(int j = 0; j < GameActivity.BLOCK_ROW_NUM; ++j){
            int i = 0;
            for(i = 0; i < GameActivity.BLOCK_COLUMN_NUM; ++i){
                ItemData itemData = dataList.get(j*GameActivity.BLOCK_COLUMN_NUM+i);
                if(itemData.bEmpty){
                    break;
                }
            }

            if(GameActivity.BLOCK_COLUMN_NUM <= i){
                //如果第j行所有方格都被占满了，那么从第j行开始，整体向前移动一行
                moveLine(j);
            }
        }
    }

    private void moveLine(int nRow){
        if(nRow < 0 || GameActivity.BLOCK_COLUMN_NUM <= nRow){
            Log.e(tag, "error!!!, 出现错误 nRow:"+nRow);
        }
        //计算出开始替换的最后一个方格的位置
        int nStart = nRow*GameActivity.BLOCK_COLUMN_NUM-1;
        for(; 0<=nStart; --nStart){
            ItemData item = dataList.get(nStart);
            int nIndex = nStart+GameActivity.BLOCK_COLUMN_NUM;
            dataList.set(nIndex, item);
        }

        //处理最上面一排的数据
        for(int i = 0; i < GameActivity.BLOCK_COLUMN_NUM; ++i){
            BlockAdapter.ItemData item = new BlockAdapter.ItemData(R.color.white, true);
            dataList.set(i, item);
        }
    }

    private void nextShape(){
        movingShape.restart();
    }

    public class MovingShape{

        //移动的图形每个item的数据
        public List<ItemData> moveShapeDataList = new ArrayList<ItemData>();
        public int nLeftPosition = (GameActivity.BLOCK_COLUMN_NUM/2)-1;
        public int nTopPosition = 0;

        public MovingShape(){}

        //判断数组当前的是否有正在移动的图形
        public boolean isDataListEmpty(){
            return moveShapeDataList.isEmpty();
        }

        //初始化原始数组的内容
        public void restart(){
            moveShapeDataList.clear();
            double d = Math.random();
            int random = ((int)(d*10) % Shape.SHAPE_SIZE);
            int [][]array = Shape.shape[random];

            for(int j = 0; j < GameActivity.MINI_BLOCK_ROW_NUM; ++j){
                for(int i = 0; i < GameActivity.MINI_BLOCK_COLUMN_NUM; ++i){
                    if(1 == array[j][i]){
                        BlockAdapter.ItemData item = new BlockAdapter.ItemData(R.color.black, false);
                        moveShapeDataList.add(item);
                    } else {
                        BlockAdapter.ItemData item = new BlockAdapter.ItemData(R.color.white, true);
                        moveShapeDataList.add(item);
                    }
                }
            }

            nLeftPosition = (GameActivity.BLOCK_COLUMN_NUM/2)-1;
            nTopPosition = 0;
        }

        public void pause(){

        }

        public void end(){
            nLeftPosition = (GameActivity.BLOCK_COLUMN_NUM/2)-1;
            nTopPosition = 0;
        }

        public void clear(){
            moveShapeDataList.clear();
            nLeftPosition = (GameActivity.BLOCK_COLUMN_NUM/2)-1;
            nTopPosition = 0;
        }

        public ItemData getData(int position){
            int nLeftPositionTemp = position%GameActivity.BLOCK_COLUMN_NUM;
            int nTopPositionTemp = position/GameActivity.BLOCK_COLUMN_NUM;
            ItemData data = null;
            if(nLeftPosition <= nLeftPositionTemp
                    && nLeftPositionTemp < nLeftPosition + GameActivity.MINI_BLOCK_COLUMN_NUM
                    && nTopPosition <= nTopPositionTemp
                    && nTopPositionTemp < nTopPosition + GameActivity.MINI_BLOCK_ROW_NUM) {
                int i = nLeftPositionTemp - nLeftPosition;
                int nIndex = i + (nTopPositionTemp - nTopPosition)*GameActivity.MINI_BLOCK_COLUMN_NUM;
                data = moveShapeDataList.get(nIndex);
                if(data.bEmpty){
                   data = null;
                }
            }

            return data;
        }

        public ItemData getData(int row, int column){
            ItemData data = null;
            if(0 <= column
                    && column < GameActivity.MINI_BLOCK_COLUMN_NUM
                    && 0 <= row
                    && row < GameActivity.MINI_BLOCK_ROW_NUM) {
                int nIndex = column + row * GameActivity.MINI_BLOCK_COLUMN_NUM;
                data = moveShapeDataList.get(nIndex);
                if(data.bEmpty){
                    data = null;
                }
            }

            return data;
        }

        //判断postion是否在移动的shape里面
        public  boolean isInShape(int position){
            ItemData data = getData(position);
            return  null != data;
        }

        //shape向下移动
        public boolean down(){
            ++nTopPosition;
            if(!checkIfOpValid()){
                --nTopPosition;
                return false;
            }
            notifyDataSetChanged();
            return  true;
        }

        //shape向左移动
        public boolean left(){
            --nLeftPosition;
            if(!checkIfOpValid()){
                ++nLeftPosition;
                return false;
            }
            notifyDataSetChanged();
            return  true;
        }

        //shape向右移动
        public boolean right(){
            ++nLeftPosition;
            if(!checkIfOpValid()){
                --nLeftPosition;
                return false;
            }
            notifyDataSetChanged();
            return  true;
        }
    }


    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            if(!movingShape.isDataListEmpty()){
                movingShape.down();
            }

            if(checkIfCantDown()){
                saveCurrentShape();
            }

            if(checkGameOver()){
                stopTimer();
                Toast.makeText(null, "Game Over!!!",
                        Toast.LENGTH_LONG).show();
            }

            if(checkIfCantDown()){
                nextShape();
            }
        }
    };

    private void startTimer(){
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0);
            }
        }, 0, 800);
    }

    private void stopTimer(){
        if(null != timer){
            timer.cancel();
            timer = null;
        }
    }

    //检查当前这一步操作是否合法
    private boolean checkIfOpValid(){
        for(int j = movingShape.nTopPosition; j < movingShape.nTopPosition + GameActivity.MINI_BLOCK_ROW_NUM; ++j){
            for(int i = movingShape.nLeftPosition; i < movingShape.nLeftPosition + GameActivity.MINI_BLOCK_COLUMN_NUM; ++i){
                int nRow = j - movingShape.nTopPosition;
                int nColumn = i - movingShape.nLeftPosition;
                ItemData movingShapeData = movingShape.getData(nRow, nColumn);
                if(j < 0 || i < 0 || GameActivity.BLOCK_ROW_NUM <= j || GameActivity.BLOCK_COLUMN_NUM <= i){
                    //如果移动后有方格超出了界面之外，那么这个显然就是非法了，直接返回
                    if(null != movingShapeData && !movingShapeData.bEmpty){
                        return false;
                    }
                }

                //
                ItemData itemData = dataList.get(j*GameActivity.BLOCK_COLUMN_NUM+i);
                if(null != itemData){
                    if(!itemData.bEmpty && !movingShapeData.bEmpty){
                        return false;
                    }
                }
            }
        }


        return true;
    }

    private boolean checkGameOver(){
        boolean bOver = false;
        boolean bCantDown = checkIfCantDown();
        if(bCantDown){
            if(0 == movingShape.nLeftPosition){
                bOver = true;
            }
        }

        return false;
    }

    private boolean checkIfCantDown(){
        for(int j = movingShape.nTopPosition; j < movingShape.nTopPosition + GameActivity.MINI_BLOCK_ROW_NUM; ++j){
            if(j < 0 || GameActivity.BLOCK_ROW_NUM <= j ){
                //如果移动后有方格超出了界面之外，那么这个显然就是非法了，
                Log.e(tag, "error!!, j:"+j);
                continue;
            }
            for(int i = movingShape.nLeftPosition; i < movingShape.nLeftPosition + GameActivity.MINI_BLOCK_COLUMN_NUM; ++i){
                int nRow = j - movingShape.nTopPosition;
                int nColumn = i - movingShape.nLeftPosition;
                ItemData movingShapeData = movingShape.getData(nRow, nColumn);
                if(i < 0 || GameActivity.BLOCK_COLUMN_NUM <= i){
                    //如果移动后有方格超出了界面之外，那么这个显然就是非法了
                    Log.e(tag, "error!!, j:"+j+" i:"+i);
                    continue;
                }

                if(null != movingShapeData && !movingShapeData.bEmpty){
                    if(GameActivity.BLOCK_COLUMN_NUM <= j+1){
                        //如果当前移动的方块在最后一排
                        return true;
                    }
                    //
                    ItemData itemData = dataList.get((j+1)*GameActivity.BLOCK_COLUMN_NUM+i);
                    if(null != itemData && !itemData.bEmpty){
                        //如果下方有不可移动的方块，那么移动的方块不能向下再移动了
                        return true;
                    }
                }

            }
        }

        return false;
    }
}

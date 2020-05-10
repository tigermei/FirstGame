package com.example.firstgame;

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

        if(nextShape()){
            startTimer();
            return true;
        }

        return false;
    }

    public boolean left(){
        movingShape.left();
        return true;
    }

    public boolean right(){
        movingShape.right();
        return true;
    }

    public boolean down(){
        movingShape.down();
        return true;
    }

    public boolean change(){
        movingShape.change();
        return true;
    }

    public boolean pause(){
        if(!movingShape.isDataListEmpty()){
            Log.e(tag, "game has started, why start again?");
            return false;
        }

        return true;
    }

    //游戏结束调用
    public boolean end(){
        if(!movingShape.isDataListEmpty()){
            Log.e(tag, "game has started, why start again?");
            return false;
        }

        stopTimer();

        return true;
    }

    //获取主面板的数据，nIndex表示线性的位置
    private ItemData getItemData(int nIndex){
        ItemData data = null;
        if(nIndex < 0 || GameActivity.BLOCK_NUM <= nIndex){
            Log.e(tag, "");
            return null;
        }

        data = dataList.get(nIndex);
        return data;
    }

    //保存当前图形的数据到主游戏面板
    private void saveCurrentShape() {
        Log.v(tag, "saveCurrentShape");
        if(movingShape.isDataListEmpty()){
            Log.e(tag, "数据为空，保存不了，退出！");
            return;
        }

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
                    ItemData itemData = getItemData(j*GameActivity.BLOCK_COLUMN_NUM+i);
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
        int j = GameActivity.BLOCK_ROW_NUM - 1;
        while(0 <= j){
            int i = 0;
            for(i = 0; i < GameActivity.BLOCK_COLUMN_NUM; ++i){
                ItemData itemData = getItemData(j*GameActivity.BLOCK_COLUMN_NUM+i);
                if(null != itemData && !itemData.bEmpty){
                    continue;
                } else {
                    break;
                }
            }

            if(GameActivity.BLOCK_COLUMN_NUM <= i){
                //如果第j行所有方格都被占满了，那么从第j行开始，整体向前移动一行
                moveLine(j);
            } else {
                --j;
            }
        }
    }

    //当一行被填满，消掉
    // 这一行上方所有的方块下移
    private void moveLine(int nRow){
        Log.e(tag, "moveLine!!");

        if(nRow < 0 || GameActivity.BLOCK_ROW_NUM <= nRow){
            Log.e(tag, "error!!!, 出现错误 nRow:"+nRow);
            return;
        }
        //计算出开始替换的最后一个方格的位置
        int nStart = nRow*GameActivity.BLOCK_COLUMN_NUM-1;
        for(; 0<=nStart; --nStart){
            ItemData item = getItemData(nStart);
            int nIndex = nStart+GameActivity.BLOCK_COLUMN_NUM;
            dataList.set(nIndex, item);
        }

        //处理最上面一排的数据
        for(int i = 0; i < GameActivity.BLOCK_COLUMN_NUM; ++i){
            BlockAdapter.ItemData item = new BlockAdapter.ItemData(R.color.white, true);
            dataList.set(i, item);
        }

        notifyDataSetChanged();
    }

    //当一个图形结束后，启动下一个图形的游戏。
    private boolean nextShape(){
        movingShape.restart();
        if(checkGameOver()){
            onGameOver();
            return false;
        }

        notifyDataSetChanged();
        return true;
    }

    public class MovingShape{

        //移动的图形每个item的数据
        public List<ItemData> moveShapeDataList = new ArrayList<ItemData>();
        public int [][] currentShape = null;
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
            int [][]array = Shape.shape[0];
            currentShape = array;

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

        public void pause(){}

        public void end(){
            nLeftPosition = (GameActivity.BLOCK_COLUMN_NUM/2)-1;
            nTopPosition = 0;
        }

        //重新清0数据
        public void clear(){
            moveShapeDataList.clear();
            nLeftPosition = (GameActivity.BLOCK_COLUMN_NUM/2)-1;
            nTopPosition = 0;
        }

        //获取shape中的shape数据对象，position表示在主面板的位置
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

        //获取图形里面的数据对象（row表示行，column表示列）
        public ItemData getData(int row, int column){
            ItemData data = null;
            if(moveShapeDataList.isEmpty()){
                Log.e(tag, "moving Shape Datalist empty");
                return null;
            }
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

        //shape改变形状
        public boolean change(){
            //1.先备份当前的图形形状和数据
            List<ItemData> moveShapeDataListTmp = new ArrayList<ItemData>();
            for(ItemData data :moveShapeDataList){
                moveShapeDataListTmp.add(data);
            }

            int [][]array = new int[GameActivity.MINI_BLOCK_ROW_NUM][GameActivity.MINI_BLOCK_COLUMN_NUM];
            for(int j = 0; j < GameActivity.MINI_BLOCK_ROW_NUM; ++j){
                for(int i = 0; i < GameActivity.MINI_BLOCK_COLUMN_NUM; ++i){
                    array[j][i] = currentShape[j][i];
                }
            }

            //2.将图形的数组旋转90度
            Shape.rotateLeft90Angle(array, GameActivity.MINI_BLOCK_ROW_NUM, GameActivity.MINI_BLOCK_COLUMN_NUM);

            moveShapeDataList.clear();
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

            //3.检查旋转后的图形是否合法
            if(!checkIfOpValid()){
                //还原正在移动的图形形状
                Log.e(tag, "旋转不合法");
                moveShapeDataList = moveShapeDataListTmp;
                return false;
            } else {
                Log.i(tag, "旋转成功");
                currentShape = array;
                notifyDataSetChanged();
            }

            return  true;
        }
    }

    //定时的任务回抛到主线程
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            if(!movingShape.isDataListEmpty()){
                movingShape.down();
            }

            boolean bCantDown = checkIfCantDown();
            if(bCantDown){
                saveCurrentShape();
                nextShape();
            }
        }
    };

    //启动定时计时器
    private void startTimer(){
        Log.i(tag, "startTimer");
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0);
            }
        }, 0, 1000);
    }

    //停止定时计时器
    private void stopTimer(){
        Log.i(tag, "stopTimer");
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
                ItemData itemData = getItemData(j*GameActivity.BLOCK_COLUMN_NUM+i);
                if(null != itemData && null != movingShapeData){
                    if(!itemData.bEmpty && !movingShapeData.bEmpty){
                        return false;
                    }
                }
            }
        }


        return true;
    }

    //当游戏结束的时候触发
    private boolean onGameOver(){
        stopTimer();
        Toast.makeText(context, "Game Over!!!",
                Toast.LENGTH_LONG).show();

        notifyDataSetChanged();
        return true;
    }

    //检查游戏是否结束
    private boolean checkGameOver(){
        Log.i(tag, "checkGameOver");
        boolean bOver = false;
        boolean bCantDown = checkIfCantDown();
        if(bCantDown){
            if(0 == movingShape.nTopPosition){
                bOver = true;
            }
        }

        return bOver;
    }

    //检查是否当前的shape可以向下继续移动
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
                    if(GameActivity.BLOCK_ROW_NUM-1 <= j){
                        //如果当前移动的方块在最后一排
                        return true;
                    }
                    //
                    ItemData itemData = getItemData((j+1)*GameActivity.BLOCK_COLUMN_NUM+i);
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

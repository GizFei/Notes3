package utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.giz.notes3.R;

import java.util.List;

import BmobUtils.SharedBmobObject;
import readDatabase.ReadDatabase;
import readDatabase.ReadDbSchema;
import readDatabase.ReadItem;

/**
 * Created by Giz on 2018/8/17.
 */
public class ReadAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<ReadItem> mList;
    private Context mContext;

    public ReadAdapter(Context context, List<ReadItem> list){
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.tab_read_item, null);
            viewHolder.mTextView = (TextView)convertView.findViewById(R.id.item_movie_tv);
            viewHolder.mImageView = convertView.findViewById(R.id.label_img);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        viewHolder.mTextView.setText(mList.get(position).getName());
        viewHolder.mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MovieDialog movieDialog = new MovieDialog(mContext, mList.get(position));
                movieDialog.show();
            }
        });
        viewHolder.mTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(mContext)
                        .setTitle("确定删除该条看点吗？")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteItem(position);
                            }
                        }).show();
                return true;
            }
        });
        if(mList.get(position).getType().equals(ReadDbSchema.TYPE_VIDEO)){
            viewHolder.mImageView.setImageResource(R.drawable.read_label);
        }else{
            viewHolder.mImageView.setImageResource(R.drawable.book_label);
        }

        return convertView;
    }

    private class ViewHolder{
        TextView mTextView;
        ImageView mImageView;
    }

    public void addItem(ReadItem item){
        mList.add(item);
        ReadDatabase.get(mContext, SharedBmobObject.sUserName).addReadItem(item);
        notifyDataSetChanged();
    }

    public void setReadItemList(List<ReadItem> list){
        mList = list;
    }

    private void deleteItem(int pos){
        ReadItem item = mList.remove(pos);
        ReadDatabase.get(mContext, SharedBmobObject.sUserName).deleteReadItem(item);
        notifyDataSetChanged();
    }
}

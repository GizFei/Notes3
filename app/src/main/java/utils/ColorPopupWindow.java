package utils;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.giz.notes3.R;

import java.util.ArrayList;
import java.util.List;

public class ColorPopupWindow {

    private PopupWindow mPopupWindow;
    private Context mContext;
    private TextView mOkBtn;
    private TextView mCancelBtn;
    private ColorAdapter mAdapter;

    private int selectedColorId;

    public ColorPopupWindow(Context context, final int currentColorId){
        mContext = context;
        selectedColorId = currentColorId;

        View view = LayoutInflater.from(mContext).inflate(R.layout.pop_up_window, null);
        mOkBtn = view.findViewById(R.id.color_ok_btn);
        mCancelBtn = view.findViewById(R.id.color_cancel_btn);
        RecyclerView recyclerView = view.findViewById(R.id.color_rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new ColorAdapter(Theme.getThemeColors());
        recyclerView.setAdapter(mAdapter);

        mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setAnimationStyle(R.style.PopupWindowAnim);

        mOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
            }
        });
        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedColorId = currentColorId;
                mPopupWindow.dismiss();
            }
        });
    }

    public PopupWindow getPopupWindow(){
        return mPopupWindow;
    }

    private class ColorHolder extends RecyclerView.ViewHolder{

        private ImageView mColorImg;
        private ColorItem mColor;

        private ColorHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.color_item, parent, false));

            mColorImg = itemView.findViewById(R.id.color_img);
            mColorImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    selectedColorId = mColor.colorId;
                    mAdapter.resetColor(pos);
                }
            });
        }

        private void bind(ColorItem item){
            mColor = item;
            if(mColor.isSelected){
                mColorImg.setBackground(mContext.getResources().
                        getDrawable(R.drawable.color_item_select));
            }else{
                mColorImg.setBackground(mContext.getResources().
                        getDrawable(R.drawable.color_item_shape));
            }
            GradientDrawable gd = (GradientDrawable)mColorImg.getBackground();
            gd.setColor(mContext.getResources().getColor(mColor.colorId));
        }
    }

    private class ColorAdapter extends RecyclerView.Adapter<ColorHolder>{

        List<ColorItem> mColors;

        private ColorAdapter(List<Integer> colors){
            mColors = new ArrayList<>();
            for(int color : colors){
                ColorItem item = new ColorItem();
                item.colorId = color;
                item.isSelected = (color == selectedColorId);
                mColors.add(item);
            }
        }

        @NonNull
        @Override
        public ColorHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            return new ColorHolder(inflater, viewGroup);
        }

        @Override
        public void onBindViewHolder(@NonNull ColorHolder colorHolder, int i) {
            colorHolder.bind(mColors.get(i));
        }

        @Override
        public int getItemCount() {
            return mColors.size();
        }

        private void resetColor(int pos){
            for(int i = 0; i < mColors.size(); i++){
                mColors.get(i).isSelected = (pos == i);
            }
            notifyDataSetChanged();
        }
    }

    public int getSelectedColorId(){
        return selectedColorId;
    }

    private class ColorItem {
        int colorId;
        boolean isSelected;
    }
}

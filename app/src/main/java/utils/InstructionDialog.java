package utils;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class InstructionDialog extends Dialog {

    public InstructionDialog(@NonNull Context context, int resId) {
        super(context);
        setContentView(resId);

        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        WindowManager wm = (WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        if(getContext().getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE){
            layoutParams.width = dm.heightPixels;
        }else{
            layoutParams.width = dm.widthPixels;
        }
        getWindow().setAttributes(layoutParams);
    }
}

package com.redvolunteer.Utils;

import android.graphics.Color;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;

public class StyleUtils {


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setStatusBarColor(Window window, int color)
    {


        if (color == Color.BLACK && window.getNavigationBarColor() == Color.BLACK) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
        window.setStatusBarColor(color);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setNavigationBarColor(Window window, int color)
    {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setNavigationBarColor(color);
    }


}

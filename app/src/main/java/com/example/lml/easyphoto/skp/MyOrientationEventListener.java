package com.example.lml.easyphoto.skp;

import android.content.Context;
import android.view.OrientationEventListener;


/**
 * Created by 陈忠磊 on 2017/12/5.
 */

public class MyOrientationEventListener extends OrientationEventListener {
    Xuanzhuan xuanzhaun;
    int old = 0;

    public MyOrientationEventListener(Context context) {
        super(context);
    }

    public MyOrientationEventListener(Context context, Xuanzhuan xuanzhaun) {
        super(context);
        this.xuanzhaun = xuanzhaun;
    }

    public MyOrientationEventListener(Context context, int rate) {
        super(context, rate);
    }

    @Override
    public void onOrientationChanged(int orientation) {

        if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
            orientation = old;  //手机平放时，检测不到有效的角度
        }
//只检测是否有四个角度的改变
        if (orientation >= 315 || orientation <= 45) { //0度
            orientation = 0;
        } else if (orientation > 45 && orientation <= 135) { //90度
            orientation = 90;
        } else if (orientation > 135 && orientation <= 225) { //180度
            orientation = 180;
        } else if (orientation > 225 && orientation < 315) { //270度
            orientation = 270;
        } /*else {
            orientation = old;
        }*/
        old = orientation;
        xuanzhaun.xuanzhaun(orientation);
    }

    public interface Xuanzhuan {
        void xuanzhaun(int zhuan);
    }
}

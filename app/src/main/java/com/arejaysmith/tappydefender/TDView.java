package com.arejaysmith.tappydefender;

import android.content.Context;
import android.view.SurfaceView;

/**
 * Created by Urge_Smith on 11/30/16.
 */

public class TDView extends SurfaceView implements Runnable {
    public TDView(Context context) {
        super(context);
    }

    @Override
    public void run() {
        while (playing) {
            
        }
    }
}

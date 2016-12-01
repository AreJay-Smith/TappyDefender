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

    volatile boolean playing;
    Thread gameThread = null;

    @Override
    public void run() {
        while (playing) {
            update();
            draw();
            control();
        }

    }

    private void update() {

    }

    private void draw() {

    }

    private void control() {

    }

    public void pause() {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
        }
    }

    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }
}

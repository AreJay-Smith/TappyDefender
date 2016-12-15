package com.arejaysmith.tappydefender;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

/**
 * Created by Urge_Smith on 11/30/16.
 */

public class TDView extends SurfaceView implements Runnable {

    private float distanceRemaining;
    private long timeTaken;
    private long timeStarted;
    private long fastestTime;

    // Game objects
    private PlayerShip player;
    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder ourHolder;

    public EnemyShip enemy1;
    public EnemyShip enemy2;
    public EnemyShip enemy3;

    public ArrayList<SpaceDust> dustList = new ArrayList<SpaceDust>();


    public TDView(Context context, int x, int y) {
        super(context);

        // Initialize our drawing objects
        ourHolder = getHolder();
        paint = new Paint();

        // Initialize our player
        player = new PlayerShip(context, x, y);

        // Initialize enemy ships
        enemy1 = new EnemyShip(context, x, y);
        enemy2 = new EnemyShip(context, x, y);
        enemy3 = new EnemyShip(context, x, y);

        // create all the space dust
        int numSpecs = 40;

        for (int i = 0; i < numSpecs; i++) {
            SpaceDust spec = new SpaceDust(x,y);
            dustList.add(spec);
        }
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

        // Check for collision
        if(Rect.intersects(player.getHitBox(), enemy1.getHitBox())) {
            enemy1.setX(-150);
        }
        if(Rect.intersects(player.getHitBox(), enemy2.getHitBox())) {
            enemy2.setX(-150);
        }
        if(Rect.intersects(player.getHitBox(), enemy3.getHitBox())) {
            enemy3.setX(-150);
        }

        // Update the player
        player.update();

        // Update the enemies
        enemy1.update(player.getSpeed());
        enemy2.update(player.getSpeed());
        enemy3.update(player.getSpeed());

        // Update space dust
        for (SpaceDust sd : dustList) {
            sd.update(player.getSpeed());
        }
    }

    private void draw() {
        if (ourHolder.getSurface().isValid()) {

            // Lock the area of memory we will be drawing to
            canvas = ourHolder.lockCanvas();

            // Rub out the last frame
            canvas.drawColor(Color.argb(255, 0, 0, 0));

            // Debug
//            paint.setColor(Color.argb(255,255,255,255));
//
//            canvas.drawRect(player.getHitBox().left,
//                    player.getHitBox().top,
//                    player.getHitBox().right,
//                    player.getHitBox().bottom,
//                    paint);
//            canvas.drawRect(enemy1.getHitBox().left,
//                    enemy1.getHitBox().top,
//                    enemy1.getHitBox().right,
//                    enemy1.getHitBox().bottom,
//                    paint);
//            canvas.drawRect(enemy2.getHitBox().left,
//                    enemy2.getHitBox().top,
//                    enemy2.getHitBox().right,
//                    enemy2.getHitBox().bottom,
//                    paint);
//            canvas.drawRect(enemy3.getHitBox().left,
//                    enemy3.getHitBox().top,
//                    enemy3.getHitBox().right,
//                    enemy3.getHitBox().bottom,
//                    paint);

            // Draw the dust
            paint.setColor(Color.argb(255, 255, 255, 255));
            for (SpaceDust sd : dustList) {
                canvas.drawPoint(sd.getX(), sd.getY(), paint);
            }

            // Draw the player
            canvas.drawBitmap(
                    player.getBitmap(),
                    player.getX(),
                    player.getY(),
                    paint);

            // Draw the enemies
            canvas.drawBitmap(enemy1.getBitmap(),
                    enemy1.getX(),
                    enemy1.getY(), paint);
            canvas.drawBitmap(enemy2.getBitmap(),
                    enemy2.getX(),
                    enemy2.getY(), paint);
            canvas.drawBitmap(enemy3.getBitmap(),
                    enemy3.getX(),
                    enemy3.getY(), paint);

            // Draw the hud
            paint.setTextAlign(Paint.Align.LEFT);
            paint.setColor(Color.argb(255, 255, 255, 255));
            paint.setTextSize(25);
            canvas.drawText("Fastest:" + fastestTime + "s", 10, 20, paint);
            canvas.drawText("Distance:" + distanceRemaining / 1000 + " KM", screenX / 3, screenY - 20, paint);
            canvas.drawText("Shield:" + player.getShieldStrength(), 10, screenY - 20, paint);
            canvas.drawText("Speed: " + player.getSpeed() * 60 + " MPS", (screenX / 3) * 2, screenY - 20, paint);

            // Unlock and draw the scene
            ourHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void control() {

        try {
            gameThread.sleep(17);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_UP:
                player.stopBoosting();
                break;

            case MotionEvent.ACTION_DOWN:
                player.setBoosting();
                break;
        }
        return true;
    }

    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }
}

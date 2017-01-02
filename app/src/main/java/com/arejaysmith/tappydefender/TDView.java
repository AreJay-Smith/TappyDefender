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

    private Context context;

    private int screenX;
    private int screenY;

    private float distanceRemaining;
    private long timeTaken;
    private long timeStarted;
    private long fastestTime;
    private boolean gameEnded;

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

        this.context = context;

        screenX = x;
        screenY = y;

        // Initialize our drawing objects
        ourHolder = getHolder();
        paint = new Paint();

//        // Initialize our player
//        player = new PlayerShip(context, x, y);
//
//        // Initialize enemy ships
//        enemy1 = new EnemyShip(context, x, y);
//        enemy2 = new EnemyShip(context, x, y);
//        enemy3 = new EnemyShip(context, x, y);
//
//        // create all the space dust
//        int numSpecs = 40;
//
//        for (int i = 0; i < numSpecs; i++) {
//            SpaceDust spec = new SpaceDust(x,y);
//            dustList.add(spec);
//        }

        startGame();
        gameEnded = false;
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

    private void startGame() {
        // Initialize game objects
        // Initialize our player
        player = new PlayerShip(context, screenX, screenY);

        // Initialize enemy ships
        enemy1 = new EnemyShip(context, screenX, screenY);
        enemy2 = new EnemyShip(context, screenX, screenY);
        enemy3 = new EnemyShip(context, screenX, screenY);

        // create all the space dust
        int numSpecs = 40;

        for (int i = 0; i < numSpecs; i++) {
            SpaceDust spec = new SpaceDust(screenX,screenY);
            dustList.add(spec);
        }

        // Reset time and distance
        distanceRemaining = 10000; // 10 km
        timeTaken = 0;

        // Get start time
        timeStarted = System.currentTimeMillis();
    }

    private void update() {

        // Check for collision
        boolean hitDetected = false;
        if(Rect.intersects(player.getHitBox(), enemy1.getHitBox())) {
            hitDetected = true;
            enemy1.setX(-100);
        }
        if(Rect.intersects(player.getHitBox(), enemy2.getHitBox())) {
            hitDetected = true;
            enemy2.setX(-100);
        }
        if(Rect.intersects(player.getHitBox(), enemy3.getHitBox())) {
            hitDetected = true;
            enemy3.setX(-100);
        }

        if (hitDetected) {
            player.reduceShieldStrength();
            if (player.getShieldStrength() < 0) {
                // Game over
                gameEnded = true;
            }
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

        if (!gameEnded) {
            // Subtract distance to home planet based on current speed
            distanceRemaining -= player.getSpeed();

            // How long has the player been flying
            timeTaken = System.currentTimeMillis() - timeStarted;
        }

        // Completed the game!
        if (distanceRemaining < 0) {
            // Check for new fastest time
            if (timeTaken <  fastestTime) {
                fastestTime = timeTaken;
            }

            // avoid ugly negative numbers
            distanceRemaining = 0;

            // Now end the game
            gameEnded = true;
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

            if (!gameEnded) {
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
            } else {
                // Show pause screen
                paint.setTextSize(80);
                paint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText("Game Over", screenX/2, 100, paint);
                paint.setTextSize(25);
                canvas.drawText(" Fastest:" + fastestTime + "s", screenX/ 2, 160, paint);
                canvas.drawText(" Time:" + timeTaken + "s", screenX / 2, 200, paint);
                canvas.drawText(" Distance remaining:" + distanceRemaining/ 1000 + " KM", screenX/ 2, 240, paint);
                paint.setTextSize( 80); canvas.drawText(" Tap to replay!", screenX/ 2, 350, paint);

            }
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

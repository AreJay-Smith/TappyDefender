package com.arejaysmith.tappydefender;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import java.util.Random;

/**
 * Created by Urge_Smith on 12/8/16.
 */

public class EnemyShip {

    private Bitmap bitmap;
    private int x, y;
    private int speed = 1;

    private int maxX;
    private int minX;

    private int maxY;
    private int minY;

    private Rect hitBox;

    public EnemyShip (Context context, int screenX, int screenY) {

        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.enemy);

        maxX = screenX;
        maxY = screenY;
        minX = 0;
        minY = 0;

        Random generator = new Random();
        speed = generator.nextInt(6)+10;

        x = screenX;
        y = generator.nextInt(maxY) - bitmap.getHeight();

        // Initialize the hit box
        hitBox = new Rect(x, y, bitmap.getWidth(),
                bitmap.getHeight());
    }

    public void update (int playerSpeed) {
        // Move to the left
        x -= playerSpeed;
        x-= speed;

        // Respawn when off screen
        if(x < minX-bitmap.getWidth()) {
            Random generator = new Random();
            speed = generator.nextInt(10) + 10;
             x = maxX;
            y = generator.nextInt(maxY) - bitmap.getHeight();
        }

        hitBox.left = x;
        hitBox.top = y;
        hitBox.right = x + bitmap.getWidth();
        hitBox.bottom = y + bitmap.getHeight();
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Rect getHitBox() {
        return hitBox;
    }

    public void setX(int x) {
        this.x = x;
    }
}

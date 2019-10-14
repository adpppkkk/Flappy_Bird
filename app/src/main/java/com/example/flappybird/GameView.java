package com.example.flappybird;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

import java.util.Random;

public class GameView extends View {

    //Custom view class
    Handler handler;  //for delay plays
    Runnable runnable;
    final int Update_MILLIS = 30;
    Bitmap background;
    Bitmap toptube,bottomtube;

    Display display;
    Point point;
    int dWidth, dHeight; //Get device width and height
    Rect rect;

    //bitmap array for bird
    Bitmap[] birds;

    //Integer variable to track the bird image frame
    int birdFrame = 0;

    int velocity = 0, gravity = 3;
    //bird position
    int birdX,birdY;

    boolean gameStart = false;
    int gap = 400; //the gap between tubes
    int minTubeoffset, maxTubeOffset;
    int numberOfTubes = 4;
    int distanceBetweenTubes;
    int[] tubeX = new int[numberOfTubes];
    int[] topTubeY = new int[numberOfTubes];
    Random rng;
    int tubeVelocity = 8;

    public GameView(Context context) {
        super(context);
        handler = new android.os.Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                invalidate(); //Call onDraw();
            }
        };

        background = BitmapFactory.decodeResource(getResources(),R.drawable.bg_day);
        toptube = BitmapFactory.decodeResource(getResources(),R.drawable.pipe_down);
        bottomtube = BitmapFactory.decodeResource(getResources(),R.drawable.pipe_up);
        display = ((Activity)getContext()).getWindowManager().getDefaultDisplay();
        point = new Point();
        display.getSize(point);
        dWidth = point.x;
        dHeight = point.y;
        rect = new Rect(0,0,dWidth,dHeight);

        birds = new Bitmap[2];
        birds[0] = BitmapFactory.decodeResource(getResources(),R.drawable.bird0_0);
        birds[1] = BitmapFactory.decodeResource(getResources(),R.drawable.bird0_1);

        birdX = dWidth/2-birds[0].getWidth()/2;
        birdY = dHeight/2-birds[0].getHeight()/2;

        distanceBetweenTubes = dWidth*3/4;
        minTubeoffset = gap/2;
        maxTubeOffset = dHeight - minTubeoffset -gap;

        rng = new Random();
        for (int i=0; i<numberOfTubes; i++){
            tubeX[i] = dWidth + i*distanceBetweenTubes;
            topTubeY[i] = minTubeoffset + rng.nextInt(maxTubeOffset - minTubeoffset+1);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //draw view here

        //draw background on canvas

        //canvas.drawBitmap(background,0,0,null);
        canvas.drawBitmap(background,null,rect,null);

        //switching frames
        if(birdFrame == 0){
            birdFrame = 1;
        }
        else{
            birdFrame = 0;
        }

        if (gameStart) {

            //keep bird on the screen
            if (birdY < dHeight - birds[0].getHeight() || velocity < 0) {  //pop up when reach the bot
                velocity += gravity;  //fall faster
                birdY += velocity;

            }
            for (int i=0; i<numberOfTubes; i++) {
                tubeX[i] -= tubeVelocity;
                if (tubeX[i] < -toptube.getWidth()){
                    tubeX[i] += numberOfTubes * distanceBetweenTubes;
                    topTubeY[i] = minTubeoffset + rng.nextInt(maxTubeOffset - minTubeoffset+1);
                }
                canvas.drawBitmap(toptube, tubeX[i], topTubeY[i] - toptube.getHeight(), null);
                canvas.drawBitmap(bottomtube, tubeX[i], topTubeY[i] + gap, null);
            }
        }



        //display the bird at center relatively
        canvas.drawBitmap(birds[birdFrame],birdX,birdY,null);

        handler.postDelayed(runnable,Update_MILLIS);
    }

    //Touch event

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if(action == MotionEvent.ACTION_DOWN){  //tap
            //FLY!
            velocity = -30;
            gameStart = true;
        }
        return true;// not further action
    }
}

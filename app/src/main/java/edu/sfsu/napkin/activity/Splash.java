package edu.sfsu.napkin.activity;


import android.os.Bundle;
import android.content.Intent;

import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import android.widget.ImageView;

import java.io.UnsupportedEncodingException;

import edu.sfsu.napkin.R;
import edu.sfsu.napkin.api.APIRequestQueue;

/**
 * @author Ty Daniels
 *Napkin is an app designed to allow users to pick from a list of ingredients from on-hand ingredients.
 * @version 1.0
 * @since 2015-09-25
 *
 * Splash.java handles all animation within activity_splash.xml located within /layout.
 * Functionality Includes:
 * @see Animation
 * @see AnimationDrawable
 * @see Toolbar
 *
 */

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageView img = (ImageView)findViewById(R.id.logoImageView);
        img.setBackgroundResource(R.drawable.rotateback);

        // Get the background, which has been compiled to an AnimationDrawable object.
        AnimationDrawable frameAnimation = (AnimationDrawable) img.getBackground();

        // Start the animation (looped playback by default).
        frameAnimation.start();

        APIRequestQueue.getInstance(this.getCacheDir()).getRequestQueue().start();
        final ImageView logoIV = (ImageView) findViewById(R.id.logoImageView);

        //spin anim 0-180deg
        final Animation rotateAnim1 = AnimationUtils.loadAnimation(getBaseContext(), R.anim.rotate);

        final Animation rotateAnim3 = AnimationUtils.loadAnimation(getBaseContext(), R.anim.rotatetooriginal);

        //fade anim
        final Animation fadeAnim = AnimationUtils.loadAnimation(getBaseContext(), R.anim.abc_fade_out);

        logoIV.startAnimation(rotateAnim1);
        rotateAnim1.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
                            public void onAnimationEnd(Animation animation) {
                                logoIV.startAnimation(fadeAnim);
                                //when anim ends
                                finish();
                                Intent endAnimation = new Intent(Splash.this, Home.class);
                                startActivity(endAnimation);
                            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }


        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

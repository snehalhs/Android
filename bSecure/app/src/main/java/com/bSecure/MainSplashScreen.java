package com.bSecure;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.widget.ImageView;

public class MainSplashScreen extends Activity {
	ImageView im;
	Animation an;
	static int c = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_splash_screen);
		im = (ImageView) findViewById(R.id.img1);
		//an = AnimationUtils.loadAnimation(this, R.animator.anim);
		an.reset();
		im.startAnimation(an);

		// METHOD 1

		/****** Create Thread that will sleep for 5 seconds *************/
		Thread background = new Thread() {
			public void run() {

				try {
					// Thread will sleep for 5 seconds
					sleep(7 * 1000);

					// After 5 seconds redirect to another intent

					Intent i = new Intent(getBaseContext(), UserRegistration.class);
					startActivity(i);

					// Remove activity
					finish();

				} catch (Exception e) {

				}
			}
		};

		// start thread
		background.start();

		// METHOD 2

		/*
		 * new Handler().postDelayed(new Runnable() {
		 * 
		 * // Using handler with postDelayed called runnable run method
		 * 
		 * @Override public void run() { Intent i = new
		 * Intent(MainSplashScreen.this, FirstScreen.class); startActivity(i);
		 * 
		 * // close this activity finish(); } }, 5*1000); // wait for 5 seconds
		 */
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();

	}
}

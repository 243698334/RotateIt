package com.kevinychen.rotateit.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.kevinychen.rotateit.R;

public class WelcomeActivity extends Activity {

	Button startButton, aboutButton;
	ImageView logo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_welcome);
		

		startButton = (Button)findViewById(R.id.button_start);
		aboutButton = (Button)findViewById(R.id.button_about);
		//logo = (ImageView)findViewById(R.id.imageView_logo);
		
		//logo.setImageResource(R.drawable.logo_orange);
		
		startButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(WelcomeActivity.this, GameActivity.class);
                startActivity(intent);		
                WelcomeActivity.this.finish();
			}
		});
	
		aboutButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(WelcomeActivity.this, AboutActivity.class);
				startActivity(intent);
				WelcomeActivity.this.finish();
			}
		});
		
	}

	
}


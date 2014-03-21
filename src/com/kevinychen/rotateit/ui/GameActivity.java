package com.kevinychen.rotateit.ui;

import java.util.Arrays;
import java.util.Collections;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.kevinychen.rotateit.R;
import com.kevinychen.rotateit.util.NumberCard;
import com.kevinychen.rotateit.util.RotateValidator;

public class GameActivity extends Activity {

	private final int NUMBER_QUANTITY = 9;
	
	private Button buttonUndo;
	private Button buttonShuffle;
	private NumberCard[] numberCards = new NumberCard[NUMBER_QUANTITY + 1];
	private Drawable defaultNumberCardBackground;
	
	public RotateValidator rotateValidator = RotateValidator.getInstance();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_gamegrid);

		numberCardsInit();

		buttonShuffle = (Button) findViewById(R.id.button_shuffle);
		buttonShuffle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				shuffle();
			}
		});
		
		buttonUndo = (Button) findViewById(R.id.button_undo);
		buttonUndo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
	    super.onWindowFocusChanged(hasFocus);
	    if(hasFocus) {
	        numberCardsCoordsInit();
	    }
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int penX = (int) event.getX();
		int penY = (int) event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: 
			selectWithPenMove(penX, penY);
			break;
		case MotionEvent.ACTION_MOVE: 
			selectWithPenMove(penX, penY);
			break;
		case MotionEvent.ACTION_UP: 
			selectWithPenMove(penX, penY);
			rotate();
			for (int i = 1; i < numberCards.length; i++) {
				findViewById(numberCards[i].getImageButtonId()).setBackground(defaultNumberCardBackground);
			}
			RotateValidator.reset();
			break;
		default:
			break;
		}
		return false;
	}
	
	/**
	 * Initialize the resource references for each NumberCard
	 * Called in onCreate();
	 */
	private void numberCardsInit() {
		numberCards[0] = null;
		for (int i = 1; i < numberCards.length; i++) {
			numberCards[i] = new NumberCard(i);
		}
		numberCards[1].setImageId(R.drawable.number_1);
		numberCards[2].setImageId(R.drawable.number_2);
		numberCards[3].setImageId(R.drawable.number_3);
		numberCards[4].setImageId(R.drawable.number_4);
		numberCards[5].setImageId(R.drawable.number_5);
		numberCards[6].setImageId(R.drawable.number_6);
		numberCards[7].setImageId(R.drawable.number_7);
		numberCards[8].setImageId(R.drawable.number_8);
		numberCards[9].setImageId(R.drawable.number_9);
		numberCards[1].setImageButtonId(R.id.imageButton_num_1);
		numberCards[2].setImageButtonId(R.id.imageButton_num_2);
		numberCards[3].setImageButtonId(R.id.imageButton_num_3);
		numberCards[4].setImageButtonId(R.id.imageButton_num_4);
		numberCards[5].setImageButtonId(R.id.imageButton_num_5);
		numberCards[6].setImageButtonId(R.id.imageButton_num_6);
		numberCards[7].setImageButtonId(R.id.imageButton_num_7);
		numberCards[8].setImageButtonId(R.id.imageButton_num_8);
		numberCards[9].setImageButtonId(R.id.imageButton_num_9);
		
		defaultNumberCardBackground = findViewById(numberCards[1].getImageButtonId()).getBackground();
	}
	
	/**
	 * Initialize the size and position for each NumberCard
	 * Called in onWindowFocusChanged() since position and size values for an ImageButton won't be ready during onCreate().
	 */
	private void numberCardsCoordsInit() {
		ImageButton topLeftNumberCard = (ImageButton) findViewById(numberCards[1]
				.getImageButtonId());
		topLeftNumberCard = (ImageButton)findViewById(R.id.imageButton_num_1);
		int topLeftX = topLeftNumberCard.getLeft();
		int topLeftY = topLeftNumberCard.getTop();
		int numberCardWidth = topLeftNumberCard.getWidth();
		int numberCardHeight = topLeftNumberCard.getHeight();
		for (int i = 0; i < numberCards.length - 1; i++) {
			int currNumberCardX = topLeftX + numberCardWidth * (i % 3);
			int currNumberCardY = topLeftY + numberCardHeight * (i / 3);
			numberCards[i + 1].setNumberCardRect(currNumberCardX,
					currNumberCardY, numberCardWidth, numberCardHeight);
		}
	}

	/**
	 * Handle onTouchEvent()
	 * Searching for the NumberCard which contains the coordinate of the pen
	 * @param x, y 
	 */
	private void selectWithPenMove(int x, int y) {
		for (int i = 1; i < numberCards.length; i++) {
			if (numberCards[i].containCoord(x, y)) {
				findViewById(numberCards[i].getImageButtonId()).setBackgroundColor(Color.RED);
				rotateValidator.selectNumberCard(numberCards[i]);
				break;
			}
		}
	}

	/**
	 * Shuffle the NumberCards
	 */
	public void shuffle() {
		// creating a shuffled Integer array with value [1, 9]
		Integer[] shuffledNumbers = new Integer[NUMBER_QUANTITY];
		for (int i = 0; i < shuffledNumbers.length; i++) {
			shuffledNumbers[i] = i + 1;
		}
		Collections.shuffle(Arrays.asList(shuffledNumbers));
		moveNumberCards(shuffledNumbers);
	}

	/**
	 * Perform a rotation if the selection of NumberCards is valid
	 * Post a Toast message
	 */
	private void rotate() {
		if (rotateValidator.validate()) {
			Integer[] rotatedNumbers = rotateValidator.getRotatedIndices();
			Log.d("Rotate result", Arrays.toString(rotatedNumbers));
			if (rotatedNumbers != null) {
				moveNumberCards(rotatedNumbers);
				Toast.makeText(getApplicationContext(), "Rotated!", Toast.LENGTH_SHORT).show();
			} 
		} else {
			Toast.makeText(getApplicationContext(), "No Way!", Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * Change the location of NumberCards based on a given relative indices order
	 * Swapping the value of 'number' and 'imageId' for each NumberCard
	 * @param newPositionByIndex
	 */
	private void moveNumberCards(Integer[] newPositionByIndex) {
		for (int i = 1; i < numberCards.length; i++) {
			numberCards[i].resetLast();
		}
		for (int i = 1; i < numberCards.length; i++) {
			numberCards[i].setNumber(numberCards[newPositionByIndex[i - 1]].getLastNumber());
			numberCards[i].setImageId(numberCards[newPositionByIndex[i - 1]].getLastImageId());
			// apply change by assigning new imageId to imageButtons
			ImageButton currNumCard = (ImageButton)findViewById(numberCards[i].getImageButtonId());
			currNumCard.setImageDrawable(getResources().getDrawable(numberCards[i].getImageId()));
		}
	}
}


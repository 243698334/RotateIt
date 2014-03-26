package com.kevinychen.rotateit.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.kevinychen.rotateit.R;
import com.kevinychen.rotateit.util.NumberCard;
import com.kevinychen.rotateit.util.RotateSolver;
import com.kevinychen.rotateit.util.RotateValidator;
import com.kevinychen.rotateit.util.RotateValidator.RotateDirection;
import com.kevinychen.rotateit.util.StepRecorder;
import com.kevinychen.rotateit.util.StepRecorder.Step;

public class GameActivity extends Activity {

	private final int NUMBER_QUANTITY = 9;
	
	private boolean gameStarted;
	private Difficulty difficultyLevel;
	private int minStepsToSolve;
	private enum Difficulty {
		EASY, NORMAL, HARD, IMPOSSIBLE, CUSTOM
	}

	private AlertDialog alertDialogDifficulty;
	private Button buttonStart;
	private Button buttonUndo;
	private Button buttonShuffle;
	private Button buttonHint;
	private TextView textViewCurrentStepCount;
	private TextView textViewMinStepsToSolve;
	
	private NumberCard[] numberCards = new NumberCard[NUMBER_QUANTITY + 1];
	private Drawable defaultNumberCardBackground;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_game);
		
		numberCardsInit();
		gameStarted = false;
		
		final CharSequence[] difficultyLevels = {"Easy", "Normal", "Hard", "Impossible", "Custom"};
		AlertDialog.Builder difficultyBuilder = new AlertDialog.Builder(this);
		difficultyBuilder.setTitle("Select Difficulty Level");
		difficultyBuilder.setSingleChoiceItems(difficultyLevels, -1, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				switch (item) {
				case 0:
					difficultyLevel = Difficulty.EASY;
					minStepsToSolve = 2;
					break;
				case 1:
					difficultyLevel = Difficulty.NORMAL;
					minStepsToSolve = 20;
					break;
				case 2: 
					difficultyLevel = Difficulty.HARD;
					minStepsToSolve = 30;
					break;
				case 3: 
					difficultyLevel = Difficulty.IMPOSSIBLE;
					minStepsToSolve = 40;
					break;
				case 4:
					difficultyLevel = Difficulty.CUSTOM;
					AlertDialog.Builder customDifficultySetter = new AlertDialog.Builder(GameActivity.this);
					customDifficultySetter.setTitle("Custom");
					customDifficultySetter.setMessage("Please enter your prefered minium steps to solve the puzzle: ");
					final EditText difficultyLevelEntry = new EditText(GameActivity.this);
					difficultyLevelEntry.setFilters(new InputFilter[] {
						new InputFilter.LengthFilter(2), DigitsKeyListener.getInstance()
					});
					difficultyLevelEntry.setKeyListener(DigitsKeyListener.getInstance());
					customDifficultySetter.setView(difficultyLevelEntry);
					customDifficultySetter.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							Editable value = difficultyLevelEntry.getText();
							minStepsToSolve = Integer.valueOf(value.toString());
						}
					});

					customDifficultySetter.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {}
					});

					customDifficultySetter.show();
					break;
				}
				alertDialogDifficulty.dismiss();
			}
		});
		alertDialogDifficulty = difficultyBuilder.create();
		alertDialogDifficulty.show();
		
		textViewCurrentStepCount = (TextView) findViewById(R.id.textView_currentStepCount);
		textViewMinStepsToSolve = (TextView) findViewById(R.id.textView_minStepsToSolve);
		
		buttonShuffle = (Button) findViewById(R.id.button_shuffle);
		buttonShuffle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				shuffle();
			}
		});
		
		buttonUndo = (Button) findViewById(R.id.button_undo);
		buttonUndo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				undo();
			}
		});
		
		buttonHint = (Button) findViewById(R.id.button_hint);
		buttonHint.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				hint();
			}
		});
		
		buttonStart = (Button) findViewById(R.id.button_start);
		buttonStart.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View arg0) {
				buttonStart.setVisibility(View.INVISIBLE);
				textViewCurrentStepCount.setVisibility(View.VISIBLE);
				textViewMinStepsToSolve.setVisibility(View.VISIBLE);
				shuffle();
				gameStarted = true;
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
	    if (hasFocus) {
	        numberCardsCoordsInit();
	    }
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int penX = (int) event.getX();
		int penY = (int) event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: 
			if (!gameStarted) break;
			selectWithPenMove(penX, penY);
			break;
		case MotionEvent.ACTION_MOVE: 
			if (!gameStarted) break;
			selectWithPenMove(penX, penY);
			break;
		case MotionEvent.ACTION_UP: 
			if (!gameStarted) break;
			selectWithPenMove(penX, penY);
			rotate();
			resetNumberCardsBackground();
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
		// The first slot of numberCards is null to match the indices
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
	 * Reset the background of all NumberCards
	 * Called after each move
	 */
	private void resetNumberCardsBackground() {
		for (int i = 1; i < numberCards.length; i++) {
			findViewById(numberCards[i].getImageButtonId()).setBackground(defaultNumberCardBackground);
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
				RotateValidator.selectNumberCard(numberCards[i]);
				break;
			}
		}
	}

	/**
	 * Shuffle the NumberCards
	 */
	private void shuffle() {
		// creating a shuffled Integer array with value [1, 9]
		Integer[] shuffledNumbers = new Integer[NUMBER_QUANTITY];
		for (int i = 0; i < shuffledNumbers.length; i++) {
			shuffledNumbers[i] = i + 1;
		}
		// steps to solve differs from each difficulty level
		for (int i = 0; i < minStepsToSolve; i++) {
			int randomRotateType = (int) (Math.random() * 8);
			RotateDirection randomRotateDirection = RotateDirection.values()[(int) (Math.random()*2)];
			Integer[] rotatedIndices = RotateValidator.getRotatedIndicesByGivenType(randomRotateType, randomRotateDirection);
			Integer[] newShuffledNumbers = new Integer[NUMBER_QUANTITY];
			for (int j = 0; j < shuffledNumbers.length; j++) {
				newShuffledNumbers[j] = shuffledNumbers[rotatedIndices[j] - 1];
			}
			shuffledNumbers = newShuffledNumbers;
		}
		moveNumberCards(shuffledNumbers);
		StepRecorder.reset();
		updateStepCounter();
	}

	/**
	 * Perform a rotation if the selection of NumberCards is valid
	 * Post a Toast message
	 */
	private void rotate() {
		if (RotateValidator.validate()) {
			Integer[] rotatedNumbers = RotateValidator.getRotatedIndicesByCurrentType();
			if (rotatedNumbers != null) {
				moveNumberCards(rotatedNumbers);
				StepRecorder.newStep(RotateValidator.getRotateType(), RotateValidator.getRotateDirection());
				updateStepCounter();
				if (checkSolved()) {
					Toast.makeText(getApplicationContext(), "Yay! Solved!", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getApplicationContext(), "Rotated!", Toast.LENGTH_SHORT).show();
				}
			} 
		} else {
			Toast.makeText(getApplicationContext(), "No Way!", Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * Undo the last move
	 */
	private void undo() {
		Step lastStep = StepRecorder.popLastMove();
		if (lastStep == null) {
			Toast.makeText(getApplicationContext(), "Nothing to Undo", Toast.LENGTH_SHORT).show();
		} else {
			int rotateType = lastStep.rotateType;
			RotateDirection direction = RotateValidator.getOppositeDirection(lastStep.direction);
			moveNumberCards(RotateValidator.getRotatedIndicesByGivenType(rotateType, direction));
			updateStepCounter();
			Toast.makeText(getApplicationContext(), "Undo Last Rotate", Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * Give the user a hint of the next move
	 */
	private void hint() {
		// TODO
		//RotateSolver.setInitialState(numberCards);
		//Log.d("Steps away", Integer.toString(RotateSolver.solve()));
		//RotateSolver.reset();
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
	
	/**
	 * Update the current step count textView
	 * @param new value to be set
	 */
	private void updateStepCounter() {
		textViewCurrentStepCount.setText("Step Count: " + StepRecorder.getStepCount());
		textViewMinStepsToSolve.setText("Min Steps to Solve: " + minStepsToSolve);
	}
	
	/**
	 * Check if the puzzle is solved
	 */
	private boolean checkSolved() {
		for (int i = 1; i < numberCards.length; i++) {
			if (numberCards[i].getNumber() != i) {
				return false;
			}
		}
		return true;
	}
}


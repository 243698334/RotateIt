package com.kevinychen.rotateit.util;

import java.util.ArrayList;
import java.util.Arrays;

import android.util.Log;

/**
 * 9 numbers
 */
public class RotateValidator {
	
	private final static int TOTAL_NUMBERS = 9;
	private static CircularArrayList<Integer>[] rotateRules;
	
	public static boolean[] selectionStatus;
	public static ArrayList<NumberCard> selections;
	public static int startNumber;
	public static RotateDirection rotateDirection;
	public static int rotateType;
	
	
	private enum RotateDirection {
		CLOCKWISE, COUNTER_CLOCKWISE, NULL
	}
	
	public static RotateValidator getInstance() {
		RotateValidator instance = new RotateValidator();
		RotateValidator.selectionStatus = new boolean[TOTAL_NUMBERS];
		for (int i = 0; i < selectionStatus.length; i++) {
			selectionStatus[i] = false;
		}
		RotateValidator.selections = new ArrayList<NumberCard>();
		RotateValidator.startNumber = -1;
		RotateValidator.rotateDirection = RotateDirection.NULL;
		RotateValidator.rotateType = -1;
		
		return instance;
	}
	
	public static void reset() {
		for (int i = 0; i < selectionStatus.length; i++) {
			selectionStatus[i] = false;
		}
		selections = new ArrayList<NumberCard>();
		startNumber = -1;
		rotateType = -1;
	}
	
	private RotateValidator() {
		// initialize the ratateRules
		rotateRules = new CircularArrayList[9];
		rotateRules[0] = new CircularArrayList<Integer>(Arrays.asList(1, 2, 5, 4));
		rotateRules[1] = new CircularArrayList<Integer>(Arrays.asList(4, 5, 8, 7));
		rotateRules[2] = new CircularArrayList<Integer>(Arrays.asList(2, 3, 6, 5));
		rotateRules[3] = new CircularArrayList<Integer>(Arrays.asList(5, 6, 9, 8));
		rotateRules[4] = new CircularArrayList<Integer>(Arrays.asList(1, 2, 5, 8, 7, 4));
		rotateRules[5] = new CircularArrayList<Integer>(Arrays.asList(2, 3, 6, 9, 8, 5));
		rotateRules[6] = new CircularArrayList<Integer>(Arrays.asList(1, 2, 3, 6, 5, 4));
		rotateRules[7] = new CircularArrayList<Integer>(Arrays.asList(4, 5, 6, 9, 8, 7));
		rotateRules[8] = new CircularArrayList<Integer>(Arrays.asList(1, 2, 3, 6, 9, 8, 7, 4));
	}
	
	/**
	 * Set the start number.
	 * @param input
	 * @return true if set successfully, false otherwise
	 */
	public boolean setStartNumber(int input) {
		if (startNumber == -1) {
			startNumber = input;
			selectionStatus[input - 1] = true;
			return true;
		} else return false;
	}
	
	public boolean validate() {
		String listString = "";
		for (NumberCard nc : selections) {
		    listString += nc.getNumber() + "\t";
		}
		Log.d("Numbers selected", listString);

		
		// check selection size
		if (selections.size() != 5 && selections.size() != 7 && selections.size() != 9) {
			return false;
		}
		// check head and tail
		if (!selections.get(0).equals(selections.get(selections.size() - 1))) {
			return false;
		}
		// extract indices order
		int[] selectedIndices = new int[selections.size() - 1];
		for (int i = 0; i < selectedIndices.length; i++) {
			selectedIndices[i] = selections.get(i).getIndex();
		}
		
		if (selectedIndices.length == 4) {
			for (int i = 0; i < 4; i++) {
				int startIndex = rotateRules[i].setIteratorByElement(selectedIndices[0]);
				if (startIndex == -1)
					continue;
				// check for clockwise
				boolean validClockwise = true;
				for (int j = 1; j < selectedIndices.length; j++) {
					if (selectedIndices[j] != rotateRules[i].get(startIndex + j)) {
						validClockwise = false;
						break;
					}
				}
				if (validClockwise) {
					rotateDirection = RotateDirection.CLOCKWISE;
					rotateType = i;
					return true;
				}
				// check for counter-clockwise
				boolean validCounterClockwise = true;
				for (int j = 1; j < selectedIndices.length; j++) {
					if (selectedIndices[j] != rotateRules[i].prev()) {
						validCounterClockwise = false;
						break;
					}
				}
				if (validCounterClockwise) {
					rotateDirection = RotateDirection.COUNTER_CLOCKWISE;
					rotateType = i;
					return true;
				}
			}
		}
		
		if (selectedIndices.length == 6) {
			for (int i = 4; i < 8; i++) {
				int startIndex = rotateRules[i].setIteratorByElement(selectedIndices[0]);
				if (startIndex == -1)
					continue;
				// check for clockwise
				boolean validClockwise = true;
				for (int j = 1; j < selectedIndices.length; j++) {
					if (selectedIndices[j] != rotateRules[i].get(startIndex + j)) {
						validClockwise = false;
						break;
					}
				}
				if (validClockwise) {
					rotateDirection = RotateDirection.CLOCKWISE;
					rotateType = i;
					return true;
				}
				// check for counter-clockwise
				boolean validCounterClockwise = true;
				for (int j = 1; j < selectedIndices.length; j++) {
					if (selectedIndices[j] != rotateRules[i].prev()) {
						validCounterClockwise = false;
						break;
					}
				}
				if (validCounterClockwise) {
					rotateDirection = RotateDirection.COUNTER_CLOCKWISE;
					rotateType = i;
					return true;
				}
			}
		}
		
		if (selectedIndices.length == 8) {
			int startIndex = rotateRules[8].setIteratorByElement(selectedIndices[0]);
			if (startIndex == -1)
				return false;
			// check for clockwise
			boolean validClockwise = true;
			for (int j = 1; j < selectedIndices.length; j++) {
				if (selectedIndices[j] != rotateRules[8].get(startIndex + j)) {
					validClockwise = false;
					break;
				}
			}
			if (validClockwise) {
				rotateDirection = RotateDirection.CLOCKWISE;
				rotateType = 8;
				return true;
			}
			// check for counter-clockwise
			boolean validCounterClockwise = true;
			for (int j = 1; j < selectedIndices.length; j++) {
				if (selectedIndices[j] != rotateRules[8].prev()) {
					validCounterClockwise = false;
					break;
				}
			}
			if (validCounterClockwise) {
				rotateDirection = RotateDirection.COUNTER_CLOCKWISE;
				rotateType = 8;
				return true;
			}
		}
		return false;
	}
	
	public Integer[] getRotatedIndices() {
		switch (rotateType) {
		case 0:
			if (rotateDirection == RotateDirection.CLOCKWISE)
				return new Integer[] {4, 1, 3, 5, 2, 6, 7, 8, 9};
			if (rotateDirection == RotateDirection.COUNTER_CLOCKWISE)
				return new Integer[] {2, 5, 3, 1, 4, 6, 7, 8, 9};
		case 1:
			if (rotateDirection == RotateDirection.CLOCKWISE)
				return new Integer[] {1, 2, 3, 7, 4, 6, 8, 5, 9};
			if (rotateDirection == RotateDirection.COUNTER_CLOCKWISE)
				return new Integer[] {1, 2, 3, 5, 8, 6, 4, 7, 9};
		case 2:
			if (rotateDirection == RotateDirection.CLOCKWISE)
				return new Integer[] {1, 5, 2, 4, 6, 3, 7, 8, 9};
			if (rotateDirection == RotateDirection.COUNTER_CLOCKWISE)
				return new Integer[] {1, 3, 6, 4, 2, 5, 7, 8, 9};
		case 3:
			if (rotateDirection == RotateDirection.CLOCKWISE)
				return new Integer[] {1, 2, 3, 4, 8, 5, 7, 9, 6};
			if (rotateDirection == RotateDirection.COUNTER_CLOCKWISE)
				return new Integer[] {1, 2, 3, 4, 6, 9, 7, 5, 8};
		case 4:
			if (rotateDirection == RotateDirection.CLOCKWISE)
				return new Integer[] {4, 1, 3, 7, 2, 6, 8, 5, 9};
			if (rotateDirection == RotateDirection.COUNTER_CLOCKWISE)
				return new Integer[] {2, 5, 3, 1, 8, 6, 4, 7, 9};
		case 5:
			if (rotateDirection == RotateDirection.CLOCKWISE)
				return new Integer[] {1, 5, 2, 4, 8, 3, 7, 9, 6};
			if (rotateDirection == RotateDirection.COUNTER_CLOCKWISE)
				return new Integer[] {1, 3, 6, 4, 2, 9, 7, 5, 8};
		case 6:
			if (rotateDirection == RotateDirection.CLOCKWISE)
				return new Integer[] {4, 1, 2, 5, 6, 3, 7, 8, 9};
			if (rotateDirection == RotateDirection.COUNTER_CLOCKWISE)
				return new Integer[] {2, 3, 6, 1, 4, 5, 7, 8, 9};
		case 7:
			if (rotateDirection == RotateDirection.CLOCKWISE)
				return new Integer[] {1, 2, 3, 7, 4, 5, 8, 9, 6};
			if (rotateDirection == RotateDirection.COUNTER_CLOCKWISE)
				return new Integer[] {1, 2, 3, 5, 6, 9, 4, 7, 8};
		case 8:
			if (rotateDirection == RotateDirection.CLOCKWISE)
				return new Integer[] {4, 1, 2, 7, 5, 3, 8, 9, 6};
			if (rotateDirection == RotateDirection.COUNTER_CLOCKWISE)
				return new Integer[] {2, 3, 6, 1, 5, 9, 4, 7, 8};
		default:
			return null;
		
		}
	}

	public void selectNumberCard(NumberCard newNumberCard) {
		if (selections.size() == 0) {
			selections.add(newNumberCard);
		} else if (selections.get(selections.size() - 1).getIndex() != newNumberCard.getIndex()) {
			selections.add(newNumberCard);
		}
	}

}

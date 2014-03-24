package com.kevinychen.rotateit.util;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 9 numbers
 */
public class RotateValidator {
	
	private static RotateValidator instance = null;
	
	private final static int TOTAL_NUMBERS = 9;
	private static CircularArrayList<Integer>[] rotateRules;
	
	private static boolean[] selectionStatus;
	private static ArrayList<NumberCard> selections;
	private static RotateDirection rotateDirection;
	private static int rotateType;
	
	
	public enum RotateDirection {
		CLOCKWISE, COUNTER_CLOCKWISE, NULL
	}
	
	@SuppressWarnings("unchecked")
	private static void initialize() {
		instance = new RotateValidator();
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
		
		selectionStatus = new boolean[TOTAL_NUMBERS];
		for (int i = 0; i < selectionStatus.length; i++) {
			selectionStatus[i] = false;
		}
		
		selections = new ArrayList<NumberCard>();
		rotateDirection = RotateDirection.NULL;
		rotateType = -1;
	}
	
	public static RotateValidator getInstance() {
		if (instance == null) {
			initialize();
		}
		return instance;
	}
	
	public static void reset() {
		instance = null;
	}
	
	private RotateValidator() {}
	
	public static boolean validate() {
		if (instance == null) {
			initialize();
		}
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
	
	public static Integer[] getRotatedIndicesByCurrentType() {
		if (instance == null) {
			initialize();
		}
		return getRotatedIndices(rotateType, rotateDirection);
	}
	
	public static Integer[] getRotatedIndicesByGivenType(int myRotateType, RotateDirection myDirection) {
		return getRotatedIndices(myRotateType, myDirection);
	}
	
	private static Integer[] getRotatedIndices(int myRotateType, RotateDirection myDirection) {
		switch (myRotateType) {
		case 0:
			if (myDirection == RotateDirection.CLOCKWISE)
				return new Integer[] {4, 1, 3, 5, 2, 6, 7, 8, 9};
			if (myDirection == RotateDirection.COUNTER_CLOCKWISE)
				return new Integer[] {2, 5, 3, 1, 4, 6, 7, 8, 9};
		case 1:
			if (myDirection == RotateDirection.CLOCKWISE)
				return new Integer[] {1, 2, 3, 7, 4, 6, 8, 5, 9};
			if (myDirection == RotateDirection.COUNTER_CLOCKWISE)
				return new Integer[] {1, 2, 3, 5, 8, 6, 4, 7, 9};
		case 2:
			if (myDirection == RotateDirection.CLOCKWISE)
				return new Integer[] {1, 5, 2, 4, 6, 3, 7, 8, 9};
			if (myDirection == RotateDirection.COUNTER_CLOCKWISE)
				return new Integer[] {1, 3, 6, 4, 2, 5, 7, 8, 9};
		case 3:
			if (myDirection == RotateDirection.CLOCKWISE)
				return new Integer[] {1, 2, 3, 4, 8, 5, 7, 9, 6};
			if (myDirection == RotateDirection.COUNTER_CLOCKWISE)
				return new Integer[] {1, 2, 3, 4, 6, 9, 7, 5, 8};
		case 4:
			if (myDirection == RotateDirection.CLOCKWISE)
				return new Integer[] {4, 1, 3, 7, 2, 6, 8, 5, 9};
			if (myDirection == RotateDirection.COUNTER_CLOCKWISE)
				return new Integer[] {2, 5, 3, 1, 8, 6, 4, 7, 9};
		case 5:
			if (myDirection == RotateDirection.CLOCKWISE)
				return new Integer[] {1, 5, 2, 4, 8, 3, 7, 9, 6};
			if (myDirection == RotateDirection.COUNTER_CLOCKWISE)
				return new Integer[] {1, 3, 6, 4, 2, 9, 7, 5, 8};
		case 6:
			if (myDirection == RotateDirection.CLOCKWISE)
				return new Integer[] {4, 1, 2, 5, 6, 3, 7, 8, 9};
			if (myDirection == RotateDirection.COUNTER_CLOCKWISE)
				return new Integer[] {2, 3, 6, 1, 4, 5, 7, 8, 9};
		case 7:
			if (myDirection == RotateDirection.CLOCKWISE)
				return new Integer[] {1, 2, 3, 7, 4, 5, 8, 9, 6};
			if (myDirection == RotateDirection.COUNTER_CLOCKWISE)
				return new Integer[] {1, 2, 3, 5, 6, 9, 4, 7, 8};
		case 8:
			if (myDirection == RotateDirection.CLOCKWISE)
				return new Integer[] {4, 1, 2, 7, 5, 3, 8, 9, 6};
			if (myDirection == RotateDirection.COUNTER_CLOCKWISE)
				return new Integer[] {2, 3, 6, 1, 5, 9, 4, 7, 8};
		default:
			return null;
		
		}
	}

	public static void selectNumberCard(NumberCard newNumberCard) {
		if (instance == null) {
			initialize();
		}
		if (selections.size() == 0) {
			selections.add(newNumberCard);
		} else if (selections.get(selections.size() - 1).getIndex() != newNumberCard.getIndex()) {
			selections.add(newNumberCard);
		}
	}
	
	public static int getRotateType() {
		return rotateType;
	}
	
	public static RotateDirection getRotateDirection() {
		return rotateDirection;
	}
	
	public static RotateDirection getOppositeDirection(RotateDirection direction) {
		switch (direction) {
		case CLOCKWISE:
			return RotateDirection.COUNTER_CLOCKWISE;
		case COUNTER_CLOCKWISE:
			return RotateDirection.CLOCKWISE;
		default:
			return RotateDirection.NULL;
		}
	}
	
}

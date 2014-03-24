package com.kevinychen.rotateit.util;

import android.graphics.Rect;

public class NumberCard {

	private int number;
	private int lastNumber;
	private int imageId;
	private int lastImageId;
	private int imageButtonId; // fixed
	private int[] coord; // 0: x, 1: y
	private int[] size;  // 0: w, 1: h
	private boolean selected;
	private final int index;
	private Rect numberCardRect;
	
	public NumberCard(int number) {
		this.number = this.lastNumber = number;
		this.selected = false;
		coord = new int[2];
		size = new int[2];
		numberCardRect = new Rect();
		lastImageId = 0;
		this.index = number;
	}
	
	public int getIndex() {
		return this.index;
	}
	
	public void resetLast() {
		lastNumber = number;
		lastImageId = imageId;
	}
	
	public boolean isSelected() {
		return selected;
	}
	
	public void select() {
		this.selected = true;
	}
	
	public void unselect() {
		this.selected = false;
	}
	
	public void setNumber(int number) {
		this.number = number;
	}
	
	public int getNumber() {
		return number;
	}
	
	public int getLastNumber() {
		return lastNumber;
	}
	
	public int getImageId() {
		return imageId;
	}
	
	public int getLastImageId() {
		return lastImageId;
	}
	
	public void setImageId(int imageId) {
		this.imageId = imageId;
		if (this.lastImageId == 0) {
			this.lastImageId = imageId;
		}
	}

	public int getImageButtonId() {
		return imageButtonId;
	}

	public void setImageButtonId(int imageButtonId) {
		this.imageButtonId = imageButtonId;
	}
	
	public int getXCoord() {
		return coord[0];
	}
	
	public int getYCorrd() {
		return coord[1];
	}
	
	public void setNumberCardRect(int x, int y, int w, int h) {
		coord[0] = x;
		coord[1] = y;
		size[0] = w;
		size[1] = h;
		numberCardRect.set(x, y, x + w, y + h);
	}
	
	public boolean containCoord(int x, int y) {
		return numberCardRect.contains(x, y);
	}
}

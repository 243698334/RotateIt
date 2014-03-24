package com.kevinychen.rotateit.util;

import java.util.Stack;

import com.kevinychen.rotateit.util.RotateValidator.RotateDirection;

public class StepRecorder {
	
	private static StepRecorder instance = null;
	private static Stack<Step> steps;
	
	public class Step {
		public int rotateType;
		public RotateDirection direction;
		
		public Step(int rotateType, RotateDirection direction) {
			this.rotateType = rotateType;
			this.direction = direction;
		}
	}
	
	private static void initialize() {
		instance = new StepRecorder();
		StepRecorder.steps = new Stack<Step>();
	}
	
	public static StepRecorder getInstance() {
		if (instance == null) {
			initialize();
		}
		return instance;
	}
	
	private StepRecorder() {}
	
	public static void newStep(int rotateType, RotateDirection direction) {
		if (instance == null) {
			initialize();
		}
		steps.push(instance.new Step(rotateType, direction));
	}
	
	public static Step popLastMove() {
		return instance == null || steps.size() == 0 ? null : steps.pop();
	}
	
	public static int getStepCount() {
		return instance == null ? 0 : steps.size();
	}
	
	public static void reset() {
		instance = null;
	}
}

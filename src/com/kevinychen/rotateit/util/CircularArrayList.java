package com.kevinychen.rotateit.util;

import java.util.ArrayList;
import java.util.Collection;

@SuppressWarnings("serial")
public class CircularArrayList<T> extends ArrayList<T> {

	private int at;

	public CircularArrayList() {
		super();
		at = 0;
	}
	
	public CircularArrayList(Collection<? extends T> c) {
		super(c);
		at = 0;
	}

	@Override
	public void clear() {
		super.clear();
		at = 0;
	}

	public T next() {
		if (++at > (super.size() - 1))
			at = 0;
		return super.get(at);
	}

	public T prev() {
		if (--at < 0)
			at = (super.size() - 1);
		return super.get(at);
	}

	public T get(int index) {
		if (index < 0) {
			while (index < 0) {
				index += super.size();
			}
			return super.get(index);
		} else 
			return super.get(index % super.size());
	}
	
	public int size() {
		return super.size();
	}

	public int setIteratorByElement(T element) {
		at = super.indexOf(element);
		return at;
	}
}
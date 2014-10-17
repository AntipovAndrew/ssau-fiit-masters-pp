package com.aacorp.masters.pp.first;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created with IntelliJ IDEA.
 * User: Andrew Antipov
 * Date: 18.09.14
 * Time: 16:06
 */
public class CyclicQueue<T> {

	private Object[] buffer;
	private int startPointer;
	private int finalPointer;
	private int size;
	private boolean isEnd;
	private AtomicInteger curQSize;

	public CyclicQueue(int size) {
		buffer = new Object[size];
		startPointer = 0;
		finalPointer = 0;
		this.size = size;
		curQSize = new AtomicInteger(0);
	}

	public T readNext() {
		while(true) {
			if(curQSize.intValue() > 0) {
				T res = (T) buffer[startPointer];
				startPointer++;
				if(startPointer == size) {
					startPointer = 0;
				}
				curQSize.decrementAndGet();
				return res;
			}
			if(isEnd) {
				return null;
			}
		}
	}

	public void add(T value) {
		while(true) {
			if(curQSize.intValue() < size) {
				buffer[finalPointer] = value;
				finalPointer++;
				if(finalPointer == size) {
					finalPointer = 0;
				}
				curQSize.incrementAndGet();
				break;
			}
		}
	}


	public void add(T value, boolean isEnd) {
		while(true) {
			if(curQSize.intValue() < size) {
				buffer[finalPointer] = value;
				finalPointer++;
				if(finalPointer == size) {
					finalPointer = 0;
				}
				if(isEnd) {
					this.isEnd = true;
				}
				curQSize.incrementAndGet();

				break;
			}
		}
	}


	public void setEnd() {

	}
}

package com.aacorp.masters.pp.first;

import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: Andrew Antipov
 * Date: 18.09.14
 * Time: 16:50
 */
public class First {

	private static int SIZE = 1000 * 1000;

	public static void main(String[] args) throws InterruptedException {
		int[] sizes = {1, 3, 5, 10, 100, 1000, 10000, 100000, 300000, 500000, 700000, 900000, 1000000};
		int numOfTest = 20;
		int from = 0;
		for(int i = from; i < sizes.length; i++) {
			long min = 100000000;
			for(int j = 0; j < numOfTest; j++) {
				long curRes = test(sizes[i]);
				min = Math.min(curRes, min);
			}
			System.out.println("Size: " + sizes[i] + " Time: " + min + "ms");
		}
	}

	static long test(int queueSize) throws InterruptedException {
		int[] a = generateArray(SIZE);
		int[] b = new int[SIZE];
		CyclicQueue queue = new CyclicQueue(queueSize);
		Reader reader = new Reader(a, queue);
		Writer writer = new Writer(b, queue);

		long startTime = System.currentTimeMillis();

		reader.start();
		writer.start();

		reader.join();
		writer.join();

		long finishTime = System.currentTimeMillis();

		boolean check = true;
		for(int i = 0; i < SIZE; i++) {
			if(a[i] != b[i]) {
				check = false;
			}
		}

		if(!check) {
			System.out.println("FAIL");
		}
//		System.out.println(finishTime - startTime +"ms");
		return finishTime - startTime;
	}

	public static int[] generateArray(int size) {
		int[] res = new int[size];
		Random rg = new Random();
		for(int i = 0; i < size; i++) {
			res[i] = rg.nextInt();
		}
		return res;
	}

	static class Reader extends Thread {
		private int[] from;
		private CyclicQueue queue;

		Reader(int[] from, CyclicQueue queue) {
			this.from = from;
			this.queue = queue;
		}

		@Override
		public void run() {
			for(int i = 0; i < from.length; i++) {
				queue.add(from[i]);
			}
		}
	}

	static class Writer extends Thread {
		private int[] to;
		private CyclicQueue<Integer> queue;

		Writer(int[] to, CyclicQueue queue) {
			this.to = to;
			this.queue = queue;
		}

		@Override
		public void run() {
			for(int i = 0; i < to.length; i++) {
				to[i] = queue.readNext();
			}
		}
	}
}

package com.aacorp.masters.pp.second;

import com.aacorp.masters.pp.first.CyclicQueue;

/**
 * Created with IntelliJ IDEA.
 * User: Andrew Antipov
 * Date: 15.10.14
 * Time: 23:13
 */
public class Second {

	private static final int SIZE = 1;

	public static void main(String[] args) {
		long t1 = System.currentTimeMillis();
		notParallel(args);
		long t2 = System.currentTimeMillis();
		parallel(args);
		long t3 = System.currentTimeMillis();
		System.out.println("Parallel: " + (t3 - t2) + "ms\nNot parallel: " + (t2-t1) + "ms");
	}

	public static void notParallel(String[] args) {
		CyclicQueue<String> q1 = new CyclicQueue<String>(SIZE);
		FileReader first = new FileReader(q1, args[0]);
		CyclicQueue<String> q2 = new CyclicQueue<String>(SIZE);
		FileReader second = new FileReader(q2, args[1]);
		int numberOfDiff = 0;
		for(int i = 0; true; i++) {
			String firstString = first.readNext();
			String secondString = second.readNext();
			if(firstString == null && secondString == null) {
				break;
			}
			if(firstString == null || !firstString.equals(secondString)) {
				numberOfDiff++;
				System.out.println((i+1) + ": " + (firstString == null ? "null" : firstString) + " " + (secondString == null ? "null" : secondString));
			}
		}
		System.out.println(numberOfDiff + " lines different");
	}


	public static void parallel(String[] args) {
		CyclicQueue<String> q1 = new CyclicQueue<String>(SIZE);
		FileReader first = new FileReader(q1, args[0]);
		CyclicQueue<String> q2 = new CyclicQueue<String>(SIZE);
		FileReader second = new FileReader(q2, args[1]);
		int numberOfDiff = 0;
		first.start();
		second.start();
		for(int i = 0; true; i++) {
			String firstString = q1.readNext();
			String secondString = q2.readNext();
			if(firstString == null && secondString == null) {
				break;
			}
			if(firstString == null || !firstString.equals(secondString)) {
				numberOfDiff++;
				System.out.println((i+1) + ": " + (firstString == null ? "null" : firstString) + " " + (secondString == null ? "null" : secondString));
			}
		}
		System.out.println(numberOfDiff + " lines different");
	}


}

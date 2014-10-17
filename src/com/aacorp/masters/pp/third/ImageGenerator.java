package com.aacorp.masters.pp.third;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: Andrew Antipov
 * Date: 18.10.14
 * Time: 1:40
 */
public class ImageGenerator {
	public static void main(String[] args) throws IOException {
		int n = Integer.parseInt(args[0]);
		int m = Integer.parseInt(args[1]);
		int max = Integer.parseInt(args[2]);
		PrintWriter pw = new PrintWriter("image.txt");
		pw.println(n + " " + m);
		Random rg = new Random();
		for(int i = 0; i < n; i++) {
			for(int j = 0; j < m; j++) {
				int cur = rg.nextInt();
				if(cur < 0) {
					cur = -cur;
				}
				cur %= max;
				pw.print(cur + " ");
			}
			pw.println();
		}
		pw.flush();
		pw.close();
	}
}

package com.aacorp.masters.pp.third;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Scanner;


public class Third {

	private int[][] image;
	private int[] qx;
	private int[] qy;
	private int[] dsu;
	private int[] size;
	private boolean[][] used;
	private int[][] res;
	private int n, m;

	public void run() {
		init();
		long t1 = System.currentTimeMillis();
		sequential();
		long t2 = System.currentTimeMillis();
		save("sequential.txt");
		System.out.println("Sequential: " + (t2 - t1) + "ms");
		init();
		t1 = System.currentTimeMillis();
		parallel(2);
		t2 = System.currentTimeMillis();
		System.out.println("Parallel: " + (t2 - t1) + "ms");
		save("parallel.txt");
		if(check("sequential.txt", "parallel.txt")) {
			System.out.println("EQUALS");
		} else {
			System.out.println("FAIL!");
		}
	}

	private void init() {
		try {
			Scanner sc = new Scanner(new File("image.txt"));
			n = sc.nextInt();
			m = sc.nextInt();
			image = new int[n][m];
			res = new int[n][m];
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < m; j++) {
					image[i][j] = sc.nextInt();
					res[i][j] = i * m + j;
				}
			}
			used = new boolean[n][m];
			qx = new int[n * m];
			qy = new int[n * m];
			dsu = new int[n * m];
			size = new int[n * m];
			for (int i = 0; i < n * m; i++) {
				dsu[i] = i;
				size[i] = 1;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void sequential() {
		find(0, n - 1, 0, m - 1, 0);
	}

	private void parallel(int numberOfThreads) {
		new Task(0, n-1, 0, m-1, 0, numberOfThreads).run();
		for(int i = 0; i < n; i++) {
			for(int j = 0; j < m; j++) {
				res[i][j] = parent(res[i][j]);
			}
		}
	}

	private void find(int xl, int xr, int yl, int yr, int qStart) {
		for (int i = xl; i <= xr; i++) {
			for (int j = yl; j <= yr; j++) {
				if (!used[i][j]) {
					qStart = bfs(i, j, xl, xr, yl, yr, qStart);
				}
			}
		}
	}

	public int bfs(int x, int y, int xl, int xr, int yl, int yr, int qStart) {
		int qf = qStart;
		int ql = qf;
		qx[ql] = x;
		qy[ql] = y;
		used[x][y] = true;
		ql++;
		int color = image[x][y];
		int num = res[x][y];
		while (qf != ql) {
			int cx = qx[qf];
			int cy = qy[qf];
			res[cx][cy] = num;
			qf++;
			for (int i = 0; i < 4; i++) {
				int nx = cx + sx[i];
				int ny = cy + sy[i];
				if (!(nx < xl || nx > xr || ny < yl || ny > yr || used[nx][ny] || image[nx][ny] != color)) {
					used[nx][ny] = true;
					qx[ql] = nx;
					qy[ql] = ny;
					ql++;
				}
			}
		}
		return ql;
	}

	private void save(String fileName) {
		try {
			PrintWriter pw = new PrintWriter(fileName);
			int[] p = new int[n * m];
			Arrays.fill(p, -1);
			int cur = 1;
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < m; j++) {
					if (p[res[i][j]] == -1) {
						p[res[i][j]] = cur++;
					}
					pw.printf("%10d", p[res[i][j]]);
				}
				pw.println();
			}
			pw.flush();
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean check(String fileNameSeq, String fileNamePar) {
		int[][] seq = read(fileNameSeq, n, m);
		int[][] par = read(fileNamePar, n, m);
		for(int i = 0; i < n; i++) {
			for(int j = 0; j < m; j++) {
				if(seq[i][j] != par[i][j]) {
					return false;
				}
			}
		}
		return true;
	}

	private int[][] read(String fileName, int n, int m) {
		try {
			Scanner sc = new Scanner(new File(fileName));
			int[][] res = new int[n][m];
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < m; j++) {
					res[i][j] = sc.nextInt();
				}
			}
			return res;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	class Task extends Thread {
		int xl;
		int xr;
		int yl;
		int yr;
		int qStart;
		int numOfThreads;

		Task(int xl, int xr, int yl, int yr, int qStart, int numOfThreads) {
			this.xl = xl;
			this.xr = xr;
			this.yl = yl;
			this.yr = yr;
			this.qStart = qStart;
			this.numOfThreads = numOfThreads;
		}

		@Override
		public void run() {
			findRes(xl, xr, yl, yr, qStart, numOfThreads);
		}

		private void findRes(int xl, int xr, int yl, int yr, int qStart, int numOfThreads) {
			if (numOfThreads == 1) {
				find(xl, xr, yl, yr, qStart);
				return;
			}
			int threadLeft, threadRight;
			if ((numOfThreads & 1) == 0) {
				threadLeft = threadRight = numOfThreads >> 1;
			} else {
				threadRight = numOfThreads >> 1;
				threadLeft = numOfThreads - threadRight;
			}
			int dx = xr - xl;
			int dy = yr - yl;
			if (dx == 0 && dy == 0) {
				return;
			}
			int mid;
			boolean hor;
			if (dx < dy) {
				mid = yl + (dy >> 1);
				hor = false;
			} else {
				mid = xl + (dx >> 1);
				hor = true;
			}

			if (hor) {
				Task child = new Task(mid + 1, xr, yl, yr, qStart + (dy + 1) * (mid - xl + 1), threadRight);
				child.start();
				findRes(xl, mid, yl, yr, qStart, threadLeft);
				try {
					child.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				for(int i = yl; i <= yr; i++) {
					if(image[mid][i] == image[mid+1][i]) {
						unite(res[mid][i], res[mid+1][i]);
					}
				}
			} else {
				Task child = new Task(xl, xr, mid + 1, yr, qStart + (dx + 1) * (mid - yl + 1), threadRight);
				child.start();
				findRes(xl, xr, yl, mid, qStart, threadLeft);
				try {
					child.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				for (int i = xl; i <= xr; i++) {
					if (image[i][mid] == image[i][mid+1]) {
						unite(res[i][mid], res[i][mid+1]);
					}
				}
			}
		}
	}

	private int parent(int v) {
		if (dsu[v] != v) {
			return dsu[v] = parent(dsu[v]);
		}
		return v;
	}

	private void unite(int x, int y) {
		x = parent(x);
		y = parent(y);
		if(x != y) {
			if(size[x] > size[y]) {
				dsu[y] = x;
				size[x] += size[y];
			} else {
				dsu[x] = y;
				size[y] += size[x];
			}
		}
	}

	static int[] sx = {0, 0, -1, 1};
	static int[] sy = {1, -1, 0, 0};

	public static void main(String[] args) {
		new Third().run();
	}
}

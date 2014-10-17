package com.aacorp.masters.pp.second;

import com.aacorp.masters.pp.first.CyclicQueue;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Andrew Antipov
 * Date: 15.10.14
 * Time: 23:13
 */
public class FileReader extends Thread {

	private CyclicQueue<String> q;
	private String fileName;
	private BufferedReader reader;

	public FileReader(CyclicQueue<String> q, String fileName) {
		this.q = q;
		this.fileName = fileName;
		try {
			this.reader = new BufferedReader(new java.io.FileReader(fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String readNext() {
		try {
			String s = reader.readLine();
			return s;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void run() {
		try {
			String s;
			while((s = reader.readLine()) != null) {
				q.add(s);
			}
			q.add(s, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

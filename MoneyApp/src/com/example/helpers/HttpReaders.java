package com.example.helpers;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

/* A class contains helper functions which converts InputStream data to the appropriate format */
public class HttpReaders {

	// Reads an InputStream and converts it to a String.
	public static String readIt(InputStream stream, int len)
			throws IOException, UnsupportedEncodingException {
		Reader reader = null;
		reader = new InputStreamReader(stream, "UTF-8");
		char[] buffer = new char[len];
		reader.read(buffer);
		return new String(buffer);
	}
	
	public static int readInt(InputStream stream, int len)
			throws IOException, UnsupportedEncodingException {
		return Integer.parseInt(readIt(stream,len));
	}
	
}

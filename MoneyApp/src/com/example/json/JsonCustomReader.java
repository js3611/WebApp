package com.example.json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import android.util.JsonReader;
import android.util.JsonToken;

public class JsonCustomReader {

	public static int readJsonRetCode(InputStream in)
			throws UnsupportedEncodingException, IOException {
		JsonReader jr = new JsonReader(new BufferedReader(new InputStreamReader(in,"UTF-8")));
		jr.setLenient(true);
		
		jr.beginObject();
		while (jr.peek() != JsonToken.NUMBER) {
			jr.skipValue();
		}
		int response = jr.nextInt();
		jr.endObject();
		return response;
	}
	
}

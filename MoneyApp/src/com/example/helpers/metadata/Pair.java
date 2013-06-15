package com.example.helpers.metadata;

public class Pair<S, T> {

	private S first;
	private T second;
	
	
	public Pair(S first, T second) {
		super();
		this.first = first;
		this.second = second;
	}
	
	public S getFirst() {
		return first;
	}
	public void setFirst(S first) {
		this.first = first;
	}
	public T getSecond() {
		return second;
	}
	public void setSecond(T second) {
		this.second = second;
	}

	@Override
	public String toString() {
		return "Pair [first=" + first + ", second=" + second + "]";
	}
	
	
	
	
}

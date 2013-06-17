package com.example.helpers;

import java.math.BigDecimal;

public class MyMath {

	public static final double EPSILON = 0.0000001;
	
	public static double round(double unrounded)
	{
	    BigDecimal bd = new BigDecimal(unrounded);
	    BigDecimal rounded = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
	    return rounded.doubleValue();
	}
	
	public static boolean practically_equal(double amount, double sum) {
		return Math.abs(amount-sum) < EPSILON;
	}
}

package com.example.helpers;

import java.math.BigDecimal;

public class MyMath {

	public static double round(double unrounded)
	{
	    BigDecimal bd = new BigDecimal(unrounded);
	    BigDecimal rounded = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
	    return rounded.doubleValue();
	}
	
}

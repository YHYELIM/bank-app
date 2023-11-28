package com.tenco.mybankapp.utils;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class BalanceUtil {
	
	public static String balanceToString(Long balance) {
		DecimalFormat decimalFormat = new DecimalFormat("###.###");
		return decimalFormat.format(balance);
		
	}
}





package jyotish.main.calc;

import jyotish.main.BirthData;

/**
 * @author Michael W. Taft
 * 16 May, 2003
 * 
 */
public class BirthDataValidator {
	///CURRENTLY CHECKS A BIRTH DATA OBJECT FOR BASIC DATA BOUNDARY VIOLATIONS
	public static void checkBirthDataValidity(BirthData inputData)
		throws FJCalculationInputException {
		try {
// System.out.printf("GMT: %s, DST: %s%n", inputData.getTimeZoneOffset(), inputData.getDstOffset());
// System.out.printf("GMT Time Zone Name: %s%n", inputData.getGmtOffsetName());
// System.out.printf("Latitude: %f, Longitude: %f%n", inputData.getLongitude(), inputData.getLatitude());
			checkLongitude_Deg(inputData.getLongitude());
			checkLatitude_Deg(inputData.getLatitude());
			//checkDSTOffset(inputData.getDSTOffset());
			//checkTimeZoneOffset(inputData.getTimeZoneOffset());
			/*
			 * compareTZandLongitude( inputData.getTimeZoneOffset(),
			 * inputData.getLongitude_Deg());
			 */
		} catch (FJCalculationInputException fje) {
			//System.out.println(fje);
		}
	}
	private static void checkLongitude_Deg(double d)
		throws FJCalculationInputException {
		String errorMsg = "Longitude Degree value out of bounds.";
		if (-180 > d || d > 180)
			throw new FJCalculationInputException(errorMsg);
	}
	private static void checkLatitude_Deg(double d)
		throws FJCalculationInputException {
		String errorMsg = "Latitude Degree value out of bounds.";
		if (-90 > d || d > 90)
			throw new FJCalculationInputException(errorMsg);		
	}
	
	/*private static void checkMinSec(int minSec)
		throws FJCalculationInputException {
		String errorMsg =
			"Longitude/Latitude minutes or seconds value out of bounds.";
		if (0 > minSec || minSec > 60)
			throw new FJCalculationInputException(errorMsg);
	}*/
	
	@SuppressWarnings("unused")
	private static void checkDSTOffset(double dstOffset)
		throws FJCalculationInputException {
		String errorMsg = "Daylight Savings Time value out of bounds.";
		if (0 > dstOffset && dstOffset > 2)
			throw new FJCalculationInputException(errorMsg);
	}
	@SuppressWarnings("unused")
	private static void checkTimeZoneOffset(double tzOffset)
		throws FJCalculationInputException {
		String errorMsg = "Time Zone value out of bounds.";
		if (-13.0 > tzOffset || tzOffset > 13.0)
			throw new FJCalculationInputException(errorMsg);
	}
	public static void compareTZandLongitude(
		double tzOffset,
		double d)
		throws FJCalculationInputException {
		//Sign on value must be + or - for both. They cannot be different signs.
		String errorMsg = "Time Zone and Longitude are mismatched.";
		if (0 <= d && 0.0 <= tzOffset)
			return;
		else if (0 >= d && 0.0 >= tzOffset)
			return;
		else
			throw new FJCalculationInputException(errorMsg);
	}
}

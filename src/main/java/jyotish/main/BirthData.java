package jyotish.main;

import java.io.Serializable;
import java.util.Comparator;
// 15 February, 2003, Michael W. Taft
// Reducing the complexity of the earlier version of this class
//


/**
 *  Description of the Class
 *
 *@author     Michael W. Taft
 *@created    February 16, 2003
 */
@SuppressWarnings("serial")
public class BirthData implements Serializable, Comparable<Object>
{

	private String firstName, lastName;
	private String birthCity, birthState, birthCountry;
	private double longitude, latitude;
	private int birthDay,birthYear, birthMonth, birthDate, birthHour, birthMinute, birthSecond;
	private double timeZoneOffset, dstOffset, utcOffset;
	
	public double getUtcOffset() {
		return utcOffset;
	}


	public void setUtcOffset(double utcOffset) {
		this.utcOffset = utcOffset;
	}

	private String gmtOffsetName;
	private int sex;
	private boolean isNorth, isEast, isWest, isSouth;//These have yet to be implemented

	public boolean getIsSouth() {
		return isSouth;
	}


	public void setIsSouth(boolean isSouth) {
		this.isSouth = isSouth;
	}


	public boolean getIsWest() {
		return isWest;
	}


	public void setIsWest(boolean isWest) {
		this.isWest = isWest;
	}


	/**
	 *  Constructor for the BirthData object
	 */
	public BirthData() { }


	/**
	 *  Sets the firstName attribute of the BirthData object
	 *
	 *@param  first  The new firstName value
	 */
	public void setFirstName(String first)
	{
		//System.out.println("First name set as: " + first);//TESTING ONLY
		firstName = first;
	}


	/**
	 *  Gets the firstName attribute of the BirthData object
	 *
	 *@return    The firstName value
	 */
	public String getFirstName()
	{
		return firstName;
	}


	/**
	 *  Sets the lastName attribute of the BirthData object
	 *
	 *@param  last  The new lastName value
	 */
	public void setLastName(String last)
	{
		lastName = last;
	}


	/**
	 *  Gets the lastName attribute of the BirthData object
	 *
	 *@return    The lastName value
	 */
	public String getLastName()
	{
		return lastName;
	}


	/**
	 *  Sets the birthCity attribute of the BirthData object
	 *
	 *@param  city  The new birthCity value
	 */
	public void setBirthCity(String city)
	{
		birthCity = city;
	}


	/**
	 *  Gets the birthCity attribute of the BirthData object
	 *
	 *@return    The birthCity value
	 */
	public String getBirthCity()
	{
		return birthCity;
	}


	/**
	 *  Sets the birthState attribute of the BirthData object
	 *
	 *@param  state  The new birthState value
	 */
	public void setBirthState(String state)
	{
		birthState = state;
	}


	/**
	 *  Gets the birthState attribute of the BirthData object
	 *
	 *@return    The birthState value
	 */
	public String getBirthState()
	{
		return birthState;
	}


	/**
	 *  Sets the birthCountry attribute of the BirthData object
	 *
	 *@param  country  The new birthCountry value
	 */
	public void setBirthCountry(String country)
	{
		birthCountry = country;
	}


	/**
	 *  Gets the birthCountry attribute of the BirthData object
	 *
	 *@return    The birthCountry value
	 */
	public String getBirthCountry()
	{
		return birthCountry;
	}

	/////LONGITUDE
	
	public void setLongitude(double ld)
	{
		longitude = ld;
	}


	public double getLongitude()
	{
		return longitude;
	}
	
	public void setIsEast(boolean east)
	{
		isEast = east;
	}
	
	public boolean getIsEast()
	{
		return isEast;
	}
	/////LATITUDE
	
	public void setLatitude(double ltd)
	{
		latitude = ltd;
	}


	public double getLatitude()
	{
		return latitude;
	}

	public void setIsNorth(boolean north)
	{
		isNorth = north;
	}
	
	public boolean getIsNorth()
	{
		return isNorth;
	}
	
	/**
	 *  Sets the birthDay attribute of the BirthData object
	 *
	 *@param  year  The new birthDay value
	 */
	public void setBirthDay(int day)
	{
		birthDay = day;
	}


	/**
	 *  Gets the birthDay attribute of the BirthData object
	 *
	 *@return    The birthDay value
	 */
	public int getBirthDay()
	{
		return birthDay;
	}
	
	/**
	 *  Sets the birthYear attribute of the BirthData object
	 *
	 *@param  year  The new birthYear value
	 */
	public void setBirthYear(int year)
	{
		birthYear = year;
	}


	/**
	 *  Gets the birthYear attribute of the BirthData object
	 *
	 *@return    The birthYear value
	 */
	public int getBirthYear()
	{
		return birthYear;
	}


	/**
	 *  Sets the birthMonth attribute of the BirthData object
	 *
	 *@param  month  The new birthMonth value
	 */
	public void setBirthMonth(int month)
	{
		birthMonth = month;
	}


	/**
	 *  Gets the birthMonth attribute of the BirthData object
	 *
	 *@return    The birthMonth value
	 */
	public int getBirthMonth()
	{
		return birthMonth;
	}


	/**
	 *  Sets the birthDate attribute of the BirthData object
	 *
	 *@param  date  The new birthDate value
	 */
	public void setBirthDate(int date)
	{
		birthDate = date;
	}


	/**
	 *  Gets the birthDate attribute of the BirthData object
	 *
	 *@return    The birthDate value
	 */
	public int getBirthDate()
	{
		return birthDate;
	}


	/**
	 *  Sets the birthHour attribute of the BirthData object
	 *
	 *@param  hour  The new birthHour value
	 */
	public void setBirthHour(int hour)
	{
		birthHour = hour;
	}


	/**
	 *  Gets the birthHour attribute of the BirthData object
	 *
	 *@return    The birthHour value
	 */
	public int getBirthHour()
	{
		return birthHour;
	}


	/**
	 *  Sets the birthMinute attribute of the BirthData object
	 *
	 *@param  minute  The new birthMinute value
	 */
	public void setBirthMinute(int minute)
	{
		birthMinute = minute;
	}


	/**
	 *  Gets the birthMinute attribute of the BirthData object
	 *
	 *@return    The birthMinute value
	 */
	public int getBirthMinute()
	{
		return birthMinute;
	}


	/**
	 *  Sets the birthSecond attribute of the BirthData object
	 *
	 *@param  second  The new birthSecond value
	 */
	public void setBirthSecond(int second)
	{
		birthSecond = second;
	}


	/**
	 *  Gets the birthSecond attribute of the BirthData object
	 *
	 *@return    The birthSecond value
	 */
	public int getBirthSecond()
	{
		return birthSecond;
	}


	/**
	 *  Sets the timeZoneOffset attribute of the BirthData object
	 *
	 *@param  tzo  The new timeZoneOffset value
	 */
	public void setTimeZoneOffset(double tzo)
	{
		timeZoneOffset = tzo;
	}


	/**
	 *  Gets the timeZoneOffset attribute of the BirthData object
	 *
	 *@return    The timeZoneOffset value
	 */
	public double getTimeZoneOffset()
	{
		return timeZoneOffset;
	}


	/**
	 *  Sets the dSTOffset attribute of the BirthData object
	 *
	 *@param  dst  The new dSTOffset value
	 */
	public void setDstOffset(double dst)
	{
		dstOffset = dst;
	}


	/**
	 *  Gets the dSTOffset attribute of the BirthData object
	 *
	 *@return    The dstOffset value
	 */
	public double getDstOffset()
	{
		return dstOffset;
	}


	/**
	 *  Sets the sex attribute of the BirthData object
	 *
	 *@param  sx  The new sex value
	 */
	public void setSex(int sx)
	{
		sex = sx;
	}


	/**
	 *  Gets the sex attribute of the BirthData object
	 *
	 *@return    The sex value
	 */
	public int getSex()
	{
		return sex;
	}
	
	
	public String toString()
	{
		return firstName + " " + lastName.trim();	
	}
	
	public int compareTo(Object anotherNative) throws ClassCastException
	{
		if (!(anotherNative instanceof BirthData)) throw new ClassCastException ("A BirthData object expected.");
		String anotherNativeLastName = ((BirthData) anotherNative).getLastName();
		String lastName1 = anotherNativeLastName.toUpperCase();
		String lastName2 = this.getLastName().toUpperCase();
		
			return lastName1.compareTo(lastName2);
		
	}
	
	public String getGmtOffsetName() {
		return gmtOffsetName;
	}


	public void setGmtOffsetName(String gmtOffsetName) {
		this.gmtOffsetName = gmtOffsetName;
	}

	public static Comparator<Object> FirstNameComparator = new Comparator<Object>()
	{
		public int compare (Object firstNative, Object anotherNative)
		{
			String lastName1 = ((BirthData) firstNative).getLastName().toUpperCase();
			String firstName1 = ((BirthData) firstNative).getFirstName().toUpperCase();
			String lastName2 = ((BirthData) anotherNative).getLastName().toUpperCase();
			String firstName2 = ((BirthData) anotherNative).getFirstName().toUpperCase();
			if (!(firstName1.equals(firstName2)))
				return firstName1.compareTo(firstName2);
			else 
				return lastName1.compareTo(lastName2);
	}};
}


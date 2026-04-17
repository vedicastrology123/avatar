package jyotish.main.calc;

import java.util.Hashtable;

/*
*	Created 28 April, 2003 by Michael Taft
*	A new, more flexible type of preferences object
*	From an idea suggested by Ken Lee
*/

public class CalculationPreferences
{
	private Hashtable<String, Object> prefs;
	
	public void setPreference(String name, Object o)
	{
		if (prefs == null) prefs = new Hashtable<String, Object>();
		prefs.put(name, o);
	}
	
	public Object getPreference(String name)
	{
		if (prefs == null) return null;
		return prefs.get(name);
	}
	
	public Hashtable<String, Object> getAllPreferences()
	{
		if (prefs == null) return null;
		return prefs;
	}


}

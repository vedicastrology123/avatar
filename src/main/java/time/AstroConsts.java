package time;

public class AstroConsts {

    /*public static final int[] planetOwners =
        { 2, 5, 3, 1, 0, 3, 5, 2, 4, 6, 6, 4
         };*/
    
    // Corresponds to planet numbers in swiss eph.
    /*public static final int[] planetNo = { 0, 1, 4, 2, 5, 3, 6, 11, 12
         };*/

	public static final double nakLength = ( double ) ( 360.00 / 27.0 );
	
	public static final double padaLength = ( double ) ( 360.00 / 108.0 );
	
	public static final double thithiLength = 12.0;
	
	public static final double karanaLength = 6.0;
	
	public static final double yogaLength = nakLength;
	
	public static final double rasiLength = 30.0;
	
	public static final double MILLIS_IN_DAY = 24 * 60 * 60 * 1000;
	
	public static final long MILLIS_IN_HR = (1000 * 60 * 60);
	
	public enum Grahas {
	    Ketu, Shukra, Surya, Chandra, Kuja, Rahu, Guru, Shani, Budha
	}
}
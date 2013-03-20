package firstpage;

public class P15 {

	
	public static void main( String[] args ) {
		// answer = 40! / (20! * 20!)
		// = (40*39*...*22*21) / (20!)
		// = (2*39*2*27*...*2*21) / (10!)

		// 2
		
		long l = 2 * 39;
		System.out.println( l );
		l *= 2;
		System.out.println( l );
		l *= 37;
		System.out.println( l );
		l *= 2;
		System.out.println( l );
		l *= 35;
		System.out.println( l );
		System.out.println( l % 10 );
		l /= 10;
		System.out.println( l );
		System.out.println( l % 7 );
		l /= 7;
		System.out.println( l );
		l *= 2;	
		System.out.println( l );
		l *= 33;	
		System.out.println( l );
		System.out.println( l % 9 );
		l /= 9;
		System.out.println( l );
		l *= 2;	
		System.out.println( l );
		l *= 31;	
		System.out.println( l );
		l *= 2;	
		System.out.println( l );
		System.out.println( l % 8 );
		l /= 8;
		System.out.println( l );
		l *= 29;	
		System.out.println( l );
		l *= 2;
		System.out.println( l );
		System.out.println( l % 4 );
		l /= 4;
		System.out.println( l );
		l *= 27;
		System.out.println( l );
		System.out.println( l % 6 );
		l /= 6;
		System.out.println( l );
		System.out.println( l % 3 );
		l /= 3;
		System.out.println( l );
		l *= 2;
		System.out.println( l );
		l *= 25;
		System.out.println( l );
		System.out.println( l % 5 );
		l /= 5;
		System.out.println( l );
		System.out.println( l % 2 );
		l /= 2;
		System.out.println( l );
		l *= 2;
		System.out.println( l );
		l *= 23;
		System.out.println( l );
		l *= 2;
		System.out.println( l );
		l *= 21;
		System.out.println( l );
	}
}
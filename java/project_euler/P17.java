package firstpage;

public class P17 {

	static int ONE = 3;
	static int TWO = 3;
	static int THREE = 5;
	static int FOUR = 4;
	static int FIVE = 4;
	static int SIX = 3;
	static int SEVEN = 5;
	static int EIGHT = 5;
	static int NINE = 4;
	static int TEN = 3;
	static int ELEVEN = 6;
	static int TWELVE = 6;
	static int THIRTEEN = 8;
	static int FOURTEEN = 8;
	static int FIFTEEN = 7;
	static int SIXTEEN = 7;
	static int SEVENTEEN = 9;
	static int EIGHTEEN = 8;
	static int NINETEEN = 8;
	static int TWENTY = 6;
	static int THIRTY = 6;
	static int FORTY = 5;
	static int FIFTY = 5;
	static int SIXTY = 5;
	static int SEVENTY = 7;
	static int EIGHTY = 6;
	static int NINETY = 6;
	static int HUNDRED = 7;
	static int AND = 3;
	static int THOUSAND = 8;
	
	public static void main(String[] args) {
		System.out.println( oneToOneThousand() );
	}
	
	public static int oneToTen() {
		int result = ONE + TWO + THREE + FOUR + FIVE + SIX + SEVEN + EIGHT + NINE + TEN;
		return result;
	}
	
	public static int elevenToNineteen() {
		int result = ELEVEN + TWELVE + THIRTEEN + FOURTEEN + FIFTEEN + SIXTEEN + SEVENTEEN + EIGHTEEN + NINETEEN;
		return result;
	}
	
	public static int tensInvariant() {
		int result = ONE + TWO + THREE + FOUR + FIVE + SIX + SEVEN + EIGHT + NINE;
		return result;
	}
	
	public static int twentyToNinetyNine() {
		int result = 8 * tensInvariant();
		result += ( TWENTY + THIRTY + FORTY + FIFTY + SIXTY + SEVENTY + EIGHTY + NINETY ) * 10;
		
		return result;
	}
	
	public static int hundredsInvariant() {
		int result = oneToTen() + elevenToNineteen() + twentyToNinetyNine();
		return result;
	}
	
	public static int andsHundredInvariant() {
		int result = 99 * AND;
		result += ( 100 * HUNDRED );
		return result;
	}
	
	public static int oneToOneThousand() {
		int result = hundredsInvariant() * 10;
		result += ( andsHundredInvariant() * 9 );
		result += ( tensInvariant() * 100 );
		result += ( ONE + THOUSAND );
		
		return result;
	}
}

package firstpage;


public class P19 {
	

	public static void main(String[] args) {
		
		Day firstDay = new Day(
			2, // Monday
			1, // 1st
			1, // January
			1900
		);
		
		Day lastDay = new Day(
			1, // doesn't matter
			31, // 31st
			12, // December
			2000
		);
		
		Day firstOfCentury = new Day(
			1, // doesn't matter
			1, // 1st
			1, // January
			1901
		);
		
		int firstDaySundays = 0;
		
		while ( firstDay.compareTo( lastDay ) < 0 ) {
			if ( firstDay.compareTo( firstOfCentury ) >= 0 ) {
				if ( isFirstOfMonthSunday( firstDay ) )
					firstDaySundays++;
			}
			
			firstDay = Day.getNextDay( firstDay );
		}
		System.out.println( firstDaySundays );
	}
	
	private static boolean isFirstOfMonthSunday( final Day aDay ) {
		return aDay.getDayOfMonth() == 1 && aDay.getDayOfWeek() == 1;
	}
	
	
	 private static class Day implements Comparable<Day> {
		
		private static final int[] daysPerMonth = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
		private static final int leapYearFebruary = 29;
		
		private final int dayOfWeek; // 1 is Sunday, 7 is Saturday
		private final int dayOfMonth; // 1 is first
		private final int monthOfYear; // 1 is January, 12 is December
		private final int year; // 1900 to 2001
		
		public Day( 
			final int dayOfWeek,
			final int dayOfMonth,
			final int monthOfYear,
			final int year
		) {
			if ( ! isValidDay( dayOfWeek ) )
				throw new IllegalArgumentException();
			
			if ( ! isValidMonth( monthOfYear ) )
				throw new IllegalArgumentException();
			
			if ( ! isValidYear( year ) )
				throw new IllegalArgumentException();
			
			if ( ! isValidDayOfMonth( dayOfMonth, monthOfYear, year ) )
				throw new IllegalArgumentException();
			
			this.dayOfWeek = dayOfWeek;
			this.dayOfMonth = dayOfMonth;
			this.monthOfYear = monthOfYear;
			this.year = year;
		}

		private static boolean isValidDayOfMonth(
			int aDayOfMonth, 
			int aMonthOfYear,
			int aYear
		) {
			if ( aDayOfMonth < 1 || aDayOfMonth > 31 )
				return false;
			
			if ( 
				aMonthOfYear == 4 || // April
				aMonthOfYear == 6 || // June
				aMonthOfYear == 9 || // September
				aMonthOfYear == 11 // November
			) {
				if ( aDayOfMonth == 31 )
					return false;
			}
			
			if ( aMonthOfYear == 2 && aDayOfMonth == 30 )
				return false;
			
			if ( ! isLeapYear( aYear ) ) {
				if ( aMonthOfYear == 2 && aDayOfMonth == leapYearFebruary )
					return false;
			}
			
			return true;
		}
		
		public static boolean isLeapYear( final int aYear ) {
			
			// must be divisible by 4
			if ( aYear % 4 != 0 )
				return false;
			
			// if it's a century
			if ( aYear % 100 == 0 ) {
				// divisble by 400 -> leap year
				if ( aYear % 400 == 0 )
					return true;

				// centuries not divisible by 400 -> no leap year
				return false;
			}
			
			// otherwise, it's a leap year
			return true;
		}
		
		public static boolean isValidDay( final int aDay ) {
			return aDay >= 1 && aDay <= 7;
		}
		
		public static boolean isValidMonth( final int aMonth ) {
			return aMonth >= 1 && aMonth <= 12;
		}
		
		public static boolean isValidYear( final int aYear ) {
			return aYear >= 1582 && aYear <= 2015;
		}
		
		public static int getNextDayOfWeek( int aDay ) {
			if ( ! isValidDay( aDay ) )
				throw new IllegalArgumentException();
			
			int result = aDay + 1;
			if ( result > 7 )
				result = 1;
			
			return result;
		}
		
		
		private static boolean isLastOfMonth( final int aDay, final int aMonth, final int aYear ) {
			if ( isLeapYear( aYear ) ) {
				if ( aMonth == 2 )
					return aDay == 29;					
			}

			return daysPerMonth[ aMonth - 1 ] == aDay;
		}
		
		private static boolean isLastOfYear( final int aDay, final int aMonth ) {
			return aDay == 31 && aMonth == 12;
		}
		
		public static Day getNextDay( final Day previousDay ) {
			int newDayOfMonth;
			int newDayOfWeek;
			int newMonth;
			int newYear;
			
			if ( isLastOfMonth( previousDay.getDayOfMonth(), previousDay.getMonthOfYear(), previousDay.getYear() ) ) {
				if ( isLastOfYear( previousDay.getDayOfMonth(), previousDay.getMonthOfYear() ) ) {
					newDayOfMonth = 1; // first of new month
					newDayOfWeek = getNextDayOfWeek( previousDay.getDayOfWeek() ); // next day of week
					newMonth = 1; // first month of new year
					newYear = previousDay.getYear() + 1; // next year
					
				} 
				else {
					newDayOfMonth = 1; // first of new month
					newDayOfWeek = getNextDayOfWeek( previousDay.getDayOfWeek() ); // next day of week
					newMonth = previousDay.getMonthOfYear() + 1; // next month
					newYear = previousDay.getYear(); // same year
				}
			}
			else {
				newDayOfMonth = previousDay.getDayOfMonth() + 1; // next day of month
				newDayOfWeek = getNextDayOfWeek( previousDay.getDayOfWeek() ); // next day of week
				newMonth = previousDay.getMonthOfYear(); // same month
				newYear = previousDay.getYear(); // same year
			}
			
			return new Day(
				newDayOfWeek,
				newDayOfMonth,
				newMonth,
				newYear
			);
		}

		public int getDayOfWeek() {
			return this.dayOfWeek;
		}

		public int getDayOfMonth() {
			return this.dayOfMonth;
		}

		public int getMonthOfYear() {
			return this.monthOfYear;
		}
		
		public int getYear() {
			return this.year;
		}

		@Override
		public int compareTo( Day otherDay ) {
			
			if ( otherDay.getYear() < getYear() )
				return 1;
			if ( otherDay.getYear() > getYear() )
				return -1;
			if ( otherDay.getMonthOfYear() < getMonthOfYear() )
				return 1;
			if ( otherDay.getMonthOfYear() > getMonthOfYear() )
				return -1;
			if ( otherDay.getDayOfMonth() < getDayOfMonth() )
				return 1;
			if ( otherDay.getDayOfMonth() > getDayOfMonth() )
				return -1;
			
			return 0;
		}

		@Override
		public String toString() {
			return "Day [dayOfWeek=" + dayOfWeek + ", dayOfMonth=" + dayOfMonth
					+ ", monthOfYear=" + monthOfYear + ", year=" + year + "]";
		}
	}
}

#!/usr/bin/perl -w

use strict;

print "\n\nEnter letters to unscramble.\nPut spaces between different words: ";

chomp (my $jumbles = <STDIN>); # user enters list of scrambled words, divided by spaces
$jumbles = uc($jumbles); # letters are all uppercased, to match word banks

my @jumble_list = split (/ /, $jumbles); # entry is split into array of terms

my @count_list;  # will be array with 26 elements, each being the number
                         # of occurrences of a letter in the current jumbled "term."
                         # resets to empty set for each term.
                         # MUST BE DECLARED OUTSIDE FOREACH, or it will not reset to
                         # empty set with each call of the loop.
my @answers;     # will be an array of matches, for each term. MUST BE DECLARED before loop.

foreach my $term (@jumble_list)
    {my @guess_letters = split(//, $term); # split current word into its characters

    if (scalar (@guess_letters) == 3)
           {open (FILE, '<', 'three_letter_words.txt') or die $!;} #opens correct wordlist
    elsif (scalar (@guess_letters) == 4)
           {open (FILE, '<', 'four_letter_words.txt') or die $!;}
    elsif (scalar (@guess_letters) == 5)
           {open (FILE, '<', 'five_letter_words.txt') or die $!;}
    elsif (scalar (@guess_letters) == 6)
           {open (FILE, '<', 'six_letter_words.txt') or die $!;}
    elsif (scalar (@guess_letters) == 7)
           {open (FILE, '<', 'seven_letter_words.txt') or die $!;}
    else {print "Number of letters not available.\n";
	   exit;}

    my  $word_bank = <FILE>; # initializes word_bank to the text of appropriate file
    close FILE;
    my $count; # will be used a few times, as a counter


    @count_list = ();


    foreach my $find ('A'..'Z')  # $find is the letter to look for; must be uppercase to match
            {$count = 0;
	     while ($term =~ /$find/g)  # counts how many of each letter are in jumbled word
	             {$count ++ ;}
	     push @count_list, $count; # makes array of the counts, in order
	     }


    my @word_list = split(/ /, $word_bank); # wordlist is an array of the word_bank
    @answers = ();                       # this will be an array of matches, for each "term"

    my $match_counter;      # will count how many letters from term appear in each bank word,
                            # as a quick initial search for narrowing the array down

    foreach my $element (@word_list) # for each banked word . . .
            {$match_counter = 0;
	    foreach my $letter (@guess_letters) # for each letter of term . . .
	            {if ($element =~ /$letter/) # if the bank word has that letter,
		              {$match_counter ++;} # count it as a matched letter.
		     }                             # after all letters have been checked . . .
	     if ($match_counter == scalar (@guess_letters)) # if every one matched,
		     {count_check ($element);}              # run a thorough check on the
	     }                                              # banked word.


    sub count_check
           {my @compare_list;                     # array of count for each letter, for "$element"
	    foreach my $seek ('A'..'Z')           # (must be uppercase)
	            {$count = 0;
		    while ($_[0] =~ /$seek/g)    # for each occurrence of the current letter
                                                 # in the banked word ($_[0]) . . .
		             {$count ++ ;}       # count it.
		     push @compare_list, $count; # makes an array of each count, in order
		    }

	    $count = 0;
	    foreach my $index (0..25)
	           {if ($compare_list[$index] == $count_list[$index])  # if each letter occurs
		                                                       # equal times in both . . .
		            {$count ++;}                               # make a tally
		   }
	    if ($count == 26)                    # if all letters occur equal times . . .
	           {push @answers, $_[0];}       # add the banked word to the answers list.
            }

     print "@answers\n\n";
     }

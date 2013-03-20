#!/usr/bin/perl     # purposely omitted -w warning flag, to avoid "deep recursion" warnings with high disc numbers

use strict;

##########################################

my $total = 10; # total number of disks in the puzzle; entered by the user.
my $delay; # delay between moves, in milliseconds; user may enter if desired.
my $time;
my $sec_count;

my @board; # a matrix of the "board": by row, then by column. Stores the letter of the "disc" in each place, null if empty.
my $column;
my $tier;
my @top_card; #finds the top disc in each column (skipping the column with disc "A")
my $whose_turn = 0; # 0 means it's A's turn to move; A alternates moves with other discs.
my $a_column = 0; # tells in which column A currently sits.
my $move_to; # where a non-A letter will move.

my $move_counter = 0; # counts total number of moves used so far

###########################################

welcome_screen ();

sub welcome_screen
  {print "\n\n\n\n\n\n\n\n\n\n\n\n##############################\n\n\n\nTower of Hanoi Alarm Clock";
   print "\n\n\n\nThe monks of Hanoi use their famed towers to count down to the end of time.";
   print "\n\nWhat time would you like that to be? (24 hour time)";
   prompt_time ();
  }

sub prompt_time
  {print "\nhrs:min:sec: ";
   chomp($time = <STDIN>);
   unless ($time =~ /^\d+:\d+:\d+$/)
          {print "\nWhat time to count down to?\nMust be in the format #:#:#.";
	   prompt_time ();
	  }
   my @split_time = split(/:/, $time); 
   if ($split_time[0] > 23)
          {print "\nMaximum 23 hours.\nWhat time to count down to? ";
	   prompt_time ();
	  }
   if ($split_time[1] > 59)
          {print "\nMaximum 59 minutes.\nWhat time to count down to? ";
	   prompt_time ();
	  }
   if ($split_time[2] > 59)
          {print "\nMaximum 59 seconds.\nWhat time to count down to? ";
	   prompt_time ();
	  }
   my @time_now = localtime(time);
   my $sec_now = 3600 * $time_now[2] + 60 * $time_now[1] + $time_now[0];
   my $sec_then = 3600 * $split_time[0] + 60 * $split_time[1] + $split_time[2];

   my $sec_wait = $sec_then - $sec_now;
   if ($sec_wait < 1)
         {$sec_wait += 3600 * 24;
	 }
  
   $delay = $sec_wait / 1023;
   initialize_board ();
  }

sub initialize_board
    {foreach (0..($total - 1))
          {my $char = chr(65 + ($total - 1) - $_);
           $board[0][$_] = $char;
	  }
     display_board ();
    }


sub display_board
  {print "\n\n\n\n\n\n\n\n\n\n\n\n\n";
   my $column = $total - 1;
   while ($column >= 0)
        {foreach my $row (0..2)
	      {if (defined $board[$row][$column])
		    {print "$board[$row][$column]\t";
		    }
	       else
		    {print " \t";
		    }
	      }
	 print "\n";
	 $column--;
        }
   print "=\t=\t=\n";
   checkmate ();
  }


sub checkmate
    {if ((defined $board[1][($total - 1)]) || (defined $board[2][($total - 1)]))
       {print "\n\nEND OF WORLD!\a\n\n\n";
	exit;
       }
     elsif ($whose_turn == 0)
       {a_moves ();}
     elsif ($whose_turn == 1)
       {another_moves ();}
    }


sub a_moves
      {if ($a_column < 2)
	     {push @{$board[($a_column + 1)]}, 'A';
	      pop  @{$board[($a_column)]};
	      $a_column++;
	     }
       else
	     {push @{$board[0]}, 'A';
	      pop  @{$board[2]};
	      $a_column = 0;
	     }
       $whose_turn = 1;
       move_made ();
     }


sub another_moves
	{find_low_topcard ();
        }


sub find_low_topcard
     {@top_card = 'Z';
	foreach $column (0..2)
	   {if ($column != $a_column)
	          {$tier = $total - 2;
	           while ($tier >=0)
	                 {if (!defined $board[$column][$tier])
		              {$tier--;
		               next;
			      }
	                  else
		              {my $trial = ord($board[$column][$tier]);
		               my $benchmark = ord($top_card[0]);
		               if ($trial < $benchmark)
		                    {@top_card = ($board[$column][$tier], $column, $tier);
			            }
		               last;
		              }
			 }
	           }
	   }
     where_to ();
     }


sub where_to
      {$move_to;
       foreach my $location (0..2)
	     {if ($location != $top_card[1] && $location != $a_column)
		   {$move_to = $location;
		    last;
		   }
	     }
       move_topcard ();
      }


sub move_topcard
      {push @{$board[$move_to]}, $board[($top_card[1])][($top_card[2])];
       pop  @{$board[($top_card[1])]};
       $whose_turn = 0;
       move_made ();
      }


sub move_made
      {$move_counter++;
       $| = 1;
       select (undef, undef, undef, $delay);
       $| = 0;
       display_board ();
      }

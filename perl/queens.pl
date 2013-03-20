#!/usr/bin/perl
# queens.pl

use strict;

my @board; # [row][column]

my $row = 0;
my $column = 0;
my @chain; # holds the current candidate chain.
my $check; # 1 means queen currently being tested is allowable, 0 not
my @solutions;

print "\n\n\nI will show you every way to arrange queens on a chessboard, one per row, so the queens cannot attack each other.\n\n";
print "How many rows or columns per side of the board? ";
chomp(my $size = <STDIN>);

add_queen();

sub add_queen
    {if ($column == $size)                        # if you've gone past the last column
       {$column = $chain[$#chain] + 1;        # set column to 1 greater than the last tried in chain
	pop @chain;                           # remove last entry from answer chain
	$row--;                               # move up a row in your search
	undef @{board[($row)]};              # undefine that row of the board
	if ($row == -1)                      # if this puts you above the top row
	    {closing();
	    }
	add_queen();                          # try adding to that space.
       }	 
	
     else                                    # if there is still space in the row you're searching
        {check_queen();                      # check to see if a queen could go there.
	 if ($check == 0)                    # if not,
	       {$column ++;                      # move a column to the right and see if that's too far.
		add_queen();
	        }
	 elsif ($check == 1)                # if you CAN add a queen there,
	      {push @chain, $column;           # push the column value to the chain
	       if ($row == $size - 1)                   # if this Q was in the final row (solution found)
		    {add_solution();
		    }
	       else
	            {$board[$row][$column] = 'Q';    # and add a 'Q' to that cell of the board layout
		     $row++;                          # if it's not the final row, go to next row, set column to 0
		     $column = 0;
		     add_queen();                     # try to add the next queen.
		    }
	       }
	  }
    }


sub check_queen
    {$check = 1;
     foreach my $a (0..($size - 1))
    	     {if (defined $board[$a][$column])                       # if there's a 'Q' in same column
	     	 {$check = 0;}
     	     elsif (defined $board[$row][$a])                        # if there's a 'Q' in same row
	     	 {$check = 0;}
	     }
     foreach $a (0..$column)
             {if (defined $board[($row - $a)][($column - $a)])
		{$check = 0;
	        }
	      }
     foreach $a (0..(($size - 1) - $column))
            {if (defined $board[($row - $a)][($column + $a)])
	          {$check = 0;
		 }
	   }
   }


sub add_solution
    {my $a = $#solutions;
     my @b = @chain;
     foreach(0..($size - 1))
        {$solutions[($a + 1)][$_] = shift @b;
        }
     $column = $chain[$#chain] + 1;        # set column to 1 greater than the last tried in chain
     pop @chain;
     add_queen();
    }


sub closing
      {if(scalar(@solutions) == 0)
	  {print "\n\nNo solutions found.\n\n";
	   exit;
	   }
       print "\n\n\nSolutions:\nEach solution lists the column number of every queen, from the top row to the bottom row of the board.\n\n";
       print "\ttop row","\t"x($size - 1),"bottom row\n";
       foreach my $a (0..$#solutions)
	      {my $c = $a + 1;
	       print "$c:\t";
       	       foreach my $b (0..($size - 1))
       		     {my $d = $solutions[$a][$b] + 1;
	       	      print "$d\t";
	       	     }
       	      print "\n";
       	      }
        print "\n";
        exit; 	     
      }

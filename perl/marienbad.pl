#!/usr/bin/perl -w

use strict;

### setting up the board: ###

my @piles = (1, 3, 5, 7); # number of stones in each pile
my $turn; # 1 for your turn, 0 for computer's turn.


### for making moves: ###

my $bag; # a letter from standard input, tells which pile is chosen to draw from. Later transliterated to the index of that pile in the piles array.
my $bag_letter; # stores the letter form of bag after transliteration.
my $handful; # number from standard input: how many stones to draw from pile "bag".
my $plural_counter; # counts the piles with multiple stones remaining.


### for navigating menus: ###

my $action; # stored version of standard input (help, pile choice, etc.)
my $location; # tells help menu where to go "back"


##### For choosing computer's move when nimsum does not equal zero: ######

my @list_a; # lists number of stones that can be drawn from pile A to leave a nimsum of zero (that is, to guarantee victory)
my @list_b; # same for pile b.
my @list_c;
my @list_d;
my $left_a; # lists number of stones remaining in pile "A" during the foreach loop that simulates taking stones away, then evaluates the nimsum of the board in each case.
my $left_b; # same for pile b.
my $left_c;
my $left_d;
my $counter; # counts how many one-piles are left. Used to decide whether to leave 1 or 0 stones when drawing from the last "plural pile." (You want to leave an odd number of 1-piles.)
my $taken; # counts how many stones have been "taken" when simulating possible moves.
my $list_a; # used to select a random element from the list_a array, before move is made.
my $list_b; # same for list_b array.
my $list_c;
my $list_d;



### GENERAL OUTLINE ########
#
# welcome_screen
# who_first: player chooses to go first or second
# display_board: displays current board status. Goes to choose_pile or computer_moves, alternating
# choose_pile: player chooses draw pile
# evaluate pile choice: invalid > some error loop
# draw_stones: player chooses how many stones to draw
# evaluate_draw: if too many > error loop
# remove_stones: Loops back to display board.
#
# checkmate: If one stone remains, declares win or loss, depending on turn counter.
#
# computer_moves: computer thinks and makes its play. Loops to display_board.
# count_plural_piles: leads to pick_one if none; pick_from_last_plural if one; and nimsum if more than one.
# pick_from_last_plural: counts 1-piles and decides to leave 1 or 0 stones from last plural-pile.
# nimsum: Takes the nimsum of the board (binary sum without rounding, or sequential XOR); if zero, leads to stall, but if nonzero, leads to make_zero.
# stall: draws random number of stones from random pile. returns to display_board.
# make_zero: Evaluates all moves for resultant nimsum of board. Picks random move that produces nimsum of zero. Returns to display_board.
#
##############################




welcome_screen ();

sub welcome_screen
  {print "\n\n\n++++++++++++++++++++++++++++++++++++++++++++\n\n++++++++++++++++++++++++++++++++++++++++++++\n\n\n\nNim\n\n
Hit enter to play.\n
(Anytime, type \"help\" for rules or \"quit\" to exit.)\n";
$action = <STDIN>;
# action: player's input for help, quit, enter, or back.

$location = "welcome_screen";

if ($action eq "\n")
{print "\n++++++++++++++++++++++++++++++++++++++++++++\n\n";
who_first ();}
elsif (lc($action) eq "help\n")
  {help ();}
elsif (lc ($action) eq "quit\n")
  {quit ();}
else {welcome_screen ();}}
#welcome screen may lead to help, quit, new game, or welcome screen in event of error.



sub who_first
  {
print "Who goes first? Enter \"me\" or \"computer\" ";
chomp ($action = <STDIN>);
$action = lc($action);
if ($action eq "help")
  {help ();}
elsif ($action eq "quit")
  {quit ();}
elsif ($action eq "me")
  {$turn = 1;
display_board ();}
elsif ($action eq "computer")
  {$turn = 0;
display_board ();}
else {who_first ();}}



display_board ();

sub display_board
  {
print "\n\n\nA\t$piles[0]\t", "X  " x ($piles[0]), "\n"; #prints X's representing stones.
print "B\t$piles[1]\t", "X  " x ($piles[1]), "\n";
print "C\t$piles[2]\t", "X  " x ($piles[2]), "\n";
print "D\t$piles[3]\t", "X  " x ($piles[3]), "\n\n";

checkmate (); # tests to see if someone has won; if so, skip rest of subroutine.

if ($turn == 1)     # player's turn
{choose_pile ();}
if ($turn == 0)     # computer's turn
  {computer_moves ();}
}




sub checkmate
  {
my $sum = 0;
foreach my $element (@piles)   #takes the sum of all remaining stones
  {$sum += $element;}

if ($sum == 1) #if there is only 1 stone left on the board . . .
  {
if ($turn == 1)
  {print "You lose.\n\n";
exit;}
if ($turn ==0)
  {print "You win!\n\n";
exit;}}}



sub choose_pile
{print "From which pile will you draw?";
$location = "choose_pile";
chomp ($bag = <STDIN>);
$bag = lc($bag);    # allows for capitalized or lowercase entries
if ($bag eq "help")
  {help ();}
elsif ($bag eq "quit")
  {quit ();}
evaluate_pile_choice ();}


sub evaluate_pile_choice
{
if ($bag ne "a" and $bag ne "b" and $bag ne "c" and $bag ne "d")
  {print "Invalid pile selection.\n";
choose_pile ();} # if invalid, return to choose_pile

$bag_letter = $bag;

$bag =~ tr/a-d/0-3/; # to call on chosen pile from @piles, transliterate input letter into array index, and store original letter in a new variable.

if ($piles[$bag] == 0)
  {empty_pile ("$bag_letter");} #if there are no stones in the chosen pile, error message.
else {draw_stones ();}
}

sub draw_stones
{print "How many stones will you draw?";
$location = "draw_stones";
chomp ($handful = <STDIN>);
if (lc($handful) eq "help")
  {help ();}
elsif (lc ($handful) eq "quit")
  {quit ();}
evaluate_draw ();}


sub evaluate_draw
{
if ($handful > $piles[$bag])
  {print "There are not enough stones in the pile.\n";
draw_stones ();}
else {$turn --;     # here, it becomes the computer's turn to move.
remove_stones ();}
}


sub remove_stones
  {$piles[$bag] = ($piles[$bag] - $handful);
display_board ();}


sub empty_pile
  {print "Pile $_ is empty.\n";
choose_pile ();}



sub help
  {print "\n\n++++++++++++++++++++++++++++++++++++++++++++\n\n\nTry not to be left with the last stone!\n
Take turns with the computer removing as many stones as you want from any pile.\n
First, select a pile to draw from: a, b, c, or d.
Then, enter how many stones you would like to remove.\n
You win if you leave the computer the final stone!

Type \"back\" to go back.\n";

chomp ($action = <STDIN>);
$action = lc($action);

if ($action eq "back") #decides where to go back.
{
if ($location eq "welcome_screen")
{print "\n\n\n\n";
welcome_screen ();}
elsif ($location eq "choose_pile")
  {print "\n++++++++++++++++++++++++++++++++++++++++++++\n\n\n\n";
display_board ();}
elsif ($location eq "draw_stones")
  {print "\n++++++++++++++++++++++++++++++++++++++++++++\n\n\n\n";
display_board ();}
}

if ($action eq "quit")
  {quit ();}

else {help ();}}
#help screen returns user to previous page, via location variable.



sub quit
  {print "\n\nGoodbye.\n\n\n";
exit;}


######### COMPUTER'S MOVE ###########

sub computer_moves
    {$turn ++;     # here, it becomes the player's turn again.

$| = 1;   # turning off buffering, so the sleep function will work in pieces.
sleep 1;
print "Making my move";

foreach (1..3)
  {sleep 1;
print " .";}
print "\n";

$| = 0; # turns buffering back on, after ". . ." has been written to denote thought.



count_plural_piles ();}


sub count_plural_piles
      {$plural_counter = 0;
foreach my $element (@piles)
  {
if ($element > 1)
  {$plural_counter ++;}}
if ($plural_counter > 1)
  {nimsum ();}
elsif ($plural_counter == 1)
  {pick_from_last_plural ();}
else {pick_one ();}}


sub nimsum
	{if ((($piles[0] ^ $piles[1]) ^ $piles[2] ^ $piles[3]) == 0) #if nimsum is zero already
	   {stall ();}
else
  {make_zero ();}}


####################

sub make_zero
	  {
@list_a = (); @list_b = (); @list_c = (); @list_d = (); #resets the lists to empty set from last move

$left_a = $piles[0];

if ($left_a > 0)
  {
$left_a --; # first test is on the board with one fewer stone in pile A.
$taken = 1; # the number of stones hypothetically "taken" from pile A is set at one, at first.
make_list_a ();} # this makes a list of how many stones you can draw from pile A to leave an overall nimsum of zero. the list may be empty, or may have multiple options.

sub make_list_a
{if (((($left_a ^ $piles[1]) ^ $piles[2]) ^ $piles[3]) == 0) # if nimsum is zero with a dimished pile A . . .
  {push @list_a, $taken;} # then add the number of stones hypothetically removed to our list.
$taken ++; # now increase the number hypothetically taken, for the next test
$left_a --; # and decrease the number remaining in pile A
if ($left_a >= 0) {make_list_a ();} # if the pile would not be negative, test again.
}



###

$left_b = $piles[1];

if ($left_b > 0)
  {
$left_b --;
$taken = 1;
make_list_b ();}

sub make_list_b
{
if (((($left_b ^ $piles[0]) ^ $piles[2]) ^ $piles[3]) == 0)
  {push @list_b, $taken;}
$taken ++;
$left_b --;
if ($left_b >= 0) {make_list_b ();}
}



###

$left_c = $piles[2];

if ($left_c > 0)
{$left_c --;
$taken = 1;
make_list_c ();}

sub make_list_c
{
if (((($left_c ^ $piles[0]) ^ $piles[1]) ^ $piles[3]) == 0)
  {push @list_c, $taken;}
$taken ++;
$left_c --;
if ($left_c >= 0) {make_list_c ();}
}



###


$left_d = $piles[3];

if ($left_d > 0)
{$left_d --;
$taken = 1;
make_list_d ();}

sub make_list_d
{
if (((($left_d ^ $piles[0]) ^ $piles[1]) ^ $piles[2]) == 0)
  {push @list_d, $taken;}
$taken ++;
$left_d --;
if ($left_d >= 0) {make_list_d ();}
}



###

my @pile_choices;

if (@list_a) #if there are any elements in the list (how many to draw from A to leave zero nimsum)
  {push @pile_choices, 0 x ($#list_a + 1);} # push A's index in @array, times the number of elements in the list. this will lead to a fair random move selection.)
if (@list_b)
  {push @pile_choices, 1 x ($#list_b + 1);}
if (@list_c)
  {push @pile_choices, 2 x ($#list_c + 1);}
if (@list_d)
  {push @pile_choices, 3 x ($#list_d + 1);}


$bag = $pile_choices[rand @pile_choices]; #bag index is chosen from the list of pile_choices


if ($bag == 0) #now, the number of stones to draw is chosen from the list of possibilities that would leave a zero nimsum.
  {$handful = $list_a[rand @list_a];}
if ($bag == 1)
  {$handful = $list_b[rand @list_b];}
if ($bag == 2)
  {$handful = $list_c[rand @list_c];}
if ($bag == 3)
  {$handful = $list_d[rand @list_d];}
	
remove_stones ();
 }



##########################

sub stall
	  	  {
my @options; #indexes of piles that still have at least one stone

my $index = 0; #elements of options list
foreach my $element (@piles)
  {
if ($element >= 1)
  {push @options, $index;}
$index ++;}

$bag = $options[rand @options];
$handful = int(rand($piles[$bag]));
if ($handful == 0) {$handful ++;}

remove_stones ();}




sub pick_from_last_plural
	  {
my $tally_ones = 0; # will count the piles with 1 stone.

foreach my $element (@piles)
  {
if ($element == 1)
  {$tally_ones ++;}
}

### store index of last pile of multiple stones from piles array, in plural_index ###
my $plural_index; # will be the index in piles array of the last pile with multiple stones.
my $counter = 0; # will be a counter in the loop, used to set the value of plural_index.

foreach my $entry (@piles)
  {
if ($entry > 1)
  {$plural_index = $counter;
last;}
$counter ++;}
###

$bag = $plural_index; #set bag to draw from to the last pile with multiple stones.

if ($tally_ones % 2) #if 1 and thus true, there are an odd number of piles with 1 stone.
  {$handful = $piles[$plural_index];}
else {$handful = ($piles[$plural_index] - 1);}

remove_stones ();
	  }





sub pick_one
	  {
my @options; # will hold the indexes of piles that still have a stone

my $index = 0; # tracks which pile is being tested, by its index in @piles.

foreach my $element (@piles)
  {
if ($element == 1)
  {push @options, $index;}
$index ++;}

$bag = $options[rand @options]; #the bag to draw from is randomly chosen from the list
$handful = 1;

remove_stones ();}



# to do:
# Option: random pile sizes.
# Option: imperfect computer opponent. (A fraction of time, will randomly choose to sub_stall instead of sub_make_zero.)

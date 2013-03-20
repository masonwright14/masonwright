#!/usr/bin/perl -w
# normas_noodle.pl

use strict;

my $solutions_found = 0; # number of ways Norma has been taught to increase mall revenue
my $actor_word; # who starts the new link
my $rec_word; # who receives the new link
my @terms = ('MALL REVENUE', 'YUPPIES', 'APPLE STORE', 'HOOLIGANS', 'AEROPOSTALE', 'SECURITY GUARDS', 
	     'ELDERLY MALL WALKERS', 'COLDWATER CREEK'); # the list of concepts in the map. Their order will be used to code each one with a letter, to simplify the "ask" family of subroutines' procedures.
my $first_letter; # returned by to_letters () -- the letter that codes for an entry in @terms
my $second_letter; # same, but for the second value passed in to to_letters ()
my $counter = 0; # counts up the index of the next link the user can make (for adding to the @brain matrix)
my @brain; # a user-generated matrix to store "links". Each element in the matrix represents one link. Each element is an array: link actor, link recipient, 1/-1 (increase/decrease)
my @receivers; # a user-generated matrix to store "links", equivalent to @brain, but in a different format. Each element stands for a concept in the map, in the order of their appearance in @terms. Each element is an array of the recipients of links from that concept.
my $ask_or_teach; # used to direct flow through methods used (in part) for both "ASK" and "TEACH" tasks
my $start_word; # first user input to "ASK" task (If _____ changes)
my $start; # code letter for start_word
my $incr_decr; # from user input to ask () : 1 for increase, -1 for decrease
my $endpoint_word; # user input to ask () (. . . what is the effect on _____?)
my $endpoint; # code letter for endpoint_word
my $answer_stem; # passed from ask () to show_answers (), for printing to screen
my @answers; # for an ask() or teach() search, this is an array of strings representing all (letter-coded) paths through the concept map leading from the specified startpoint to the endpoint. The startpoint letter may be left off from the beginning of each element (it is added later), but the other letters, in sequence, show the path.
my $sum; # sum of the net values of all final @answers for a search. Each chain that goes from startpoint to endpoint registers as 1 or -1 (increase or decrease). Their sum determines whether the overall result is an increase, decrease, or no change.
my $error_finder; # returns 1 from check_for_bad_link () if there is a link that doesn't match @correct.
my $error_index; # if a link is in error, check_for_bad_link () returns its index in @brain in this variable.
my $first_word; # first return from to_words (), translated back from code letter
my $second_word; # second return from to_words ()

my @correct; # a matrix of correct links. (actor, recipient, 1 for increase/-1 for decrease)
$correct[0][0] = "YUPPIES";
$correct[0][1] = "MALL REVENUE";
$correct[0][2] = 1;
$correct[1][0] = "APPLE STORE";
$correct[1][1] = "YUPPIES";
$correct[1][2] = 1;
$correct[2][0] = "HOOLIGANS";
$correct[2][1] = "ELDERLY MALL WALKERS";
$correct[2][2] = -1;
$correct[3][0] = "HOOLIGANS";
$correct[3][1] = "YUPPIES";
$correct[3][2] = -1;
$correct[4][0] = "AEROPOSTALE";
$correct[4][1] = "HOOLIGANS";
$correct[4][2] = 1;
$correct[5][0] = "ELDERLY MALL WALKERS";
$correct[5][1] = "HOOLIGANS";
$correct[5][2] = -1;
$correct[6][0] = "COLDWATER CREEK";
$correct[6][1] = "ELDERLY MALL WALKERS";
$correct[6][2] = 1;
$correct[7][0] = "SECURITY GUARDS";
$correct[7][1] = "HOOLIGANS";
$correct[7][2] = -1;

my @solutions; # a matrix of the 4 things a mall developer can do to increase revenue.
$solutions[0][0] = "COLDWATER CREEK";
$solutions[0][1] = "ADD";
$solutions[1][0] = "APPLE STORE";
$solutions[1][1] = "ADD";
$solutions[2][0] = "SECURITY GUARDS";
$solutions[2][1] = "ADD";
$solutions[3][0] = "AEROPOSTALE";
$solutions[3][1] = "REMOVE";

my @solutions_left = (0, 1, 2, 3); # array of indexes in @solutions that haven't been guessed yet. Used to prevent double-guessing.

####################################


entry_screen ();




sub entry_screen
  {print "\n\n\n++++++++++++++++++++++++++++++++++++++++++++\n\n++++++++++++++++++++++++++++++++++++++++++++\n\n\n\nNorma's Noodle\n\n\nYou must train Norma in the mystic ways of mall development.\n\nFirst, make a concept map for Norma by linking together the UPPERCASE keywords from the library.\n\nTo beat the game, you must then teach Norma four ways she can increase mall revenue. Be warned: Norma will not understand you, unless her concept map verifies your suggestions!\n\nHit enter to play!\n";
   my $a = <STDIN>;
   if ($a eq "\n")
         {print "\n\n++++++++++++++++++++++++++++++++++++++++++++\n\n";
	  home_screen ();
	 }
   }


sub home_screen
    {if ($solutions_found == 4)
         {victory ();}
    print "\n$solutions_found/4 solutions found.\nADD link, REMOVE link, ASK Norma, go to LIBRARY, TEACH solution, or DISPLAY links? ";
    chomp (my $a = <STDIN>);
    $a = uc($a);
    if ($a eq "ADD")
          {add_link ();}
    elsif ($a eq "REMOVE")
          {remove_link ();}
    elsif ($a eq "ASK")
          {ask ();}
    elsif ($a eq "TEACH")
          {teach ();}
    elsif ($a eq "DISPLAY")
          {display_links ();}
    elsif ($a eq "LIBRARY")
          {library ();}
    elsif ($a eq "QUIT")
          {exit;}
    else 
          {home_screen ();}
   }


sub add_link
     {print "Link actor? ";
      chomp ($actor_word = <STDIN>);
      $actor_word = uc($actor_word);
      my $a = 0;
      foreach (0..7)
	    {if ($actor_word eq $terms[$_])
	           {$a = 1;}
	    }
      if ($a == 0)
	    {print "The concept $actor_word does not exist.\n";
	     home_screen ();}

      print "Link recipient? ";
      chomp ($rec_word = <STDIN>);
      $rec_word = uc($rec_word);
      $a = 0;
      foreach (0..7)
	    {if ($rec_word eq $terms[$_])
	           {$a = 1;}
	    }
      if ($a == 0)
	    {print "The concept $rec_word does not exist.\n";
	     home_screen ();}
      if ($actor_word eq $rec_word)
	    {print "A concept can't link to itself.\n";
	     home_screen ();}

      to_letters ($actor_word, $rec_word);
      my $actor = $first_letter;
      my $rec = $second_letter;

      if ($counter > 0)  # Prevents two links between same concepts, in same direction.
             {foreach (0..($counter - 1))
                     {if (($brain[$_][0] eq $actor) && ($brain[$_][1] eq $rec))
	                    {print "Illegal link: already exists.\n";
			     home_screen ();
			     }
		     }
             }
      print "Should $actor_word increase or decrease $rec_word? Type +/-: ";
      chomp (my $b = <STDIN>);
      if (($b ne "+") && ($b ne "-"))
	     {print "You must enter \"+\" or \"-.\n";
	      add_link ();}
      $brain[$counter][0] = $actor;
      $brain[$counter][1] = $rec;
      if ($b eq '+')
	     {$brain[$counter][2] = 1;}
      elsif ($b eq '-')
    	     {$brain[$counter][2] = -1;}
      $counter++;

      my $c = 1;
      foreach (@{$receivers[(ord($actor) - 65)]})
	     {if ($_ eq $rec)
		     {$c = 0;}
	     }
      if ($c == 1)
	     {push @{$receivers[(ord($actor) - 65)]}, $rec;}

      check_new_link ();
      }


sub check_new_link # if user's new link is not on list of "correct" links, Mr. T gives a warning.
     {my $a = 0;
      foreach (0..7)
            {if (($actor_word eq $correct[$_][0]) && ($rec_word eq $correct[$_][1]) && ($brain[($counter - 1)][2] == $correct[$_][2]))
	           {$a = 1;
		    last;
	           }
             }
      if ($a == 0)
             {Mr_T ();
	      print "Mr. T: \"I pity the fool that thinks $actor_word cause";
	      my @plural_check = split (//, $actor_word);
	      if ($plural_check[$#plural_check] ne "S")
	            {print "s";}
	      if ($brain[($counter - 1)][2] == 1)
	            {print " an increase";}
	      else
	            {print " a decrease";}
	      print " in $rec_word!\"\n\n";
             }
      home_screen ();
      }


sub remove_link
      {print "Link actor? ";
       chomp ($actor_word = <STDIN>);
       $actor_word = uc($actor_word);
       my $a = 0;
       foreach (0..7)
	     {if ($actor_word eq $terms[$_])
		    {$a = 1;}
	     }
       if ($a == 0)
	     {print "The concept $actor_word does not exist.\n";
	      home_screen ();}
 
       print "Link recipient? ";
       chomp ($rec_word = <STDIN>);
       $rec_word = uc($rec_word);
       $a = 0;
       foreach (0..7)
	     {if ($rec_word eq $terms[$_])
		    {$a = 1;}
              }
       if ($a == 0)
	     {print "The concept $rec_word does not exist.\n";
	      home_screen ();}

       to_letters ($actor_word, $rec_word);
       my $actor = $first_letter;
       my $rec = $second_letter;

       foreach my $b (0..$#brain)
	      {if (($brain[$b][0] eq $actor) && ($brain[$b][1] eq $rec))
		     {splice (@brain, $b, 1);
		      $counter--;
		      last;
		     }
	       }

       my $c = 0;
       foreach (0..$#{$receivers[(ord($actor) - 65)]})
	       {if ($receivers[(ord($actor) - 65)][$_] eq $rec)
		     {splice (@{$receivers[(ord($actor) - 65)]}, $_, 1);
		      $c = 1;
		     }
	       }
       if ($c == 0)
	       {print "Can't remove link: Does not exist.\n";}
       else
	       {print "Link removed.\n";}
       home_screen ();
      }


sub ask
      {$ask_or_teach = "ASK";
       print "If __ changes . . .: ";
       chomp ($start_word = <STDIN>);
       $start_word = uc($start_word);
       my $a = 0;
       foreach (0..7)
	       {if ($start_word eq $terms[$_])
		      {$a = 1;}
	       }
       if ($a == 0)
	       {print "The concept $start_word does not exist.\n";
		home_screen ();}
       to_letters ($start_word, 'HOOLIGANS');
       $start = $first_letter;

       print "If $start_word _______ (+ or -) . . .: ";
       chomp(my $b = <STDIN>);
       if (($b ne "+") && ($b ne "-"))
	      {print "You must enter \"+\" or \"-\".\n";
	       ask ();}
       if ($b eq '+')
	      {$incr_decr = 1;}
       elsif ($b eq '-')
	      {$incr_decr = -1;}

       print ". . . what is the effect on _____?: ";
       chomp ($endpoint_word = <STDIN>);
       $endpoint_word = uc($endpoint_word);
       $a = 0;
       foreach (0..7)
	      {if ($endpoint_word eq $terms[$_])
		       {$a = 1;}
	      }
       if ($a == 0)
	      {print "The concept $endpoint_word does not exist.\n";
	       home_screen ();}
       to_letters ('HOOLIGANS', $endpoint_word);
       $endpoint = $second_letter;

       $answer_stem = "If $start_word ";
       if ($b eq '+')
	      {$answer_stem = $answer_stem."increase";}
       else
	      {$answer_stem = $answer_stem."decrease";}
       my @plural_check = split (//, $start_word);
       if ($plural_check[$#plural_check] ne "S")
	      {$answer_stem = $answer_stem."s";}
       $answer_stem = $answer_stem.", $endpoint_word";
     ask_after_prompt (); 
     }


sub ask_after_prompt # continuation of ask(), but also allows teach() to share the algorithm.
       {my @treelist; # used to store strings representing chains through the concept map, during the search
	if (defined @{$receivers[(ord($start) - 65)]})
	      {@treelist = @{$receivers[(ord($start) - 65)]};}
       else
	      {@treelist = ();}
       @answers = ();
       while (scalar(@treelist) > 0)
	      {my $a = $#treelist;
	       foreach (0..$a)
		      {my $entry = $treelist[($a - $_)];
		       my @entry_split = split //, $entry;
		       my $last_letter = $entry_split[$#entry_split];
		       if ($last_letter eq $endpoint)
			      {push @answers, $entry;}
		       else
		              {my @c;
			       if (defined @{$receivers[(ord($last_letter) - 65)]})
			             {@c = @{$receivers[(ord($last_letter) - 65)]};}
			       else
				     {@c = ();}
			       foreach my $b (@c)
				     {if ($b eq $endpoint)
					    {push @answers, $entry.$b;}
				      elsif ($entry =~ /$b/)
					    {next;}
				      else
					    {push @treelist, $entry.$b;}
				      }
			       }
		       splice(@treelist, ($a - $_), 1);
		       }
	       }
       unless (defined $answers[0])
	      {norma ();
	       print "Norma: \"I see no effect of $start_word on $endpoint_word.\"\n";
	       home_screen ();
	      }
       use_answers ();
     }


sub use_answers # continuation of ask_after_prompt()
       {$sum = 0;
	foreach (@answers)
	      {my $product = $incr_decr;
	       my @answer_split = split //, $_;
	       unshift @answer_split, $start;
	       while ($#answer_split > 0)
		      {foreach my $a (0..$#brain)
			      {if (($brain[$a][0] eq $answer_split[0]) && ($brain[$a][1] eq $answer_split[1]))
				      {$product *= $brain[$a][2];
				       shift @answer_split;
				       last;
				      }
			      }
		       }
	       $sum += $product;
	       }
	if ($ask_or_teach eq "ASK")
	       {show_answers ();}
       }


sub show_answers # continuation of the ask() family of routines: after use_answers()
	{norma ();
	print "Norma: \"";
	if ($sum > 0)
	       {print "$answer_stem increase";}
	if ($sum < 0)
	       {print "$answer_stem decrease";}
	if ($sum == 0)
	       {print "$answer_stem do";}
	my @plural_check = split (//, $answer_stem);
	if ($plural_check[$#plural_check] ne "S")
	       {if ($sum == 0)
		      {print "es";}
		else
		      {print "s";}
	       }
	if ($sum == 0)
	       {print " not change";}
	print ".\"\n";
	home_screen ();
       }


sub teach
     {$ask_or_teach = "TEACH";
      print "What should I add to/remove from the mall? ";
      chomp (my $change = <STDIN>);
      $change = uc($change);
      my $q = 0;
      foreach (0..7)
	       {if ($change eq $terms[$_])
		      {$q = 1;}
	       }
      if ($q == 0)
	       {print "The concept $change does not exist.\n";
		home_screen ();}
      if (($change eq "HOOLIGANS") || ($change eq "ELDERLY MALL WALKERS") || ($change eq "YUPPIES") || ($change eq "MALL REVENUE"))
	    {Mr_T ();
	     print "Mr. T: \"Quit your jibber-jabber! Mall developers have no direct control over $change.\"\n";
             home_screen ();
	     }
      check_for_bad_link ();
      if ($error_finder == 1)
	    {Mr_T ();
	     print "Mr. T: \"I'm tired of your crazy rap! You can't teach a child until you straighten out the concepts in your own head. Check your links related to $terms[(ord($brain[$error_index][0]) - 65)].\"\n";
	     home_screen ();}
      my $a = lc($change);
      print "Should I ADD or REMOVE $a? ";
      chomp (my $decision = <STDIN>);
      $decision = uc($decision);
      if (($decision ne "ADD") && ($decision ne "REMOVE"))
	     {print "You must enter \"ADD\" or \"REMOVE\".\n";
	      teach ();}

     to_letters ($change, "MALL REVENUE");
      $start = $first_letter;
      $endpoint = $second_letter;
      $start_word = $change;
      $endpoint_word = "MALL REVENUE";
      if ($decision eq "ADD")
	   {$incr_decr = 1;}
      elsif ($decision eq "REMOVE")
	   {$incr_decr = -1;}
      ask_after_prompt ();
      unless ($sum > 0)
	   {norma ();
	    print "Norma: \"I don't see how it would increase mall revenue to $decision $change.\"\n";
	    home_screen ();
	   }

      my $b = 0;
      my $c = 0;
      foreach (0..3)
	    {if (($solutions[$_][0] eq $change) && ($solutions[$_][1] eq $decision))
	           {my $already_used = 1;
		     foreach my $element (@solutions_left)
		         {if ($_ == $element)
			       {$already_used = 0;}
			 }
		    if ($already_used == 1)
		           {Mr_T ();
			    print "Mr. T: \"Don't make me mad! You already used that answer. I don't want to see it again!\"\n";
			    home_screen ();
			   }
		    $b = 1;
		    splice(@solutions_left, $c, 1);
		    $solutions_found ++;
		    last;
		   }
	     $c++;
	    }
      if ($b == 0)
	   {norma ();
	    print "Norma: \"I don't see how it would increase mall revenue to $decision $change.\"\n";
	    home_screen ();
	   }
      norma ();
      print "Norma: \"Of course! ";
      if ($start_word eq "AEROPOSTALE")
	   {print "Removing AEROPOSTALE decreases HOOLIGANS, which increases YUPPIES, which increases MALL REVENUE!\"";}
      elsif ($start_word eq "COLDWATER CREEK")
	   {print "Adding COLDWATER CREEK increases ELDERLY MALL WALKERS, which decreases HOOLIGANS, which increases YUPPIES, which increases MALL REVENUE!\"";}
      elsif ($start_word eq "APPLE STORE")
	   {print "Adding APPLE STORE increases YUPPIES, which increases MALL REVENUE!\"";}
      elsif ($start_word eq "SECURITY GUARDS")
	   {print "Adding SECURITY GUARDS decreases HOOLIGANS, which increases YUPPIES, which increases MALL REVENUE!\"";}
      print "\n";
      home_screen ();
     }


sub check_for_bad_link # called when user tries to teach() Norma something. teaching is not allowed if a link exists that is not on the list of "correct" links (even if the bad link is out of the scope of the "proof" behind the teaching).
      {$error_finder = 0;
       foreach my $brain_index (0..($counter - 1))
	    {my $a = 0;
	     foreach my $correct_index (0..7)
	            {my $word_one = $terms[(ord($brain[$brain_index][0]) - 65)];
		     my $word_two = $terms[(ord($brain[$brain_index][1]) - 65)];
		     if (($word_one eq $correct[$correct_index][0]) && ($word_two eq $correct[$correct_index][1]) && ($brain[$brain_index][2] == $correct[$correct_index][2]))
		            {$a = 1;}
		     }
	     if ($a == 0)
	            {$error_finder = 1;
		     $error_index = $brain_index;
		     last;
		    }
	     }
      }


sub display_links # shows all user-generated "links"
      {foreach (0..($counter - 1))
	     {to_words ($brain[$_][0], $brain[$_][1]);
	      print "$first_word ";
	      if ($brain[$_][2] == 1)
		    {print "increase";}
	      elsif ($brain[$_][2] == -1)
		    {print "decrease";}
	      my @plural_check = split (//, $first_word);
	      if ($plural_check[$#plural_check] ne "S")
		    {print "s";}
	      print " $second_word.\n";
	     }
       if ($counter == 0)
	     {print "No links to display.\n";}
       home_screen ();
      }


sub library
      {print "\n\n\n++++++++++++++++++++++++++++++++++++++++++++\n\nWelcome! to the exciting world of mall development.\n\nYour goal is to maximize MALL REVENUE, through careful store selection and the use of SECURITY GUARDS.\n\nIn a mall, many independent elements affect MALL REVENUE. The only direct contributors to MALL REVENUE -- YUPPIES -- are drawn to the APPLE STORE. But YUPPIES can be driven away by HOOLIGANS.\n\nHOOLIGANS are attracted to AEROPOSTALE but repelled by SECURITY GUARDS. HOOLIGANS drive away not only YUPPIES, but also ELDERLY MALL WALKERS.\n\nELDERLY MALL WALKERS, in turn, reduce the number of HOOLIGANS. All ELDERLY MALL WALKERS are partial to COLDWATER CREEK.";
       print "\n\nYou are in the library.\nType \"BACK\" to go back. ";
       chomp(my $a = <STDIN>);
       $a = uc($a);
       if ($a eq "BACK")
	     {print "\n\n++++++++++++++++++++++++++++++++++++++++++++\n";
	      home_screen ();
	     }
       else
	     {library ();}
       }


sub victory
      {sleep 5;
       print "\n\n\n\n\n";
       winning_Mr_T ();
       print "Mr. T: \"Good work! You taught Norma all four ways to increase mall revenue.\"\n\nGAME OVER.\n\n";
       exit;}


sub to_letters # translates user input keywords from the list @terms into their code letters (i.e., zeroth term is A, . . .)
     {my $a = shift;
      my $b = shift;
      foreach (0..7)
	     {if ($terms[$_] eq $a)
	            {$first_letter = chr(65 + $_);}
	      if ($terms[$_] eq $b)
		    {$second_letter = chr(65 + $_);}
	     }
      }


sub to_words # translates code letters back into keywords from @terms.
      {my $a = shift;
       my $b = shift;
       $first_word = $terms[(ord($a) - 65)];
       $second_word = $terms[(ord($b) - 65)];
       }


sub norma
      {print "\n\n",'                  :Z8DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD8Z,                     
             .  =788DDDDDDD88DDDDDDDDDDDDDDDDDDDDDDDDDDDDD8:                    
             . :O88DDDDDD8888DD8DDD88DDDDDDDDDD88DDDDDDDDDDO:  ..               
              ,O88DDDDDD8OO8888888888888888DDD88888DD8DDDDDD$,.                 
             ,Z888DDDDDOOOOOOO88888OOO88O8O8888OO888OOO8DDDDDZ$.                
            .?88DDDDDOOOOOOOOOOOOOOOOOOOOOOO88OOOOOOOOOO8DDDDDD?.               
            :88DDDDD8OOO88888888888OOO888888888888888888O8DDDDDD.               
          . ~88DDDDD888888888888888OO888DDDD88888888DD888OODDDDD~               
           .O88DDDD88888DDDDD88888888DDDDDDDD888DDDDDDD88888DDDD$.              
        . .,D88DDDD88DDDDDDDDD8DDDD8DDDDDD8DDDDDDDDD8DDD8888DDDDO,              
        . .?D8DDDDDDDDDDD8NDDDDDDDDDDDDDDD~DDDDDDDDDD8DDDDDDDDDDD~              
          :7D8DDDDDDDDDDN+8DDDDDDDDDDDDDDD~8DDDDDDDDD~DDDDDDDDDDD,              
        . I8D8DD88DDZ8NND7ZI++?7$?+~~~~~~~===+$O7?I$D8=8DDDNDDDDD+              
        ..78D8DD$7+=~7O?=+=~~~~~?$?~~~~~~~~==========+7I==+=IDDDD$  ..          
         .ZDDDNZ==~~~~~~===~~~~~~==~~~~~~~~=====~~==~====~==7NNDD8              
          $DDDD?~~~~~~====7O8I=~~~~~~~~~~~======78Z$===~~===?NNDDD. .           
          8DDDD7~~~~~~=?+=~~MN8?~~~~~~~~~~+===$O:MNN~+I~~====NDDDD,             
         .8DDDD8~~~~~~I,,NNMMMO,=~~~~~~~~=+==+:NMMMOO,~======DDDDD.  .          
         :DDDDD8=~~~~~~=I~N88O++?~~~~~~~~=+==Z=?N8OO:7~~~====DDDDD=             
        .?DDDDDD~~~~~~~~~======~~~~~~~~~~+====~~======~~~~==7DDDDD~             
         IDDDDDD~~~~~~~~~~~~~~~~~~~~~~~~~?====~~=~~~~~~~~~==ZDDDDD, .           
         ?DDDDDN~~~~~~~~~~~~~~~~~~~~~~~~~+==~~~~~~~~~~~~~~==DDDDDD:             
         ,DDDDDN~~~~~~~~~~~~~~~~~~~~~~~~~+=~=~~~~~~~~~~~~~=+DDDDDD:             
         ,DDDDDD7~~~~~~~~~~~~~~~~~~~~~~~~+=~~~~~~~~~~~~~~~=?NDDDDD,             
         :DDDDDDN~~~~~~~~~~~~~~~~~~~~~~~~~=~~~~~~~~~~~~~~==ONDDDD8..            
        .=DDDDDDDN~~~~~~~~~~~~~~~~~~~~~~~~==~~~~~~~~~~~~==+DNDDDDD.             
         +DDDDDDDD$~~~~~~~~~~~~~~~~~~I??$+==~~~~~~~~~~~==+7NDDDDDD..            
         IDDDDDDDDN7~~~~~~~~~~~~~~~~~~===~~~~~~~~~~~~===+IDNDDDDD8,             
         IDDDDDDDDNNZ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~====+?O8DDDDDDDO.             
         7DDDDDDDDNNDO7~~~~~~~~~~~~=+?=+?+~~~~~~~~~~~=?8NNDDDDDDDO. .           
         IDDDDDDDDDNNDD7~~~~~~~~=?II77$$7$77I=~~~~~~=7DNNDDDDDDDDZ              
         7DDDDDDDDDNNNNN8+~~~~~~~~~==~~~=+=~~~~~~~=+$DNNDDDDDDDDD7              
         DDDDDDD88DNNNNNNN?~~~~~~~~~=++++==~~~~~=+IDNNNNDDDDDDDDDO  .           
        .DDDDDDD888NNNNNNND=~~~~~~~~~~~~~~~~~~~=+?8NNNNNDDDDDDDDD8              
        .DDDD8888DDNNNNNNNN87+~~~~~~~~~~~~~~~==+++8NNNNDDDDDDDDDDD              
        ,DD888O88DDDNNNNNNN8$+===~~~~~~~~~===+++++DNNNNDDDDDDDDDDZ,             
        :DOOO888DDDDNNNNNNNNO++====+=~~~~=+++++==+8NNNNDDDDDDDDDDDI,            
        :D8888DDDDDDNNNNNNNDO+=======+=+===+++===+DNNNDDDDDDDDDDDDD7=           
        .DDDDDDDDDDDDNNNNNNDO=====================8NNNDDDDDDDDDDDDDDD+.         
        .DDDDDDDDDDDDNNNNNND7======================ONNDDDDDDDDDDDDDDDO,.        
         DDDDDDDDDDDDNNNNNON+======================$$8DDDDDDDDDDDDZOZ+. .       
        .?DDDDDDDDDDDDDZ$$$I==~~~~~~~~~~~~~~~=~~~~$$$$DDDDDDDDDNNN    ..        
         .:=ODDDDDDDDD$$$$$$$=~~~~~~~~~~~~~~~~~~7$$$$ZDDDDDD8Z$$$Z,.            
            :ZO8DDDDD8$$$$$$$$$$+~~~~~~~~~~=?$$$$$$$$$8DDDD8Z$$$$$$$$I. .       
       ..?7$$$$$$$DDD$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$8DDDZ$$$$$$$$$$$7:       
      .?7$$$$$$$$$DDO$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ZDD$$$$$$$$$$$$$$7',"\n\n";
      }


sub Mr_T
      {print "\n\n",'............................~MMMMMMMMMMMMMMMN...................................
............................7MMMMMMMMMMMMMMMMO..................................
...........................~OMMMMMMMMMMMMMMMM8?.................................
...........................:NMMMMMMMMMMMMMMMMMM,................................
..........................~:MMMMMMMMMMMMMMMMZ.MMM?..............................
..........................:8MMMMMMMMMMMMMMMD....MM=.............................
..........................ON7+MMMMMMMMNDO=,......MM:............................
.........................O$??=IIZO$7==~:..........MM............................
.........................DI$$+I7OZ$~+::...........MM............................
.........................O$$$+$$OO$++~,,...........MM...........................
........................=8$$$$$$$$$=,..............~M,..........................
.......................:~O$$$$$$$$$7..:...MMM......MMN..........................
.......................:,D8$$$$$$$NMMM+=MMM,.......=MMM.........................
.......................DMMM$$$$$$$$7,MMMM?DMMMMMM...,MM.........................
......................,M?MM$?$$$$$NMON8M,.,D?++++..+?MM.........................
......................:MMMM$O$MMMM?MMN....,D8M.~,...8M+.........................
.......................,MMM$OMDOMM:8$MO.............MM..........................
.......................,MMM$$7$7?~Z$$M$,............MMM+........................
.......................,~MM$7$IIZO88MO7~...........MM.MM........................
.......................,+MMM$$OZ$ZOMNMI~...........M+.MM........................
.......................,7MMMD$$$$Z8M$MI:..M.......7D..MM........................
.......................,,MMMMMD$$78OMMMM::........MMMMM8........................
......................,,,,MMMMMMMMMMMMM8NI.......=NDM+..........................
....................,:.,=MMMMMMMMMMMMMMI=,MM7.:I7M,MM+..........................
....................,,MMMN.?MMMMMMMMMZ8N~M..MMMMMO.MM=,.........................
..................,,8MM8,~.IOMMMMMMMMM?.....~MMMM,.MM++?........................
...............8MMMMMM.:7DD:MMMMMMMMMMMMM..,MMMM7..:MMMZ??+.....................
...........MMMMMMMMMMM.?~Z~~IIMMMMMMMZZ?..:MMMMM.....MMMMMIII7..................
.........MMMMMMMMMMMMO~=Z.:.ZMNMMMMMMMNMMMMMMMMM..,..MMMMMMMI777................
.......MMMMMMMMMMMMMM$.I+Z.Z..MMMMMMMMMMMMMMMM+.....MMMMMMMMMMNZ$$+.............
...$88MMMMMMMMMMMMMMMZO:??+.MM+OMMMMMMMMMMMM7~I.....MMMMMMMMMMMMMMNO8...........
,?IZZ8MMMMMMMMMMMMMMMM7=.=I~~.MOMMMMMMIMMMM8=N$...,MMMMMMMMMMMMMMMMMMMMM:.......
==I$7MMMMMMMMMMMMMMMMM$D=.~M7.=8DMIZMZMONNMM.$~?MMMM.?MMMMMMMMMM~.....IMMMM.....
=+$7$MMMMMMMMMMMMMMMMMZ$DM$~+8?:~N?MMMMDI..8...MM........?II.~MMM.........MM....
?7$$NMMMMMMMMMMMMMMMMM$$$Z8?$.?:MM.=:..,:.,.I..MM=.......I,...ZMMM,........MMMMM
I77OMMMMMMMMMMMMMMMMMMMD7$$7N:MN.+,,..:...I+..MMMMMMMM..~....7..DMM:........MMMM
I$7NMMMMMMMMMMMMMMMMMMMMMI==?IOZ:I$MM=+8OIMMMMMMMM?MMI=~....?Z...MMMM,.......,MM
$$OMMMMMMMMMMMMMMMMMMMMMMMMI~~~=?D.==?$NMMMMMMMMDIMM$?+...MM,.....+7MMI.......OM
O8DMMMMMMMMMMMMMMMMMMMMMMMMMM7,:+..:=MMMMMMMMMM?+?MM7I..,MMN...:?....MM........M
8DDMMMMMMMMMMMMMMMMMMMMMMMMMMM:D.Z,~7MMMMMMMMMM$+OMM$7.=MM+...IMN....8MM........
88MMMMMMMMMMMMMMMMMMMMMMMMMMMM.I...?MMMMMMMMMMMM$=NMMMDMMI....MM,.....MM........
D8MMMMMMMMMMMMMMMMMMMMMMMMMMMMM,...MMMMMMMMMMMMM88...MMMM...MMMO.....+MM........
ONMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM8.NMMMMMMMMMMMMMMM...MMMD..MMI.......MMMM.......
NMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM7.MMMMMMMMMMMMMMMM..MMMMMMMMM......OMM:MM.......
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM..MMMMMMMMMMMMMMMMMMMMMMMMMM~..NMMMMZ...........',"\n\n";
      }

sub winning_Mr_T
	{print "\n\n",'M......NMM........MMM.......:MMMMMMMMMMMMMMMM~.......................MMMMM..... 
.......DMM........OMM.......=MMMMMMMMMMMMMMMMM.........MMM$.........MMMN....... 
.......MMM.........MMN.....,IMMMMMMMMMMMMMMMMM$........MMMMMN.................. 
.......MMM.................:=MMMMMMMMMMMMMMMMOMMO.........MMMMM....MMM......... 
..........................::MMMMMMMMMMMMMMMN,..=MM.................MM=......... 
MMMM..........MMMM........~ONOMMMMMMMMMMMMM?....,MM............MMMOD=.......... 
MMM..........MMMM=........88+~ZMMMMMN?:,:.........MM............MMMMM..MMMMMM.. 
....MMMM....NMM..........8O$$~+ZZZ7?+:=:,.........MM......MMMMMMMMMM8MMMMMMMMMM 
.....=MMM...MMM..........Z$$$I?$ZZI?+,............DMM...NMMMMMMMMMMMMMM=.....MM 
.......MMM.MMM..........:8$$$?$$$$$??:.............NM..MMMM......MMMMM......... 
.......MMMMMMM8MMMMMM...=M$$$$$$$$$$:,....7MN......=M:MMMM..MMMMMMMMMMM........ 
.MMMMMMMMMMMMMMMMMMMMMMM,N$$$$$$$$$MMM:.+MMD.......MMM.M...........MMMM........ 
MMMMMMMMMMMMM.........8:,M$?$$$$$$$=OMMMMM...........MM....MMM......MMMM....... 
MM...DMMMMMMMMM........MMMM$7$$$$$$$~$MMD.8MMMMMM,...MM....MMM.......MMM..M.... 
..7MMMMMMMM8MMMM.......N?MMOZ$$MMMMMMM.,~...,.......=MM....MM=.......MMM.MMM... 
.7MMMZMMMM...OMM.......,MMMZOMMM~M.?MM8~...+=Z......IM+....MMM.......MMM....... 
MMMM.MMM...............,NMMIZOI$Z7IZ$MZ,............MM.....IM........MMM...MMM. 
MM....M........MMM......~MMZ$I+??7D$DN7:...........DMMM~...................=MM8 
MM.............MMM.....,.MMN$$I777ZONO7:...........MM.MM....................MMM 
..~MMM.........NMM......8MMMD$$ZZ7DMI$I,..,.......7M..MM.....................MM 
..MMM....................MMMM$$$$$$DMNMI.:........ND.MMM....................... 
.ZMM...................,,.MMMMM8$8D8MMM7?,........M$MMZ........................ 
.....................,,,.MMMMMMMMMMMMMMMMMO=,...?MO?D+......................... 
.....................,,?MMI$MMMMMMMMMMO+...OM=7OMM.MM?........................M 
MMM................,,,MMM,~IIMMMMMMMMM:ZMZ..,MMMMZ.MM++.......................M 
MM.............,,~MMMMZ~:.ZN:MMMMMMMMMD:....MMMMM..MM+++?...................... 
MM..........,NMMMMMMMM7Z,.,.MMMMMMMMMMMMD:.DMMMM.....MMMM?II................... 
$........,MMMMMMMMMMMMI+7MN,:MMMMMMMMD7ID+MMMMMM.,...MMMMMM$I77................ 
........DMMMMMMMMMMMM7:~,+~=I=DMMMMMMMMMMMMMMMD,:....MMMMMMMM8$7$.............. 
....IDNMMMMMMMMMMMMMM$Z+$.7MO.MMMMMMMMMMMMMMM8O~....MMMMMMMMMMMMMZZO........... 
,,I$88MMMMMMMMMMMMMMMMO:~.:.NMIMMMMMMMMMM7$M8..8...=MMMMMMMMMMMMMMMMMZ......... 
:??77ZMMMMMMMMMMMMMMMMZO+=.+==,,MMMMMM~MMMM...?:.MMMMMMMMMMMMMMD:...MMMMMM:.... 
=~I$7MMMMMMMMMMMMMMMMMZ8N+..,.,,?N?M$.DM7ZI..,.MMMN,...=NMMMMMMM=.......OMM.... 
++I7ZMMMMMMMMMMMMMMMMM$$Z8$8,,8M,:.+M+.~..~~~++MM........?I...MMM,........8MM,: 
?I$ZMMMMMMMMMMMMMMMMMMMZ7$787M,:.:?::Z?,~:+=,.ZMMMM8+~..~?...=.MMMM,........MMM 
II$OMMMMMMMMMMMMMMMMMMMM7+I$IO8~M,,8..N+~$7MMMMMMM7MMM.:,...=Z:..MM+:,.......ZM 
I7Z8MMMMMMMMMMMMMMMMMMMMMM~~~~=+O,,,???I$MMMMMMMM?NMM=~...7M:=....MMMM,.......M 
$O8MMMMMMMMMMMMMMMMMMMMMMMMMM:,:~D.:=7MMMMMMMMMMI+MM??:...MM........=MM........ 
Z8DMMMMMMMMMMMMMMMMMMMMMMMMMMM:~..~=IMMMMMMMMMM8=IMMII..MMM....8$....MM........ 
ODDMMMMMMMMMMMMMMMMMMMMMMMMMMM+7.~:.DMMMMMMMMMMM$IMMMD,7MM,...MMD.....MM....... 
8DMMMMMMMMMMMMMMMMMMMMMMMMMMMMZ+...=MMMMMMMMMMMM$$.=MMMMM=...MMM......MM....... 
88MMMMMMMMMMMMMMMMMMMMMMMMMMMMMD7.$MMMMMMMMMMMMMMN....MMZ..8MMD......7MM....... 
DMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMI.MMMMMMMMMMMMMMMM...MMMM.$MM.......MMNMM...... 
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM:.MMMMMMMMMMMMMMMMMMMMMMMMMMN..:M7MMM7..:...... 
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM.ZMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM............',"\n\n";
       }




### To do:
# Add "ask for definition" feature
#       -Norma lists all things acted on in one sentence (increases with commas between, then decreases with
#        commas between). Then all things that act on it.

#!/usr/bin/perl -w

use strict;

my $file_name = shift;

open (SCRIPT, '<', "$file_name") or die $!;
my $script;
while (<SCRIPT>)
  {$script = $script.$_;}
close SCRIPT;

my @split_script = split (//, $script);

my @array;
my $pointer = 0;
my $command_index = 0;

foreach (0..30000)
  {$array[$_] = 0;}

run();

sub run
  {print "\n";
   while ($command_index <= $#split_script)
    {if ($split_script[$command_index] eq '>')
       {$pointer++;
        $command_index++;
       }
   elsif ($split_script[$command_index] eq '<')
       {$pointer--;
        $command_index++;
       }
   elsif ($split_script[$command_index] eq '+')
       {$array[$pointer]++;
        $command_index++;
       }
   elsif ($split_script[$command_index] eq '-')
       {$array[$pointer]--;
	$command_index++;
       }
   elsif ($split_script[$command_index] eq ',')
       {get_input();
	$command_index++;
        }
   elsif ($split_script[$command_index] eq '.')
       {print chr($array[$pointer]);
	$command_index++;
       }
   elsif ($split_script[$command_index] eq '[')
       {if ($array[$pointer] != 0)
	    {$command_index++;}
	else
	    {my $a = 1;
	     $command_index++;
	     while ($a > 0)
	          {if ($split_script[$command_index] eq '[')
		        {$a++;}
		   elsif ($split_script[$command_index] eq ']')
		        {$a--;}
		   $command_index++;
		  }
	     }
       }
    elsif ($split_script[$command_index] eq ']')
       {if ($array[$pointer] == 0)
	    {$command_index++;}
	else
	    {my $a = 1;
	     $command_index--;
	     while ($a > 0)
	         {if ($split_script[$command_index] eq ']')
		       {$a++;}
		  elsif ($split_script[$command_index] eq '[')
		       {$a--;}
		  if ($a > 0)
		       {$command_index--;}
		 }
	     }
       }
     else
       {$command_index++;}
    }
   print "\n";
   exit;
   }


sub get_input
    {print "Enter one character and hit enter: ";
     my $input = (<STDIN>);
     if ($input eq "\n")
          {get_input();}
     chomp ($input);
     my @split_input = split(//, $input);
     $array[$pointer] = ord($split_input[0]);
    }

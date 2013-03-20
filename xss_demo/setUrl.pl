#!/usr/bin/perl -w

# string to replace is first command-line argument, new string is second command-line argument.
# DON'T wrap them in quotes.
# intended usage is for string to replace to be TODO, new string to be a URL:
#    perl setUrl.pl TODO newURL
# to change back to TODO, run the program again with the arguments in reverse order:
#    perl setUrl.pl newURL TODO

$oldString = $ARGV[0];
$url = $ARGV[1];

replaceUrlInFile("links.txt");
replaceUrlInFile("email.html");
replaceUrlInFile("blog_safer.js");
replaceUrlInFile("blog_unsafe.js");

# replaces all occurrences of $oldString with $url.
# first argument is the file name.
sub replaceUrlInFile {
  # store all text from the file in $fileText
  $fileText = "";

  # open the file as read-only
  open FILE, "<", $_[0] or die "Couldn't open file: $!"; 
  while (<FILE>) {
    $fileText .= $_;
  }
  close FILE;

  # open the file as write-only
  open FILE, ">", $_[0] or die "Couldn't open file: $!";

  # replace all occurrences of $oldString in $fileText
  $outputText = replaceText($fileText);
  print FILE $outputText;
  close FILE;
}

# replace all occurrences of $oldString with $url.
# first argument is the input text.
sub replaceText {
  $inputText = $_[0];

  # replace all occurrences of $oldString with $url. g means "global," or replace all.
  $inputText =~ s/$oldString/$url/g;
  return $inputText;
}


#!/usr/bin/perl
#
# This program looks at all the .txt files in a directory (not recursive) and compares the filename
#	to the contents
#
# It is meant to compare OCR processed data.
#
# The filename (excluding the .txt) is the expected value
#
# The contents are the processed value from the OCR
#
# Spaces delimit words and a count of partial value matches (one or more words but not all) is also given

use strict;


my $directory = $ARGV[0];

print "Looking in '$directory'...";

my @files;
# Dont' do a simple glob since it doesn't handle spaces very easily
opendir(IMD, $directory) || die("Cannot open directory");
while( $_ = readdir IMD ) {
	if($_ =~ m/.txt$/i) {
		push @files, $_;
	}
}
closedir(IMD);

print "Found " . @files . " files\n";



my $numCompleteMatch = 0;
my $numPartialMatch = 0;


print "OCR Contents\tExpected Valid Result\tMatch Type\n";

foreach my $file (@files) {
	my $validResult = substr($file, 0, length($file) - 4);

	open(FH, "<", "$directory/$file") or die("$file  $!\n");
	binmode FH;
	
	my $contents;
	{
		local $/=undef;
		
		$contents = <FH>;
	}
	close(FH);
	
	
	$contents =~ s/\r?\n//g;
	
	
	print $contents;
	print "\t";
	print $validResult;
	print "\t";
	
	
	if($contents eq $validResult) {
		$numCompleteMatch++;
		
		print "COMPLETE\n";
		
		next;
	}
	
	my @validPartialResult = split(" ", $validResult);
	my @contentPartialResult = split(" ", $contents);
	
	my $foundMatch;
	foreach my $contentPartial (@contentPartialResult) {
		$foundMatch = 0;
	
		foreach my $validPartial (@validPartialResult) {
			if($contentPartial eq $validPartial) {
				$numPartialMatch++;
				$foundMatch = !0;
				print "PARTIAL\n";
				last;
			}
		}
		
		if($foundMatch) {
			last;
		}
	}
	
	if(!$foundMatch) {
		print "NONE\n";
	}
}

print "\n";
print "Out of " . @files . " had $numCompleteMatch complete matches and $numPartialMatch partial matches\n";
print "\n";
print "Complete %:  " . (100 * $numCompleteMatch / @files) . "\n";
print "Partial %:  " . (100 * $numPartialMatch / @files) . "\n";
print "\n";

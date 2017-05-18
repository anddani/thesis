#!/usr/bin/env perl

use strict;
use warnings;

my $author;
my $title;
my $swetitle;
my $abstract;
my $sammanfattning;

while (<>) {
    if (m/\\def\\thesistitle\{\\.*? (.*)\}/) {
        $_ = $1;
        s/\\//g; # Remove newline
        $title = $_;
    } elsif (m/\\def\\swethesistitle\{\\.*? (.*)\}/) {
        $_ = $1;
        s/\\//g; # Remove newline
        $swetitle = $_;
    } elsif (m/\\def\\theauthor\{\\.*? (.*)\}/) {
        $author = $1;
    } elsif (m/\\def\\theauthor\{(.*)\}/) {
        $author = $1;
    } elsif (m/\\def\\theabstract\{\\input\{(.*)\}\}/) {
        open my $file, '<', $1; 
        $abstract = <$file>; 
        close $file;
        $abstract =~ s/\r\n//g;
        $abstract =~ s/\\texttt\{(.*?)\}/<b>$1<\/b>/g;
    } elsif (m/\\def\\thesweabstract\{\\input\{(.*)\}\}/) {
        open my $file, '<', $1; 
        $sammanfattning = <$file>; 
        close $file;
        $sammanfattning =~ s/\r\n//g;
        $sammanfattning =~ s/\\texttt\{(.*?)\}/<b>$1<\/b>/g;
    }
}

open (my $eng_abstract, '>', "abstract.html") || die $!;
my $eng_html = "<html>
  <head>
    <meta charset=\"UTF-8\">
    <title>$title</title>
  </head>
  <body>
    <h1>$author</h1>
    <h2>$title</h2>
    <h3>Abstract</h3>
    <p>$abstract</p>
  </body>
</html>
";
print $eng_abstract $eng_html;
close $eng_abstract;

open (my $swe_abstract, '>', "sammanfattning.html") || die $!;
my $swe_html = "<html>
  <head>
    <meta charset=\"UTF-8\">
    <title>$swetitle</title>
  </head>
  <body>
    <h1>$author</h1>
    <h2>$swetitle</h2>
    <h3>Sammanfattning</h3>
    <p>$sammanfattning</p>
  </body>
</html>
";
print $swe_abstract $swe_html;
close $swe_abstract;

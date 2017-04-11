#!/usr/bin/env perl

use warnings;
use strict;

use List::Util qw/min max/;

my ($partial_sum, $sticky_sum) = (0, 0);
my ($partial_num, $sticky_num) = (0, 0);
my ($min, $max) = (1000.0, 0);

while (<>) {
    chomp $_;

    if ($_ =~ m/Background partial.* paused ([0-9.]+)ms/) {
        $partial_sum += $1;
        $partial_num++;
        $min = min $1, $min;
        $max = max $1, $max;
    } elsif ($_ =~ m/Background partial.* paused ([0-9.]+)us/) {
        $partial_sum += $1/1000.0;
        $partial_num++;
        $min = min $1/1000.0, $min;
        $max = max $1/1000.0, $max;
    } elsif ($_ =~ m/Background sticky.* paused ([0-9.]+)ms/) {
        $sticky_sum += $1;
        $sticky_num++;
        $min = min $1, $min;
        $max = max $1, $max;
    } elsif ($_ =~ m/Background sticky.* paused ([0-9.]+)us/) {
        $sticky_sum += $1/1000.0;
        $sticky_num++;
        $min = min $1/1000.0, $min;
        $max = max $1/1000.0, $max;
    }
}

print "& $partial_num & $partial_sum & $sticky_num & $sticky_sum\\\\\n";
print STDERR "max: $max, min $min\n";

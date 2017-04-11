#!/usr/bin/env perl

use warnings;
use strict;

my ($partial_sum, $sticky_sum) = (0, 0);
my ($partial_num, $sticky_num) = (0, 0);

while (<>) {
    chomp $_;

    if ($_ =~ m/Background partial.* paused ([0-9.]+)ms/) {
        $partial_sum += $1;
        $partial_num++;
    } elsif ($_ =~ m/Background partial.* paused ([0-9.]+)us/) {
        $partial_sum += $1/1000.0;
        $partial_num++;
    } elsif ($_ =~ m/Background sticky.* paused ([0-9.]+)ms/) {
        $sticky_sum += $1;
        $sticky_num++;
    } elsif ($_ =~ m/Background sticky.* paused ([0-9.]+)us/) {
        $sticky_sum += $1/1000.0;
        $sticky_num++;
    }
}

print "& $partial_num & $partial_sum & $sticky_num & $sticky_sum\\\\\n";

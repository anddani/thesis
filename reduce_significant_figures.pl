#!/usr/bin/env perl

use warnings;
use strict;

use List::Util qw/min max/;

my $DEBUG = 0;

sub num_significant {
    $_[0] =~ /(^0*)/g;
    return length($1);
}

sub is_table {
    my $filename = $_;

    open(my $in, '<', $filename) or die $!;

    while (<$in>) {
        chomp;
        if ($_ =~ m/tabular/) {
            close($in);
            return 1;
        }
    }
    close($in);
    return 0;
}

# We want to find the number significant figures that
# are possible to shorten to without having 0 as mean
# or confidence interval. Minimum decimals is 2
sub reduce_significants {
    my $filename = shift;
    my $in;
    open($in, '<', $filename) or die $!;
    my ($mean_min, $conf_min) = (2, 2);

    # Find minimum significants
    while (<$in>) {
        chomp;
        while ($_ =~ m/(\d+)\.(\d{4}) \$\\pm\$ (\d+)\.(\d{4})/g) {
            my $mean = $1;
            my $mean_dec = $2;
            my $conf = $3;
            my $conf_dec = $4;
            if (int($mean) == 0) {
                $mean_min = max $mean_min, num_significant($mean_dec)+1;
            }
            if (int($conf) == 0) {
                $conf_min = max $conf_min, num_significant($conf_dec)+1;
            }
            print "$mean +- $conf | " if $DEBUG;
        }
        print "\n" if $DEBUG;
    }
    close($in);

    print "$mean_min $conf_min\n" if $DEBUG;

    # Remove last decimal digits
    my @new_file;
    open($in, '<', $filename) or die $!;
    while (<$in>) {
        my $line = $_;
        $line =~ s/(\.\d{$mean_min})\d*( \$\\pm\$ \d*\.\d{$conf_min})\d*/$1$2/g;
        push @new_file, $line;
        print "$line\n" if $DEBUG;
    }
    close($in);

    open(my $out, '>', $filename) or die $!;
    print $out @new_file;
    close($out);
}

while (<>) {
    chomp $_;
    my $file = $_;
    if (is_table($file)) {
        reduce_significants $file;
    }
}

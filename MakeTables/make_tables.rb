#!/usr/bin/env ruby

n = ARGF.readline.to_i

cos = Array.new(n/2)
sin = Array.new(n/2)

(n/2).times do |i|
  cos[i] = Math.cos(-2*Math::PI*i/n)
  sin[i] = Math.sin(-2*Math::PI*i/n)
end

File.open("precomputed_tables.h", "w+") do |f|
  f.puts("double sin_a[#{n/2}] = {")
  sin.each { |s| f.puts("\t#{"%.16f" % s},") }
  f.puts("};\n")
  f.puts("double cos_a[#{n/2}] = {")
  cos.each { |c| f.puts("\t#{"%.16f" % c},") }
  f.puts("};\n")
end

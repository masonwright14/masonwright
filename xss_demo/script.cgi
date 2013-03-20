#!/usr/bin/env ruby
require 'cgi'
File.open('output.txt', 'w') do |f1|  
cgi = CGI.new
data = cgi['data']
f1.puts "result: #{data}"
puts "Content-type: text/plain\n\n"
puts "Hello, world"
end

# note: when running from command line, enter Ctl-D to finish
# entering parameters.

# how to add a parameter: data=12345

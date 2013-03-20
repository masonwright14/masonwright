#!/usr/bin/env ruby
require 'cgi'
File.open('comments.txt', 'a') do |f1|  
cgi = CGI.new
comment = cgi['comment']
f1.puts "*****\n #{comment}"
puts "Content-type: text/plain\n\n"
puts "OK"
end


#!/usr/bin/env ruby
require 'cgi'
File.open('comments.txt', 'r') do |f1|  
cgi = CGI.new
cookie = cgi['cookie']
puts "Content-type: text/html\n\n"
  while (line = f1.gets)
    puts "#{line}"
  end
puts "\n"
end


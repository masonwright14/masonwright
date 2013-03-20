#!/usr/bin/ruby

require 'webrick'

dir = Dir::pwd

puts "Hit Control-C to stop the server"
puts "---"

server = WEBrick::HTTPServer.new(:Port => 3000, :DocumentRoot => dir)

trap("INT") { server.shutdown }

server.start

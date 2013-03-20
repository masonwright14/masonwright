
1. Look up own IP address.
-Apple -> About This Mac -> More Info… -> Network -> Airport IPv4 Addresses (example: 10.0.0.13)

2. Update IP addresses:
	run:
	cd dropbox/os/project/src
	perl setUrl.pl TODO myUrl
	-DON'T add quotes around TODO and the URL.

	-Update IP address from TODO:
	A. links.txt
	B. email.html
	-in TextEdit: Preferences > Open and Save > Ignore rich text
		commands in HTML files. Open again.

	-Update var ip_body:
	A. blog_unsafe.js
	B. blog_safer.js

3. Start server from project directory.
	run:
	# from dropbox/os/project/src
	ruby server.rb

4. Clear extra comments from comments.txt:
It should read:

i h8 this blog. it is so awful.
*****
hey doodz, you like anime?

5. Delete all text in output.txt.

6. Double-click code.html to open in in Chrome.

7. Go through links in links.txt, in order. 
(Do NOT just open files by double-clicking! Cookies would not be set.)

Take-down:
run:
	cd dropbox/os/project/src
	perl setUrl.pl myUrl TODO

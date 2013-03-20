
window.onload = function() {
    showQueryValue();
};

var showQueryValue = function() {
    var decodedUrl = decodeURIComponent(document.location.href);
    var newHTML = '';
    if (decodedUrl.indexOf('?') != -1) {
	var query = decodedUrl.substring(decodedUrl.indexOf('?') + 1);

	if (query.indexOf('name') != -1) {
	    var value = query.substring(query.indexOf('name') + 5);
	    value = cleanText(value);
	    newHTML = '<p>Welcome, ' + value + '!</p>';
	}
    } else {
	newHTML = '<p>Welcome!<\p>';
    }
    
    document.getElementById('welcome').innerHTML = newHTML;
};

var dangerousCharList = ['<', '>'];

var cleanText = function(text) {
    var result = "";

    for (var i = 0; i < text.length; i++) {
	var c = text.charAt(i);
	if (dangerousCharList.indexOf(c) == -1) {
	    // not a dangerous character
	    result += c;
	}
    }

    return result;
};

var submit = function() {
    // var username = document.getElementById('username').value;
    // var password = document.getElementById('password').value;
    // do nothing
};


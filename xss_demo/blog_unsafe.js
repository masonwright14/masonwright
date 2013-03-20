
window.onload = function() {
    document.cookie = "secret_personal_info=42";
    requestFileText();
};

// replace with current IP address body
var ip_body = '10.20.65.224';

var ip_prefix = 'http://'
var ip_postfix = ':3000';
var ip = ip_prefix + ip_body + ip_postfix;

var commentList = [];

var submit = function() {
    var newComment = document.getElementById('message').value;
    var request = new XMLHttpRequest();
    request.onreadystatechange = function() {
	if (request.readyState == 4) {
	    requestFileText();
	}
    };

    request.open('GET', ip + '/postFileText.cgi?comment=' + newComment, true);
    request.send();
};

var DELIMITER = "*****";

var requestFileText = function() {
    var request = new XMLHttpRequest();
    request.onreadystatechange = function() {
	if (request.readyState == 4) {
	    textAvailable(request.responseText);
	}
    };

    request.open('GET', ip + '/getFileText.cgi', true);
    request.send();
};

var textAvailable = function(text) {
    commentList = text.split(DELIMITER);
    displayComments();
};

var displayComments = function() {
    var newHTML = '';

    for (var i = 0; i < commentList.length; i++) {
	newHTML += '<p>';
	newHTML += commentList[i];
	newHTML += '</p>';
    }

    document.getElementById('userComments').innerHTML = newHTML;
};


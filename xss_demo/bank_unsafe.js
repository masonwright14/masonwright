
window.onload = function() {
    document.cookie = "secret_user_info=42";
    showQueryValue();
};

var showQueryValue = function() {
    var decodedUrl = decodeURIComponent(document.location.href);
    var newHTML = '';
    if (decodedUrl.indexOf('?') != -1) {
	var query = decodedUrl.substring(decodedUrl.indexOf('?') + 1);

	if (query.indexOf('name') != -1) {
	    var value = query.substring(query.indexOf('name') + 5);
	    newHTML = '<p>Welcome, ' + value + '!</p>';
	}
    } else {
	newHTML = '<p>Welcome!<\p>';
    }
    
    document.getElementById('welcome').innerHTML = newHTML;
};


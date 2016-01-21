/* global remote, user, model */

(function () {
	var result = remote.connect('alfresco').get('/lecm/security/api/isAdmin?login=' + encodeURI(user.id));

	model.isAdmin = false;

	if (200 == result.status) {
		var nativeObject = eval('(' + result + ')');
		model.isAdmin = nativeObject.isAdmin;
	}
})();

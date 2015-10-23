model.hasRole = false;
// Get the user name of the person to get
var login = user.id;
var url = '/lecm/security/api/isAdmin?login=' + encodeURI(login);
var isAdminResponse = remote.connect("alfresco").get(url);
if (isAdminResponse.status == 200) {
    var result = eval('(' + isAdminResponse + ')');
    model.hasRole = result.isAdmin;
}

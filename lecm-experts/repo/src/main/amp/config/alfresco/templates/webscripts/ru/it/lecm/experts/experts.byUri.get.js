function checkExpertExist(exists, newExp) {
    for (var index in exists) {
        var exist = exists[index];
        var existLogin = exist.properties["cm:userName"];
        if (newExp == existLogin) {
            return true;
        }
    }
    return false;
}


var nodeRef = 'empty';

for (arg in args) {
    if (arg == 'nodeRef') {
        nodeRef = args[arg];
    }
}

var res = [];

if (nodeRef != 'empty') {
    // get experts from service
    var resp = new String(expertsByUri.get(nodeRef));

    //get current Node
    var node = search.findNode(nodeRef);

    var existExperts = [];
    if (node != null) {
        existExperts = node.assocs["lecm-exp:experts"];
    }
    //process response
    var oExperts = eval("(" + resp + ")");

    for (var index in oExperts) {
        var login = oExperts[index].lname;

        if (!checkExpertExist(existExperts, login)) {
            var ppl = people.getPerson(login);
            if (ppl != null) {
                // if user find in system add it to node
                var first = ppl.properties["cm:firstName"];
                var last = ppl.properties["cm:lastName"];
                var fullName = first + " " + last;
                var link = new String(url.server + "/" + base.getShareContext() + "/page/user/" + login + "/profile");
                var ref = ppl.getNodeRef().toString();

                node.createAssociation(ppl, "lecm-exp:experts");

                res.push({
                    lname:login,
                    fname:fullName,
                    nodeRef:ref,
                    ulink:encodeURI(link)
                });
            }
        }
    }
}
// pass to page
model.experts = res;
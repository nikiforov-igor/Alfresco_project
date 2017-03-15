var docRef = args["nodeRef"];
var doc = search.findNode(docRef);
var hasAccess = false;
if (doc){
    hasAccess = lecmPermission.hasReadAccess(doc);
}
model.hasAccess = hasAccess;
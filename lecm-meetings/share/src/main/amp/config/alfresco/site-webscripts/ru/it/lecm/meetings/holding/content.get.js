<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">

function main() {
    var nodeRef = "";
    for (var prop in page.url.args) {
        if (prop == "nodeRef") {
            nodeRef = page.url.args[prop];
        }
    }

	var mayView = hasPermission(nodeRef, PERM_ATTR_LIST);
	var mayAdd = hasPermission(nodeRef, PERM_ATTR_EDIT);
	var docHasStatemachine = hasStatemachine(nodeRef);

    var documentEdit = {
        name: "LogicECM.module.Meetengs.Holding",
        options: {
            nodeRef: nodeRef,
            formId: "holding",
			mayView: mayView,
			mayAdd: mayAdd,
			hasStatemachine: docHasStatemachine
        }
    };
    model.widgets = [documentEdit];

    var isEditLockEnabled = "false";
    var connector = remote.connect("alfresco").get('/lecm/documents/isEditLockEnabled');
    if(connector.status == 200){
        var nativeObject = eval("(" + connector + ")");
        isEditLockEnabled = nativeObject.isEditLockEnabled;
    }
    model.isEditLockEnabled = isEditLockEnabled;
}

main();
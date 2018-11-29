<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">

function main() {
    var urlArgs = {};
    var nodeRef = "";
    var formId = "editForm";
    for (var prop in page.url.args) {
        if (prop == "nodeRef") {
            nodeRef = page.url.args[prop];
        } else if (prop == "formId") {
            formId = page.url.args[prop];
        } else {
            urlArgs[prop] = page.url.args[prop];
        }
    }

    var highlightedFields = [];
    var params = page.url.args["p1"];
    if (params != null) {
        var decodeParams = new java.lang.String(Packages.org.apache.commons.codec.binary.Base64.decodeBase64(params));

        var hash = page.url.args["p2"];
        if (hash == (new Packages.java.lang.String(decodeParams)).hashCode()) {
            var paramsArray = decodeParams.split("&");
            for (var i in paramsArray) {
                var param = paramsArray[i];
                var name = param.split("=")[0];
                var value = param.split("=")[1];
                if (name && value) {
                    if (name == "highlightedFields") {
                        highlightedFields = eval("(" + value + ")");
                    }
                }
            }
        }
    }

    var mayView = hasPermission(nodeRef, PERM_ATTR_LIST);
    var mayAdd = hasPermission(nodeRef, PERM_ATTR_EDIT);
    var docHasStatemachine = hasStatemachine(nodeRef);
    model.nodeRef = nodeRef;

    var documentEdit = {
        name: "LogicECM.module.Documents.Edit",
        options: {
            nodeRef: nodeRef,
            formId: formId,
            mayView: mayView,
            mayAdd: mayAdd,
            hasStatemachine: docHasStatemachine,
            args: urlArgs,
            higlightedFields: highlightedFields
        }
    };

    var documentPreview = {
        name: "LogicECM.control.Preview",
        initArgs: ["\"" + args["htmlid"] + "-preview\"" ]
    };

    model.widgets = [documentEdit, documentPreview];
    model.isEditLockEnabled = false;
    var reqBody = {nodeRef: nodeRef};

    var connector = remote.connect("alfresco").post("/lecm/document/api/checkLock", jsonUtils.toJSONString(reqBody), "application/json");
    if (connector.status == 200) {
        var nativeObject = JSON.parse(connector);
        model.isEditLockEnabled = nativeObject.isEditLockEnabled;
        model.locked = nativeObject.locked;
        model.canRelease = nativeObject.canRelease;
    }

    //если не залочено, вероятно переход в редактирование осуществлен по прямой ссылке, блокируем
    if(!model.locked){
        remote.connect("alfresco").post("/lecm/document/api/lockDocument", jsonUtils.toJSONString(reqBody), "application/json");
    }
};

main();
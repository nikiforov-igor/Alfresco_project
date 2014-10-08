function main() {
    var urlArgs = {};
    var nodeRef = "";
    var formId = "";
    for (var prop in page.url.args) {
        if (prop == "nodeRef") {
            nodeRef = page.url.args[prop];
        } else if (prop == "formId") {
            formId = page.url.args[prop];
        } else {
            urlArgs[prop] = page.url.args[prop];
        }
    }

    var params = page.url.args["p1"];
    if (params != null) {
        var decodeParams = new java.lang.String(Packages.org.apache.commons.codec.binary.Base64.decodeBase64(params));

        var highlightedFields = [];
        var hash = page.url.args["p2"];
        if (hash == (new Packages.java.lang.String(decodeParams)).hashCode()) {
            var paramsArray = decodeParams.split("&");
            for (var i in paramsArray) {
                var param = paramsArray[i];
                var name = param.split("=")[0];
                var value = param.split("=")[1];
                if (name != null && value != null) {
                    if (name == "highlightedFields") {
                        highlightedFields = eval("(" + value + ")");
                    }
                }
            }
        }
    }

    var documentEdit = {
        name: "LogicECM.module.Documents.Edit",
        options: {
            nodeRef: nodeRef,
            formId: formId,
            args: urlArgs,
            higlightedFields: highlightedFields
        }
    };

    var documentPreview = {
        name: "LogicECM.control.Preview",
        initArgs: ["\"" + args["htmlid"] + "-preview\"" ]
    };

    model.widgets = [documentEdit, documentPreview];
}

main();
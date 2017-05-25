<import resource="classpath:alfresco/site-webscripts/org/alfresco/callutils.js">
(function () {
    var params = page.url.args["p1"];
    var hash = page.url.args["p2"];
    var currentEmployee = doGetCall("/lecm/orgstructure/api/getCurrentEmployee");

    var decodeParams = new java.lang.String(Packages.org.apache.commons.codec.binary.Base64.decodeBase64(params));
    var paramsArray = decodeParams.split("&");
    for (var i in paramsArray) {
        var param = paramsArray[i];
        var name = param.split("=")[0];
        var value = param.split("=")[1];
        if (name && value) {
            if (name == "employee") {
                model.employee = value;
                var shortName = doPostCall('/lecm/substitude/format/node', jsonUtils.toJSONString({
                    nodeRef: value,
                    substituteString: "{lecm-orgstr:employee-short-name}"
                }));
                model.employeeName = shortName ? shortName.formatString : "";
            } else if (name == "templateCode") {
                model.templateCode = value;
            }  else if (name == "template") {
                var tempDesc = doPostCall('/lecm/substitude/format/node', jsonUtils.toJSONString({
                    nodeRef: value,
                    substituteString: "{lecm-notification-template:description}"
                }));
                model.templateDescription = tempDesc ? tempDesc.formatString : value;
            }
        }
    }
    model.hasPermission = !!(hash == (new Packages.java.lang.String(decodeParams)).hashCode() && currentEmployee.nodeRef == model.employee);

})();

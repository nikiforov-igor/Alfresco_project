var url = "/lecm/statemachine/documents?documentType=lecm-contract:document";
var json = remote.connect("alfresco").get(url);

if (json.status == 200) {
    var obj = eval("(" + json + ")");
    model.documents = obj;
}

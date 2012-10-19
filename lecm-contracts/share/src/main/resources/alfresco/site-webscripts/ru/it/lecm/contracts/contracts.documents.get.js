var json = remote.call("/lecm/statemachine/documents?documentType=lecm-contract:document");

if (json.status == 200) {
    var obj = eval("(" + json + ")");
    model.documents = obj;
}

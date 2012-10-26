function main() {
    var source = json.has("source") ? json.get("source") : '';
    var target = json.has("target") ? json.get("target") : '';
    var assocType = json.has("assocType") ? json.get("assocType") : '';

    if (source != '' && target !='' && assocType != ''){
        var s = search.findNode(source);
        var t = search.findNode(target);
        s.createAssociation(t, assocType);
    }  else {
        status.setCode(status.STATUS_BAD_REQUEST, "Bad request. Must set all parameters");
    }
}

main();
function main() {
    var source = json.has("source") ? json.get("source") : '';
    var target = json.has("target") ? json.get("target") : '';
    var assocType = json.has("assocType") ? json.get("assocType") : '';

    if (source != '' && target !='' && assocType != ''){
        var s = search.findNode(source);
        var t = search.findNode(target);
        s.createAssociation(t, assocType);
        status.code = 200;
    }  else {
        status.code = 0;
    }
}

main();
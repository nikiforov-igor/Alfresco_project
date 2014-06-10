function main() {
    var source = json.has("source") ? json.get("source") : '';
    var target = json.has("target") ? json.get("target") : '';
    var assocType = json.has("assocType") ? json.get("assocType") : '';
    var createdAssoc = null;
    if (source != '' && target !='' && assocType != ''){
        var s = search.findNode(source);
        var t = search.findNode(target);
        createdAssoc = s.createAssociation(t, assocType);
        model.message = "Successfully created association for items [" + source + "," + target + "]";
    }  else {
        model.message = "Bad request. Must set all parameters";
    }
    model.createdAssoc = createdAssoc != null ? createdAssoc.getAssociationRef().toString() : null;

}

main();

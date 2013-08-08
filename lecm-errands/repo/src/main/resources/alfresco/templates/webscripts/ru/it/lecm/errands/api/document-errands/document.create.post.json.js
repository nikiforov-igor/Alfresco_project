function main() {
    var nodeRef = json.get("alf_destination");
    var name = json.get("prop_cm_name");
    var url = json.get("prop_lecm-links_url");
    var isExecute = json.get("isExecute");

    var link = errands.createLinks(nodeRef, name, url, (isExecute == "true"));
    model.link = link;
    model.success = (link.nodeRef !== null);
}
main();
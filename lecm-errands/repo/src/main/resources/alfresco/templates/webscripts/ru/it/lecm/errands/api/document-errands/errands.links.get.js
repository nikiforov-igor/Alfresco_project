function main() {
    var nodeRef = args['nodeRef'];
    var assocType = args['assocType'];

    var links;

    if (assocType != null) {
        links = errands.getLinksByAssociation(nodeRef, assocType);
    } else {
        links = errands.getLinks(nodeRef);
    }

    model.links = links;
}
main();
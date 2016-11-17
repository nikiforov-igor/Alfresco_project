function main() {
    var documentNodeRef = args['documentNodeRef'];
    var nodeRef = utils.getNodeFromString(documentNodeRef);

    // for all documents except meetings
    var pageName = 'document';

    // for meetings
	if (nodeRef.typeShort == 'lecm-meetings:document') {
        pageName = 'event';
    }
    model.pageName = pageName;
}

main();
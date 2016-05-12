function main() {
    var ctx = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
    var nodeService = ctx.getBean("nodeService");

    var documentNodeRef = args['documentNodeRef'];
    var nodeRef = new Packages.org.alfresco.service.cmr.repository.NodeRef(documentNodeRef);

    var EVENTS_NAMESPACE_URI = "http://www.it.ru/logicECM/meetings/1.0";
    var EVENT_TYPE = Packages.org.alfresco.service.namespace.QName.createQName(EVENTS_NAMESPACE_URI, "document");

    // for all documents except meetings
    var pageName = 'document';

    // for meetings
    if (nodeService.getType(nodeRef).equals(EVENT_TYPE)) {
        pageName = 'event';
    }
    model.pageName = pageName;
}

main();
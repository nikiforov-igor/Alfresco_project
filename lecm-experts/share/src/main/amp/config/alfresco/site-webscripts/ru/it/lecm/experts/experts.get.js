<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:/alfresco/templates/ru/it/lecm/experts/experts-util.js">

function main()
{
    var l = AlfrescoUtil.param('nodeRef');
    var experts = [];
    experts = ExpertsUtil.getExperts(model.nodeRef);
    model.experts = experts;
}

main();
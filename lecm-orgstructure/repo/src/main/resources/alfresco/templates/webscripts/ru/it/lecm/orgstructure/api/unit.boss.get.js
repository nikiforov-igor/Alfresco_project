<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/orgstructure/api/orgstructure.lib.js">

function main() {
    var node = search.findNode(args["nodeRef"]);
    var boss = findBoss(node);
    model.boss = boss;
}

main();
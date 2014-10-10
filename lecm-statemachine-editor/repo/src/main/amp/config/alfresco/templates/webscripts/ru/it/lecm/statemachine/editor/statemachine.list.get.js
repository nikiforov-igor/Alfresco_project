var ctx = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
var dictionaryService = ctx.getBean("dictionaryService");
var qname = Packages.ru.it.lecm.documents.beans.DocumentService.TYPE_BASE_DOCUMENT;
var types = dictionaryService.getSubTypes(qname, true);
types.remove(qname);

types = types.toArray();

var defaultStatemachines = ctx.getBean("defaultStatemachines");


var machines = [];
for each (var type in types) {
    var typeDef = dictionaryService.getType(type)
    var typeString = type.toPrefixString();
    if (defaultStatemachines.getPath(typeString) != null) {
        var machineFolder = lecmRepository.getHomeRef().childByNamePath("statemachines/" + typeString.replace(":", "_") + "/statuses");
        machines.push({
            id: typeString.replace(":", "_"),
            title: typeDef.getTitle(),
            description: typeDef.getDescription(),
            packageNodeRef: machineFolder == null ? null : machineFolder.nodeRef.toString()
        });
    }
}

model.result = jsonUtils.toJSONString(machines);
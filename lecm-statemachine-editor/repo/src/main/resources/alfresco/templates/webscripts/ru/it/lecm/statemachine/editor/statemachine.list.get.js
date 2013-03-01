var ctx = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
var dictionaryService = ctx.getBean("dictionaryService");
var qname = Packages.ru.it.lecm.documents.beans.DocumentService.TYPE_BASE_DOCUMENT;
var types = dictionaryService.getSubTypes(qname, true);
types.remove(qname);

types = types.toArray();

var machines = [];
for each (var type in types) {
    var typeDef = dictionaryService.getType(type)
    machines.push({
        id: type.toPrefixString().replace(":", "_"),
        title: typeDef.getTitle(),
        description: typeDef.getDescription()
    });
}

model.machines = machines;
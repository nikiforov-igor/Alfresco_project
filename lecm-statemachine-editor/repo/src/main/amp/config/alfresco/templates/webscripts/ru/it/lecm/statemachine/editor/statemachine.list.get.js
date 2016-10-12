var qname = base.createQName('lecm-document:base');
var types = base.getSubTypes(qname, true);
types.remove(qname);

types = types.toArray();

var machines = [];
for each (var type in types) {
    var typeDef = base.getType(type);
    var typeString = type.toPrefixString();
    if (statemachine.getDefaultStatemachinePath(typeString) != null) {
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
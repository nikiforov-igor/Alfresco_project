var privileges = lecmPermission.getPermGroups().toArray();

var result = [];

var exludeNonePrivilege = (args["dynamic"] == null);
for each (var privilegy in privileges) {
    if (exludeNonePrivilege && privilegy.getName() != "LECM_BASIC_PG_None") {
        result.push({
            value: privilegy.getName(),
            label: privilegy.getLabel()
        });
    } else {
        result.push({
            value: privilegy.getName(),
            label: privilegy.getLabel()
        });
    }
}

model.result = result;


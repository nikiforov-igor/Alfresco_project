var result = [];
var incoming = search.findNode(args["documentNodeRef"]);
if (incoming) {
    var recipientsAssoc = incoming.associations["lecm-incoming:recipient-assoc"];
    if (recipientsAssoc) {
        var i, boss;
        for (i = 0; i < recipientsAssoc.length; i++) {
            if (recipientsAssoc[i].typeShort == "lecm-orgstr:organization-unit") {
                boss = orgstructure.findUnitBoss(recipientsAssoc[i].nodeRef.toString());
                if (!boss) {
                    result.push({
                        nodeRef: recipientsAssoc[i].nodeRef.toString(),
                        name: substitude.getObjectDescription(recipientsAssoc[i])
                    });
                }
            }
        }
    }
}
model.results = result;

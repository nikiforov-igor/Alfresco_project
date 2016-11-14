var results = [];
var incoming =  search.findNode(args["documentNodeRef"]);
if (incoming) {
    var recipientsAssoc = incoming.associations["lecm-incoming:recipient-assoc"];
    if (recipientsAssoc) {
        var recipients = [];
        var i, boss;
        for (i = 0; i < recipientsAssoc.length; i++) {
            if (recipientsAssoc[i].typeShort == "lecm-orgstr:employee") {
                recipients.push(recipientsAssoc[i]);
            } else if (recipientsAssoc[i].typeShort == "lecm-orgstr:organization-unit") {
                boss = orgstructure.findUnitBoss(recipientsAssoc[i].nodeRef.toString());
                if (boss) {
                    recipients.push(boss);
                }
            }
        }

        var errandTypeToReview, errandTypeToExecute;
        var errandTypesDictionary = dictionary.getDictionaryByName("Типы поручений");
        if (errandTypesDictionary) {
            errandTypeToReview = errandTypesDictionary.childByNamePath("На рассмотрение");
            errandTypeToExecute = errandTypesDictionary.childByNamePath("На исполнение (неконтрольное)");
        }

        for (i = 0; i < recipients.length; i++) {
            var errandType = errandTypeToExecute;
            if (orgstructure.hasBusinessRole(recipients[i], "RVZ")) {
                errandType = errandTypeToReview;
            }

            results.push({
                recipient: recipients[i].nodeRef.toString(),
                errandType: errandType.nodeRef.toString()
            })
        }
    }
}
model.results = results;

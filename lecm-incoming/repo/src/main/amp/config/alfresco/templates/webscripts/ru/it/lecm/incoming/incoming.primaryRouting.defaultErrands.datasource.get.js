var toExecute = [];
var toReview = [];
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
        var dicName = msg.get("ru.it.lecm.dictionaries.errandTypes.name") || "Типы поручений";
        var errandTypesDictionary = dictionary.getDictionaryByName(dicName);
        if (errandTypesDictionary) {
            errandTypeToReview = errandTypesDictionary.childByNamePath("На рассмотрение");
            if (!errandTypeToReview) {
                errandTypeToReview = errandTypesDictionary.childByNamePath(msg.get("lecm.errands.dictionary.types.errandTypeToReview"));
            }
            errandTypeToExecute = errandTypesDictionary.childByNamePath("На исполнение (неконтрольное)");
            if (!errandTypeToExecute) {
                errandTypeToExecute = errandTypesDictionary.childByNamePath(msg.get("lecm.errands.dictionary.types.errandTypeToExecute"));
            }
        }

        var incomingType = null;
        var incomingTypeAssoc = incoming.assocs["lecm-eds-document:document-type-assoc"];
        if (incomingTypeAssoc && incomingTypeAssoc.length) {
            incomingType = incomingTypeAssoc[0];
        }

        for (i = 0; i < recipients.length; i++) {
            if (orgstructure.hasBusinessRole(recipients[i], "RVZ")) {
                var attrs = {
                    recipient: recipients[i].nodeRef.toString(),
                    errandType: errandTypeToReview.nodeRef.toString()
                };
                if (incomingType) {
                    attrs.executionDateDays = incomingType.properties["lecm-incoming-dic:review-date-days"];
                    attrs.executionDateType = incomingType.properties["lecm-incoming-dic:review-date-type"];
                }
                toReview.push(attrs)
            } else {
                attrs = {
                    recipient: recipients[i].nodeRef.toString(),
                    errandType: errandTypeToExecute.nodeRef.toString()
                };
                if (incomingType) {
                    attrs.executionDateDays = incomingType.properties["lecm-incoming-dic:execution-date-days"];
                    attrs.executionDateType = incomingType.properties["lecm-incoming-dic:execution-date-type"];
                }
                toExecute.push(attrs)
            }
        }
    }
}
model.results = toReview.concat(toExecute);

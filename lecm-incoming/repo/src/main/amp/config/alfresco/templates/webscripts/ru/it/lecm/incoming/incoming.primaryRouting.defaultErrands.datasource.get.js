var results = [];
var incoming =  search.findNode(args["parentDoc"]);
if (incoming) {
    var recipients = incoming.associations["lecm-incoming:recipient-assoc"];
    if (recipients) {
        var i, errandTypeToReview, errandTypeToExecute;
        var errandTypesDictionary = dictionary.getDictionaryByName("Типы поручений");
        if (errandTypesDictionary) {
            errandTypeToReview = errandTypesDictionary.childByNamePath("На рассмотрение");
            errandTypeToExecute = errandTypesDictionary.childByNamePath("На исполнение (неконтрольное)");
        }

        for (i = 0; i < recipients.length; i++) {
            var errandType = errandTypeToExecute;
            if (orgstructure.hasBusinessRole(recipients[i], "DA_SIGNERS")) {
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

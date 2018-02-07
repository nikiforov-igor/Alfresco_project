var ErrandsScripts = {

    changeStatusDocumentByExecution: function(errand) {
        if (errand) {
            var executorAssoc = errand.assocs["lecm-errands:executor-assoc"];
            var nodeList = documentConnection.getConnectedWithDocument(errand, "onBasis", "lecm-incoming:document");

            nodeList.forEach(function (node) {
                var status = node.properties["lecm-statemachine:status"];
                if ((status == ("Направлен на исполнение") || status == ("На рассмотрении") || status == ("Зарегистрирован")) ||
                    (status == msg.get("lecm.incoming.statemachine-status.direct_to_execution") || status == msg.get("lecm.incoming.statemachine-status.on_review") || status == msg.get("lecm.incoming.statemachine-status.registrated"))) {
                    if (executorAssoc) {
                        node.properties["lecm-incoming:auto-transition-from-errand"] = executorAssoc[0].nodeRef;
                        node.save();
                    }
                }
            });
        }

    }

};
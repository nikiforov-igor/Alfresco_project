var item = search.findNode(args["nodeRef"]);
if (item) {
    var title = item.properties["lecm-ord-table-structure:title"];
    var content = item.properties["lecm-ord-table-structure:item-content"];
    var number = item.properties["lecm-document:indexTableRow"];
    var currentUser = orgstructure.getCurrentEmployee();
    var reason = "Поручение автоматически закрыто в связи с исполнением пункта";
    var errandAssoc = item.assocs["lecm-ord-table-structure:errand-assoc"];
    if (errandAssoc && errandAssoc.length) {
        edsDocument.sendCompletionSignal(errandAssoc[0], reason, currentUser);
    }
    ordStatemachine.changePointStatus(item.nodeRef.toString(), "EXECUTED_STATUS");
    var recipients = [];
    var ordDoc = documentTables.getDocumentByTableDataRow(item);
    var controllerAssoc = ordDoc.assocs["lecm-ord:controller-assoc"];
    if (controllerAssoc && controllerAssoc.length) {
        recipients.push(controllerAssoc[0]);
    }
    var status = ordStatemachine.getPointStatusTextByCode("EXECUTED_STATUS");
    notifications.sendNotificationFromCurrentUser({
        recipients: recipients,
        templateCode: 'ORD_ITEM_CHANGE_STATUS',
        templateConfig: {
            mainObject: ordDoc,
            eventExecutor: currentUser,
            number: "Пункт номер " + number,
            numberTitle: title + " " + content,
            status: status,
            statusTitle: ""
        },
        dontCheckAccessToObject: true
    });

    var wrapPoint = documentScript.wrapperTitle((msg.get("ru.it.lecm.ord.bjMessages.pointExecuted.wrapPointParamText") || "пункт номер ") + number, title + " " + content);
    var logText = msg.get("ru.it.lecm.ord.bjMessages.pointExecuted.message") || "#initiator исполнил {wrapPoint} ОРД:#mainobject";
    logText = logText.replace("{wrapPoint}", wrapPoint);
    businessJournal.log(ordDoc.nodeRef.toString(), "POINT_EXECUTED", logText, []);

    model.success = true;
}

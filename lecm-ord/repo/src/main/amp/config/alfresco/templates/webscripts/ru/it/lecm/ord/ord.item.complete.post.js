var item = search.findNode(args['nodeRef']);
var requestContent = eval("(" + requestbody.getContent() + ")");
var completionOption = requestContent["prop_lecm-ord-table-structure_completion-option"];
var comment = requestContent["prop_lecm-ord-table-structure_item-comment"];
var errand = item.assocs["lecm-ord-table-structure:errand-assoc"][0];
var currentUser = orgstructure.getCurrentEmployee();
var reason = "";
var recipients = [];

var ordDoc = documentTables.getDocumentByTableDataRow(item);
var ordControllerAssoc = ordDoc.assocs["lecm-ord:controller-assoc"];
var ordController = null;
if (ordControllerAssoc && ordControllerAssoc.length) {
    ordController = ordControllerAssoc[0];
}
var itemController = null;
var itemControllerAssoc = item.assocs["lecm-ord-table-structure:controller-assoc"];
if (itemControllerAssoc && itemControllerAssoc.length) {
    itemController = itemControllerAssoc[0];
}
if (ordController && itemController) {
    if (currentUser.equals(ordController)) {
        recipients.push(itemController);
    } else if (currentUser.equals(itemController)) {
        recipients.push(ordController);
    }
}

var itemExecutorAssoc = item.assocs["lecm-ord-table-structure:executor-assoc"];
if (itemExecutorAssoc && itemExecutorAssoc.length) {
    recipients.push(itemExecutorAssoc[0]);
}
var itemCoexecutorsAssoc = item.assocs["lecm-ord-table-structure:coexecutors-assoc"];
if (itemCoexecutorsAssoc && itemCoexecutorsAssoc.length) {
    itemCoexecutorsAssoc.forEach(function (coexecutor) {
        recipients.push(coexecutor);
    })
}
var statusCode = "";
if (completionOption == "CANCEL") {
    reason = "Поручение отменено в связи с отменой работы по пункту Контролером пункта/Контролером ОРД ";
    reason += documentScript.wrapperLink(currentUser, currentUser.properties["lecm-orgstr:employee-short-name"]);
    errands.sendCancelSignal(errand.nodeRef.toString(), reason, currentUser.nodeRef.toString());
    statusCode = "CANCELED_BY_CONTROLLER_STATUS";
} else if (completionOption == "EXECUTE") {
    reason = "Поручение исполнено Контролером пункта/Контролером ОРД ";
    reason += documentScript.wrapperLink(currentUser, currentUser.properties["lecm-orgstr:employee-short-name"]);
    edsDocument.sendCompletionSignal(errand, reason, currentUser);
    statusCode = "EXECUTED_BY_CONTROLLER_STATUS";
    item.properties["lecm-ord-table-structure:item-comment"] = comment;
    item.save();
}
ordStatemachine.changePointStatus(item.nodeRef.toString(), statusCode);
var status = ordStatemachine.getPointStatusTextByCode(statusCode);
var content = item.properties["lecm-ord-table-structure:item-content"];
var title = item.properties["lecm-ord-table-structure:title"];

var number = item.properties["lecm-document:indexTableRow"];
notifications.sendNotificationFromCurrentUser({
    recipients: recipients,
    templateCode: 'ORD_ITEM_CHANGE_STATUS',
    templateConfig: {
        mainObject: ordDoc,
        eventExecutor: currentUser,
        number: "Пункт номер " + number,
        numberTitle: title + " " + content,
        status: status,
        statusTitle: comment
    },
    dontCheckAccessToObject: true
});
var logText = "#initiator перевел ";
logText += documentScript.wrapperTitle("пункт номер " + number, title + " " + content);
logText += " " + documentScript.wrapperDocumentLink(ordDoc, "ОРД") + " в статус ";
logText += documentScript.wrapperTitle(status, comment);
businessJournal.log(ordDoc.nodeRef.toString(), "POINT_COMPLETED", logText, []);
model.success = true;

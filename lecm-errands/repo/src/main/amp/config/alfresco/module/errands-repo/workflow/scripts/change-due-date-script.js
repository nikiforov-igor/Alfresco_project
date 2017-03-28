function processDueDateChanges(params) {
    var document = params.document;
    var currentUser = orgstructure.getCurrentEmployee();
    var processChild, changeDateReason, newDueDate, dateRadio;
    var isSignal = params.isSignal;
    var logObjects = [];
    var logText;
    var changed = true;
    var dueDateString = "";
    var limitless = false;
    var shiftSize = 0;
    var newLimitationDate = null;
    var oldLimitDate = document.properties["lecm-errands:limitation-date"];
    var oldLimitRadio = document.properties["lecm-errands:limitation-date-radio"];
    if (isSignal) {
        limitless = document.properties["lecm-eds-aspect:duedate-limitless"];
        shiftSize = document.properties["lecm-eds-aspect:duedate-shift-size"];
        newDueDate = document.properties["lecm-eds-aspect:new-limitation-date"];
        changeDateReason = document.properties["lecm-eds-aspect:change-duedate-reason"];
        processChild = true;
        if (limitless) {
            dateRadio = "LIMITLESS";
        } else {
            dateRadio = "DATE";
        }
    } else {
         processChild = params.processChild;
         changeDateReason = params.changeDateReason;
         newDueDate = params.newDueDate;
         dateRadio = params.dateRadio;
    }
    if (dateRadio == "DATE") {
        newLimitationDate = new Date(newDueDate.getTime());
        if (!isSignal) {
            document.properties["lecm-errands:limitation-date"] = newDueDate;
            if (oldLimitDate && oldLimitRadio != "LIMITLESS") {
                shiftSize = newLimitationDate.getTime() - oldLimitDate.getTime();
            }
        } else {
            if (oldLimitDate && oldLimitRadio != "LIMITLESS") {
                newLimitationDate = new Date(oldLimitDate.getTime() + shiftSize);
                document.properties["lecm-errands:limitation-date"] = newLimitationDate;
            } else {
                document.properties["lecm-errands:limitation-date"] = newLimitationDate;
            }
        }
        var day = utils.pad(newLimitationDate.getDate(), 2);
        var month = utils.pad(newLimitationDate.getMonth() + 1, 2);
        var year = utils.pad(newLimitationDate.getFullYear(), 4);
        dueDateString = day + "." + month + "." + year;
        var wfeDate = new Date(document.properties["lecm-errands:wait-for-execution-date"]);
        var fromWFELimitDays = Math.round((newLimitationDate - wfeDate) / (1000 * 60 * 60 * 24));
        var halfLimitDays = Math.round(fromWFELimitDays / 2);
        wfeDate.setDate(wfeDate.getDate() + halfLimitDays);
        document.properties["lecm-errands:half-limit-date"] = wfeDate;
        var shortLimitDays = edsGlobalSettings.getSettingsShortLimitDays();
        document.properties["lecm-errands:is-limit-short"] = fromWFELimitDays <= shortLimitDays;
        document.properties["lecm-errands:limitation-date-radio"] = dateRadio;
    } else if (dateRadio == "LIMITLESS") {
        limitless = true;
        if (oldLimitDate && oldLimitRadio != "LIMITLESS") {
            document.properties["lecm-errands:limitation-date-radio"] = dateRadio;
            document.properties["lecm-errands:limitation-date"] = null;
            document.properties["lecm-errands:is-limit-short"] = false;
            document.properties["lecm-errands:half-limit-date"] = null;
            dueDateString = "Без срока";
        } else {
            changed = false;
        }
    }
    if (processChild) {
        var children = [];
        var childrenErrands = errands.getChildErrands(document.nodeRef.toString());
        var childrenResolutions = errands.getChildResolutions(document.nodeRef.toString());
        if (childrenErrands) {
            children = children.concat(childrenErrands);
        }
        if (childrenResolutions) {
            children = children.concat(childrenResolutions);
        }
        lecmPermission.pushAuthentication();
        lecmPermission.setRunAsUserSystem();
        children.forEach(function (child) {
            edsDocument.sendChangeDueDateSignal(child, shiftSize, limitless, newLimitationDate, changeDateReason);
        });
        lecmPermission.popAuthentication();
    }
    if (changed) {
        lecmPermission.pushAuthentication();
        lecmPermission.setRunAsUserSystem();
        document.properties["lecm-errands:limitation-date-text"] = dueDateString;
        document.save();
        if (isSignal) {
            edsDocument.resetChangeDueDateSignal(document);
        }
        lecmPermission.popAuthentication();
        var recipients = [];
        var executorAssoc = document.assocs["lecm-errands:executor-assoc"];
        var coexecutorsAssoc = document.assocs["lecm-errands:coexecutors-assoc"];
        var controllerAssoc = document.assocs["lecm-errands:controller-assoc"];
        if (executorAssoc && executorAssoc.length == 1) {
            recipients.push(executorAssoc[0]);
        }
        if (coexecutorsAssoc && coexecutorsAssoc.length) {
            for (i = 0; i < coexecutorsAssoc.length; i++) {
                recipients.push(coexecutorsAssoc[i]);
            }
        }
        if (controllerAssoc && controllerAssoc.length == 1) {
            recipients.push(controllerAssoc[0]);
        }
        notifications.sendNotificationFromCurrentUser({
            recipients: recipients,
            templateCode: 'ERRANDS_CHANGE_DUE_DATE',
            templateConfig: {
                mainObject: document,
                eventExecutor: currentUser,
                dueDate: dueDateString,
                reason: changeDateReason
            }
        });
        logObjects.push(dueDateString);
        logText = "#initiator ";
        logText += documentScript.wrapperTitle("изменил", changeDateReason);
        logText += " срок исполнения поручения #mainobject на  #object1";
        businessJournal.log(document.nodeRef.toString(), "EDS_CHANGE_DUE_DATE", logText, logObjects);
    }
}
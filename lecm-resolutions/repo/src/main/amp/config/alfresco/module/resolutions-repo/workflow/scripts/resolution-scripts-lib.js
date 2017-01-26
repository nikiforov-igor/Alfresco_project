var LECMResolutionActions = {
    processChangeDueDateSignal: function (document) {
        var limitless = document.properties["lecm-eds-aspect:duedate-limitless"];
        var shiftSize = document.properties["lecm-eds-aspect:duedate-shift-size"];
        var newDate = document.properties["lecm-eds-aspect:new-limitation-date"];
        var reason = document.properties["lecm-eds-aspect:change-duedate-reason"];

        var oldLimitRadio = document.properties["lecm-resolutions:limitation-date-radio"];

        if (limitless) {
            if (oldLimitRadio != "LIMITLESS") {
                document.properties["lecm-resolutions:limitation-date-radio"] = "LIMITLESS";
                document.properties["lecm-resolutions:limitation-date"] = null;
                document.save();
            } else {
                return;
            }
        } else {
            if (oldLimitRadio == "DATE") {
                var oldLimitDate = document.properties["lecm-resolutions:limitation-date"];
                newDate = new Date(oldLimitDate.getTime() + shiftSize);
                document.properties["lecm-resolutions:limitation-date"] = newDate;
            } else {
                document.properties["lecm-resolutions:limitation-date-radio"] = "DATE";
                document.properties["lecm-resolutions:limitation-date"] = newDate;
            }
            document.save();
        }

        var recipients = [];
        var authorAssoc = document.assocs["lecm-resolutions:author-assoc"];
        if (authorAssoc && authorAssoc.length) {
            recipients.push(authorAssoc[0]);
        }
        var creatorAssoc = document.assocs["lecm-document:author-assoc"];
        if (creatorAssoc && creatorAssoc.length) {
            recipients.push(creatorAssoc[0]);
        }

        var controllerAssoc = document.assocs["lecm-resolutions:controller-assoc"];
        if (controllerAssoc && controllerAssoc.length) {
            recipients.push(controllerAssoc[0]);
        }

        notifications.sendNotificationFromCurrentUser({
            recipients: recipients,
            templateCode: 'RESOLUTION_CHANGE_LIMITATION_DATE',
            templateConfig: {
                mainObject: document,
                reason: reason
            }
        });

        var logText = base.wrapperTitle("Изменен", reason) + " срок исполнения резолюции. Причина изменения: " + reason;
        businessJournal.log(document.nodeRef.toString(), "EDS_CHANGE_DUE_DATE", logText, []);

        var childrenErrands = errands.getChildErrands(document.nodeRef.toString());
        if (childrenErrands && childrenErrands.length) {
            childrenErrands.forEach(function (errand) {
                edsDocument.sendChangeDueDateSignal(errand, shiftSize, limitless, newDate, reason);
            });
        }

        edsDocument.resetChangeDueDateSignal(document);
    }
};
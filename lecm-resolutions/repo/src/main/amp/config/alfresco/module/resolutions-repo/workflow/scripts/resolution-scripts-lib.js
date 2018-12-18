var LECMResolutionActions = {
    processChangeDueDateSignal: function (document) {
        var limitless = document.properties["lecm-eds-aspect:duedate-limitless"];
        var shiftSize = document.properties["lecm-eds-aspect:duedate-shift-size"];
        var newDate = document.properties["lecm-eds-aspect:new-limitation-date"];
        var reason = document.properties["lecm-eds-aspect:change-duedate-reason"];
        var isExpired = !!document.properties["lecm-resolutions:is-expired"];

        var oldLimitRadio = document.properties["lecm-resolutions:limitation-date-radio"];
        lecmPermission.pushAuthentication();
        lecmPermission.setRunAsUserSystem();
        if (limitless) {
            if (oldLimitRadio != "LIMITLESS") {
                document.properties["lecm-resolutions:limitation-date-radio"] = "LIMITLESS";
                document.properties["lecm-resolutions:limitation-date"] = null;
                document.properties["lecm-errands:limitation-date-text"] = "Без срока";
                if (isExpired) {
                    document.properties["lecm-resolutions:is-expired"] = false;
                }
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

            var day = utils.pad(newDate.getDate(), 2);
            var month = utils.pad(newDate.getMonth() + 1, 2);
            var year = utils.pad(newDate.getFullYear(), 4);
            document.properties["lecm-errands:limitation-date-text"] = day + "." + month + "." + year;

            if (isExpired) {
                var now = new Date();
                now.setHours(0, 0, 0, 0);
                var dueDate = new Date(newDate);
                dueDate.setHours(0, 0, 0, 0);
                if (dueDate.getTime() > now.getTime()) {
                    document.properties["lecm-resolutions:is-expired"] = false;
                }
            }
            document.save();
        }
        lecmPermission.popAuthentication();

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

        var msg = Packages.ru.it.lecm.base.beans.BaseBean.getMessage;
        var changeWrap = base.wrapperTitle(msg("ru.it.lecm.resolutions.bjMessages.changeDueDate.changeParamText", "Изменен"), reason);
        var logText = msg("ru.it.lecm.resolutions.bjMessages.changeDueDate.message", "{change} срок исполнения резолюции. Причина изменения: {reason}");
        logText = logText.replace("{change}", changeWrap);
        logText = logText.replace("{reason}", reason)
        businessJournal.log(document.nodeRef.toString(), "EDS_CHANGE_DUE_DATE", logText, []);
        lecmPermission.pushAuthentication();
        lecmPermission.setRunAsUserSystem();
        var childrenErrands = errands.getChildErrands(document.nodeRef.toString());
        if (childrenErrands && childrenErrands.length) {
            childrenErrands.forEach(function (errand) {
                edsDocument.sendChangeDueDateSignal(errand, shiftSize, limitless, newDate, reason);
            });
        }

        edsDocument.resetChangeDueDateSignal(document);
        lecmPermission.popAuthentication();
    },

    processChangeChildSignal: function (document) {
        var msg = org.springframework.extensions.surf.util.I18NUtil.getMessage;
        if (document) {
            var childrenErrands = errands.getChildErrands(document.nodeRef.toString());
            var canceledErrandsCount = 0;
            var executedErrandsCount = 0;
            if (childrenErrands && childrenErrands.length) {
                childrenErrands.forEach(function (errand) {
                    if ("Исполнено" == errand.properties["lecm-statemachine:status"] || msg("lecm.errands.statemachine-status.executed") == errand.properties["lecm-statemachine:status"]) {
                        executedErrandsCount++;
                    } else if ("Отменено" == errand.properties["lecm-statemachine:status"] || msg("lecm.errands.statemachine-status.cancelled") == errand.properties["lecm-statemachine:status"]) {
                        canceledErrandsCount++;
                    }
                });
            }
            var reviewRecords = document.assocs["lecm-review-aspects:related-review-records-assoc"];
            var reviewRecordsCount = 0;
            var reviewedRecordsCount = 0;
            var canceledRecordsCount = 0;
            var notReviewRecordsCount = 0;
            if (reviewRecords && reviewRecords.length) {
                reviewRecordsCount = reviewRecords.length;
                reviewRecords.forEach(function (record) {
                    if ("REVIEWED" == record.properties["lecm-review-ts:review-state"]) {
                        reviewedRecordsCount++;
                    } else if ("CANCELLED" == record.properties["lecm-review-ts:review-state"]) {
                        canceledRecordsCount++;
                    } else if ("NOT_REVIEWED" == record.properties["lecm-review-ts:review-state"]) {
                        notReviewRecordsCount++;
                    }
                });
            }

            if (childrenErrands.length) {
                if (childrenErrands.length == (executedErrandsCount + canceledErrandsCount)) {
                    if (childrenErrands.length == executedErrandsCount && notReviewRecordsCount == 0) {
                        document.properties["lecm-resolutions:auto-complete"] = true;
                    } else if (childrenErrands.length == canceledErrandsCount) {
                        if (notReviewRecordsCount > 0) {
                            return;
                        } else {
                            document.properties["lecm-resolutions:annul-signal"] = true;
                        }
                    } else {
                        document.properties["lecm-resolutions:require-closers-decision"] = true;
                        notifications.sendNotificationFromCurrentUser({
                            recipients: resolutionsScript.getResolutionClosers(document),
                            templateCode: 'RESOLUTION_REQUIRES_SOLUTION_CLOSERS',
                            templateConfig: {
                                mainObject: document
                            },
                            dontCheckAccessToObject: true
                        });
                    }
                    document.save();
                }
            } else if (reviewRecordsCount) {
                if (reviewRecordsCount == reviewedRecordsCount) {
                    document.properties["lecm-resolutions:auto-complete"] = true;
                } if (reviewRecordsCount == canceledRecordsCount) {
                    document.properties["lecm-resolutions:annul-signal"] = true;
                }
                document.save();
            } else {
                document.properties["lecm-resolutions:auto-complete"] = true;
                document.save();
            }
        }
    }
};
var OrdScript = {

    changePointStatusByErrand: function(ord) {
        var senders = documentEvent.getEventSenders(ord);
        if (senders) {
            senders.forEach(function (sender) {
                var snederTypeShort = sender.typeShort;
                if (snederTypeShort == "lecm-errands:document") {
                    var status = sender.properties["lecm-statemachine:status"];
                    var point = ordStatemachine.getErrandLinkedPoint(sender);
                    if (point) {
                        var isExpired = sender.properties["lecm-errands:is-expired"];
                        var pointNumber, bjMessage, pointStatus, newPointStatus;
                        var pointStatusAssoc = point.assocs["lecm-ord-table-structure:item-status-assoc"];
                        if (pointStatusAssoc) {
                            pointStatus = pointStatusAssoc[0].properties["cm:name"];
                        }
                        if (msg.get("lecm.errands.statemachine-status.executed").equals(status)) {
                            ordStatemachine.changePointStatus(point, "EXECUTED_STATUS");
                            pointStatusAssoc = point.assocs["lecm-ord-table-structure:item-status-assoc"];
                            if (pointStatusAssoc) {
                                newPointStatus = pointStatusAssoc[0].properties["cm:name"];
                            }
                            if (pointStatus != newPointStatus) {
                                pointNumber = point.properties["lecm-document:indexTableRow"];
                                bjMessage = "Пункт номер " + pointNumber + " документа #mainobject перешел в статус " + newPointStatus;
                                businessJournal.log(ord.nodeRef.toString(), 'POINT_STATUS_CHANGE', bjMessage, [point]);
                            }
                        } else if (msg.get("lecm.errands.statemachine-status.not-executed").equals(status)){
                            ordStatemachine.changePointStatus(point, "NOT_EXECUTED_STATUS");
                            pointStatusAssoc = point.assocs["lecm-ord-table-structure:item-status-assoc"];
                            if (pointStatusAssoc) {
                                newPointStatus = pointStatusAssoc[0].properties["cm:name"];
                            }
                            if (pointStatus != newPointStatus) {
                                pointNumber = point.properties["lecm-document:indexTableRow"];
                                bjMessage = "Пункт номер " + pointNumber + " документа #mainobject перешел в статус" + newPointStatus;
                                businessJournal.log(ord.nodeRef.toString(), 'POINT_STATUS_CHANGE', bjMessage, [point]);
                            }
                        } else if (!msg.get("lecm.errands.statemachine-status.executed").equals(status) && isExpired) {
                            ordStatemachine.changePointStatus(point, "EXPIRED_STATUS");
                            pointStatusAssoc = point.assocs["lecm-ord-table-structure:item-status-assoc"];
                            if (pointStatusAssoc) {
                                newPointStatus = pointStatusAssoc[0].properties["cm:name"];
                            }
                            if (pointStatus != newPointStatus) {
                                pointNumber = point.properties["lecm-document:indexTableRow"];
                                bjMessage = "Исполнение пункта № " + pointNumber + "документа #mainobject просрочено";
                                businessJournal.log(ord.nodeRef.toString(), 'POINT_STATUS_CHANGE', bjMessage, [point]);
                            }
                        }
                    }
                }
                documentEvent.removeEventSender(protocol, sender);
            });
        }
    }


};
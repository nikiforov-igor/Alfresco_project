var ProtocolScript = {

    changePointStatusByErrand: function(protocol) {
        var senders = documentEvent.getEventSenders(protocol);
        if (senders) {
            senders.forEach(function (sender) {
                var snederTypeShort = sender.typeShort;
                if (snederTypeShort == "lecm-errands:document") {
                    var status = sender.properties["lecm-statemachine:status"];
                    var point = protocolService.getErrandLinkedPoint(sender);
                    if (point) {
                        var isExpired = sender.properties["lecm-errands:is-expired"];
                        var justInTime = sender.properties["lecm-errands:just-in-time"];
                        var msg = org.springframework.extensions.surf.util.I18NUtil.getMessage;
                        if (!protocolService.checkPointExecutedStatus(point) && "Исполнено" == status || (msg && status == msg("lecm.errands.statemachine-status.executed"))) {
                            protocolService.changePointStatus(protocol, point, "EXECUTED_STATUS");
                        } else if (!protocolService.checkPointExecutedStatus(point) && isExpired && justInTime){
                            protocolService.changePointStatus(protocol, point, "NOT_EXECUTED_STATUS");
                        } else if (!protocolService.checkPointExecutedStatus(point) && isExpired && !justInTime) {
                            protocolService.changePointStatus(protocol, point, "EXPIRED_STATUS");
                        }
                    }
                }
                try {
                    documentEvent.removeEventSender(protocol, sender);
                } catch (e) {
                    logger.error(e);
                }
            });
        }
    }


};
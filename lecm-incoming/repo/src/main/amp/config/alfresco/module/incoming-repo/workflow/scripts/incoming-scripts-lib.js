var LECMIncomingActions = {
    processExecutionSignals: function (document) {
        var reviewState = document.properties["lecm-review-ts:doc-review-state"];
        if (reviewState == "NOT_REQUIRED" || reviewState == "COMPLETE") {
            var allErrandsFinal = true;
            var childErrands = document.sourceAssocs["lecm-errands:additional-document-assoc"];
            if (childErrands) {
                allErrandsFinal = childErrands.every(function (errand) {
                    return statemachine.isFinal(errand.nodeRef.toString());
                });
            }

            if (allErrandsFinal) {
                var allResolutionsFinal = true;
                var childResolutions = document.sourceAssocs["lecm-resolutions:base-document-assoc"];
                if (childResolutions) {
                    allResolutionsFinal = childErrands.every(function (resolution) {
                        return statemachine.isFinal(resolution.nodeRef.toString());
                    });
                }

                if (allResolutionsFinal) {
                    var allExecutedErrands = true;
                    if (childErrands) {
                        allExecutedErrands = childErrands.every(function (errand) {
                            return errand.properties["lecm-statemachine:status"] == "Исполнено";
                        });
                    }
                    var allExecutedResolutions = true;
                    if (childResolutions) {
                        allExecutedResolutions = childResolutions.every(function (resolution) {
                            return resolution.properties["lecm-statemachine:status"] == "Завершено";
                        });
                    }

                    if (reviewState == "COMPLETE" && allExecutedErrands && allExecutedResolutions) {
                        lecmPermission.pushAuthentication();
                        lecmPermission.setRunAsUserSystem();
                        document.properties["lecm-incoming:auto-transition-to-execute"] = true;
                        document.save();
                        lecmPermission.popAuthentication();
                    } else {
                        var registrars = orgstructure.getEmployeesByBusinessRoleId("DA_REGISTRARS", true);
                        notifications.sendNotificationFromCurrentUser({
                            recipients: registrars,
                            templateCode: 'INCOMING_MAKE_DECISION',
                            templateConfig: {
                                mainObject: document
                            }
                        });
                    }
                }
            }
        }
    }
};
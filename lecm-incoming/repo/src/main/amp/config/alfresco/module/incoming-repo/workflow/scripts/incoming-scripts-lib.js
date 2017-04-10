var LECMIncomingActions = {
    processExecutionSignals: function (document) {
        var reviewState = document.properties["lecm-review-ts:doc-review-state"];
        if (reviewState == "NOT_REQUIRED" || reviewState == "COMPLETE") {
            var hasReview = false;
            var allReviewCancelled = true;
            var reviewTable = document.associations['lecm-review-ts:review-table-assoc'];
            if (reviewTable && reviewTable.length) {
                var reviewRecords = documentTables.getTableDataRows(reviewTable[0].nodeRef.toString());
                hasReview = reviewRecords && reviewRecords.length;
                if (hasReview) {
                    allReviewCancelled = reviewRecords.every(function (record) {
                        return record.properties["lecm-review-ts:review-state"] == "CANCELLED";
                    });
                }
            }

            if (allReviewCancelled && hasReview) {
                var allErrandsFinalOrDraft = true;
                var childErrands = document.sourceAssocs["lecm-errands:additional-document-assoc"];
                if (childErrands) {
                    allErrandsFinalOrDraft = childErrands.every(function (errand) {
                        return (statemachine.isFinal(errand.nodeRef.toString()) || statemachine.isDraft(errand));
                    });
                }

                if (allErrandsFinalOrDraft) {
                    var allResolutionsFinalOrDraft = true;
                    var oneOrMoreResolutionsOnExecution = false;
                    var childResolutions = document.sourceAssocs["lecm-resolutions:base-document-assoc"];
                    if (childResolutions) {
                        allResolutionsFinalOrDraft = childResolutions.every(function (resolution) {
                            return (statemachine.isFinal(resolution.nodeRef.toString()) || statemachine.isDraft(resolution));
                        });
                    }

                    if (!allResolutionsFinalOrDraft) {
                        oneOrMoreResolutionsOnExecution = childResolutions.every(function (resolution) {
                            return resolution.properties["lecm-statemachine:status"] == "На исполнении";
                        });
                    }

                    if (allResolutionsFinalOrDraft || !oneOrMoreResolutionsOnExecution) {
                        var hasErrands = childErrands && childErrands.length;
                        var oneOrMoreExecutedErrands = false;
                        if (hasErrands) {
                            for (var i = 0; i < childErrands.length; i++) {
                                var errand = childErrands[i];
                                if (errand.properties["lecm-statemachine:status"] == "Исполнено") {
                                    oneOrMoreExecutedErrands = true;
                                    break;
                                }
                            }
                        }

                        lecmPermission.pushAuthentication();
                        lecmPermission.setRunAsUserSystem();

                        if (oneOrMoreExecutedErrands) {
                            //Переход в статус "Исполнен"
                            document.properties["lecm-incoming:auto-transition-to-execute"] = true;
                        } else {
                            //Переход в статус "Зарегистрирован"
                            document.properties["lecm-incoming:auto-transition-to-registered"] = true;
                        }

                        document.save();
                        lecmPermission.popAuthentication();
                    }
                }
            } else {
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
                        allResolutionsFinal = childResolutions.every(function (resolution) {
                            return statemachine.isFinal(resolution.nodeRef.toString());
                        });
                    }

                    if (allResolutionsFinal) {
                        var hasErrands = childErrands && childErrands.length;
                        var allExecutedErrands = true;
                        if (hasErrands) {
                            allExecutedErrands = childErrands.every(function (errand) {
                                return errand.properties["lecm-statemachine:status"] == "Исполнено";
                            });
                        }
                        var hasResolutions = childResolutions && childResolutions.length;
                        var allExecutedResolutions = true;
                        if (hasResolutions) {
                            allExecutedResolutions = childResolutions.every(function (resolution) {
                                return resolution.properties["lecm-statemachine:status"] == "Завершено";
                            });
                        }

                        var allReviewReviewed = true;
                        if (hasReview) {
                            allReviewReviewed = reviewRecords.every(function (record) {
                                return record.properties["lecm-review-ts:review-state"] == "REVIEWED";
                            });
                        }

                        if (allReviewReviewed && allExecutedErrands && allExecutedResolutions
                            && (hasErrands || hasResolutions || hasReview)) {
                            lecmPermission.pushAuthentication();
                            lecmPermission.setRunAsUserSystem();
                            document.properties["lecm-incoming:auto-transition-to-execute"] = true;
                            document.save();
                            lecmPermission.popAuthentication();
                        } else {
                            var registrars = orgstructure.getEmployeesByBusinessRoleId("DA_REGISTRARS", true);
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
    }
};
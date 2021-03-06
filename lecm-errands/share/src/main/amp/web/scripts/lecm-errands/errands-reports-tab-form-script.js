(function () {
    LogicECM.module.Base.Util.loadCSS([
        'css/lecm-errands/errands-reports-tab-form.css'
    ]);

    var Dom = YAHOO.util.Dom,
        Selector = YAHOO.util.Selector,
        Bubbling = YAHOO.Bubbling;

    Bubbling.on("errandsReportsTabViewFormScriptLoaded", init);
    Bubbling.on("errandsReportsTabEditFormScriptLoaded", init);

    function init(layer, args) {
        if (args.formId) {
            var formId = args[1].formId;
            var realFormElId = formId;
            var documentNodeRef = args[1].nodeRef;
            if (layer == "errandsReportsTabViewFormScriptLoaded") {
                realFormElId = formId + "_metadata";
            } else {
                realFormElId = formId;
            }
            if (documentNodeRef) {
                Alfresco.util.Ajax.jsonRequest(
                    {
                        method: Alfresco.util.Ajax.GET,
                        url: Alfresco.constants.PROXY_URI + "lecm/errands/api/getCurrentEmployeeRoles?errandNodeRef=" + encodeURIComponent(documentNodeRef),
                        successCallback: {
                            fn: function (response) {
                                var roles = response.json;
                                if (roles) {
                                    var isExecutor = roles.isExecutor;
                                    Alfresco.util.Ajax.jsonPost({
                                        url: Alfresco.constants.PROXY_URI + "lecm/substitude/format/node",
                                        dataObj: {
                                            nodeRef: documentNodeRef,
                                            substituteString: "{lecm-errands:execution-report-status},{lecm-errands-ts:execution-reports-assoc/cm:contains/lecm-document:indexTableRow}"
                                        },
                                        successCallback: {
                                            fn: function (response) {
                                                if (response && response.json.formatString) {
                                                    var resp = response.json.formatString.split(",");
                                                    processForm(realFormElId, isExecutor, resp[0], resp[1]);
                                                }
                                            },
                                            scope: this
                                        },
                                        failureMessage: Alfresco.util.message("message.details.failure"),
                                        scope: this
                                    });

                                }
                            },
                            scope: this
                        },
                        failureMessage: Alfresco.util.message("message.details.failure"),
                        scope: this
                    });
            }
        }
    }

    function processForm(realFormElId, isExecutor, reportStatus, declinedReportsCount) {
        var form = Dom.get(realFormElId);
        var executionReportsSet = Selector.query(".errands-execution-report-set", form, true);
        var reportFieldsDiv = Selector.query(".errands-execution-report-fields", executionReportsSet, true);
        var reportsTableDiv = Selector.query(".form-field.with-grid.execution-reports", executionReportsSet, true);
        var noItemsBlock = Selector.query(".errands-execution-report-empty", form, true);
        var reportInProcessBlock = Selector.query(".errands-hidden-execution-report-processing", form, true);
        var reportStatusBlock = Selector.query(".errands-execution-report-status", executionReportsSet, true);
        var reportDeclineReasonEl = reportFieldsDiv.children[0];

        if (!reportStatus) {
            Dom.addClass(executionReportsSet, 'hidden1');
            Dom.removeClass(noItemsBlock, 'hidden1');
        } else {
            if (!declinedReportsCount || ((reportStatus == "????????????????" || reportStatus == Alfresco.util.message("lecm.errands.report-status.decline")) && declinedReportsCount <= 1)) {
                Dom.addClass(reportsTableDiv, 'hidden1');
            }
            if (reportStatus == "????????????????" || reportStatus == Alfresco.util.message("lecm.errands.report-status.decline")) {
                Dom.addClass(reportStatusBlock, "decline");
            } else {
                Dom.addClass(reportDeclineReasonEl, "hidden1");
            }
            if (!isExecutor && (reportStatus == "????????????" || reportStatus == Alfresco.util.message("lecm.errands.report-status.project"))) {
                Dom.addClass(reportFieldsDiv, 'hidden1');
                Dom.addClass(reportStatusBlock, 'hidden1');
                executionReportsSet.insertBefore(reportInProcessBlock,executionReportsSet.firstElementChild);
                Dom.removeClass(reportInProcessBlock, 'hidden1');
            }
        }
    }
})();
<#assign htmlId = fieldHtmlId/>
<#assign formId = args.htmlid/>
<script type="text/javascript">
(function() {
    function init() {
        LogicECM.module.Base.Util.loadResources([
                    'scripts/lecm-errands/controls/errands-coexecutor-report-expand-actions-control.js'
                ],
                [
                    'css/lecm-errands/errands-coexecutor-report-expand.css'
                ], loadData);
    }

    function loadData() {
        Alfresco.util.Ajax.jsonGet({
            url: Alfresco.constants.PROXY_URI + "lecm/api/metadata",
            dataObj: {
                nodeRef: "${form.arguments.itemId}"
            },
            successCallback: {
                fn: function (response) {
                    var data = response.json;
                    if (data) {
                        createControl(data);
                    }
                }
            },
            failureMessage: Alfresco.util.message("message.details.failure"),
            scope: this
        });
    }

    function createControl(data) {
        var actions = new LogicECM.module.Errands.CoexecutorReportExpandActions("${htmlId}");

        actions.setOptions({
            fieldId: "${field.configName}",
            formId: "${formId}",
            expandedReport: {
                nodeRef: "${form.arguments.itemId}",
                coexecutor: data.properties["{http://www.it.ru/logicECM/errands/table-structure/1.0}coexecutor-assoc-ref"],
                status: data.properties["{http://www.it.ru/logicECM/errands/table-structure/1.0}coexecutor-report-status"],
                permissions: data.permissions.user,
                type: "lecm-errands-ts:coexecutor-report"
            }
        }).setMessages( ${messages});

        actions.onReady();
    }

    YAHOO.util.Event.onDOMReady(init);
})();
</script>

<div id="${formId}-coexecutor-report-expand-actions" class="coexecutor-report-expand-actions"></div>

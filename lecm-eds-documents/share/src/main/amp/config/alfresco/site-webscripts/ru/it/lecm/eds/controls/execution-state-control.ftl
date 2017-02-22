<#assign params = field.control.params>
<#assign formId=args.htmlid?js_string?html/>
<#assign fieldId=field.id!"">
<#assign nodeRef=form.arguments.itemId/>

<#assign expandable = true>
<#if params.expandable?? && params.expandable == "false">
    <#assign expandable = false>
</#if>
<#assign showEmptyStatuses = false>
<#if params.showEmptyStatuses?? && params.showEmptyStatuses == "true">
    <#assign showEmptyStatuses = true>
</#if>

<script>
    function init() {
        LogicECM.module.Base.Util.loadResources([
                    'scripts/lecm-eds-documents/execution-state-control.js'
                ], [
                    'css/lecm-eds-documents/execution-state-control.css'
                ],
                createControl
        );
    }

    function createControl() {
        var control = new LogicECM.module.ExecutionStateControl("${fieldHtmlId}");
        control.setOptions({
            fieldId: "${field.configName}",
            formId: "${args.htmlid}",
            value: "${field.value}",
            documentNodeRef: "${nodeRef}",
            expandable: ${expandable?string},
            showEmptyStatuses: ${showEmptyStatuses?string}
        <#if field.control.params.statusesOrder??>,
            statusesOrder: "${field.control.params.statusesOrder}".split(",")
        </#if>
        <#if field.control.params.statisticsField??>,
            statisticsField: "${field.control.params.statisticsField}"
        </#if>
        });
    }
    YAHOO.util.Event.onDOMReady(init);
</script>

<div id="${fieldHtmlId}-cntrl" class="execution-state control">
    <div class="label-div">
        <label>${field.label?html}:</label>
    </div>
    <div class="container">
        <div class="value-div">
            <#if field.value?string != "">
                <#if expandable == true>
                    <span class="execution-statistics-icon collapsed">
                        <a href="javascript:void(0)" id="${fieldHtmlId}-displayValue" class="execution-state-value"></a>
                    </span>
                    <div class="execution-statistics hidden1" id="${fieldHtmlId}-statistics">
                    </div>
                <#else>
                    <span id="${fieldHtmlId}-displayValue" class="execution-state-value"></span>
                </#if>
                <input type="hidden" name="${field.name}" id="${fieldHtmlId}" value="${field.value?html}"/>
            <#else>
                <span>${msg("form.control.novalue")}</span>
            </#if>
        </div>
    </div>
</div>
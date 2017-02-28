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
                    'scripts/lecm-documents/state-statistics-control.js'
                ], [
                    'css/lecm-documents/state-statistics-control.css'
                ],
                createControl
        );
    }

    function createControl() {
        var control = new LogicECM.module.StateStatisticsControl("${fieldHtmlId}");
        control.setOptions({
            fieldId: "${field.configName}",
            formId: "${args.htmlid}",
            value: "${field.value}",
            documentNodeRef: "${nodeRef}",
            expandable: ${expandable?string},
            showEmptyStatuses: ${showEmptyStatuses?string}
        <#if field.control.params.statusesWithOrder??>,
            statusesWithOrder: "${field.control.params.statusesWithOrder}".split(",")
        </#if>
        <#if field.control.params.statisticsField??>,
            statisticsField: "${field.control.params.statisticsField}"
        </#if>
        });
    }
    YAHOO.util.Event.onDOMReady(init);
</script>

<div id="${fieldHtmlId}-cntrl" class="state-statistics control viewmode">
    <div class="label-div">
        <label>${field.label?html}:</label>
    </div>
    <div class="container">
        <div class="value-div">
        <#if field.value?string != "">
            <#if expandable>
                <span class="statistics-icon collapsed">
                        <a href="javascript:void(0)" id="${fieldHtmlId}-displayValue"
                           class="state-statistics-value"></a>
                    </span>
                <div class="statistics hidden1" id="${fieldHtmlId}-statistics"></div>
            <#else>
                <span id="${fieldHtmlId}-displayValue" class="state-statistics-value"></span>
            </#if>
            <input type="hidden" name="${field.name}" id="${fieldHtmlId}" value="${field.value?html}"/>
        <#else>
            <span>${msg("form.control.novalue")}</span>
        </#if>
        </div>
    </div>
</div>
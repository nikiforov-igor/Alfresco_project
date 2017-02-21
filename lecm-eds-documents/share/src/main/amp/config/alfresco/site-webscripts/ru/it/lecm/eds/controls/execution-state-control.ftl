<#assign params = field.control.params>
<#assign formId=args.htmlid?js_string?html/>
<#assign fieldId=field.id!"">
<#assign nodeRef=form.arguments.itemId/>
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
            documentNodeRef: "${nodeRef}"
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
                <span class="execution-statistics-icon collapsed">
                    <a href="javascript:void(0)" id="${fieldHtmlId}-displayValue" class="execution-state-value"></a>
                </span>
                <div class="execution-statistics hidden1" id="${fieldHtmlId}-statistics">
                </div>
                <input type="hidden" name="${field.name}" id="${fieldHtmlId}" value="${field.value?html}"/>
            <#else>
                <span>${msg("form.control.novalue")}</span>
            </#if>
        </div>
    </div>
</div>
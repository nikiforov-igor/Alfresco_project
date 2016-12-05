<#assign formId=args.htmlid?js_string + "-form">
<#assign params = field.control.params>

<#assign readonly = false>
<#assign defaultValue = "">
<#if form.arguments[field.name]?has_content>
    <#assign defaultValue = form.arguments[field.name]>
<#elseif form.arguments['readonly_' + field.name]?has_content>
	<#assign defaultValue=form.arguments['readonly_' + field.name]>
	<#assign readonly = true>
</#if>

<#assign value = field.value>
<#if value == "" && defaultValue != "">
    <#assign value = defaultValue>
</#if>

<#assign disabled = form.mode == "view" || (field.disabled && !(params.forceEditable?? && params.forceEditable == "true"))>

<script type="text/javascript">
    (function () {

        function init() {
            LogicECM.module.Base.Util.loadResources([
                'scripts/lecm-base/components/lecm-reiteration-control-ext.js'
            ],
            [
                'css/lecm-base/components/reiteration-control-ext.css'
            ], createControl);
        }

        function createControl() {
            var reiteration = new LogicECM.module.Base.ReiterationExt("${fieldHtmlId}");
	        reiteration.setOptions({
                fieldId: "${field.configName}",
                formId: "${args.htmlid}"
            });
            reiteration.setMessages(
                ${messages}
            );
		<#if readonly>
			LogicECM.module.Base.Util.readonlyControl('${args.htmlid}', '${field.configName}', true);
		</#if>
        }

        YAHOO.util.Event.onDOMReady(init);

    })();
</script>

<div id="${fieldHtmlId}-parent" class="control reiteration editmode">
    <div class="label-div">
        <label for="${fieldHtmlId}-displayValue">
        ${field.label?html}:
        <#if field.mandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if>
        </label>
    </div>
    <#if disabled >
    <div class="container">
        <div class="value-div">
            <span id="${fieldHtmlId}-displayValue" class="mandatory-highlightable"></span>
        </div>
    </div>
    <#else>
    <div class="container">
        <div class="value-div">
            <span class="mandatory-highlightable"><a id="${fieldHtmlId}-displayValue" href="#"></a></span>
        </div>
    </div>
    </#if>
    <input id="${fieldHtmlId}" type="hidden" name="${field.name}" value="${value?html}" <#if disabled >disabled="true"</#if>/>
</div>
<div class="clear"></div>

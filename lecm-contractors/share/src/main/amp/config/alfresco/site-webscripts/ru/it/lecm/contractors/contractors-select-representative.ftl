<#assign controlId = fieldHtmlId + "-cntrl">
<#assign selectId = fieldHtmlId + "-slct">
<#assign inputId = fieldHtmlId + "-inpt">
<#assign fieldRepresentativesId = fieldHtmlId + "-fld-rep">
<#if form.mode == "view">
    <#assign fieldRepresentativesId = fieldRepresentativesId + "-view-repsesentative">
</#if>

<#assign emptyMessageId = field.control.params.emptyMessageId ! "">
<#assign emptyMessage = (field.control.params.emptyMessage) ! msg(emptyMessageId)>
<#if emptyMessage?length == 0>
    <#assign emptyMessage = msg("label.no_addressee")>
</#if>

<#assign disabled = form.mode == "view" || (field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true"))>

<#assign defaultValue=field.control.params.defaultValue!"">
<#if form.arguments[field.name]?has_content>
    <#assign defaultValue=form.arguments[field.name]>
</#if>

<#assign fieldValue=field.value!"">
<#if fieldValue?string == "" && field.control.params.defaultValueContextProperty??>
    <#if context.properties[field.control.params.defaultValueContextProperty]??>
        <#assign fieldValue = context.properties[field.control.params.defaultValueContextProperty]>
    <#elseif args[field.control.params.defaultValueContextProperty]??>
        <#assign fieldValue = args[field.control.params.defaultValueContextProperty]>
    </#if>
</#if>

<#if disabled>
<div id="${fieldRepresentativesId}" class="control contractors-select-representative viewmode">
    <div class="label-div">
        <#if showViewIncompleteWarning && field.mandatory && !(fieldValue?is_number) && fieldValue?string == "">
        <span class="incomplete-warning"><img src="${url.context}/res/components/form/images/warning-16.png"
                                              title="${msg("form.field.incomplete")}"/><span>
        </#if>
        <label>${field.label?html}:</label>
    </div>
    <div class="container">
        <div class="value-div">
            <input type="hidden" id="${controlId}" name="${field.name}" value="${field.value?html}"/>
            <span id="${controlId}-currentValueDisplay" class="mandatory-highlightable"></span>
        </div>
    </div>
</div>
<#else>
<div id="${fieldRepresentativesId}" class="control contractors-select-representative editmode">
    <div class="label-div">
        <label for="${selectId}">
        ${field.label?html}:
            <#if field.mandatory>
                <span class="mandatory-indicator">${msg("form.required.fields.marker")}</span>
            </#if>
        </label>
        <input type="hidden" id="${controlId}-removed" name="${field.name}_removed"/>
        <input type="hidden" id="${controlId}-added" name="${field.name}_added"/>
    </div>
    <div class="container">
        <div class="buttons-div">
            <span class="create-new-button">
                <input type="button" id="${controlId}-add-new-representative-button"/>
            </span>
        </div>
        <div class="value-div">
            <input type="hidden" id="${controlId}" name="${field.name}" value="${field.value?html}"/>
            <select id="${selectId}" name="${field.name}" class="mandatory-highlightable"></select>
        </div>
    </div>
</div>
</#if>
<div class="clear"></div>

<script type="text/javascript">//<![CDATA[
(function () {
    LogicECM.CurrentModules = LogicECM.CurrentModules || {};

    function init() {
        LogicECM.module.Base.Util.loadScripts([
                    'scripts/contractors/select-representative-control.js'
                ],
                createControls,
                ['container', 'datasource']);
    }

    function createControls() {
        new LogicECM.module.SelectRepresentativeForContractor("${controlId}",
                "${field.control.params.updateOnAction!"contractor.selected"}").setOptions({
                    representativeSelectId: "${selectId}",
                <#if defaultValue?has_content>
                    defaultValue: "${defaultValue?string}",
                </#if>
                    emptyMessage: '${emptyMessage}',
                    disabled: ${disabled?string},
                <#if field.control.params.createNewMessage??>
                    createNewMessage: "${field.control.params.createNewMessage}"
                <#elseif field.control.params.createNewMessageId??>
                    createNewMessage: "${msg(field.control.params.createNewMessageId)}"
                </#if>
                });

        <#if disabled>
            // FUTURE: PLAY HARD GO PRO!!
            YAHOO.util.Event.onAvailable("${controlId}", function () {
                var currentInputEl = YAHOO.util.Dom.get("${controlId}");

                if (currentInputEl !== null && currentInputEl.value.length > 0) {
                    Alfresco.util.Ajax.jsonGet({
                        url: Alfresco.constants.PROXY_URI + "slingshot/doclib2/node/" + currentInputEl.value.replace("://", "/"),
                        successCallback: {
                            fn: function (response) {
                                var currentDisplayValueElement = YAHOO.util.Dom.get("${controlId}-currentValueDisplay"),
                                        properties = response.json.item.node.properties,
                                        name = "{lecm-representative:surname} {lecm-representative:firstname}",

                                        propSubstName,
                                        prop;

                                for (prop in properties) {
                                    propSubstName = "{" + prop + "}";

                                    if (name.indexOf(propSubstName) != -1) {
                                        name = name.replace(propSubstName, properties[ prop ]);
                                    }
                                }

                                currentDisplayValueElement.innerHTML = name;
                            },
                            scope: this
                        }
                    });
                }
            }, this, true);
        </#if>
    }

    YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>
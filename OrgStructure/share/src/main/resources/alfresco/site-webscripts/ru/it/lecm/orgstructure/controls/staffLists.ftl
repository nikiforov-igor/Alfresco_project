<#assign controlId = fieldHtmlId + "-cntrl">
<script type="text/javascript" src="${url.context}/res/modules/simple-dialog.js"></script>
<script type="text/javascript">//<![CDATA[
(function () {
    function init() {
        var unitCompositionTable = new LogicECM.module.OrgStructure.UnitCompositionCtrl("${controlId}","${fieldHtmlId}");
        unitCompositionTable.setOptions(
        {
            currentValue: "${field.value}",
            <#if field.mandatory??>
                mandatory: ${field.mandatory?string}
            <#elseif field.endpointMandatory??>
                mandatory: ${field.endpointMandatory?string}
            </#if>
        });
        unitCompositionTable.setMessages(${messages});
        unitCompositionTable.init("${form.arguments.itemId}");
    }

    //YAHOO.util.Event.onDOMReady(init);
    function ElementAvaiable(id) {
        YAHOO.util.Event.onContentReady(id, this.handleOnAvailable, this);
    }

    ElementAvaiable.prototype.handleOnAvailable = function (me) {
        init();
    };

    var obj = new ElementAvaiable("${controlId}");
})();
//]]></script>

<div class="form-field">
<#escape x as x?js_string>
    <div id="${controlId}-main">
        <label for="${controlId}-main">${field.label?html}:<#if field.endpointMandatory!false || field.mandatory!false>
            <span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if>
        </label>
        <div id="${controlId}" class="yui-skin-sam">
        </div>
        <div id="${controlId}-currentValueDisplay" class="current-values"></div>

        <#if field.disabled == false>
            <input type="hidden" id="${fieldHtmlId}" name="-" value="${field.value?html}" />
            <input type="hidden" id="${controlId}-added" name="${field.name}_added" />
            <input type="hidden" id="${controlId}-removed" name="${field.name}_removed" />
            <div id="${controlId}-itemGroupActions" class="show-picker"></div>

            <#--<@renderPickerHTML controlId />-->
        </#if>
        <br/>
        <div class="new-row">
            <span id="${controlId}-newCompositionBtn" class="yui-button yui-push-button">
               <span class="first-child">
                  <button type="button">${msg('button.new-composition')}</button>
               </span>
            </span>
        </div>
    </div>
</#escape>
</div>

<#include "/org/alfresco/components/form/controls/common/utils.inc.ftl" />
<#assign htmlid=args.htmlid?html>
<#assign fieldValue=field.value!"">

<#if fieldValue?string == "" && field.control.params.defaultValueContextProperty??>
	<#if context.properties[field.control.params.defaultValueContextProperty]??>
		<#assign fieldValue = context.properties[field.control.params.defaultValueContextProperty]>
	<#elseif args[field.control.params.defaultValueContextProperty]??>
		<#assign fieldValue = args[field.control.params.defaultValueContextProperty]>
	</#if>
</#if>

<#if field.control.params.showCreateNewButton?? && field.control.params.showCreateNewButton == "false">
	<#assign showCreateNewButton = false>
<#else>
	<#assign showCreateNewButton = true>
</#if>

<script type="text/javascript">//<![CDATA[
(function()
{
    YAHOO.util.Event.onDOMReady(function (){
        LogicECM.module.AssociationSelectOne.prototype.onSelectChange = function AssociationTreeViewer_onSelectChange() {
            Dom.get(this.controlId).value = this.selectItem.value;
            this.options.selectedValueNodeRef = this.selectItem.value;
	        if (this.options.selectedValueNodeRef != "") {
	            Alfresco.util.Ajax.jsonGet(
	                    {
	                        url: Alfresco.constants.PROXY_URI + "lecm/subscriptions/event-category?nodeRef=" + this.options.selectedValueNodeRef,
	                        successCallback:
	                        {
	                            fn: function (response) {
	                                var elements = response.json;

                                    // Получаем элемент
                                    var selected = Dom.get(this.options.htmlId+"_assoc_"+this.options.assocName.replace
		                                    (":","_") + "-added");
		                            //Очищаем элементы списка
		                            selected.options.length = 0;
		                            if (elements.length > 0) {
		                                for (var i = 0; i < elements.length; i++) {
			                                var prop = elements[i];
			                                selected.options[i] = new Option(prop.name, prop.nodeRef, false, (prop.nodeRef == this.options.selectedValueNodeRef));
		                                }
                                    } else {
                                        selected.options[0] = new Option("Empty", "", false, true);
                                    }
	                            },
	                            scope: this
	                        },
	                        failureCallback:
	                        {
	                            fn: function (response) {
	                                //todo show error message
	                            },
	                            scope: this
	                        }
	                    });
	        } else {
//                var id = this.options.htmlId +"_assoc_" + this.options.assocName.replace(":","_") + "-added";
//                YAHOO.util.Event.onContentReady(id,
//                        function () {
//                            var selected = Dom.get(id);
//                            selected.options.length = 0;
//                            selected.options[0] = new Option("Empty", "", false, true);
//                        });

	        }

        };

        var control = new LogicECM.module.AssociationSelectOne("${fieldHtmlId}").setMessages(${messages});
        control.setOptions(
                {
			    <#if field.control.params.parentNodeRef??>
                    parentNodeRef: "${field.control.params.parentNodeRef}",
			    </#if>
			    <#if field.control.params.startLocation??>
                    startLocation: "${field.control.params.startLocation}",
			    </#if>
			    <#if field.mandatory??>
                    mandatory: ${field.mandatory?string},
			    <#elseif field.endpointMandatory??>
                    mandatory: ${field.endpointMandatory?string},
			    </#if>
                    itemType: "${field.endpointType}",
                    itemFamily: "node",
                    maxSearchResults: ${field.control.params.maxSearchResults!'1000'},
                    selectedValueNodeRef: "${fieldValue}",
                    nameSubstituteString: "${field.control.params.nameSubstituteString!'{cm:name}'}",
                    showCreateNewButton: ${showCreateNewButton?string},
	                htmlId: "${htmlid}",
	                assocName: "lecm-subscr:event-category-assoc"
                });
    });
})();
//]]></script>

<div class="form-field">
<#if form.mode == "view">
    <div class="viewmode-field">
		<#if field.mandatory && !(fieldValue?is_number) && fieldValue?string == "">
        <span class="incomplete-warning"><img src="${url.context}/res/components/form/images/warning-16.png" title="${msg("form.field.incomplete")}" /><span>
		</#if>
        <span class="viewmode-label">${field.label?html}:</span>
        <span id="${fieldHtmlId}-currentValueDisplay" class="viewmode-value"></span>
    </div>
<#else>
    <label for="${fieldHtmlId}-added">${field.label?html}:<#if field.mandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
    <input type="hidden" id="${fieldHtmlId}-removed" name="${field.name}_removed" value="${fieldValue}"/>
    <div id="${fieldHtmlId}-controls" class="selectone-control">
        <select id="${fieldHtmlId}-added" name="${field.name}_added" tabindex="0"
		        <#if field.description??>title="${field.description}"</#if>
		        <#if field.control.params.size??>size="${field.control.params.size}"</#if>
		        <#if field.control.params.styleClass??>class="${field.control.params.styleClass}"</#if>
		        <#if field.control.params.style??>style="${field.control.params.style}"</#if>
		        <#if field.disabled  && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true")>disabled="true"</#if>>
			<#if field.control.params.notSelectedOptionShow?? && field.control.params.notSelectedOptionShow == "true">
                <option value="">
					<#if field.control.params.notSelectedOptionLabel??>
                            ${field.control.params.notSelectedOptionLabel}
                        <#elseif field.control.params.notSelectedOptionLabelCode??>
					${msg(field.control.params.notSelectedOptionLabelCode)}
					</#if>
                </option>
			</#if>
        </select>
		<#if showCreateNewButton>
            <div class="show-picker">
                <span class="create-new-button">
                    <input type="button" id="${fieldHtmlId}-selectone-create-new-button" name="-"/>
                </span>
            </div>
		</#if>
    </div>

</#if>
    <input type="hidden" id="${fieldHtmlId}" name="-" value="${field.value?html}" />
</div>
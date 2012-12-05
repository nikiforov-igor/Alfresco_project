<#assign  id = args.htmlid/>
<#assign isTrue=false>
<#if field.value??>
	<#if field.value?is_boolean>
		<#assign isTrue=field.value>
	<#elseif field.value?is_string && field.value == "true">
		<#assign isTrue=true>
	</#if>
</#if>

<div class="form-field">
<#if form.mode == "view">
	<div class="viewmode-field">
		<span class="viewmode-label">${field.label?html}:</span>
		<span class="viewmode-value"><#if isTrue>${msg("form.control.checkbox.yes")}<#else>${msg("form.control.checkbox.no")}</#if></span>
	</div>
<#else>
	<input id="${fieldHtmlId}" type="hidden" name="${field.name}" value="<#if isTrue>true<#else>false</#if>" />
	<input class="formsCheckBox" id="${fieldHtmlId}-entry" type="checkbox" tabindex="0" name="-" <#if field.description??>title="${field.description}"</#if>
		<#if isTrue> value="true" checked="checked"</#if>
		   <#if field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true")>disabled="true"</#if>
		   <#if field.control.params.styleClass??>class="${field.control.params.styleClass}"</#if>
		   <#if field.control.params.style??>style="${field.control.params.style}"</#if>
		   onchange='javascript:YAHOO.util.Dom.get("${fieldHtmlId}").value=YAHOO.util.Dom.get("${fieldHtmlId}-entry").checked;' />
	<label for="${fieldHtmlId}-entry" class="checkbox">${field.label?html}</label>
	<@formLib.renderFieldHelp field=field />
</#if>
</div>

<script type="text/javascript">//<![CDATA[
var Dom = YAHOO.util.Dom,
	Connect = YAHOO.util.Connect,
	Event = YAHOO.util.Event;
var bossRef;


function checkboxInit() {
	bossRef = Dom.get("toolbar-createRow-form").children[0].value;
	var  sUrl = Alfresco.constants.PROXY_URI + "/lecm/orgstructure/boss?nodeRef="+bossRef;
	var callback = {
		success:function (oResponse) {
			var oResults = eval("(" + oResponse.responseText + ")");
			if (oResults.bossExists != undefined) {
				// скрываем checkbox
				Dom.get("${fieldHtmlId}-entry").setAttribute('disabled', true);
			}
		},
		failure:function (oResponse) {
			alert("Не удалось загрузить данные о руководящей должности. Попробуйте обновить страницу.");
		},
		argument:{
		}
	};
	YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
}

function BossElemenAvailable(id) {
	YAHOO.util.Event.onContentReady(id, this.handleOnAvailable, this);
}

BossElemenAvailable.prototype.handleOnAvailable = function (me) {
	checkboxInit();
}

var obj = new BossElemenAvailable("toolbar-createRow-form");


//]]></script>

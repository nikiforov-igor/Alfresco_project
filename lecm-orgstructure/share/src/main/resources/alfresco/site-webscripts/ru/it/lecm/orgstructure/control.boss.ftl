<#include "/org/alfresco/components/form/controls/checkbox.ftl" />

<script type="text/javascript">//<![CDATA[
var Dom = YAHOO.util.Dom,
		Connect = YAHOO.util.Connect,
		Event = YAHOO.util.Event;
var bossRef;


function checkboxInit() {
	bossRef = Dom.get("toolbar-createRow-form").children[0].value;
	var sUrl = Alfresco.constants.PROXY_URI + "/lecm/orgstructure/boss?nodeRef=" + bossRef;
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
};

var obj = new BossElemenAvailable("toolbar-createRow-form");


//]]></script>

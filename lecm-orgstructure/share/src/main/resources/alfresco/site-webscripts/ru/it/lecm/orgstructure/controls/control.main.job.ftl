<#include "/org/alfresco/components/form/controls/checkbox.ftl" />
<#assign  id = args.htmlid/>

<script type="text/javascript">//<![CDATA[
(function(){
	var Dom = YAHOO.util.Dom,
			Connect = YAHOO.util.Connect,
			Event = YAHOO.util.Event;

	CheckboxMainJob = function()
	{
		YAHOO.Bubbling.on("checkboxMainJob", function(lyaer,args){
			var  sUrl = Alfresco.constants.PROXY_URI + "/lecm/orgstructure/mainjob?nodeRef="+args[1].nodeRef;
			var callback = {
				success:function (oResponse) {
					var oResults = eval("(" + oResponse.responseText + ")");
					if (oResults.mainJobExists != undefined) {
						// скрываем checkbox
						Dom.get("${fieldHtmlId}-entry").setAttribute('disabled', true);
						Dom.get("${fieldHtmlId}-entry").checked = false;
					} else {
						Dom.get("${fieldHtmlId}-entry").removeAttribute('disabled');
					}
				},
				failure:function (oResponse) {
					alert("Не удалось загрузить данные о руководящей должности. Попробуйте обновить страницу.");
				},
				argument:{
				}
			};
			YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
		}, this);

		return this;
	};
})();

var checkBoxActivate = CheckboxMainJob();

//]]></script>

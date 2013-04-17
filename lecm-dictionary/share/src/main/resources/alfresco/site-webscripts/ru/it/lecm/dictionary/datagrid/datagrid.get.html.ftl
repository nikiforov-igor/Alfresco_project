<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid>
<#assign bubblingLabel = "dictionaries-datagrid">

<@grid.datagrid id=id showViewForm=true>
<script type="text/javascript">//<![CDATA[
function createDatagrid(attributeForShow) {
	new LogicECM.module.Dictionary.DataGrid('${id}', attributeForShow).setOptions(
			{
                bubblingLabel:"${bubblingLabel}",
				usePagination:true,
				showExtendSearchBlock:false,
                actions: [
                    {
                        type:"datagrid-action-link-${bubblingLabel}",
                        id:"onActionEdit",
                        permission:"edit",
                        label:"${msg("actions.edit")}"
                    },
                    {
                        type:"datagrid-action-link-${bubblingLabel}",
                        id:"onActionVersion",
                        permission:"edit",
                        label:"${msg("actions.version")}"
                    },
                    {
                        type:"datagrid-action-link-${bubblingLabel}",
                        id:"onActionDuplicate",
                        permission:"create",
                        label:"${msg("actions.duplicate-row")}"
                    },
                    {
                        type:"datagrid-action-link-${bubblingLabel}",
                        id:"onActionDelete",
                        permission:"delete",
                        label:"${msg("actions.delete-row")}"
                    }
                ]
			}).setMessages(${messages});
}

function loadDictionary() {
	var sUrl = Alfresco.constants.PROXY_URI + "/lecm/dictionary/api/getDictionary?dicName=" + encodeURIComponent("${page.url.args.dic!''}");

	var callback = {
		success:function (oResponse) {
			var oResults = eval("(" + oResponse.responseText + ")");
			if (oResults != null) {
				createDatagrid(oResults.attributeForShow);
			}
		},
		failure:function (oResponse) {
			alert("Справочник не был загружен. Попробуйте обновить страницу.");
		},
		argument:{
		}
	};
	YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
}

function init() {
	loadDictionary();
}

YAHOO.util.Event.onDOMReady(init);
//]]></script>
</@grid.datagrid>

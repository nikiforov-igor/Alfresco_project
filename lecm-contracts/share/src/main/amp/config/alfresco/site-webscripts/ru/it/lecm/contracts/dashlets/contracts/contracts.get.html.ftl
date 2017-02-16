<#include "/org/alfresco/components/form/form.dependencies.inc">

<@markup id="css">
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-contracts/contracts-dashlet.css" />
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/components/document-metadata-form-edit.css" />
</@>
<@markup id="js">
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-contracts/contracts-dashlet.js"></@script>
    <@script type="text/javascript" src="${url.context}/res/modules/simple-dialog.js" group="armFiltersGrid"/>
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/advsearch.js"></@script>
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/lecm-datagrid.js"></@script>
	<@script type="text/javascript" src="${url.context}/res/scripts/grids/documents-journal-grid.js"></@script>
</@>
<@markup id="html">
	<#assign id = args.htmlid>
	<#assign jsid = args.htmlid?js_string>

	<#assign settingsObj = settings!""/>
	<#assign CONTRACTS_REF = settingsObj.nodeRef!""/>

	<script type="text/javascript">//<![CDATA[
	    //TODO: Переделать
	    var contracts = new LogicECM.module.Contracts.dashlet.Contracts("${jsid}").setOptions(
	            {
	                regionId: "${args['region-id']?js_string}",
	                destination: ("${CONTRACTS_REF}" != "") ? "${CONTRACTS_REF}" : null
	            }).setMessages(${messages});

	    new Alfresco.widget.DashletResizer("${jsid}", "${instance.object.id}");
        Alfresco.util.Ajax.jsonGet({
            url: Alfresco.constants.PROXY_URI + "lecm/contracts/dashlet/settings/url",
            dataObj: {},
            successCallback: {
                fn: function (oResponse) {
                    if (oResponse.json) {
                        new Alfresco.widget.DashletTitleBarActions("${jsid}").setOptions(
                            {
                                actions: [
                                    {
                                        cssClass: "arm",
                                        linkOnClick: window.location.protocol + "//" + window.location.host + Alfresco.constants.URL_PAGECONTEXT + "arm?code=" + encodeURI(oResponse.json.armCode) + "&path="  + encodeURI(oResponse.json.armPath),
                                        tooltip: "${msg("dashlet.arm.tooltip")?js_string}"
                                    }
                                ]
                            });
                    }
                }
            },
            failureMessage: "${msg("message.failure")}",
            execScripts: true
        });

	//]]></script>

	<div class="dashlet contracts">
	    <div class="title">${msg("header")}
	        <#if isStarter?? && isStarter>
	            <span class="lecm-dashlet-actions">
	            <a id="${id}-action-add" href="javascript:void(0);" onclick="contracts.onAddContractClick()" class="add" title="${msg("dashlet.add.tooltip")}">${msg("dashlet.add.contract")}</a>
	         </span>
	        </#if>
	    </div>
	    <div class="toolbar flat-button">
	        <div class="hidden">
	         <span class="align-left yui-button yui-menu-button" id="${id}-user">
	            <span class="first-child">
	               <button type="button" tabindex="0"></button>
	            </span>
	         </span>
	            <select id="${id}-user-menu">
	            <#list filterTypes as filter>
	                <option value="${filter.type?html}">${msg("filter." + filter.label)}</option>
	            </#list>
	            </select>
	         <span class="align-left yui-button yui-menu-button" id="${id}-range">
	            <span class="first-child">
	               <button type="button" tabindex="0"></button>
	            </span>
	         </span>
	            <select id="${id}-range-menu">
	            <#list filterRanges as filter>
	                <option value="${filter.type?html}">${msg("filter." + filter.label)}</option>
	            </#list>
	            </select>

	            <div class="clear"></div>
	        </div>
	    </div>
	    <div id="${id}-contractsList" class="body scrollableList"
	         <#if args.height??>style="height: ${args.height}px;"</#if>></div>
	</div>

	<#-- Empty results list template -->
	<div id="${id}-empty" class="hidden1">
	    <div class="empty"><h3>${msg("empty.title")}</h3><span>${msg("empty.description")}</span></div>
	</div>
</@>
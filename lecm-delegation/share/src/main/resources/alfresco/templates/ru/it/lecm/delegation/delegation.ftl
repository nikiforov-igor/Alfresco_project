<#include "/org/alfresco/include/alfresco-template.ftl"/>

<@templateHeader>
<script type="text/javascript">//<![CDATA[

var rootNode = ${rootNode};

/**
 * Ensure LogicECM root object exists
 */
if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

/**
 * LogicECM top-level module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module
 */
LogicECM.module = LogicECM.module || {};

/**
 * LogicECM Delegation module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module.Delegation
 */
LogicECM.module.Delegation = LogicECM.module.Delegation || {};

/**
 * Delegation module.
 *
 * @namespace LogicECM.module
 * @class LogicECM.module.Delegation.DELEGATION_ROOT
 */
LogicECM.module.Delegation.DELEGATION_ROOT = LogicECM.module.Delegation.DELEGATION_ROOT || rootNode.nodeRef;
//]]>
</script>

<!-- Alfresco default scripts -->
<@script type="text/javascript" src="${page.url.context}/res/modules/simple-dialog.js"/>

<!-- Logic ECM scripts -->
<@script type="text/javascript" src="${page.url.context}/res/scripts/ru/it/lecm/delegation/delegation-const.js"/>
</@>

<@templateBody>
<div id="alf-hd">
	<@region id="header" scope="global"/>
	<@region id="title" scope="template"/>
</div>
<div id="bd">
	<div class="yui-t1" id="alfresco-delegation">
		<div id="yui-main">
			<@region id="delegation-toolbar" scope="template"/>
			<@region id="center" scope="template"/>
		</div>
	</div>
</div>
</@>

<@templateFooter>
<div id="alf-ft">
	<@region id="footer" scope="global"/>
</div>
</@>

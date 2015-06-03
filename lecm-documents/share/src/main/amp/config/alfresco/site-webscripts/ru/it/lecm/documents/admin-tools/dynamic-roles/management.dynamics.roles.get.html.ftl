<#assign el=args.htmlid?html>

<script type="text/javascript">//<![CDATA[
(function () {
	function createObject() {
		new LogicECM.module.DocumentAdmin.DynamicRoles("${el}-body").setOptions({
			documentNodeRef: "${nodeRef}"
		}).setMessages(${messages});
	}

	function init() {
		LogicECM.module.Base.Util.loadResources([
			'scripts/components/document-admin-dynamic-roles.js'
		], [
			'css/components/document-admin-dynamic-roles.css'
		], createObject);
	}

	YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>

<div id="${el}-body" class="document-admin-dynamic-roles-body">

</div>


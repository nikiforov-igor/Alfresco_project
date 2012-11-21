<#macro tree nodeType itemType nodePattern="cm_name" itemPattern="cm_name" drawEditors=true>
	<script type="text/javascript">//<![CDATA[
	(function() {
		function init() {
			var orgStructure = new LogicECM.module.OrgStructure.Tree("orgstructure-tree");
			orgStructure.setMessages(${messages});
			orgStructure.setOptions({
				nodeType:"${nodeType}",
				itemType:"${itemType}",
				nodePattern:"${nodePattern}",
				itemPattern:"${itemPattern}",
				drawEditors:${drawEditors?string}
			});
		}
		YAHOO.util.Event.onDOMReady(init);
	})();
	//]]></script>

	<div id="orgstructure-tree" class="ygtv-highlight"></div>
</#macro>

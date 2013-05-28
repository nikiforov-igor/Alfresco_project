<#macro tree nodeType itemType nodePattern="cm_name" itemPattern="cm_name" drawEditors=true fullDelete=false maxNodesOnTopLevel=-1 markOnCreateAsParent=false>
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
				drawEditors:${drawEditors?string},
				fullDelete:${fullDelete?string},
                maxNodesOnTopLevel:${maxNodesOnTopLevel},
                markOnCreateAsParent:${markOnCreateAsParent?string}
			});
		}
		YAHOO.util.Event.onDOMReady(init);
	})();
	//]]></script>

	<div id="orgstructure-tree" class="ygtv-highlight"></div>
</#macro>

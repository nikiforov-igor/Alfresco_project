<#macro tree nodeType itemType nodePattern="cm_name" itemPattern="cm_name" drawEditors=true fullDelete=false maxNodesOnTopLevel=-1 markOnCreateAsParent=false bubblingLabel="">
	<script type="text/javascript">//<![CDATA[
	(function() {
		function createTree() {
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
                markOnCreateAsParent:${markOnCreateAsParent?string},
                bubblingLabel: "${bubblingLabel!""}"
			});
		}

        function init() {
            LogicECM.module.Base.Util.loadResources([
                'scripts/lecm-orgstructure/orgstructure-tree.js',
                'scripts/lecm-orgstructure/orgstructure-utils.js'
            ], [
                'yui/treeview/assets/skins/sam/treeview.css',
                'css/lecm-orgstructure/orgstructure-tree.css'
            ], createTree);
        }

        YAHOO.util.Event.onDOMReady(init);
	})();
	//]]></script>

	<div id="orgstructure-tree" class="ygtv-highlight"></div>
</#macro>

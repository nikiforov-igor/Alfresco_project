<script type="text/javascript">//<![CDATA[
(function(){
    function createTree() {
        var orgStructure = new LogicECM.module.OrgStructure.ArmTree("orgstructure-tree");
        orgStructure.setMessages(${messages});
    }

    function init() {
        LogicECM.module.Base.Util.loadResources([
            'scripts/lecm-orgstructure/orgstructure-arm-tree.js',
            'scripts/lecm-orgstructure/orgstructure-utils.js'
        ], [
            'yui/treeview/assets/skins/sam/treeview.css',
            'css/lecm-orgstructure/orgstructure-arm-tree.css'
        ], createTree);
    }

    YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>

<#import "/ru/it/lecm/base-share/components/view.lib.ftl" as view/>

<@view.viewForm formId="orgstructure-view-form"/>
<div id="orgstructure-tree"></div>



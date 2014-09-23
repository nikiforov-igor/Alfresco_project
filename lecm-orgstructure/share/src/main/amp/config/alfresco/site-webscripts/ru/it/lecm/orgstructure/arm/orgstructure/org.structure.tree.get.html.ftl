<#assign id = args.htmlid>

<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<script type="text/javascript">//<![CDATA[
(function(){
    function createObjects() {
        var orgStructure = new LogicECM.module.OrgStructure.ArmTree("${id}");
        orgStructure.setMessages(${messages});
        orgStructure.setOptions({
            minSTermLength:3,
            bubblingLabel:"${bubblingLabel!''}"
        });
    }

    function init() {
        LogicECM.module.Base.Util.loadResources([
            'scripts/lecm-base/components/lecm-toolbar.js',
            'scripts/lecm-orgstructure/orgstructure-toolbar.js',
            'scripts/lecm-orgstructure/orgstructure-arm-tree.js',
            'scripts/lecm-orgstructure/orgstructure-utils.js'
        ], [
            'components/data-lists/toolbar.css',
            'yui/treeview/assets/skins/sam/treeview.css',
            'css/lecm-orgstructure/orgstructure-arm-tree.css'
        ], createObjects);
    }

    YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>

<@comp.baseToolbar id false true false/>
<div id="${id}-empty">
    <div class="empty"><span>${msg("tree.empty.text")}</span></div>
</div>
<div id="orgstructure-tree" class="orgstructure-tree"></div>



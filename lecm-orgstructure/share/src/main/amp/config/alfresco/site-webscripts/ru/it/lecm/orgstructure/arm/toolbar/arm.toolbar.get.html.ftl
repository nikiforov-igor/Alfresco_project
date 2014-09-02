<#assign id = args.htmlid>

<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<script type="text/javascript">//<![CDATA[
(function(){
    function createToolbar() {
        new LogicECM.module.OrgStructure.Toolbar("${id}").setMessages(${messages}).setOptions({
            minSTermLength:3,
            bubblingLabel:"${bubblingLabel!''}"
        });
    }

    function init() {
        LogicECM.module.Base.Util.loadResources([
            'scripts/lecm-base/components/lecm-toolbar.js',
            'scripts/lecm-orgstructure/orgstructure-toolbar.js'
        ], [
            'components/data-lists/toolbar.css'
        ], createToolbar);
    }

    YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>

<@comp.baseToolbar id false true false/>
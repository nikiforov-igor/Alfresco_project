<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<#assign toolbarId = args.htmlid/>

<#assign pageId = page.id/>

<script type="text/javascript"> //<![CDATA[
	(function () {
		"use strict";

        function createToolbar() {
            var delegationToolbar = new LogicECM.module.Delegation.List.Toolbar ("${toolbarId}");
            delegationToolbar.setMessages(${messages});
        }

        function init() {
            LogicECM.module.Base.Util.loadResources([
                'scripts/lecm-base/components/lecm-toolbar.js',
                'scripts/lecm-delegation/list/delegation-list-toolbar.js'
            ], [
                'components/data-lists/toolbar.css',
                'css/lecm-base/components/toolbar.css'
            ], createToolbar);
        }

        YAHOO.util.Event.onDOMReady(init);
	})();
//]]>
</script>

<@comp.baseToolbar toolbarId true true true>
</@comp.baseToolbar>

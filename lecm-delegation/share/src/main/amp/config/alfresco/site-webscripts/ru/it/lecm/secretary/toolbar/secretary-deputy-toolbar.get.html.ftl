<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<#assign toolbarId = args.htmlid/>

<#assign pageId = page.id/>

<script type="text/javascript"> //<![CDATA[
	(function () {
		"use strict";

        function createToolbar() {
            var secretaryToolbar = new LogicECM.module.Secretary.Toolbar("${toolbarId}");
            secretaryToolbar.setMessages(${messages});
        }

        function init() {
            LogicECM.module.Base.Util.loadResources([
                'scripts/lecm-base/components/lecm-toolbar.js',
                'scripts/lecm-secretary/secretary-toolbar.js',
                'modules/simple-dialog.js'
            ], [
                'css/lecm-base/components/base-menu/base-menu.css',
                'css/lecm-delegation/delegation-menu.css',
                'components/data-lists/toolbar.css',
                'css/lecm-delegation/list/delegation-list.css'
            ], createToolbar);
        }

        YAHOO.util.Event.onDOMReady(init);
	})();
//]]>
</script>

<@comp.baseToolbar toolbarId true true true>
</@comp.baseToolbar>

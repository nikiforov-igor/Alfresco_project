<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<#assign toolbarId = args.htmlid/>

<#assign pageId = page.id/>

<script type="text/javascript"> //<![CDATA[
	(function () {
		"use strict";
		var delegationToolbar = new LogicECM.module.Delegation.List.Toolbar ("${toolbarId}");
		delegationToolbar.setMessages(${messages});
	})();
//]]>
</script>

<@comp.baseToolbar toolbarId true true true>
</@comp.baseToolbar>

<#include "/org/alfresco/include/alfresco-template.ftl" />
<@templateHeader>
	<script type="text/javascript">//<![CDATA[
		var bjContainer = ${bjContainer};

	    if (typeof LogicECM == "undefined" || !LogicECM) {
	        var LogicECM = {};
	    }
		LogicECM.module = LogicECM.module || {};
		LogicECM.module.BusinessJournal = LogicECM.module.BusinessJournal || {};
		LogicECM.module.BusinessJournal.CONTAINER = LogicECM.module.BusinessJournal.CONTAINER || bjContainer;
		//]]>
	</script>
	<#include "/org/alfresco/components/form/form.get.head.ftl">
	<@script type="text/javascript" src="${page.url.context}/res/modules/simple-dialog.js"></@script>
</@>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>

<@bpage.basePage>
	<@region id="records-grid" scope="template" />
</@bpage.basePage>

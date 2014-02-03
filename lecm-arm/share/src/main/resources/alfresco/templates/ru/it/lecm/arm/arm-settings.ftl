<#include "/org/alfresco/include/alfresco-template.ftl" />
<@templateHeader "transitional">
	<script type="text/javascript">//<![CDATA[
		function init() {
			new LogicECM.module.Base.Resizer('DictionaryResizer');
		}

		YAHOO.util.Event.onDOMReady(init);
	//]]></script>

	<#include "/org/alfresco/components/form/form.get.head.ftl">
</@>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>

<@bpage.basePage showToolbar=false>
	<div class="yui-t1" id="lecm-dictionary">
	    <div id="yui-main-2">
	        <div class="yui-b" id="alf-content">
				<@region id="toolbar" scope="template" />
	            <@region id="main-form" scope="template" />
	        </div>
	    </div>
	    <div id="alf-filters">
			<@region id="tree" scope="template"/>
	    </div>
	</div>
</@bpage.basePage>
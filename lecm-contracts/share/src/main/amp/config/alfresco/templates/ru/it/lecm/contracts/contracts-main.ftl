<script type="text/javascript">
	if (typeof LogicECM == "undefined" || !LogicECM) {
		LogicECM = {};
	}
	LogicECM.module = LogicECM.module || {};
	LogicECM.module.Documents = LogicECM.module.Documents|| {};
	(function () {
		LogicECM.module.Documents.SETTINGS = <#if settings?? >${settings}<#else>{}</#if>;
		function init() {
			setTimeout(function () {
				LogicECM.module.Base.Util.setHeight();
				LogicECM.module.Base.Util.setDashletsHeight("main-region");
			}, 10);
		}

		YAHOO.util.Event.onDOMReady(init);
	})();
</script>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>

<@bpage.basePageSimple>
<div id="doc-bd">
	<#if hasPermission>
		<#-- <@region id="toolbar" scope="template"/> -->
		<div class="yui-gc">
			<div id="main-region" class="yui-u first">
				<div class="yui-gd grid columnSize2">
					<div class="yui-u first column1">
						<@region id="summary" scope="template"/>
						<@region id="tasks" scope="template"/>
					</div>
					<div class="yui-u column2">
						<@region id="activity" scope="template"/>
						<@region id="sr-info" scope="template"/>
					</div>
				</div>
			</div>
		</div>
	<#else>
		<@region id="forbidden" scope="template"/>
	</#if>
</div>
</@bpage.basePageSimple>

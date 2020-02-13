<#assign id = args.htmlid>
<#assign jsid = args.htmlid?js_string>
<#assign settingsObj = settings!""/>
<#assign CONTRACTS_REF = settingsObj.nodeRef!""/>

<script type="text/javascript">
	(function() {
		function init() {
			LogicECM.module.Base.Util.loadResources([
				'scripts/lecm-contracts/contracts-dashlet.js'
			], [
				'css/lecm-contracts/contracts-dashlet.css'
			], createControl);
		}
		function createControl() {
			new LogicECM.module.Contracts.dashlet.Contracts("${jsid}").setOptions({
				regionId: "${args['region-id']?js_string}",
				destination: ("${CONTRACTS_REF}" != "") ? "${CONTRACTS_REF}" : null
			}).setMessages(${messages});

			new Alfresco.widget.DashletResizer("${jsid}", "${instance.object.id}");
			Alfresco.util.Ajax.jsonGet({
				url: Alfresco.constants.PROXY_URI + "lecm/contracts/dashlet/settings/url",
				successCallback: {
					fn: function (oResponse) {
						if (oResponse.json) {
							new Alfresco.widget.DashletTitleBarActions("${jsid}").setOptions({
								actions: [{
									cssClass: "arm",
									linkOnClick: window.location.protocol + "//" + window.location.host + Alfresco.constants.URL_PAGECONTEXT + "arm?code=" + encodeURI(oResponse.json.armCode) + "&path="  + encodeURI(oResponse.json.armPath),
									tooltip: "${msg("dashlet.arm.tooltip")?js_string}"
								}]
							});
						}
					}
				},
				failureMessage: "${msg("message.failure")}",
				execScripts: true
			});
		}
		YAHOO.util.Event.onDOMReady(init);
	})();
</script>

<div class="dashlet contracts">
	<div class="title">${msg("header")}</div>
	<div class="toolbar">
		<#if isStarter?? && isStarter>
			<div class="createContract align-right">
				<input id="${id}-action-add" type="button"/>
			</div>
		</#if>
		<div class="contract-filters hidden">
			<span class="align-left yui-button yui-menu-button" id="${id}-user">
				<span class="first-child">
					<button type="button" tabindex="0"></button>
				</span>
			</span>
			<select id="${id}-user-menu">
				<#list filterTypes as filter>
					<option value="${filter.type?html}">${msg("filter." + filter.label)}</option>
				</#list>
			</select>
			<span class="align-left yui-button yui-menu-button" id="${id}-range">
				<span class="first-child">
					<button type="button" tabindex="0"></button>
				</span>
			</span>
			<select id="${id}-range-menu">
				<#list filterRanges as filter>
					<option value="${filter.type?html}">${msg("filter." + filter.label)}</option>
				</#list>
			</select>

			<div class="clear"></div>
		</div>
	</div>
	<div id="${id}-contractsList" class="body scrollableList" <#if args.height??>style="height: ${args.height}px;"</#if>></div>
</div>

<#-- Empty results list template -->
<div id="${id}-empty" class="hidden1">
	<div class="empty"><span>${msg("dashlet.empty")}</span></div>
</div>
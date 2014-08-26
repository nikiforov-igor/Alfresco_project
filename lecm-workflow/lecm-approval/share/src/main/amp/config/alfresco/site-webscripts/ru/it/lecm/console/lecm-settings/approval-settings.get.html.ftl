<#assign el=args.htmlid?html>

<#--
<@markup id="css">
</@>
-->

<@markup id="js">
	<@script type="text/javascript" src="${url.context}/res/components/console/consoletool.js" group="lecm-approval-settings"/>
	<@script type="text/javascript" src="${url.context}/res/components/form/form.js" group="lecm-approval-settings"/>
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/base-utils.js" group="lecm-approval-settings"/>
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-approval/approval-settings.js" group="lecm-approval-settings"/>
</@>

<@markup id="widgets">
	<@createWidgets group="lecm-approval-settings"/>
</@>

<@markup id="html">
	<@uniqueIdDiv>
		<div id="${el}-body" class="eds-global-settings">
			<div class="yui-g">
				<div class="yui-u first">
					<div class="title">${msg("label.title")}</div>
				</div>
			</div>

			<div id="${el}-settings"></div>
		</div>
	</@>
</@>

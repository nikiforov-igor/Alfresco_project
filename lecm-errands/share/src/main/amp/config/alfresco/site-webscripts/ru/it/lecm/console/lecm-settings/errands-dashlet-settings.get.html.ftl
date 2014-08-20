<#assign el=args.htmlid?html>

<@markup id="css" >
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-errands/errands-dashlet-settings.css" group="errands-dashlet-settings"/>
</@>

<@markup id="js">
	<@script type="text/javascript" src="${url.context}/res/components/console/consoletool.js" group="errands-dashlet-settings"/>
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-errands/errands-dashlet-settings.js" group="errands-dashlet-settings"/>
	<@script type="text/javascript" src="${url.context}/res/components/form/form.js" group="errands-dashlet-settings"/>
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/base-utils.js" group="errands-dashlet-settings"/>
</@>

<@markup id="widgets">
	<@createWidgets group="errands-dashlet-settings"/>
</@>

<@markup id="html">
	<@uniqueIdDiv>
		<div id="${el}-body" class="errands-dashlet-settings">
			<div class="yui-g">
				<div class="yui-u first">
					<div class="title">${msg("label.title")}</div>
				</div>
			</div>

			<div id="${el}-settings"></div>
		</div>
	</@>
</@>
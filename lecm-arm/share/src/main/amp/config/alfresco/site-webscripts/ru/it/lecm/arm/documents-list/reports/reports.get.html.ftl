<#include "/org/alfresco/components/component.head.inc">
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-documents/documents-reports.js"></@script>
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-arm/arm-documents-reports.js"></@script>

<#assign id = args.htmlid>

<script type="text/javascript">//<![CDATA[
(function () {
	function init() {
		new LogicECM.module.ARM.DocumentsReports("${id}-body").setMessages(${messages});
	}

	YAHOO.util.Event.onContentReady("${id}", init);
})();
//]]></script>

<div id="${id}-body" class="arm-reports-list">
	<div class="datagrid visible-visible">
		<div class="grid yui-dt">
			<table>
				<thead>
					<tr class="yui-dt-first yui-dt-last">
						<th >
							<div class="yui-dt-liner">
								<span class="yui-dt-label">
									${msg("label.column.reportType")}
								</span>
							</div>
						</th>
					</tr>
				</thead>
				<tbody class="yui-dt-message" id="${id}-body-empty">
					<tr class="yui-dt-first yui-dt-last">
						<td class="yui-dt-empty">
							<div class="yui-dt-liner">
								${msg("label.reports.empty")}
							</div>
						</td>
					</tr>
				</tbody>
				<tbody class="yui-dt-data" id="${id}-body-data"></tbody>
			</table>
		</div>
	</div>
</div>

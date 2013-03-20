<#assign aDateTime = .now>
<#assign el=args.htmlid + aDateTime?iso_utc/>
<#assign docRef= nodeRef/>
<div id="${el}" class="connections-list">
	<table class="connections-title">
		<tr>
			<td class="connections-name">
				${msg("title.connected-documents")}
			</td>
			<td class="connections-add">
				<div class="connections-add">
                   <span id="${el}-addConnection-button" class="yui-button yui-push-button">
                      <span class="first-child">
                         <button type="button" title="${msg("button.connection.create")}">${msg("button.connection.create")}</button>
                      </span>
                   </span>
				</div>
			</td>
		</tr>
	</table>
	<hr>
	<table class="connections-table">
		<#if connections?? && connections.items??>
			<#list connections.items as item>
				<tr class="detail-list-item <#if item_has_next>border-bottom</#if>">
					<td class="icon">
						<img src="/share/res/images/lecm-documents/document_img.png"/>
					</td>
					<td class="connection">
						<div class="connection-type">
							<#if item.type??>
								${item.type.name!""}
							</#if>
						</div>
						<div class="connection-name">
							<#if item.connectedDocument??>
								<div class="document-name">
									<a href="${url.context}/page/document?nodeRef=${item.connectedDocument.nodeRef!""}" class="theme-color-1">
										${item.connectedDocument.presentString!""}
									</a>
								</div>
								<div class="document-list-name">
									${item.connectedDocument.listPresentString!""}
								</div>
							</#if>
						</div>
					</td>
					<td class="list-actions-td">
						<div class="list-action-set">
							<div class="onActionDelete" data-noderef="${item.nodeRef!""}" <#if item.connectedDocument??>data-name="${item.connectedDocument.presentString!""}"</#if>>
								<a title="${msg("action.delete-connection.title")}" class="list-action-link" href="#">
									<span>
										${msg("action.delete-connection.title")}
									</span>
								</a>
							</div>
						</div>
					</td>
				</tr>
			</#list>
		</#if>
	</table>

	<div class="space"></div>

	<table class="connections-title">
		<tr>
			<td class="connections-name">
				${msg("title.connected-with-documents")}
			</td>
			<td class="connections-add">
			</td>
		</tr>
	</table>
	<hr>
	<table class="connections-table">
	<#if connectionsWithDocument?? && connectionsWithDocument.items??>
		<#list connectionsWithDocument.items as item>
			<tr class="detail-list-item <#if item_has_next>border-bottom</#if>">
				<td class="icon">
					<img src="/share/res/images/lecm-documents/document_img.png"/>
				</td>
				<td class="connection">
					<div class="connection-type">
						<#if item.type??>
								${item.type.reverseName!""}
							</#if>
					</div>
					<div class="connection-name">
						<#if item.primaryDocument??>
							<div class="document-name">
								<a href="${url.context}/page/document?nodeRef=${item.primaryDocument.nodeRef!""}" class="theme-color-1">
									${item.primaryDocument.presentString!""}
								</a>
							</div>
							<div class="document-list-name">
								${item.primaryDocument.listPresentString!""}
							</div>
						</#if>
					</div>
				</td>
			</tr>
		</#list>
	</#if>
	</table>

	<script type="text/javascript">//<![CDATA[
		new LogicECM.DocumentConnectionsList("${el}").setOptions(
				{
					documentNodeRef: "${nodeRef}"
				}).setMessages(${messages});
	//]]></script>
</div>
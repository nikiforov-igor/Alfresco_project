<#if hasViewPerm>
	<#assign aDateTime = .now>
	<#assign el=args.htmlid + aDateTime?iso_utc/>
	<#assign docRef= nodeRef/>

	<script type="text/javascript">
		function hideButton() {
            if(location.hash != "#expanded") {
                YAHOO.util.Dom.addClass(this, 'hidden');
            }
        }
        YAHOO.util.Event.onAvailable("${el}-action-collapse", hideButton);
	</script>

	<div class="panel-header">
		<div class="panel-title">${msg("label.title")}</div>
		<div class="lecm-dashlet-actions">
        	<a id="${el}-action-collapse" class="collapse" title="${msg("btn.collapse")}"></a>
    	</div>
    </div>

	<div id="${el}" class="connections-list">
		<div id="${el}-view-mode-button-group" class="yui-buttongroup connections-view-mode-button-group">
			<input id="${el}-view-mode-radiofield-links" type="radio" name="view-mode-radiofield" value="${msg('label.link-list')}" checked>
			<input id="${el}-view-mode-radiofield-tree" type="radio" name="view-mode-radiofield" value="${msg('label.link-tree')}">
		</div>
		<div id="${el}-connections-list-container">
		<table class="connections-title">
			<tr>
				<td class="connections-name">
					${msg("title.connected-documents")}
				</td>
				<td class="connections-add">
					<#if hasCreatePerm && hasStatemachine>
						<div class="connections-add">
		                   <span id="${el}-addConnection-button" class="yui-button yui-push-button">
		                      <span class="first-child">
		                         <button type="button" title="${msg("button.connection.create")}">${msg("button.connection.create")}</button>
		                      </span>
		                   </span>
						</div>
					</#if>
				</td>
			</tr>
		</table>
		<hr>
		<table class="connections-table">
			<#if connections?? && connections.items??>
				<#list connections.items as item>
					<tr class="detail-list-item <#if item_has_next>border-bottom</#if>">
						<td class="icon">
							<#if item.connectedDocument?? && item.connectedDocument.type??>
								<img src="${url.context}/res/images/lecm-documents/type-icons/${item.connectedDocument.type?replace(":", "_")}.png"
								     onerror="this.src = '${url.context}/res/images/lecm-documents/type-icons/default_document.png';"/>
							<#else>
								<img src="${url.context}/res/images/lecm-documents/type-icons/default_document.png"/>
							</#if>
						</td>
						<td class="connection <#if item.connectedDocument?? && !item.connectedDocument.hasAccess>dont-have-access</#if>">
							<div class="connection-type <#if item.isSystem>system-connection</#if>">
								<#if item.type??>
									${item.type.name!""}
								</#if>
							</div>
							<div class="connection-name">
								<#if item.connectedDocument??>
									<div class="document-name">
										<a href="${url.context}/page/${item.connectedDocument.viewUrl!""}?nodeRef=${item.connectedDocument.nodeRef!""}" class="theme-color-1">
											${item.connectedDocument.extPresentString!""}
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
								<#if hasDeletePerm && !item.isSystem>
									<div class="onActionDelete" data-noderef="${item.nodeRef!""}" <#if item.connectedDocument??>data-name="${item.connectedDocument.extPresentString!""}"</#if>>
										<a title="${msg("action.delete-connection.title")}" class="list-action-link" href="#">
											<span>
												${msg("action.delete-connection.title")}
											</span>
										</a>
									</div>
								</#if>
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
						<#if item.primaryDocument?? && item.primaryDocument.type??>
							<img src="${url.context}/res/images/lecm-documents/type-icons/${item.primaryDocument.type?replace(":", "_")}.png"
							     onerror="this.src = '${url.context}/res/images/lecm-documents/type-icons/default_document.png';"/>
						<#else>
							<img src="${url.context}/res/images/lecm-documents/type-icons/default_document.png"/>
						</#if>
					</td>
					<td class="connection <#if item.primaryDocument?? && !item.primaryDocument.hasAccess>dont-have-access</#if>">
						<div class="connection-type <#if item.isSystem>system-connection</#if>">
							<#if item.type??>
								${item.type.reverseName!""}
							</#if>
						</div>
						<div class="connection-name">
							<#if item.primaryDocument??>
								<div class="document-name">
									<a href="${url.context}/page/${item.primaryDocument.viewUrl!""}?nodeRef=${item.primaryDocument.nodeRef!""}" class="theme-color-1">
										${item.primaryDocument.extPresentString!""}
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
		</div>

		<div id="${el}-graph-tree" class="graph-tree hidden1">
			<div class ="yui-skin-sam">
				<div id="expandable_table"> </div>
				<div id="pagination"></div>
			</div>
		</div>
	</div>

		<script type="text/javascript">//<![CDATA[
			new LogicECM.DocumentConnectionsList("${el}").setOptions({
				documentNodeRef: "${nodeRef}",
				excludeType: "${excludeType!""}"
			}).setMessages(${messages});
		YAHOO.util.Event.onContentReady("${el}-graph-tree", function() {
			YAHOO.Bubbling.fire("graphContainerReady");
		});
		//]]></script>
</#if>

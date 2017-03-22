<#if hasViewListPerm>
	<!-- Parameters and libs -->
	<#assign aDateTime = .now>
	<#assign el=args.htmlid + aDateTime?iso_utc/>
	<script type="text/javascript">
		function hideButton() {
            if(location.hash != "#expanded") {
                YAHOO.util.Dom.addClass(this, 'hidden');
            }
        }
        function hideShowPreviewerButton() {
            if(location.hash != "#expanded") {
                YAHOO.util.Dom.addClass(this, 'hidden');
            }
        }
        YAHOO.util.Event.onAvailable("${el}-action-collapse", hideButton);
        YAHOO.util.Event.onAvailable("${el}-action-show-previewer", hideShowPreviewerButton);
	</script>
    <div class="panel-header">
		<div class="panel-title">${msg("label.title")}</div>
        <div class="lecm-dashlet-actions">
        	<a id="${el}-action-collapse" class="collapse" title="${msg("btn.collapse")}"></a>
            <a id="${el}-action-show-previewer" class="show-previewer" title="${msg("btn.show-previewer")}"></a>
    	</div>
    </div>
    <script type="text/javascript">//<![CDATA[
    (function () {
        new LogicECM.DocumentAttachmentsList("${el}").setOptions(
                {
                    <#if baseDocAssocName??>
                        baseDocAssocName: "${baseDocAssocName}",
                    </#if>
                    nodeRef: "${nodeRef}"
                }
        );
    })();
    //]]></script>
	<div id="${el}" class="attachments-list-container">
	    <#if categories??>
	        <#list categories as category>
	            <#assign categoryId = el + "-" + category.nodeRef?replace("/", "")?replace(":", "")/>
	            <div id="${categoryId}"  class="attachment-list no-check-bg">
		            <div id="${categoryId}-main-template" class="hidden">
			            <div>
			            </div>
		            </div>

		            <table class="category-title">
	                    <tr>
	                        <td class="category-name">
	                            ${category.name}
	                        </td>
	                        <td class="category-upload">
		                        <#if hasAddAttachmentPerm && !category.isReadOnly && hasStatemachine>
		                            <div class="file-upload">
		                               <span id="${categoryId}-fileUpload-button" class="yui-button yui-push-button">
		                                  <span class="first-child">
		                                     <button name="fileUpload">${msg("button.upload.file")}</button>
		                                  </span>
		                               </span>
		                            </div>
			                        <#--<div class="add-link">-->
		                               <#--<span id="${categoryId}-addLink-button" class="yui-button yui-push-button">-->
		                                  <#--<span class="first-child">-->
		                                     <#--<button name="addLink">${msg("button.upload.file")}</button>-->
		                                  <#--</span>-->
		                               <#--</span>-->
		                            <#--</div>-->
		                        </#if>
	                        </td>
	                    </tr>
	                </table>

		            <div id="${categoryId}-documents" class="documents"></div>

		            <div class="hidden1">

		                <#-- Action Set "More" template -->
			            <div id="${categoryId}-moreActions">
				            <div class="internal-show-more" title="onActionShowMore"><a href="#" class="doc-list-show-more doc-list-show-more-${category.nodeRef}-${aDateTime?iso_utc}" title="${msg("actions.more")}"><span>${msg("actions.more")}</span></a></div>
				            <div class="more-actions hidden"></div>
			            </div>

		            </div>
	            </div>
	        </#list>
	    </#if>

	    <script type="text/javascript">//<![CDATA[
	    (function () {
	        function init() {
				<#if categories??>
					<#list categories as category>
						<#assign categoryId = el + "-" + category.nodeRef?replace("/", "")?replace(":", "")/>
						<#if !hasReadAttachmentPerm>
							<#assign showActions = []/>
						<#elseif category.isReadOnly || !hasStatemachine>
							<#assign showActions = readOnlyActions/>
						<#else>
							<#assign showActions = allActions/>
						</#if>

						var path = "${category.path}";
						path = path.substring(path.indexOf("/", 1), path.length);

			            new LogicECM.DocumentCategoryAttachmentsList("${categoryId}").setOptions(
			                    {
			                        nodeRef: "${category.nodeRef}",
				                    categoryName: "${category.name}",
				                    path: path,
				                    showFileFolderLink: ${hasViewAttachmentPerm?string},
				                    hasAddAttachmentPerm: ${(hasAddAttachmentPerm && !category.isReadOnly)?string},
				                    hasDeleteOwnAttachmentPerm: ${hasDeleteOwnAttachmentPerm?string},
				                    showActions: [
					                    <#if showActions??>
				                            <#list showActions as action>
					                            {
						                            id: "${action.id}",
						                            onlyForOwn: ${action.onlyForOwn?string}
					                            }<#if action_has_next>,</#if>
				                            </#list>
				                        </#if>
				                    ],
				                    bubblingLabel: "${category.nodeRef}-${aDateTime?iso_utc}"
			                    }).setMessages(${messages});
					</#list>
				</#if>
	        }

	        YAHOO.util.Event.onDOMReady(init);
	    })();
	    //]]></script>
	</div>

</#if>
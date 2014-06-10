<#if hasViewListPerm>
	<!-- Parameters and libs -->
	<#assign aDateTime = .now>
	<#assign el=args.htmlid + aDateTime?iso_utc/>
	<#import "/ru/it/lecm/base-share/components/view.lib.ftl" as view/>
	<script type="text/javascript">
		function hideButton() {
            if(location.hash != "#expanded") {
                YAHOO.util.Dom.setStyle(this, 'display', 'none');
            }
        }
        YAHOO.util.Event.onAvailable("${el}-action-collapse", hideButton);
	</script>
	<div class="metadata-form">
		<div class="lecm-dashlet-actions">
        	<a id="${el}-action-collapse" class="collapse" title="Свернуть"></a>
    	</div>
    </div>
	<div id="${el}">
	    <#if categories??>
	        <@view.viewForm formId="${el}-view-modifier-form"/>
	        <#list categories as category>
	            <div id="${el}-${category.nodeRef}"  class="attachment-list no-check-bg">
		            <div id="${el}-${category.nodeRef}-main-template" class="hidden">
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
		                               <span id="${el}-${category.nodeRef}-fileUpload-button" class="yui-button yui-push-button">
		                                  <span class="first-child">
		                                     <button name="fileUpload">${msg("button.upload.file")}</button>
		                                  </span>
		                               </span>
		                            </div>
			                        <#--<div class="add-link">-->
		                               <#--<span id="${el}-${category.nodeRef}-addLink-button" class="yui-button yui-push-button">-->
		                                  <#--<span class="first-child">-->
		                                     <#--<button name="addLink">${msg("button.upload.file")}</button>-->
		                                  <#--</span>-->
		                               <#--</span>-->
		                            <#--</div>-->
		                        </#if>
	                        </td>
	                    </tr>
	                </table>

		            <div id="${el}-${category.nodeRef}-documents" class="documents"></div>

		            <div class="hidden1">

		                <#-- Action Set "More" template -->
			            <div id="${el}-${category.nodeRef}-moreActions">
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
						<#if !hasReadAttachmentPerm>
							<#assign showActions = []/>
						<#elseif category.isReadOnly || !hasStatemachine>
							<#assign showActions = readOnlyActions/>
						<#else>
							<#assign showActions = allActions/>
						</#if>

						var path = "${category.path}";
						path = path.substring(path.indexOf("/", 1), path.length);

			            new LogicECM.DocumentAttachmentsList("${el}-${category.nodeRef}").setOptions(
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
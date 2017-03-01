<#if hasViewListPerm>
	<!-- Parameters and libs -->
	<#assign aDateTime = .now>
	<#assign el=args.htmlid + aDateTime?iso_utc/>
	<script type="text/javascript">
		function hideButton() {
            if(location.hash != "#expanded") {
                YAHOO.util.Dom.setStyle(this, 'display', 'none');
            }
        }
        function hideShowPreviewerButton() {
            if(location.hash != "#expanded") {
                YAHOO.util.Dom.setStyle(this, 'display', 'none');
            }
        }
        YAHOO.util.Event.onAvailable("${el}-action-collapse", hideButton);
        YAHOO.util.Event.onAvailable("${el}-action-show-previewer", function () {
            hideShowPreviewerButton();
            YAHOO.util.Event.addListener("${el}-action-show-previewer", 'click', function () {
                Alfresco.util.Ajax.request(
                        {
                            url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/document/attachments/preview"<#if inclBaseDoc> + "?inclBaseDoc=${inclBaseDoc?string("true","false")}"</#if>,
                            dataObj: {
                                nodeRef: "${nodeRef}",
                                htmlid: "${el}" + Alfresco.util.generateDomId()
                            },
                            successCallback: {
                                fn:function(response){
                                    var html = response.serverResponse.responseText;
                                    var formEl = Dom.get("custom-region");
                                    if (formEl != null) {
                                        formEl.innerHTML = "";
                                        formEl.innerHTML = html;
                                    }
                                    LogicECM.services = LogicECM.services || {};
									if (LogicECM.services.DocumentViewPreferences) {
                                        LogicECM.services.DocumentViewPreferences.setIsDocAttachmentsInPreview(true);
                                    }
                                },
                                scope: this
                            },
                            failureMessage: "${msg("message.failure")}",
                            scope: this,
                            execScripts: true
                        });
            });
        });
	</script>
    <div class="panel-header">
		<div class="panel-title">${msg("label.title")}</div>
        <div class="lecm-dashlet-actions">
        	<a id="${el}-action-collapse" class="collapse" title="${msg("btn.collapse")}"></a>
            <a id="${el}-action-show-previewer" class="show-previewer" title="${msg("btn.show-previewer")}"></a>
    	</div>
    </div>

	<div id="${el}">
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

			            new LogicECM.DocumentAttachmentsList("${categoryId}").setOptions(
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
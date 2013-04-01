<#if hasViewListPerm>
	<!-- Parameters and libs -->
	<#assign aDateTime = .now>
	<#assign el=args.htmlid + aDateTime?iso_utc/>
	<#import "/ru/it/lecm/base-share/components/view.lib.ftl" as view/>
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
	                            <div class="file-upload">
	                               <span id="${el}-${category.nodeRef}-fileUpload-button" class="yui-button yui-push-button">
	                                  <span class="first-child">
	                                     <button name="fileUpload">${msg("button.upload.file")}</button>
	                                  </span>
	                               </span>
	                            </div>
	                        </td>
	                    </tr>
	                </table>

		            <div id="${el}-${category.nodeRef}-documents" class="documents"></div>

		            <div style="display: none">

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

						var path = "${category.path}";
						path = path.substring(path.indexOf("/", 1), path.length);

			            new LogicECM.DocumentAttachmentsList("${el}-${category.nodeRef}").setOptions(
			                    {
			                        nodeRef: "${category.nodeRef}",
				                    path: path,
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
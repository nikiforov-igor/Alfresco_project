<!-- Parameters and libs -->
<#assign el=args.htmlid/>

<div id="${el}">
    <#if categories??>
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
            </div>
        </#list>
    </#if>

    <script type="text/javascript">//<![CDATA[
    (function () {
        function init() {
            new LogicECM.DocumentAttachmentsList("${el}").setOptions(
                    {
                        nodeRef: "${nodeRef}",
                        categories: [
                            <#if categories??>
                                <#list categories as category>
                                    "${category.nodeRef}"<#if category_has_next>,</#if>
                                </#list>
                            </#if>
                        ]
                    }).setMessages(${messages});

			<#if categories??>
				<#list categories as category>

			            new LogicECM.DocumentAttachmentsListTable("${el}-${category.nodeRef}").setOptions(
			                    {
			                        nodeRef: "${category.nodeRef}",
				                    path: "${category.path}".replace("/Company Home", "")
			                    }).setMessages(${messages});
				</#list>
			</#if>
        }

        YAHOO.util.Event.onDOMReady(init);
    })();
    //]]></script>
</div>
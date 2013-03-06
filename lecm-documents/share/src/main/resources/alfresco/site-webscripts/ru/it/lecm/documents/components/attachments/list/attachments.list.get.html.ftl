<!-- Parameters and libs -->
<#assign el=args.htmlid/>

<div id="${el}">
    Здесь будет перечень вложений

    <#if categories??>
        <#list categories as category>
            <div id="category-${category.name}">
                <table>
                    <tr>
                        <td>
                            ${category.name}
                        </td>
                        <td>
                            <div class="file-upload">
                               <span id="${el}-${category.nodeRef}-fileUpload-button" class="yui-button yui-push-button">
                                  <span class="first-child">
                                     <button name="fileUpload">${msg("button.upload")}</button>
                                  </span>
                               </span>
                            </div>
                        </td>
                    </tr>
                </table>
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
        }

        YAHOO.util.Event.onDOMReady(init);
    })();
    //]]></script>
</div>
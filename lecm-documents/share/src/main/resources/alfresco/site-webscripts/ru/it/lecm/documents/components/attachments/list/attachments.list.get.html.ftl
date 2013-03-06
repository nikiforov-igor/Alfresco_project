<!-- Parameters and libs -->
<#assign el=args.htmlid/>

<div id="${el}">
    Здесь будет перечень вложений

    <div class="file-upload">
       <span id="${el}-fileUpload-button" class="yui-button yui-push-button">
          <span class="first-child">
             <button name="fileUpload">${msg("button.upload")}</button>
          </span>
       </span>
    </div>

    <script type="text/javascript">//<![CDATA[
    (function () {
        function init() {
            new LogicECM.DocumentAttachmentsList("${el}").setOptions(
                    {
                        nodeRef: "${nodeRef}",
                        rootFolder: "${rootFolder.nodeRef}"
                    }).setMessages(${messages});
        }

        YAHOO.util.Event.onDOMReady(init);
    })();
    //]]></script>
</div>
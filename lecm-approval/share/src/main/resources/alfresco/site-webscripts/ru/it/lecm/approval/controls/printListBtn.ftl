<#assign controlId = fieldHtmlId + "-cntrl">

<div class="form-field">
<#escape x as x?js_string>
    <div id="${controlId}" class="yui-skin-sam">
        <button id="${controlId}-print-button" type="button" onclick="printNode('${form.arguments.itemId}')">${msg("button.print")}</button>
    </div>

</#escape>
    <script type="text/javascript">
        function printNode(nodeRef) {
            document.location.href = Alfresco.constants.PROXY_URI + "/lecm/report/ApprovalList?nodeRef=" + encodeURI(nodeRef);
        }
    </script>
</div>

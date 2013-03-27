<#assign el=args.htmlid/>

<div class="widget-bordered-panel">
    <div class="document-forms-panel">
        <h2 id="${el}-heading" class="thin dark">
            ${msg("heading")}
        </h2>

        <div id="${el}-formContainer">
            <div class="form-field">
            <#escape x as x?js_string>
                <div id="experts" class="yui-skin-sam">
                    <a href="#" onclick="printNode('${nodeRef}')">Печать</a>
                </div>
            </#escape>
                <script type="text/javascript">
                    function printNode(nodeRef) {
                        document.location.href = Alfresco.constants.PROXY_URI + "lecm/jforms/form/Simple?nodeRef=" + encodeURI(nodeRef);
                    }
                </script>
            </div>
        </div>
    </div>
</div>

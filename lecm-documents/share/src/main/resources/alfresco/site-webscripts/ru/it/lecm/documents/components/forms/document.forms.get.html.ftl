

<#assign el=args.htmlid/>

<div class="widget-bordered-panel">
    <div class="document-forms-panel">
        <h2 id="${el}-heading" class="grey">
            ${msg("heading")}
        </h2>

        <div id="${el}-formContainer">
            <div class="form-field">

      <!--
            <#escape x as x?js_string>
                <div id="experts" class="yui-skin-sam">
                    <a href="javascript:void(0);" onclick="printNode('${nodeRef}')" class="text-cropped" title="${msg("form.simple")}">${msg("form.simple")}</a>
                </div>
            </#escape>
        -->

                <script type="text/javascript">
                    function printNode(nodeRef) {
                        document.location.href = Alfresco.constants.PROXY_URI + "lecm/report/Simple?nodeRef=" + encodeURI(nodeRef);
                    }
                </script>
            </div>
        </div>
    </div>
</div>

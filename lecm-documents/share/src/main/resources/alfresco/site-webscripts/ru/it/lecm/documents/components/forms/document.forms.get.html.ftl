

<#assign el=args.htmlid/>

<div class="widget-bordered-panel">
    <div class="document-forms-panel">
        <h2 id="${el}-heading" class="grey">
            ${msg("heading")}
        </h2>

        <div id="${el}-formContainer">
            <div class="form-field">

            <#escape x as x?js_string>
                <div id="experts" class="yui-skin-sam">
                    <a href="javascript:void(0);" onclick="printNode('${nodeRef}')" class="text-cropped" title="${msg("form.simple")}">${msg("form.simple")}</a>
                </div>
            </#escape>

            </div>
        </div>

        <script type="text/javascript">
            var Dom = YAHOO.util.Dom,
                Event = YAHOO.util.Event;

            function printNode(nodeRef) {
                document.location.href = Alfresco.constants.PROXY_URI + "lecm/report/Simple?nodeRef=" + encodeURI(nodeRef);
            }

            function init() {
                var forms = Dom.getChildren(Dom.getChildren("${el}-formContainer")[0]);

                if (forms.length > 0) {
                    var heading = Dom.get("${el}-heading");

                    Dom.removeClass(heading, "grey");
                    Dom.addClass(heading, "dark");
                    Alfresco.util.createTwister(heading, "DocumentForms");
                }
            }

            Event.onDOMReady(init);

        </script>
    </div>
</div>

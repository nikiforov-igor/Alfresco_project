<#assign id = args.htmlid>
<#assign jsid = args.htmlid?js_string>

<script type="text/javascript">
    (function() {
        function init() {
            LogicECM.module.Base.Util.loadResources([
                'scripts/lecm-events/my-events-dashlet.js'
            ], [
                'css/lecm-events/my-events-dashlet.css'
            ], createControl);
        }
        function createControl() {
            new LogicECM.dashlet.MyEvents("${id}").setMessages( ${messages} );
            new Alfresco.widget.DashletResizer("${jsid}", "${instance.object.id}");

            var params = "documentType=lecm-events:document";
                    Alfresco.constants.URL_PAGECONTEXT + "event-create?documentType=lecm-events:document&" + LogicECM.module.Base.Util.encodeUrlParams(params);

            new Alfresco.widget.DashletTitleBarActions("${jsid}").setOptions(
            {
                actions:
                        [
                            {
                                cssClass: "createEvent",
                                linkOnClick: Alfresco.constants.URL_PAGECONTEXT + "event-create?documentType=lecm-events:document&" + LogicECM.module.Base.Util.encodeUrlParams(params),
                                tooltip: "${msg("dashlet.events.tooltip")?js_string}"
                            },

                            {
                                cssClass: "help",
                                bubbleOnClick:
                                {
                                    message: "${msg("dashlet.help")?js_string}"
                                },
                                tooltip: "${msg("dashlet.help.tooltip")?js_string}"
                            }
                        ]
            });

        }
        YAHOO.util.Event.onDOMReady(init);
    })();
</script>

<div class="dashlet my-events">
    <div class="title">${msg("label.title")}</div>
    <div class="toolbar flat-button">
         <span class="align-left yui-button yui-menu-button" id="${id}-filters">
            <span class="first-child">
               <button type="button" tabindex="0"></button>
            </span>
         </span>
        <select id="${id}-filters-menu" class="hidden1">
            <option value="today">${msg("relative.today")}</option>
            <option value="5">${msg("label.dashlet.next_count") + " " + 5}</option>
            <option value="10">${msg("label.dashlet.next_count") + " " + 10}</option>
        </select>
        <div class="clear"></div>
    </div>
    <div class="body scrollableList" <#if args.height??>style="height: ${args.height}px;"</#if>>
        <div id="${id}-events"></div>
    </div>
</div>
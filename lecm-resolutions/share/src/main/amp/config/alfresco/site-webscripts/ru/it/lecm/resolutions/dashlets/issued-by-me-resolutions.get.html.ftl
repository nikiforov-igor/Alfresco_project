<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-resolution/resolutions-dashlet.css" />
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-resolution/resolutions-dashlet.js"></@script>

<#assign id = args.htmlid>
<#assign containerId = id + "-container">

<div class="dashlet issued-resolutions resolutions">
    <div class="title">
        <span>${msg("label.title")}</span>
    </div>
    <div class="body scrollableList" id="${id}-paginator" <#if args.height??>style="height: ${args.height}px;"</#if>>
        <div id="${id}_results"></div>
    </div>
</div>

<script type="text/javascript">
    //<![CDATA[
    var info = new LogicECM.module.Resolutions.dashlet.IssuedResolutions("${id}").setMessages(${messages});

    new Alfresco.widget.DashletResizer("${id}", "${instance.object.id}");

    Alfresco.util.Ajax.jsonGet({
        url: Alfresco.constants.PROXY_URI + "lecm/resolutions/dashlet/settings/url",
        dataObj: {},
        successCallback: {
            fn: function (oResponse) {
                if (oResponse.json) {
                    new Alfresco.widget.DashletTitleBarActions("${id}").setOptions(
                            {
                                actions: [
                                    {
                                        cssClass: "arm",
                                        linkOnClick: window.location.protocol + "//" + window.location.host + Alfresco.constants.URL_PAGECONTEXT + "arm?code=" + encodeURI(oResponse.json.armCode) + "&path=" + encodeURI(oResponse.json.armGeneralPath),
                                        tooltip: "${msg("dashlet.arm.tooltip")?js_string}"
                                    },
                                    {
                                        cssClass: "help",
                                        bubbleOnClick: {
                                            message: "${msg("dashlet.help")?js_string}"
                                        },
                                        tooltip: "${msg("dashlet.help.tooltip")?js_string}"
                                    }
                                ]
                            });
                }
            }
        },
        failureCallback: {
            fn: function (oResponse) {
            }
        },
        scope: this,
        execScripts: true
    });

    //]]>
</script>
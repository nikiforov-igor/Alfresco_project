<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-resolution/resolutions-dashlet.css" />

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
    if (typeof LogicECM == "undefined" || !LogicECM) {
        LogicECM = {};
    }

    LogicECM.module = LogicECM.module || {};

    LogicECM.module.Resolutions = LogicECM.module.Resolutions || {};

    LogicECM.module.Resolutions.dashlet = LogicECM.module.Resolutions.dashlet || {};

    (function () {
        var Dom = YAHOO.util.Dom;

        LogicECM.module.Resolutions.dashlet.IssuedResolutions = function IssuedResolutions_constructor(htmlId) {
            LogicECM.module.Resolutions.dashlet.IssuedResolutions.superclass.constructor.call(this, "LogicECM.module.Resolutions.dashlet.IssuedResolutions", htmlId, ["button", "container", "resize"]);

            return this;
        };

        YAHOO.extend(LogicECM.module.Resolutions.dashlet.IssuedResolutions, Alfresco.component.Base,
                {
                    options: {
                        nodeRef: null
                    },

                    message: {
                        "issued_resolutions_on_approval": "${msg("label.info.onApprovalResolutions")?js_string}",
                        "issued_resolutions_on_completion": "${msg("label.info.onCompletionResolutions")?js_string}",
                        "issued_resolutions_on_execution": "${msg("label.info.onExecutionResolutions")?js_string}",
                        "issued_resolutions_on_solution": "${msg("label.info.onSolutionResolutions")?js_string}",
                        "issued_resolutions_expired": "${msg("label.info.expiredResolutions")?js_string}",
                        "issued_resolutions_deadline": "${msg("label.info.comingSoonResolutions")?js_string}"
                    },

                    container: null,

                    onReady: function MyResolutions_onReady() {
                        this.container = Dom.get(this.id + '_results');
                        this.drawForm();
                    },

                    createRow: function Create_row(innerHtml) {
                        var div = document.createElement('div');
                        div.setAttribute('class', 'row summary');
                        if (innerHtml) {
                            div.innerHTML = innerHtml;
                        }
                        if (this.container) {
                            this.container.appendChild(div);
                        }
                    },

                    drawForm: function Draw_form() {
                        Alfresco.util.Ajax.jsonGet({
                            url: Alfresco.constants.PROXY_URI + encodeURI("lecm/resolutions/dashlet/issued"),
                            successCallback: {
                                fn: function (response) {
                                    if (this.container) {
                                        this.container.innerHTML = '';
                                        if (response.json) {
                                            var list = response.json.list;
                                            for (var index in list) {
                                                var innerHtml = "<div class='column first'>" + this.message[list[index].key] + ":" + "</div>" +
                                                        "<div class='column second'><a class=\"status-button text-cropped\" " +
                                                        "href=\"" + Alfresco.constants.URL_PAGECONTEXT + "arm?code=" + encodeURI(list[index].armCode) + "&path=" + encodeURI(list[index].path) + "\">" + list[index].allCount + "</a></div>" +
                                                        "<div class='column third'><a style=\"color:red;\" class=\"status-button text-cropped\" " +
                                                        "href=\"" + Alfresco.constants.URL_PAGECONTEXT + "arm?code=" + encodeURI(list[index].armCode) + "&path=" + encodeURI(list[index].controlPath) + "\">(" + list[index].controlCount + ")</a></div>";
                                                this.createRow(innerHtml);
                                            }
                                        }
                                    }
                                },
                                scope: this
                            },
                            failureMessage: "${msg("message.failure")}"
                        });
                    }
                });
    })();
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
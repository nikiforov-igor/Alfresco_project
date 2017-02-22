<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-errands/errands-dashlet.css" />

<#assign id = args.htmlid>
<#assign containerId = id + "-container">
<script type="text/javascript">
    //<![CDATA[
    if (typeof LogicECM == "undefined" || !LogicECM) {
        LogicECM = {};
    }

    LogicECM.module = LogicECM.module || {};

    LogicECM.module.Errands = LogicECM.module.Errands|| {};

    LogicECM.module.Errands.dashlet = LogicECM.module.Errands.dashlet || {};

    (function () {
        var Dom = YAHOO.util.Dom;

        LogicECM.module.Errands.dashlet.IssuedErrands= function IssuedErrands_constructor(htmlId) {
            LogicECM.module.Errands.dashlet.IssuedErrands.superclass.constructor.call(this, "LogicECM.module.Errands.dashlet.IssuedErrands", htmlId, ["button", "container", "resize"]);

            return this;
        };

        YAHOO.extend(LogicECM.module.Errands.dashlet.IssuedErrands, Alfresco.component.Base,
                {
                    options: {
                        nodeRef: null
                    },

                    message: {
                        "issued_errands_all": "${msg("label.info.allErrands")?js_string}",
                        "issued_errands_expired": "${msg("label.info.expiredErrands")?js_string}",
                        "issued_errands_deadline": "${msg("label.info.comingSoonErrands")?js_string}",
                        "issued_errands_execution": "${msg("label.info.onExecutionErrands")?js_string}"
                    },

                    container: null,

                    onReady: function IssuedErrands_onReady() {
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
                            url: Alfresco.constants.PROXY_URI + encodeURI("lecm/errands/dashlet/issued"),
                            successCallback: {
                                fn: function (response) {
                                    if (this.container) {
                                        this.container.innerHTML = '';
                                        if (response.json) {
                                            var list = response.json.list;
                                            for (var index in list) {
                                                var innerHtml = "<div class='column first'>" + this.message[list[index].key] + ":" + "</div>" +
                                                        "<div class='column second'><a class=\"status-button text-cropped\" " +
                                                        "href=\"" + Alfresco.constants.URL_PAGECONTEXT + "arm?code=" + encodeURI(list[index].armCode) + "&path="  + encodeURI(list[index].path) + "\">" + list[index].allCount + "</a></div>" +
                                                        "<div class='column third'><a style=\"color:red;\" class=\"status-button text-cropped\" " +
                                                        "href=\"" + Alfresco.constants.URL_PAGECONTEXT + "arm?code=" + encodeURI(list[index].armCode) + "&path="  + encodeURI(list[index].importantPath)+ "\">(" + list[index].importantCount + ")</a></div>";
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
    var info = new LogicECM.module.Errands.dashlet.IssuedErrands("${id}").setMessages(${messages});

    new Alfresco.widget.DashletResizer("${id}", "${instance.object.id}");

    Alfresco.util.Ajax.jsonGet({
        url: Alfresco.constants.PROXY_URI + "lecm/errands/dashlet/settings/url",
        dataObj: {},
        successCallback: {
            fn: function (oResponse) {
                if (oResponse.json) {
                    new Alfresco.widget.DashletTitleBarActions("${id}").setOptions(
                    {
                        actions:
                                [
                                    {
                                        cssClass: "arm",
                                        linkOnClick: window.location.protocol + "//" + window.location.host + Alfresco.constants.URL_PAGECONTEXT + "arm?code=" + encodeURI(oResponse.json.armCode) + "&path="  + encodeURI(oResponse.json.armGeneralPath),
                                        tooltip: "${msg("dashlet.arm.tooltip")?js_string}"
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

<div class="dashlet issued-errands errands">
    <div class="title">
        <span>${msg("label.title")}</span>
    </div>
    <div class="body scrollableList" id="${id}-paginator" <#if args.height??>style="height: ${args.height}px;"</#if>>
        <div id="${id}_results"></div>
    </div>
</div>

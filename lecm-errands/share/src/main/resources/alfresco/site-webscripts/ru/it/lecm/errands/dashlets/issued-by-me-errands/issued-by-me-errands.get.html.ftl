<#assign id = args.htmlid>
<#assign containerId = id + "-container">
<script type="text/javascript">
    //<![CDATA[
    if (typeof LogicECM == "undefined" || !LogicECM) {
        var LogicECM = {};
    }

    LogicECM.module = LogicECM.module || {};

    LogicECM.module.Errands = LogicECM.module.Errands|| {};

    LogicECM.module.Errands.dashlet = LogicECM.module.Errands.dashlet || {};

    (function () {
        var Dom = YAHOO.util.Dom;

        LogicECM.module.Errands.dashlet.IssuedErrands= function Contracts_constructor(htmlId) {
            LogicECM.module.Errands.dashlet.IssuedErrands.superclass.constructor.call(this, "LogicECM.module.Errands.dashlet.IssuedErrands", htmlId, ["button", "container", "resize"]);

            return this;
        };

        YAHOO.extend(LogicECM.module.Errands.dashlet.IssuedErrands, Alfresco.component.Base,
                {
                    options: {
                        nodeRef: null
                    },

                    message: {
                        "ALL": "${msg("label.info.allErrands")?js_string}",
                        "EXPIRED": "${msg("label.info.expiredErrands")?js_string}",
                        "DEADLINE": "${msg("label.info.comingSoonErrands")?js_string}",
                        "EXECUTION": "${msg("label.info.onExecutionErrands")?js_string}"
                    },

                    container: null,

                    onReady: function Contracts_onReady() {
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
                        Alfresco.util.Ajax.jsonGet(
                                {
                                    url: Alfresco.constants.PROXY_URI + encodeURI("lecm/errands/dashlet/issued"),
                                    successCallback: {
                                        fn: function (response) {
                                            if (this.container != null) {
                                                this.container.innerHTML = '';
                                                if (response.json != null) {
                                                    var list = response.json.list;
                                                    for (var index in list) {
                                                            var innerHtml = "<div class='column first'>" + this.message[list[index].key] + ":" + "</div>" +
                                                                    "<div class='column second'>" + list[index].allCount + "</div>" +
                                                                    "<div class='column third'>" + "(" + list[index].importantCount + ")"+ "</div>";
                                                            this.createRow(innerHtml);
                                                    }
                                                }
                                            }
                                        },
                                        scope: this
                                    },
                                    failureMessage: "message.failure"
                                });
                    }
                });
    })();
    var info = new LogicECM.module.Errands.dashlet.IssuedErrands("${id}").setMessages(${messages});

    new Alfresco.widget.DashletResizer("${id}", "${instance.object.id}");
    new Alfresco.widget.DashletTitleBarActions("${id}").setOptions(
            {
                actions:
                        [
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
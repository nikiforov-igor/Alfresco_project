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
            message: {},

            container: null,

            onReady: function MyResolutions_onReady() {
                this.message = {
                    "issued_resolutions_on_approval": this.msg("label.info.onApprovalResolutions"),
                    "issued_resolutions_on_approval": this.msg("label.info.onApprovalResolutions"),
                    "issued_resolutions_on_completion": this.msg("label.info.onCompletionResolutions"),
                    "issued_resolutions_on_execution": this.msg("label.info.onExecutionResolutions"),
                    "issued_resolutions_on_solution": this.msg("label.info.onSolutionResolutions"),
                    "issued_resolutions_expired": this.msg("label.info.expiredResolutions"),
                    "issued_resolutions_deadline": this.msg("label.info.comingSoonResolutions")
                };
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
                    failureMessage: this.msg("message.failure")
                });
            }
        });
})();
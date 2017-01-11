<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>
<#assign id = args.htmlid>

<div id="report-settings">
    <div id="${id}-reportForm"></div>
</div>
<script type="text/javascript">
    //<![CDATA[
    var reportForm;
    (function () {

        LogicECM.module.ReportsEditor.REPORT_SETTINGS =
        <#if reportSettings?? >
        ${reportSettings}
        <#else>
        {}
        </#if>;

        function init() {
            var deployFunction = function() {
                Alfresco.util.PopupManager.displayPrompt({
                    title: "${msg('lecm.re.lbl.register-report')}",
                    text: "${msg('lecm.re.lbl.sure-deploy-report')}",
                    buttons: [
                        {
                            text: "${msg('lecm.re.msg.deploy.yes')}",
                            handler: function dlA_onActionDeploy() {
                                this.destroy();
                                var sUrl = Alfresco.constants.PROXY_URI + "/lecm/reports/rptmanager/deployReport?reportDescNode={reportDescNode}";
                                sUrl = YAHOO.lang.substitute(sUrl, {
                                    reportDescNode: "${args["reportId"]}"
                                });
                                var callback = {
                                    success: function (oResponse) {
                                        var response = eval("(" + oResponse.responseText + ")");;
                                        Alfresco.util.PopupManager.displayMessage(
                                                {
                                                    text: (response != null && response.success) ? "${msg('lecm.re.msg.deploy.success')}" : "${msg('lecm.re.msg.deploy.error')}",
                                                    displayTime: 3
                                                });
                                    },
                                    failure: function (oResponse) {
                                        alert(oResponse.responseText);
                                        Alfresco.util.PopupManager.displayMessage(
                                                {
                                                    text: "${msg('lecm.re.msg.deploy.error')}",
                                                    displayTime: 3
                                                });
                                    },
                                    timeout: 30000
                                };
                                YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
                            }
                        },
                        {
                            text: "${msg('lecm.re.msg.deploy.no')}",
                            handler: function dlA_onActionDelete_cancel() {
                                this.destroy();
                            },
                            isDefault: true
                        }
                    ]
                });
            };

            var htmlId = "${args["reportId"]}".replace("workspace://SpacesStore/", "").replace("-", "");
            Alfresco.util.Ajax.request(
                    {
                        url: Alfresco.constants.URL_SERVICECONTEXT + "components/form",
                        dataObj: {
                            htmlid: htmlId,
                            itemKind: "node",
                            itemId: "${args["reportId"]}",
                            mode: "edit",
                            submitType: "json",
                            showSubmitButton: "true",
                            showCancelButton: ((LogicECM.module.ReportsEditor.REPORT_SETTINGS && LogicECM.module.ReportsEditor.REPORT_SETTINGS.isSubReport == "true")
                                    ? "false": "true"),
							showCaption: false
                        },
                        successCallback: {
                            fn: function (response) {
                                var formEl = Dom.get("${id}-reportForm");
                                formEl.innerHTML = response.serverResponse.responseText;
                                Dom.setStyle("${id}-footer", "opacity", "1");

                                // Form definition
                                var form = new Alfresco.forms.Form(htmlId + '-form');

                                if (Dom.get(htmlId + "-form-cancel") !== null) {
                                    Alfresco.util.createYUIButton(null, "",
                                            deployFunction, { label: "${msg("actions.deploy")}", title: "${msg("actions.deploy")}" }, htmlId + "-form-cancel");

                                }
                                if (Dom.get(htmlId + "-form-submit") !== null) {
                                    Dom.get(htmlId + "-form-submit").value = "${msg("actions.save")}";
                                    Dom.get(htmlId + "-form-submit").setAttribute('title', "${msg("actions.save")}");
                                }

                                form.ajaxSubmit = true;
                                form.setAJAXSubmit(true,
                                        {
                                            successCallback: {
                                                fn: function () {
                                                    Alfresco.util.PopupManager.displayMessage(
                                                            {
                                                                text: "${msg('message.save.settings.success')}"
                                                            });
                                                },
                                                scope: this
                                            },
                                            failureCallback: {
                                                fn: function (response) {
                                                    alert(response.json.message);
                                                    Alfresco.util.PopupManager.displayMessage(
                                                            {
                                                                text: "${msg('message.save.settings.error')}"
                                                            });
                                                },
                                                scope: this
                                            }
                                        });
                                form.setSubmitAsJSON(true);
                                form.setShowSubmitStateDynamically(true, false);
                                form.init();
                            }
                        },
                        failureMessage: "message.failure",
                        execScripts: true
                    });
        }

        YAHOO.util.Event.onContentReady("${id}-reportForm", init);
    })();
    //]]>
</script>

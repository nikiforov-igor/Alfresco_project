<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>
<#assign id = args.htmlid>
<script type="text/javascript">//<![CDATA[
    LogicECM.module.ReportsEditor.SETTINGS.DESTINATION = LogicECM.module.ReportsEditor.SETTINGS.reportsContainer;
//]]></script>

<#if !page.url.args.reportId??>
<div class="yui-t1" id="bj-dictionary-grid">
    <div id="yui-main-2">
        <div class="yui-b" id="alf-content" style="margin-left: 0;">
            <@grid.datagrid id=id showViewForm=false>
                <script type="text/javascript">//<![CDATA[

                LogicECM.module.ReportsEditor.Grid = function (containerId) {
                    return LogicECM.module.ReportsEditor.Grid.superclass.constructor.call(this, containerId);
                };

                /**
                 * Extend from LogicECM.module.Base.DataGrid
                 */
                YAHOO.lang.extend (LogicECM.module.ReportsEditor.Grid, LogicECM.module.Base.DataGrid);

                /**
                 * Augment prototype with main class implementation, ensuring overwrite is enabled
                 */
                YAHOO.lang.augmentObject (LogicECM.module.ReportsEditor.Grid.prototype, {

                    onActionEdit: function (item) {
                        var baseUrl = window.location.protocol + "//" + window.location.host;
                        var template = "reports-editor?reportId={reportId}";
                        var url = YAHOO.lang.substitute (baseUrl + Alfresco.constants.URL_PAGECONTEXT + template, {
                            reportId: item.nodeRef
                        });
                        window.location.href = url
                    }

                }, true);

                function createDatagrid() {
                    var datagrid = new LogicECM.module.ReportsEditor.Grid('${id}').setOptions(
                            {
                                usePagination:true,
                                useDynamicPagination :true,
                                showExtendSearchBlock:false,
                                actions: [
                                    {
                                        type:"datagrid-action-link-reports",
                                        id:"onActionEdit",
                                        permission:"edit",
                                        label:"${msg("actions.edit")}"
                                    },
                                    {
                                        type:"datagrid-action-link-reports",
                                        id:"onActionDelete",
                                        permission:"delete",
                                        label:"${msg("actions.delete-row")}"
                                    }
                                ],
                                bubblingLabel: "reports",
                                showCheckboxColumn: false
                            }).setMessages(${messages});

                    YAHOO.util.Event.onContentReady ('${id}', function () {
                        YAHOO.Bubbling.fire ("activeGridChanged", {
                            datagridMeta: {
                                itemType: "lecm-rpeditor:reportDescriptor",
                                nodeRef: LogicECM.module.ReportsEditor.SETTINGS.reportsContainer,
                                actionsConfig: {
                                    fullDelete: true,
                                    trash: false
                                },
                                sort:"cm:name|desc"
                                /*,searchConfig: {
                                    filter: '+PATH:"/app:company_home/cm:Business_x0020_platform/cm:LECM/cm:Сервис_x0020_Редактор_x0020_Отчетов/cm:Отчеты/*//*"'
                                }*/
                            },
                            bubblingLabel: "reports"
                        });
                    });
                }

                function init() {
                    createDatagrid();
                }

                YAHOO.util.Event.onDOMReady(init);
                //]]></script>
            </@grid.datagrid>
        </div>
    </div>
</div>
<#else>
<div id="${id}-reportForm"></div>
<script type="text/javascript">
    //<![CDATA[
    var reportForm;
    (function () {
        function init() {
            Alfresco.util.Ajax.request(
                    {
                        url:Alfresco.constants.URL_SERVICECONTEXT + "components/form",
                        dataObj:{
                            htmlid:"Report-" + "${page.url.args.reportId}",
                            itemKind:"node",
                            itemId:"${page.url.args.reportId}",
                            mode: "edit",
                            submitType:"json",
                            showSubmitButton:"true"
                        },
                        successCallback:{
                            fn: function (response) {
                                var formEl = Dom.get("${id}-reportForm");
                                formEl.innerHTML = response.serverResponse.responseText;
                                Dom.setStyle("${id}-footer", "opacity", "1");
                                // Form definition
                                var form = new Alfresco.forms.Form('Report-' + '${page.url.args.reportId}' + '-form');
                                form.ajaxSubmit = true;
                                form.setAJAXSubmit(true,
                                        {
                                            successCallback: {
                                                fn: function () {
                                                    Alfresco.util.PopupManager.displayMessage(
                                                            {
                                                                text: "Данные обновлены"
                                                            });
                                                },
                                                scope: this
                                            },
                                            failureCallback: {
                                                fn: function () {
                                                    Alfresco.util.PopupManager.displayMessage(
                                                            {
                                                                text: "Не удалось обновить данные"
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
                        failureMessage:"message.failure",
                        execScripts:true
                    });
        }

        YAHOO.util.Event.onDOMReady(init);
    })();
    //]]>
</script>
</#if>

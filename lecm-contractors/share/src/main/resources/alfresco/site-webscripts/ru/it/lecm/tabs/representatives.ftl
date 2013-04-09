<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign datagridId = fieldHtmlId + "-representatives"/>
<#assign bubblingLabel = "representatives-datagrid"/>
<#assign contractorRef=args.itemId/>

<#assign showActions = form.mode != "view"/>

<#if form.mode != "view">
<div id="${fieldHtmlId}">
    <div id="${fieldHtmlId}-btnCreateNewRepresentative"></div>
</div>
</#if>

<div id="${fieldHtmlId}-wrapper" class="form-field with-grid">
<script type="text/javascript">//<![CDATA[
    if (typeof LogicECM == "undefined" || !LogicECM) {
        var LogicECM = {};
    }

    LogicECM.module = LogicECM.module || {};

    LogicECM.module.RepresentativesPageModule = (function() {
        "use strict";

        function RepresentativesGrid(containerId) {
            debugger;

            return RepresentativesGrid.superclass.constructor.call(this, containerId);
        }

        YAHOO.lang.extend(RepresentativesGrid, LogicECM.module.Base.DataGrid);

        YAHOO.lang.augmentObject(RepresentativesGrid.prototype, {

            onPrimaryChange: function(p_items, owner, actionsConfig, fnDeleteComplete, fnPrompt) {

                debugger;

                var currentNodeRef = p_items.nodeRef, // {String}
                           dataObj = { "representativeToAssignAsPrimary": currentNodeRef };

                Alfresco.util.Ajax.request({
                    method: "POST",
                    url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/contractors/representatives/reassign",
                    dataObj: dataObj,
                    requestContentType: "application/json",
                    responseContentType: "application/json",
                    successCallback: {
                        fn: function (response) {
                            YAHOO.Bubbling.fire("datagridRefresh", {
                                bubblingLabel: "${bubblingLabel}"//"representatives-datagrid"
                            });
                        },
                        scope: this
                    },
                    failureCallback: {
                        fn: function () {
                            Alfresco.util.PopupManager.displayMessage({
                                text: Alfresco.component.Base.prototype.msg("message.reassign-representative.failure")
                            });
                        }
                    }
                });

            }

        }, true);

        var __itemType__ = "lecm-contractor:link-representative-and-contractor";

        var _initializeDatagrid = function() {
            YAHOO.util.Event.onDOMReady(function() {

                var datagrid = new RepresentativesGrid("${datagridId}").setOptions({

                    bubblingLabel: "${bubblingLabel}",//"representatives-datagrid",
                    usePagination: false,
                    showExtendSearchBlock: false,
                    showCheckboxColumn: false,
                    searchShowInactive: false,

                    forceSubscribing: true,

                    showActionColumn: ${showActions?string},

                <#if showActions>
                    actions: [{
                        type: "datagrid-action-link-representatives-datagrid",
                        id: "onPrimaryChange",
                        permission: "edit",
                        label: "Назначить основным"//"${msg("actions.edit")}"
                    },
                    {
                        type: "datagrid-action-link-representatives-datagrid",
                        id: "onActionDelete",
                        permission: "delete",
                        label: "Удалить представителя"//"${msg("actions.delete-row")}"
                    }],
                </#if>

                    datagridMeta: {
                        itemType: __itemType__,
                        nodeRef: "${contractorRef}",
                        actionsConfig: {
                            fullDelete: true,
                            targetDelete: true
                        }
                    }
                });

                datagrid.draw();
            });
        };

        var _showAddRepresentativeForm = function() {

            debugger;

            var url = "lecm/components/form" +
                    "?itemKind={itemKind}" +
                    "&itemId={itemId}" +
                  //"&formId={formId}" +
                    "&destination={destination}" +
                    "&mode={mode}" +
                    "&submitType={submitType}" +
                    "&showCancelButton=true";

            var templateUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + url, {
                itemKind: "type",
                itemId: __itemType__,
                //formId: "addNewRepresentative",
                destination: "${contractorRef}",
                mode: "create",
                submitType: "json"
            });

            // Спасаем "тонущие" всплывающие сообщения.
            Alfresco.util.PopupManager.zIndex = 9000;

            // Создание формы добавления представителя.
            var addRepresentativeForm = new Alfresco.module.SimpleDialog("${fieldHtmlId}-add-representative-form");

            var isPrimaryCheckboxChecked,
                    selectedNodeRef;

            addRepresentativeForm.setOptions({
                width: "500px",
                templateUrl: templateUrl,
                destroyOnHide: true,
                doBeforeFormSubmit: {
                    fn: function() {
                        debugger;

                        isPrimaryCheckboxChecked = YAHOO.util.Dom.get("${fieldHtmlId}-add-representative-form_prop_lecm-contractor_link-to-representative-association-is-primary-entry").checked;
                    },
                    scope: this
                },
                onSuccess: {
                    fn: function (response) {

                        if(isPrimaryCheckboxChecked) {
                            Alfresco.util.Ajax.request({
                                method: "POST",
                                url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/contractors/representatives/reassign",
                                dataObj: { "representativeToAssignAsPrimary": response.json.persistedObject },
                                requestContentType: "application/json",
                                responseContentType: "application/json",
                                successCallback: {
                                    fn: function (response) {
                                        YAHOO.Bubbling.fire("datagridRefresh", {
                                            bubblingLabel: "${bubblingLabel}"//"representatives-datagrid"
                                        });
                                    },
                                    scope: this
                                },
                                failureCallback: {
                                    fn: function () {
                                        Alfresco.util.PopupManager.displayMessage({
                                            text: Alfresco.component.Base.prototype.msg("message.reassign-representative.failure")
                                        });
                                    }
                                }
                            });
                        }

                        YAHOO.Bubbling.fire("dataItemCreated", {
                            nodeRef: response.json.persistedObject,
                            bubblingLabel: "${bubblingLabel}"//"representatives-datagrid"
                        });

                        Alfresco.util.PopupManager.displayMessage({
                            text: Alfresco.component.Base.prototype.msg("message.add-representative.success")
                        });
                    },
                    scope: this
                },
                onFailure: {
                    fn: function() {
                        Alfresco.util.PopupManager.displayMessage({
                            text: Alfresco.component.Base.prototype.msg("message.add-representative.failure")
                        });
                    },
                    scope: this
                }
            });

            addRepresentativeForm.show();
        };

        var _initializeAddRepresentativeButton = function() {

            debugger;

            var putButtonIn = YAHOO.util.Dom.get("${fieldHtmlId}");

            if(putButtonIn !== null) { // А если не нашли, то мы во view-режиме.
                var button = Alfresco.util.createYUIButton(putButtonIn, "btnCreateNewRepresentative", _showAddRepresentativeForm, {
                    label: Alfresco.component.Base.prototype.msg("tab.representatives.addRepresentativeButton.label")
                });

                button.setStyle("margin", "0 0 5px 1px");
            }
        };

        return {
            initializePage: function() {

                debugger;

                _initializeDatagrid();
                _initializeAddRepresentativeButton();
            }
        };

    })();

    //YAHOO.util.Event.onAvailable("${fieldHtmlId}-wrapper", representativesPageModule.initializePage);
    YAHOO.util.Event.onAvailable("${fieldHtmlId}-wrapper", LogicECM.module.RepresentativesPageModule.initializePage);

//]]>
</script>
<@grid.datagrid datagridId false/>
</div>

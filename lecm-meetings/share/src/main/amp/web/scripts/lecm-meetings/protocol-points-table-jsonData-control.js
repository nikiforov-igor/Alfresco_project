if (typeof LogicECM == 'undefined' || !LogicECM) {
    LogicECM = {};
}
LogicECM.module = LogicECM.module || {};
LogicECM.module.Base = LogicECM.module.Base || {};
LogicECM.module.Meetings = LogicECM.module.Meetings || {};

(function () {

    LogicECM.module.Meetings.protocolPointsTableControl = function (htmlId) {
        return LogicECM.module.Meetings.protocolPointsTableControl.superclass.constructor.call(this, htmlId);
    };

    YAHOO.extend(LogicECM.module.Meetings.protocolPointsTableControl, LogicECM.module.DocumentTableControl, {
        setCustomParametersToGrid: function () {
            if (this.widgets.tableDateGrid) {
                this.widgets.tableDateGrid.options.reportersFilterEnabled = true;
            }
        },

        doBeforeDrawProcessing: function () {
            Alfresco.util.Ajax.jsonGet({
                url: Alfresco.constants.PROXY_URI + 'lecm/document/connections/api/getConnectionsWithDocument',
                dataObj: {
                    documentNodeRef: this.options.itemId,
                    excludeType: 'lecm-errands:document'
                },
                successCallback: {
                    scope: this,
                    fn: function (response) {
                        var oResults = JSON.parse(response.serverResponse.responseText);
                        if (oResults && oResults.items && oResults.items.length) {
                            if (oResults.items.some(function (item) {
                                    return item.primaryDocument.type == 'lecm-meetings:document';
                                }, this)) {
                                this.widgets.tableDateGrid.setOptions({
                                    reportersFilterEnabled: false
                                })
                            }
                        }

                        this.widgets.tableDateGrid.draw();
                    }
                },
                failureCallback: {
                    scope: this,
                    fn: function () {
                        this.widgets.tableDateGrid.draw();
                    }
                }
            });
        }
    }, true);

    LogicECM.module.Meetings.protocolPointsTableControlJSONDataGrid = function (htmlId) {
        LogicECM.module.Meetings.protocolPointsTableControlJSONDataGrid.superclass.constructor.call(this, htmlId);
        return this
    };

    YAHOO.lang.extend(LogicECM.module.Meetings.protocolPointsTableControlJSONDataGrid, LogicECM.module.DocumentTableControlJSONDataGrid, {
        fieldsForFilter: [
            '_assoc_lecm-protocol_meeting-chairman-assoc',
            '_assoc_lecm-protocol_secretary-assoc',
            '_assoc_lecm-protocol_attended-assoc',
            '_assoc_lecm-document_author-assoc'
        ],

        filteredFields: ['lecm-protocol-ts:reporter-assoc', 'lecm-protocol-ts:coreporter-assoc'],

        reportersFilter: [],

        notExistsInFilter: function (item) {
            return this.reportersFilter.indexOf(item) == -1 && item.length > 0;
        },

        getAllowedEmployees: function () {
            this.reportersFilter = [];

            this.fieldsForFilter.forEach(function (fieldId) {
                var controls = Alfresco.util.ComponentManager.find({id: this.options.formId + fieldId});

                if (controls && controls.length) {
                    this.reportersFilter = this.reportersFilter.concat(Object.keys(controls[0].selectedItems).filter(this.notExistsInFilter, this));
                } else {
                    var control = YAHOO.util.Dom.get(this.options.formId + fieldId);
                    if (control) {
                        this.reportersFilter = this.reportersFilter.concat((control.value.split(",")).filter(this.notExistsInFilter, this));
                    }
                }
            }, this);

            jQuery.ajax({
                url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/routes-v2/getEmployeesOfAllApprovalRoutes",
                type: "GET",
                timeout: 30000,
                async: false,
                dataType: "json",
                contentType: "application/json",
                data: {
                    nodeRef: this.options.documentNodeRef
                },
                context: this,
                processData: true,
                success: function (response) {
                    if (response && response.employees && response.employees.length) {
                        this.reportersFilter = this.reportersFilter.concat(response.employees.filter(this.notExistsInFilter, this));
                    }
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    Alfresco.util.PopupManager.displayMessage({
                        text: errorThrown
                    });
                }
            });

            return this.reportersFilter.join(",");
        },

        updateTemplateParams: function (params) {
            if (this.options.reportersFilterEnabled) {
                if (!params.args) {
                    params.args = {};
                }
                params.args.allowedForPoint = this.getAllowedEmployees();
            }
        }
    }, true);
})();

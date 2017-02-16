/**
 * Created by APanyukov on 14.10.2016.
 */

// Ensure LogicECM root object exists
if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

/**
 * LogicECM top-level module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module
 */
LogicECM.module = LogicECM.module || {};

/**
 * LogicECM Dictionary module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module.Orgstructure
 */
LogicECM.module.Orgstructure = LogicECM.module.Orgstructure || {};

(function () {
    LogicECM.module.Orgstructure.StaffListDataGrid = function (containerId) {
        return LogicECM.module.Orgstructure.StaffListDataGrid.superclass.constructor.call(this, containerId);
    };
    YAHOO.lang.extend(LogicECM.module.Orgstructure.StaffListDataGrid, LogicECM.module.Base.DataGrid,{

        onActionEmployeeAdd:function DataGridActions_onActionEmployeeAdd(p_item, owner, actionsConfig, fnCallback) {
            var me = this;
            var metaData = {
                useFilterByOrg: false,
                itemType: "lecm-orgstr:employee-link",
                createFormId: "selectEmployeeWithNoAbsences",
                nodeRef: p_item.nodeRef,
                addMessage: this.msg("label.employee-link.add")
            };

            var onAddCallback = function (employeeRef) {
                // Reload the node's metadata
                YAHOO.Bubbling.fire("datagridRefresh",
                    {
                        bubblingLabel: me.options.bubblingLabel
                    });
                Alfresco.util.PopupManager.displayMessage(
                    {
                        text: this.msg("message.employee.add.success")
                    });
            }.bind(me);

            this.showCreateDialog(metaData, onAddCallback);
        },
        onActionEmployeeDelete: function DataGridActions_onActionEmployeeDelete(p_item, owner, actionsConfig, fnDeleteComplete) {
            var staffRow = p_item;
            var subnitRow = this.datagridMeta.nodeRef;
            // Подразделение в котором находится сотрудник
            var sUrl =  Alfresco.constants.PROXY_URI + "lecm/orgstructure/api/getUnitProperties?nodeRef=" + subnitRow;
            Alfresco.util.Ajax.jsonGet({
                url: sUrl,
                successCallback: {
                    fn: function (response) {
                        var oResults = response.json;
                        var subnitRowName = oResults .fullName;
                        // Получаем для штатного расписания ссылку на сотрудника
                        var sUrl = Alfresco.constants.PROXY_URI + "/lecm/orgstructure/api/getStaffEmployeeLink?nodeRef=" + staffRow.nodeRef;
                        Alfresco.util.Ajax.jsonGet({
                            url: sUrl,
                            successCallback: {
						        fn: function (response) {
                                    var oResult = response.json;
                                    if (oResult) {
                                        var hasNoActiveAbsences = this.checkEmployeeHasNoActiveAbsences(oResult.employee, this.msg('message.employee.position.delete.failure.absence')+'\n');
                                        if (hasNoActiveAbsences && !hasNoActiveAbsences.hasNoActiveAbsences){
                                            return;
                                        }
                                        var onPrompt = function (fnAfterPrompt) {
                                            Alfresco.util.PopupManager.displayPrompt({
                                                title: this.msg("message.employee.position.delete.title"),
                                                text: this.msg("message.employee.position.delete.prompt",
                                                    staffRow.itemData["assoc_lecm-orgstr_element-member-employee-assoc"].displayValue,
                                                    staffRow.itemData["assoc_lecm-orgstr_element-member-position-assoc"].displayValue,
                                                    subnitRowName),
                                                buttons:[
                                                    {
                                                        text: this.msg("button.employee.remove"),
                                                        handler: function DataGridActions__onActionDelete_delete() {
                                                            this.destroy();
                                                            fnAfterPrompt.call(this, [oResult]);
                                                        }
                                                    },
                                                    {
                                                        text: this.msg("button.cancel"),
                                                        handler: function DataGridActions__onActionDelete_cancel() {
                                                            this.destroy();
                                                        },
                                                        isDefault:true
                                                    }
                                                ]
                                            });
                                        };

                                        if (("" + oResult.is_primary) == "true") {
                                            var sUrl = Alfresco.constants.PROXY_URI + "/lecm/orgstructure/api/getEmployeePositions?nodeRef=" + oResult.employee;
                                            Alfresco.util.Ajax.jsonGet({
                                                url: sUrl,
                                                successCallback: {
                                                    fn: function (response) {
                                                        var oResults = response.json;
                                                        if (oResults && oResults.length > 1) { // нельзя удалять руководящую должность, пока есть другие должности
                                                            Alfresco.util.PopupManager.displayMessage({
                                                                text: this.msg("message.employee.position.delete.failure.primary")
                                                            });
                                                        } else { // удаляем! вызов метода из грида
                                                            this.onDelete([oResult], owner, {fullDelete:true, trash:false, successMessage: "message.employee.position.delete.success"}, fnDeleteComplete, onPrompt);
                                                        }
                                                    },
	                                                scope: this
                                                },
                                                failureMessage: this.msg("message.employee.position.delete.failure")
                                            });
                                        } else {
                                            this.onDelete([oResult], owner, {fullDelete:true, trash:false, successMessage: "message.employee.position.delete.success"}, fnDeleteComplete, onPrompt);
                                        }
                                    } else {
                                        Alfresco.util.PopupManager.displayMessage({
                                            text: this.msg("message.employee.position.delete.failure")
                                        });
                                    }
                                },
                                scope: this
                            },
                            failureMessage: this.msg("message.employee.position.delete.failure")
                        });
                    },
                    scope: this
                },
                failureMessage: this.msg("message.employee.position.delete.failure.primary")
            });
        },
        makeJquerySyncRequestForAbsence: function _makeJquerySyncRequestForAbsence(url, payload, showMessage, comment ){
            var result = {};

            result.hasNoActiveAbsences = false;

            // Yahoo UI не умеет синхронный (блокирующий) AJAX. Придется использовать jQuery
            jQuery.ajax({
                url: Alfresco.constants.PROXY_URI_RELATIVE + url,
                type: "POST",
                timeout: 30000, // 30 секунд таймаута хватит всем!
                async: false, // ничего не делаем, пока не отработал запром
                dataType: "json",
                contentType: "application/json",
                data: YAHOO.lang.JSON.stringify(payload), // jQuery странно кодирует данные. пусть YUI эаймеся преобразованием в JSON
                processData: false, // данные не трогать, не кодировать вообще
                success: function (response, textStatus, jqXHR) {
                    if (response && response.hasNoActiveAbsences) {
                        result.hasNoActiveAbsences = true;
                    } else {
                        result.hasNoActiveAbsences = false;
                        result.reason = response.reason;
                    }
                },
                error: function(jqXHR, textStatus, errorThrown) {
                    result.hasNoActiveAbsences = false;
                    result.errorText = textStatus;
                }
            });

            if (showMessage){
                if (result.errorText){
                    Alfresco.util.PopupManager.displayMessage(
                        {
                            text:result.errorText
                        });
                }else{
                    if ( !result.hasNoActiveAbsences && result.reason){
                        Alfresco.util.PopupManager.displayMessage(
                            {
                                text:  comment + result.reason
                            });
                    }
                }
            }

            return result;
        },
        /**
         * Синхронная проверка того, что сотрудник не оформлял отсутствие
         *
         * @param {Object} employeeNodeRef - NodeRef проверяемого сотрудника
         * @returns {Object} hasNoActiveAbsences = true - если на данный момент на сотрудника не оформлено отсутствие. Иначе false
         * @private
         */
        checkEmployeeHasNoActiveAbsences: function _сheckEmployeeHasNoActiveAbsences (employeeNodeRef, comment){
            return this.makeJquerySyncRequestForAbsence(
                "lecm/orgstructure/api/employeeHasNoAbsences",
                { nodeRef : employeeNodeRef },
                true,
                comment);
        },
        checkMakeBossHasNoActiveAbsences: function _сheckMakeBossHasNoActiveAbsences (staffNodeRef, comment){

            return this.makeJquerySyncRequestForAbsence(
                "lecm/orgstructure/api/makeBossHasNoAbsences",
                { nodeRef : staffNodeRef },
                true,
                comment);
        },
        onActionMakeBoss: function DataGridActions_onActionMakeBoss(p_item, owner, actionsConfig, fnCallback) {
            var staffRow = p_item;
            // Получаем для штатного расписания ссылку на сотрудника
            var sUrl = Alfresco.constants.PROXY_URI + "/lecm/orgstructure/api/getStaffEmployeeLink?nodeRef=" + staffRow.nodeRef;
            Alfresco.util.Ajax.jsonGet({
		        url: sUrl,
		        successCallback: {
                    fn: function (response) {
                        var oResult = response.json;
                        if (oResult) {
                            Alfresco.util.PopupManager.displayPrompt({
                                title: this.msg("message.position.boss.title"),
                                text: this.msg("message.position.boss.prompt", staffRow.itemData["assoc_lecm-orgstr_element-member-position-assoc"].displayValue),
                                buttons:[
                                    {
                                        text: this.msg("button.position.makeBoss"),
                                        handler: function DataGridActions__onActionMakeBoss_make() {
                                            this.destroy();

                                            var hasNoActiveAbsences = this.checkMakeBossHasNoActiveAbsences(
                                                staffRow.nodeRef /*oResult.employee*/,
                                                this.msg('message.employee.position.primary.add.failure.absence') +'\n');

                                            if (!(hasNoActiveAbsences && hasNoActiveAbsences.hasNoActiveAbsences)){
                                                return;
                                            }

                                            Alfresco.util.Ajax.jsonPost({
                                                url: Alfresco.constants.PROXY_URI + "/lecm/orgstructure/action/makeBoss",
                                                dataObj: {
                                                    nodeRef: staffRow.nodeRef
                                                },
                                                successCallback: {
                                                    fn: function (response) {
	                                                    YAHOO.Bubbling.fire("datagridRefresh", {
		                                                    bubblingLabel: this.options.bubblingLabel
	                                                    });
	                                                    Alfresco.util.PopupManager.displayMessage({
		                                                    text: this.msg("message.position.boss.success")
	                                                    });
                                                    },
                                                    scope:this
                                                },
                                                failureMessage: this.msg("message.position.boss.failure")
                                            });
                                        }
                                    },
                                    {
                                        text: this.msg("button.cancel"),
                                        handler: function DataGridActions__onActionDelete_cancel() {
                                            this.destroy();
                                        },
                                        isDefault:true
                                    }
                                ]
                            });
                        } else {
                            Alfresco.util.PopupManager.displayMessage({
                                text: this.msg("message.position.boss.failure")
                            });
                        }
			        },
			        scope: this
                },
                failureMessage: this.msg("message.position.boss.failure")
            });
        },
        // Переопределяем метод onActionDelete. Добавляем проверки
        onActionDelete: function DataGridActions_onActionDelete(p_items, owner, actionsConfig, fnDeleteComplete) {
            var me = this;
            var	items = YAHOO.lang.isArray(p_items) ? p_items : [p_items];
            var deletedUnit = items[0]; // для штатного расписания одновременно удалить можно ТОЛЬКО ОДНУ должность
            if (deletedUnit.itemData["prop_lecm-orgstr_staff-list-is-boss"].value == false) {
                this.onDelete(p_items, owner, actionsConfig, fnDeleteComplete, null);
            } else {
                //Получаем подразделение сотрудника
                var sUrl = Alfresco.constants.PROXY_URI + "/lecm/orgstructure/api/getStaffPositionUnit?nodeRef=" + deletedUnit.nodeRef;
                Alfresco.util.Ajax.jsonGet({
					url: sUrl,
                    successCallback: {
                        fn: function (response) {
                            var oResults = response.json;
                            if (oResults && oResults.nodeRef) {
                                //Получаем все должности подразделения
                                var sUrl = Alfresco.constants.PROXY_URI + "/lecm/orgstructure/api/getUnitStaffPositions?nodeRef=" + oResults.nodeRef;
                                Alfresco.util.Ajax.jsonGet({
                                    url: sUrl,
                                    successCallback: {
                                        fn: function (response) {
                                            var oResults = response.json;
                                            if (oResults && oResults.length > 1) { // нельзя удалять руководящую должность, пока есть другие должности
                                                Alfresco.util.PopupManager.displayMessage({
                                                    text: this.msg("message.delete.staff-lest.failure.boss")
                                                });
                                            } else { // удаляем! вызов метода из грида
                                                this.onDelete(p_items, owner, actionsConfig, fnDeleteComplete, null);
                                            }
                                        },
                                        scope: this
                                    },
                                    failureMessage: this.msg("message.delete.staff-lest.error")
                                });
                            } else {
                                Alfresco.util.PopupManager.displayMessage({
                                    text: this.msg("message.delete.staff-lest.error")
                                });
                            }
                        },
                        scope: this
                    },
                    failureMessage: this.msg("message.delete.staff-lest.error")
                });
            }
        }
    });

})();
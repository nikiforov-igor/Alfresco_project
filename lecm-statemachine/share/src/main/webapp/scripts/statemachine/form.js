/**
 * LogicECM root namespace.
 *
 * @namespace LogicECM
 */
// Ensure LogicECM root object exists
if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

/**
 * LogicECM top-level module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module
 */
LogicECM.module = LogicECM.module || {};

(function () {
	/**
	 * YUI Library aliases
	 */
	var Dom = YAHOO.util.Dom,
		Event = YAHOO.util.Event;

	/**
	 * StartWorkflow constructor.
	 *
	 * @param {String} htmlId The HTML id of the parent element
	 * @return {Alfresco.component.StartWorkflow} The new StartWorkflow instance
	 * @constructor
	 */
	LogicECM.module.StartWorkflow = function StartWorkflow_constructor(htmlId) {
		var module = LogicECM.module.StartWorkflow.superclass.constructor.call(this, "LogicECM.module.StartWorkflow", htmlId, ["button"]);
		YAHOO.Bubbling.on("objectFinderReady", module.onObjectFinderReady, module);
		YAHOO.Bubbling.on("formContentReady", module.onStartWorkflowFormContentReady, module);

		return module;
	};

	YAHOO.extend(LogicECM.module.StartWorkflow, Alfresco.component.Base, {
		selectedItem:null,
		taskId:null,
        assignee: null,
		onObjectFinderReady:function StartWorkflow_onObjectFinderReady(layer, args) {
			var objectFinder = args[1].eventGroup;
			if (objectFinder.options.field == "assoc_packageItems") {
				objectFinder.selectItems(this.selectedItem);
			} else if (this.assignee != null && (objectFinder.options.field == "assoc_bpm_assignee" || objectFinder.options.field == "assoc_bpm_groupAssignee")) {
                objectFinder.selectItems(this.assignee);
            }
		},

		show:function showWorkflowForm(type, nodeRef, workflowId, taskId, actionId, assignee, errors, fields, label) {
            this.assignee = assignee;
            if (errors.length > 0) {
                viewDialog = new LogicECM.module.EditFieldsConfirm("confirm-edit-fields");
                viewDialog.show(nodeRef, label, errors, fields);
                return;
            }
			if (workflowId != null && workflowId != 'null') {
				this.selectedItem = nodeRef;
				var templateUrl = Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form?itemKind={itemKind}&itemId={itemId}&destination={destination}&mode={mode}&submitType={submitType}&formId={formId}&showCancelButton=true";
				templateUrl = YAHOO.lang.substitute(templateUrl, {
					itemKind:"workflow",
					itemId:workflowId,
					mode:"create",
					submitType:"json",
					formId:"workflow-form"
				});

				new Alfresco.module.SimpleDialog("workflow-form").setOptions({
					width:"60em",
					templateUrl:templateUrl,
					actionUrl:null,
					destroyOnHide:true,
					onSuccess:{
						fn:function (response) {
							this._chooseState(type, taskId, response.json.persistedObject, actionId);
						},
						scope:this
					}
				}).show();
			} else {
                var me = this;
                Alfresco.util.PopupManager.displayPrompt(
                    {
                        title: "Выполнение действия",
                        text: "Подтвердите выполнение для этого документа действия \"" + label + "\"",
                        buttons: [
                            {
                                text: "Ок",
                                handler: function dlA_onAction_action()
                                {
                                    this.destroy();
                                    me._chooseState("trans", taskId, null, actionId);
                                }
                            },
                            {
                                text: "Отмена",
                                handler: function dlA_onActionDelete_cancel()
                                {
                                    this.destroy();
                                },
                                isDefault: true
                            }]
                    });
			}
		},

		_chooseState:function (type, taskId, formResponse, actionId) {
			var url = Alfresco.constants.PROXY_URI + "lecm/statemachine/choosestate?actionType={actionType}&taskId={taskId}&formResponse={formResponse}&actionId={actionId}";
			url = YAHOO.lang.substitute(url, {
				actionType: type,
				taskId:taskId,
				formResponse: encodeURIComponent(formResponse),
				actionId: actionId ? actionId : ""
			});
			callback = {
				success:function (oResponse) {
					document.location.reload();
				},
				argument:{
					contractsObject:this
				},
				timeout:7000
			};
			YAHOO.util.Connect.asyncRequest('GET', url, callback);
		},

		/**
		 * Called when a workflow form has been loaded.
		 * Will insert the form in the Dom.
		 *
		 * @method onWorkflowFormLoaded
		 * @param response {Object}
		 */
		onWorkflowFormLoaded:function StartWorkflow_onWorkflowFormLoaded(response) {
			var formEl = Dom.get(this.id + "-workflowFormContainer");
			Dom.addClass(formEl, "hidden");
			formEl.innerHTML = response.serverResponse.responseText;
		},

		/**
		 * Event handler called when the "formContentReady" event is received
		 */
		onStartWorkflowFormContentReady:function FormManager_onStartWorkflowFormContentReady(layer, args) {
			var formEl = Dom.get(this.id + "-workflowFormContainer");
			Dom.removeClass(formEl, "hidden");
		}

	});
})();

(function () {
    LogicECM.module.EditFieldsConfirm = function EditFieldsConfirm_constructor(htmlId) {
        var module = LogicECM.module.EditFieldsConfirm.superclass.constructor.call(this, "LogicECM.module.EditFieldsConfirm", htmlId, ["button"]);
        return module;
    };

    YAHOO.extend(LogicECM.module.EditFieldsConfirm, Alfresco.component.Base, {
        show: function showEditFieldsConfirm(nodeRef, label, errors, fields) {
            var containerDiv = document.createElement("div");
            var form = '<div id="confirm-edit-fields-form-container" class="yui-panel">' +
                '<div id="confirm-edit-fields-head" class="hd">Ошибка действия "' + label + '"</div>' +
                '<div id="confirm-edit-fields-body" class="bd">' +
                '<div id="confirm-edit-fields-content" class="form-container"><div class="form-fields" style="padding: 1em">Выполнение действия не возможно.<br/>';
                for (var i = 0; i < errors.length; i++) {
                    form += errors[i];
                    form += "<br/>";
                }

                form += '</div></div>' +
                '<div class="bdft">' +
                '<span id="confirm-edit-fields-edit" class="yui-button yui-push-button">' +
                '<span class="first-child">' +
                '<button id="confirm-edit-fields-edit-button" type="button" tabindex="0">Редактировать</button>' +
                '</span>' +
                '</span>' +
                '<span id="confirm-edit-fields-cancel" class="yui-button yui-push-button">' +
                '<span class="first-child">' +
                '<button id="confirm-edit-fields-cancel-button" type="button" tabindex="0">Отмена</button>' +
                '</span>' +
                '</span>' +
                '</div>' +
                '</div>' +
                '</div>';
            containerDiv.innerHTML = form;
            this.dialog = Alfresco.util.createYUIPanel(Dom.getFirstChild(containerDiv),
                {
                    width: "30em"
                });
            Dom.setStyle("confirm-edit-fields-form-container", "display", "block");
            this.dialog.show();
            var button = document.getElementById("confirm-edit-fields-cancel-button");
            button.onclick = function() {
                this.dialog.hide();
            }.bind(this);
            button = document.getElementById("confirm-edit-fields-edit-button");
            button.onclick = function() {
                this.dialog.hide();
                var templateUrl = Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form?itemKind={itemKind}&itemId={itemId}&destination={destination}&mode={mode}&submitType={submitType}&formId={formId}&showCancelButton=true&fields={fields}";
                templateUrl = YAHOO.lang.substitute(templateUrl, {
                    itemKind:"node",
                    itemId: nodeRef,
                    mode:"edit",
                    submitType:"json",
                    formId: "",
                    fields: JSON.stringify(fields)
                });
                new Alfresco.module.SimpleDialog("statemachine-editor-new-status").setOptions({
                    width:"60em",
                    templateUrl:templateUrl,
                    actionUrl:null,
                    destroyOnHide:true,
                    doBeforeDialogShow:{
                        fn: function(p_form, p_dialog) {
                            var fileSpan = '<span class="light">Заголовок</span>';
                            Alfresco.util.populateHTML(
                                [ p_dialog.id + "-form-container_h", fileSpan]
                            );
                        },
                        scope: this
                    },
                    onSuccess:{
                        fn:function (response) {
                            document.location.reload();
                        },
                        scope:this
                    }
                }).show();
            }.bind(this);
        }

    });

})();

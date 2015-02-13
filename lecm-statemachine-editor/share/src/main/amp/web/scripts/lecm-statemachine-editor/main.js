/**
 * LogicECM root namespace.
 *
 * @namespace LogicECM
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

LogicECM.module.StatemachineEditorHandler = LogicECM.module.StatemachineEditorHandler || {};

/**
 *
 * @namespace LogicECM.module
 * @class LogicECM.module.StatemachineEditor
 */
(function () {

	var Bubbling = YAHOO.Bubbling;
	var Event = YAHOO.util.Event;

	LogicECM.module.StatemachineEditorHandler.restoreVersion = function() {};

	LogicECM.module.StatemachineEditor = function (htmlId) {

		var module = LogicECM.module.StatemachineEditor.superclass.constructor.call(
			this,
			"LogicECM.module.StatemachineEditor",
			htmlId,
			["button", "container", "connection", "json", "selector"]);

		LogicECM.module.StatemachineEditorHandler.restoreVersion = module.restoreStatemachineVersion.bind(module);

		return module;
	};

	YAHOO.extend(LogicECM.module.StatemachineEditor, Alfresco.component.Base, {
		statemachineId: null,
		packageNodeRef: null,
		machineNodeRef: null,
		versionsNodeRef: null,
		isFinalizeToUnit: null,
		layout: null,
		startActionsMenu: null,
		userActionsMenu: null,
		transitionActionsMenu: null,
		endActionsMenu: null,
		currentStatus: null,
		splashScreen: null,
		currentTaskName: null,
		versionsForm: null,
		listeners: [],
		options:{},

		setStatemachineId: function(statemachineId) {
			this.statemachineId = statemachineId;
		},

		draw: function () {
			this._clearListeners();
			var el = document.getElementById("statuses-cont");
			el.innerHTML = "";

			this._showSplash();
			var sUrl = Alfresco.constants.PROXY_URI + "lecm/statemachine/editor/process?statemachineId=" + this.statemachineId;
			var callback = {
				success:function (oResponse) {
					oResponse.argument.parent._hideSplash();
					var oResults = eval("(" + oResponse.responseText + ")");
					oResponse.argument.parent.packageNodeRef = oResults.packageNodeRef;
					oResponse.argument.parent.machineNodeRef = oResults.machineNodeRef;
					oResponse.argument.parent.versionsNodeRef = oResults.versionsNodeRef;
					oResponse.argument.parent.isFinalizeToUnit = oResults.isFinalizeToUnit;
					oResponse.argument.parent._drawElements(el, oResults.statuses);
					var sUrl = Alfresco.constants.PROXY_URI + "/lecm/statemachine/editor/diagram?statemachineNodeRef={statemachineNodeRef}&type=diagram";
					sUrl = YAHOO.lang.substitute(sUrl, {
						statemachineNodeRef: oResults.packageNodeRef
					});
					var diagram = document.getElementById("diagram");
					diagram.src = sUrl;
				},
				argument:{
					parent: this
				},
				timeout: 60000
			};
			YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
		},

		_clearListeners: function() {
			var obj;
			while (this.listeners.length > 0) {
				obj = this.listeners.pop();
				Event.removeListener(obj.element, obj.event);
			}
		},

		_drawElements: function(rootElement, statusesModel) {
			var table = document.createElement("table");
			table.className = "lecm_tbl";
			table.width = "100%";
			table.cellPadding = 3;
			table.cellSpacing = 1;
			table.border = 0;

			var tr = document.createElement("tr");
			var td = document.createElement("td");
			td.rowSpan = 2;
			td.className = "lecm_tbl_td_h";
			td.innerHTML = Alfresco.util.message("label.status");
			tr.appendChild(td);

			td = document.createElement("td");
			td.colSpan = 3;
			td.className = "lecm_tbl_td_h";
			td.innerHTML = Alfresco.util.message("label.transitions");
			tr.appendChild(td);

			td = document.createElement("td");
			td.rowSpan = 2;
			td.className = "lecm_tbl_td_h";
			td.innerHTML = Alfresco.util.message("label.actions");
			tr.appendChild(td);

			table.appendChild(tr);

			tr = document.createElement("tr");
			td = document.createElement("td");
			td.className = "lecm_tbl_td_h";
			td.innerHTML = Alfresco.util.message("label.transition_type");
			tr.appendChild(td);

			td = document.createElement("td");
			td.className = "lecm_tbl_td_h";
			td.innerHTML = Alfresco.util.message("label.expression");
			tr.appendChild(td);

			td = document.createElement("td");
			td.className = "lecm_tbl_td_h";
			td.innerHTML = Alfresco.util.message("label.status");
			tr.appendChild(td);

			table.appendChild(tr);

			rootElement.appendChild(table);

			for (var i = 0; i < statusesModel.length; i++) {
				this._createElement(table, statusesModel[i], i % 2 == 0 ? "odd" : "even");
			}

		},

		_createElement: function(table, model, parity) {
			var tr = document.createElement("tr");
			var td = document.createElement("td");
			td.className = "lecm_tbl_td_" + parity;
			td.innerHTML = model.name;
			td.rowSpan = model.transitions.length > 1 ? model.transitions.length : 1;
			tr.appendChild(td);

			if (model.transitions.length == 0) {
				td = document.createElement("td");
				td.className = "lecm_tbl_td_" + parity;
				td.colSpan = 3;
				td.style.textAlign = "center";
				td.innerHTML = "--";
				tr.appendChild(td);
			} else {
				this._addTransition(tr, model.transitions[0], parity);
			}

			td = document.createElement("td");
			td.className = "lecm_tbl_td_" + parity;
			td.style.textAlign = "center";

			if (model.type == "start") {
				var edit = document.createElement("a");
				edit.className = "lecm_tbl_action_edit";
				edit.innerHTML = "&nbsp;&nbsp;&nbsp;&nbsp;";
				td.appendChild(edit);

				if (Event.addListener(edit, "click", this.editAlternativeStarts, null, this)) {
					this.listeners.push({
						element: edit,
						event: "click"
					});
				}
			}


			if (model.type == "default" || model.type == "normal") {
				var edit = document.createElement("a");
				edit.className = "lecm_tbl_action_edit";
				edit.innerHTML = "&nbsp;&nbsp;&nbsp;&nbsp;";
				td.appendChild(edit);

//                me._editStatus(model.nodeRef, model.forDraft, model.type, model.name);
				if(Event.addListener(edit, "click", this._editStatus, model, this)) {
					this.listeners.push({
						element: edit,
						event: "click"
					});
				}
			}

			if (model.type == "normal") {
				var span = document.createElement("span");
				span.innerHTML = "&nbsp;&nbsp;";
				td.appendChild(span);

				var del = document.createElement("a");
				del.className = "lecm_tbl_action_delete";
				del.innerHTML = "&nbsp;&nbsp;&nbsp;&nbsp;";
				td.appendChild(del);
				tr.appendChild(td);

				if (Event.addListener(del, "click", this._deleteStatusPrompt, model, this)) {
					this.listeners.push({
						element: del,
						event: "click"
					});
				}
			}

			td.rowSpan = model.transitions.length > 1 ? model.transitions.length : 1;
			tr.appendChild(td);

			table.appendChild(tr);

			if (model.transitions.length > 1) {
				for (var i = 1; i < model.transitions.length; i++) {
					tr = document.createElement("tr");
					this._addTransition(tr, model.transitions[i], parity);
					table.appendChild(tr);
				}
			}
			tr = document.createElement("tr");
			td = document.createElement("td");
			td.colSpan = 5;
			td.className = "lecm_tbl_td_empty";
			tr.appendChild(td);
			table.appendChild(tr);
		},

		_addTransition: function (trEl, transition, parity) {
				var td = document.createElement("td");
				td.className = "lecm_tbl_td_" + parity;
				td.innerHTML = transition.label;
				trEl.appendChild(td);
				td = document.createElement("td");
				td.className = "lecm_tbl_td_" + parity;
				td.innerHTML = transition.exp;
				trEl.appendChild(td);
				td = document.createElement("td");
				td.className = "lecm_tbl_td_" + parity;
				td.innerHTML = transition.status;
				trEl.appendChild(td);
		},

		_createStatus: function() {
			this._createTask("lecm-stmeditor:taskStatus");
		},

		_createEndEvent: function() {
			this._createTask("lecm-stmeditor:endEvent");
		},

		_createTask: function(itemId) {
			var templateUrl = Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form?itemKind={itemKind}&itemId={itemId}&destination={destination}&mode={mode}&submitType={submitType}&formId={formId}&showCancelButton=true";
			templateUrl = YAHOO.lang.substitute(templateUrl, {
					itemKind:"type",
					itemId: itemId,
					destination: this.packageNodeRef,
					mode:"create",
					submitType:"json",
					formId:"statemachine-editor-new-status"
				});
			this._showSplash();
			new Alfresco.module.SimpleDialog("statemachine-editor-new-status").setOptions({
				width:"40em",
				templateUrl:templateUrl,
				actionUrl:null,
				destroyOnHide:true,
				doBeforeDialogShow:{
					fn: function(p_form, p_dialog) {
						this._hideSplash();
						this._setFormDialogTitle(p_form, p_dialog, itemId == "lecm-stmeditor:taskStatus" ? Alfresco.util.message("label.new_status") : Alfresco.util.message("label.new_final_state"));
					},
					scope: this
				},
				onSuccess:{
					fn:function (response) {
						this.draw();
					},
					scope:this
				}
			}).show();
		},

		_setFormDialogTitle:function (p_form, p_dialog, message) {
			// Dialog title
			var fileSpan = '<span class="light">' + message + '</span>';
			Alfresco.util.populateHTML(
				[ p_dialog.id + "-form-container_h", fileSpan]
			);
			//Destructor
			p_dialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, {moduleId: p_dialog.id, force: true}, this);
		},
		_deleteStatus: function(nodeRef) {
			var sUrl = Alfresco.constants.PROXY_URI + "/lecm/statemachine/editor/status?nodeRef={nodeRef}";
			sUrl = YAHOO.lang.substitute(sUrl, {
				nodeRef: nodeRef
			});
			this._showSplash();
			var callback = {
				success:function (oResponse) {
					oResponse.argument.parent._hideSplash();
					oResponse.argument.parent.draw();
				},
				argument:{
					parent: this
				},
				timeout: 60000
			};
			YAHOO.util.Connect.asyncRequest('DELETE', sUrl, callback);
		},

		_deleteStatusPrompt: function(event, obj) {
			var me = this;
			var model = obj;
			Alfresco.util.PopupManager.displayPrompt({
				title: Alfresco.util.message("title.delete_state"),
				text: Alfresco.util.message("message.state_delete_confirmation", this.name, {0:model.name}),
				buttons: [
					{
						text: Alfresco.util.message("button.remove"),
						handler: function dlA_onActionDelete_delete() {
							this.destroy();
							me._deleteStatus(model.nodeRef);
						}
					},{
						text: Alfresco.util.message("button.cancel"),
						handler: function dlA_onActionDelete_cancel() {
							this.destroy();
						},
						isDefault: true
					}]
			});
		},

		_deployStatemachine: function() {
			var commentConfirm = new LogicECM.module.CommentConfirm();
			var me = this;
			commentConfirm.setOptions({
				title: Alfresco.util.message("title.publish_new_version"),
				fieldTitle: Alfresco.util.message("label.new_version_comments"),
				onSave: function save_deployComment(comment) {
					var sUrl = Alfresco.constants.PROXY_URI + "lecm/statemachine/editor/diagram";
					var data = "statemachineNodeRef={statemachineNodeRef}&type=deploy&comment={comment}";
					data = YAHOO.lang.substitute(data, {
						statemachineNodeRef: me.packageNodeRef,
						comment: comment
					});
					me._showSplash();
					var callback = {
						success:function (oResponse) {
							oResponse.argument.parent._hideSplash();
							Alfresco.util.PopupManager.displayMessage(
								{
									text: Alfresco.util.message("msg.statemachine_deployed_successfully"),
									displayTime: 3
								});
						},
						argument:{
							parent: me
						},
						timeout: 60000
					};
					YAHOO.util.Connect.asyncRequest('GET', sUrl + "?" + encodeURI(data), callback);
				}
			});
			commentConfirm.show();
		},

		showVersions: function showVersions_function() {
			var templateUrl = Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form?itemKind={itemKind}&itemId={itemId}&destination={destination}&mode={mode}&submitType={submitType}&formId={formId}&showCancelButton=true";
			templateUrl = YAHOO.lang.substitute(templateUrl, {
				itemKind:"node",
				itemId: this.versionsNodeRef,
				mode:"edit",
				submitType:"json"
			});

			this._showSplash();

			this.versionsForm = new Alfresco.module.SimpleDialog("statemachine-editor-versions").setOptions({
				width:"60em",
				templateUrl:templateUrl,
				actionUrl:null,
				destroyOnHide:true,
				doBeforeDialogShow:{
					fn: function(p_form, p_dialog) {
						this._hideSplash();
						this._setFormDialogTitle(p_form, p_dialog, Alfresco.util.message("title.statemachine_versions_history"));
					},
					scope: this
				},
				onSuccess:{
					fn:function (response) {
//						this._hideSplash();
					},
					scope:this
				}
			}).show();
		},

		_restoreDefaultStatemachine: function() {
			var me = this;
			Alfresco.util.PopupManager.displayPrompt({
				title: Alfresco.util.message("title.restore_default_statemachine"),
				text: Alfresco.util.message("msg.restore_default_statemachine_confirm"),
				buttons: [
					{
						text: Alfresco.util.message("button.yes"),
						handler: function dlA_onActionDeploy()
						{
							this.destroy();
							var sUrl = Alfresco.constants.PROXY_URI + "/lecm/statemachine/editor/import?default=true&stateMachineId={statemachineId}";
							sUrl = YAHOO.lang.substitute(sUrl, {
								statemachineId: me.statemachineId
							});
							me._showSplash();
							var callback = {
								success:function (oResponse) {
									oResponse.argument.parent._hideSplash();
									Alfresco.util.PopupManager.displayMessage(
										{
											text: Alfresco.util.message("msg.statemashine_restored"),
											displayTime: 3
										});
									document.location.href = Alfresco.constants.URL_CONTEXT + "page/statemachine?statemachineId=" + oResponse.argument.parent.statemachineId;
								},
								argument:{
									parent: me
								},
								timeout: 60000
							};
							YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
						}
					},
					{
						text: Alfresco.util.message("button.no"),
						handler: function dlA_onActionDelete_cancel()
						{
							this.destroy();
						},
						isDefault: true
					}]
			});
		},

		restoreStatemachineVersion: function(item) {
			var me = this;
			var version = item.itemData["prop_lecm-stmeditor_version"].value;
			Alfresco.util.PopupManager.displayPrompt({
				title: Alfresco.util.message("title.restore_statemachine_version"),
				text: Alfresco.util.message("msg.restore_statemachine_version_confirm", this.name, {0:version}),
				buttons: [
					{
						text: Alfresco.util.message("button.yes"),
						handler: function dlA_onActionDeploy()
						{
							this.destroy();
							if (me.versionsForm != null) {
								me.versionsForm.hide();
								me.versionsForm.destroy();
							}
							var sUrl = Alfresco.constants.PROXY_URI + "/lecm/statemachine/editor/import?history=true&stateMachineId={statemachineId}&version={version}&nodeRef={nodeRef}";
							sUrl = YAHOO.lang.substitute(sUrl, {
								statemachineId: me.statemachineId,
								nodeRef: me.machineNodeRef,
								version: version
							});
							me._showSplash();
							var callback = {
								success:function (oResponse) {
									oResponse.argument.parent._hideSplash();
									Alfresco.util.PopupManager.displayMessage(
										{
											text: Alfresco.util.message("msg.statemachine_version_restored"),
											displayTime: 3
										});
									document.location.reload(true);
								},
								argument:{
									parent: me
								},
								timeout: 60000
							};
							YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
						}
					},
					{
						text: Alfresco.util.message("button.no"),
						handler: function dlA_onActionDelete_cancel()
						{
							this.destroy();
						},
						isDefault: true
					}]
			});
		},

		_exportStatemachine: function() {
			var sUrl = Alfresco.constants.PROXY_URI + "lecm/statemachine/editor/export?statusesNodeRef={statusesNodeRef}";
			sUrl = YAHOO.lang.substitute(sUrl, {
				statusesNodeRef: this.packageNodeRef
			});
			document.location.href = sUrl;
		},

		_importStatemachine: function() {
			var Connect = YAHOO.util.Connect;
			Connect.setForm(this.id + '-import-xml-form', true);
			var url = Alfresco.constants.PROXY_URI + "lecm/statemachine/editor/import";
			var fileUploadCallback = {
				upload:function(o){
					if (o.responseText && eval("(" + o.responseText + ")").status.code != 200) {
						Alfresco.util.PopupManager.displayMessage(
							{
								text: Alfresco.util.message("import.error"),
								spanClass: "wait",
								displayTime: 5
							});
						return;
					}

					document.location.reload(true);
				}
			};
			Connect.asyncRequest(Alfresco.util.Ajax.POST, url, fileUploadCallback);
		},

		_editStatus: function(event, obj) {
			var formId = "";
			var model = obj;
			if (model.type == "default" && model.forDraft) {
				formId = "forDraftFormTrue";
			} else if (model.type == "default") {
				formId = "forDraftFormFalse";
			}
			var templateUrl = Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form?itemKind={itemKind}&itemId={itemId}&destination={destination}&mode={mode}&submitType={submitType}&formId={formId}&showCancelButton=true";
			templateUrl = YAHOO.lang.substitute(templateUrl, {
				itemKind:"node",
				itemId: model.nodeRef,
				mode:"edit",
				submitType:"json",
				formId: formId
			});

			this._showSplash();

			new Alfresco.module.SimpleDialog("statemachine-editor-edit-status").setOptions({
				width:"60em",
				templateUrl:templateUrl,
				actionUrl:null,
				destroyOnHide:true,
				doBeforeDialogShow:{
					fn: function(p_form, p_dialog) {
						this._hideSplash();
						this._setFormDialogTitle(p_form, p_dialog, Alfresco.util.message("title.edit_state",this.name, {0:model.name}));
					},
					scope: this
				},
				onSuccess:{
					fn:function (response) {
//						this._hideSplash();
						this.draw();
					},
					scope:this
				}
			}).show();

		},

		_editStatemachine: function() {
			var templateUrl = Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form?itemKind={itemKind}&itemId={itemId}&destination={destination}&mode={mode}&submitType={submitType}&formId={formId}&showCancelButton=true";
			templateUrl = YAHOO.lang.substitute(templateUrl, {
				itemKind: "node",
				itemId: this.machineNodeRef,
				mode: "edit",
				submitType: "json",
				formId: this.isFinalizeToUnit ? "statemachine-editor-edit-statemachine-finalize-to-unit" : "statemachine-editor-edit-statemachine"
			});

			this._showSplash();
			new Alfresco.module.SimpleDialog("statemachine-editor-edit-statemachine").setOptions({
				width:"40em",
				templateUrl:templateUrl,
				actionUrl:null,
				destroyOnHide:true,
				doBeforeDialogShow:{
					fn: function(p_form, p_dialog) {
						this._hideSplash();
						this._setFormDialogTitle(p_form, p_dialog, Alfresco.util.message("title.statemachine_properties"));
					},
					scope: this
				}
			}).show();

		},

		formFieldsOnStatus: function () {
			var templateUrl = Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form?itemKind={itemKind}&itemId={itemId}&destination={destination}&mode={mode}&submitType={submitType}&formId={formId}&showCancelButton=true";
			templateUrl = YAHOO.lang.substitute(templateUrl, {
				itemKind: "node",
				itemId: this.machineNodeRef,
				mode: "edit",
				submitType: "json",
				formId: "statemachine-status-fields"
			});

			this._showSplash();
			new Alfresco.module.SimpleDialog("statemachine-editor-edit-statemachine").setOptions({
				width:"80em",
				templateUrl:templateUrl,
				actionUrl:null,
				destroyOnHide:true,
				doBeforeDialogShow:{
					fn: function(p_form, p_dialog) {
						this._hideSplash();
						this._setFormDialogTitle(p_form, p_dialog, Alfresco.util.message("title.fields_permissions"));
					},
					scope: this
				}
			}).show();
		},

		editAlternativeStarts: function (event, obj) {
			var templateUrl = Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form?itemKind={itemKind}&itemId={itemId}&destination={destination}&mode={mode}&submitType={submitType}&formId={formId}&showCancelButton=true";
			templateUrl = YAHOO.lang.substitute(templateUrl, {
				itemKind: "node",
				itemId: this.machineNodeRef,
				mode: "edit",
				submitType: "json",
				formId: "alternative-start"
			});

			this._showSplash();
			new Alfresco.module.SimpleDialog("statemachine-editor-alternative-start").setOptions({
				width:"80em",
				templateUrl:templateUrl,
				actionUrl:null,
				destroyOnHide:true,
				doBeforeDialogShow:{
					fn: function(p_form, p_dialog) {
						this._hideSplash();
						this._setFormDialogTitle(p_form, p_dialog, Alfresco.util.message("title.alternative_starts"));
					},
					scope: this
				},
				onSuccess:{
					fn:function (response) {
						document.location.reload(true);
					},
					scope:this
				}
			}).show();
		},

		attachmentCategoryPermissions: function() {
			var templateUrl = Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form?itemKind={itemKind}&itemId={itemId}&destination={destination}&mode={mode}&submitType={submitType}&formId={formId}&showCancelButton=true";
			templateUrl = YAHOO.lang.substitute(templateUrl, {
				itemKind: "node",
				itemId: this.machineNodeRef,
				mode: "edit",
				submitType: "json",
				formId: "statemachine-attachment-categories"
			});

			this._showSplash();
			new Alfresco.module.SimpleDialog("statemachine-editor-edit-statemachine").setOptions({
				width:"80em",
				templateUrl:templateUrl,
				actionUrl:null,
				destroyOnHide:true,
				doBeforeDialogShow:{
					fn: function(p_form, p_dialog) {
						this._hideSplash();
						this._setFormDialogTitle(p_form, p_dialog, Alfresco.util.message("title.attachments_permissions"));
					},
					scope: this
				}
			}).show();
		},

		_showSplash: function() {
			this.splashScreen = Alfresco.util.PopupManager.displayMessage(
				{
					text: Alfresco.util.message("label.loading"),
					spanClass: "wait",
					displayTime: 0
				});
		},

		_hideSplash: function() {
			YAHOO.lang.later(2000, this.splashScreen, this.splashScreen.destroy);
		}

	});

})();

(function () {
	var Dom = YAHOO.util.Dom;

	LogicECM.module.CommentConfirm = function CommentConfirm_constructor(htmlId) {
		var module = LogicECM.module.CommentConfirm.superclass.constructor.call(this, "LogicECM.module.CommentConfirm", htmlId, ["button"]);
		return module;
	};

	YAHOO.extend(LogicECM.module.CommentConfirm, Alfresco.component.Base, {

		options: {
			fieldTitle: "Confirm Comment",
			name: "Confirm Comment",
			onSave: null
		},

		show: function showCommentConfirm() {
			var containerDiv = document.createElement("div");
			var panel = '<div id="confirm-comment-form-container" class="yui-panel confirm-comment-panel">' +
				'<div class="hd">' + this.options.title + '</div>' +
				'<div class="bd">' +
				'<div class="form-container">' +
				'<label for="confirm-comment-textarea">' + this.options.fieldTitle+ ':</label>' +
				'<textarea id="confirm-comment-textarea" name="confirm-comment-textarea" rows="9"></textarea>' +
				'</div>' +
				'<div class="form-buttons">' +
				'<span id="confirm-comment-edit" class="yui-button yui-push-button">' +
				'<span class="first-child">' +
				'<button id="confirm-comment-edit-button" type="button" tabindex="0">'+Alfresco.util.message('button.ok')+'</button>' +
				'</span>' +
				'</span>' +
				'<span id="confirm-comment-cancel" class="yui-button yui-push-button">' +
				'<span class="first-child">' +
				'<button id="confirm-comment-cancel-button" type="button" tabindex="0">'+Alfresco.util.message('button.cancel')+'</button>' +
				'</span>' +
				'</span>' +
				'</div>' +
				'</div>' +
				'</div>';
			containerDiv.innerHTML = panel;
			this.dialog = Alfresco.util.createYUIPanel(Dom.getFirstChild(containerDiv),
				{
					width: "30em"
				});
			Dom.setStyle("confirm-comment-form-container", "display", "block");
			this.dialog.show();

			var button = document.getElementById("confirm-comment-cancel-button");
			button.onclick = function() {
				this.dialog.hide();
			}.bind(this);

			button = document.getElementById("confirm-comment-edit-button");
			button.onclick = function() {
				this.dialog.hide();
				if (this.options.onSave != null) {
					var textarea = document.getElementById("confirm-comment-textarea");
					this.options.onSave(textarea.value);
				}
			}.bind(this);
		}

	});

})();

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


/**
 * OrgStructure module.
 *
 * @namespace LogicECM.module
 * @class LogicECM.module.OrgStructure
 */
(function () {

	var Dom = YAHOO.util.Dom
	var Bubbling = YAHOO.Bubbling;
	var Event = YAHOO.util.Event;

	LogicECM.module.StatemachineEditor = function (htmlId) {
		return LogicECM.module.StatemachineEditor.superclass.constructor.call(
			this,
			"LogicECM.module.StatemachineEditor",
			htmlId,
			["button", "container", "connection", "json", "selector"]);
	};

	YAHOO.extend(LogicECM.module.StatemachineEditor, Alfresco.component.Base, {
		statemachineId: null,
		packageNodeRef: null,
		layout: null,
		startActionsMenu: null,
		userActionsMenu: null,
		transitionActionsMenu: null,
		endActionsMenu: null,
		currentStatus: null,
		splashScreen: null,
		options:{},

		setStatemachineId: function(statemachineId) {
			this.statemachineId = statemachineId;
		},

		draw: function () {
			var el = document.getElementById("statuses-cont");
			el.innerHTML = "";

			this._showSplash();
			var sUrl = Alfresco.constants.PROXY_URI + "lecm/statemachine/editor/process?statemachineId=" + this.statemachineId;
			var callback = {
				success:function (oResponse) {
					oResponse.argument.parent._hideSplash();
					var oResults = eval("(" + oResponse.responseText + ")");
					oResponse.argument.parent.packageNodeRef = oResults.packageNodeRef;
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
				timeout: 20000
			};
			YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
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
			td.innerHTML = "Статус";
			tr.appendChild(td);

			td = document.createElement("td");
			td.colSpan = 3;
			td.className = "lecm_tbl_td_h";
			td.innerHTML = "Переходы";
			tr.appendChild(td);

			td = document.createElement("td");
			td.rowSpan = 2;
			td.className = "lecm_tbl_td_h";
			td.innerHTML = "Действия";
			tr.appendChild(td);

			table.appendChild(tr);

			tr = document.createElement("tr");
			td = document.createElement("td");
			td.className = "lecm_tbl_td_h";
			td.innerHTML = "Тип перхода";
			tr.appendChild(td);

			td = document.createElement("td");
			td.className = "lecm_tbl_td_h";
			td.innerHTML = "Условие";
			tr.appendChild(td);

			td = document.createElement("td");
			td.className = "lecm_tbl_td_h";
			td.innerHTML = "Статус";
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

			if (model.editable) {
				var me = this;
				var edit = document.createElement("a");
				edit.className = "lecm_tbl_action_edit";
				edit.innerHTML = "&nbsp;&nbsp;&nbsp;&nbsp;";
				td.appendChild(edit);

				YAHOO.util.Event.addListener(edit, "click", function() {
					me._editStatus(model.nodeRef);
				});

				var span = document.createElement("span");
				span.innerHTML = "&nbsp;&nbsp;";
				td.appendChild(span);

				var del = document.createElement("a");
				del.className = "lecm_tbl_action_delete";
				del.innerHTML = "&nbsp;&nbsp;&nbsp;&nbsp;";
				td.appendChild(del);
				tr.appendChild(td);

				YAHOO.util.Event.addListener(del, "click", function() {

					Alfresco.util.PopupManager.displayPrompt(
						{
							title: "Удаление статуса",
							text: "Вы действительно хотите удалить статус \"" + model.name + "\"",
							buttons: [
								{
									text: "Удалить",
									handler: function dlA_onActionDelete_delete()
									{
										this.destroy();
										me._deleteStatus(model.nodeRef);
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

				});
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
				td.innerHTML = transition.user ? "По действию пользователя" : "По завершению";
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
			var templateUrl = Alfresco.constants.URL_SERVICECONTEXT + "components/form?itemKind={itemKind}&itemId={itemId}&destination={destination}&mode={mode}&submitType={submitType}&formId={formId}&showCancelButton=true";
			templateUrl = YAHOO.lang.substitute(templateUrl, {
					itemKind:"type",
					itemId: "lecm-stmeditor:status",
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
						this._setFormDialogTitle(p_form, p_dialog);
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

		_setFormDialogTitle:function (p_form, p_dialog) {
			// Dialog title
			var message = this.msg("actions.edit");
			var fileSpan = '<span class="light">Статус</span>';
			Alfresco.util.populateHTML(
				[ p_dialog.id + "-form-container_h", fileSpan]
			);
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
				timeout: 20000
			};
			YAHOO.util.Connect.asyncRequest('DELETE', sUrl, callback);
		},

		_deployStatemachine: function() {
			var sUrl = Alfresco.constants.PROXY_URI + "/lecm/statemachine/editor/diagram?statemachineNodeRef={statemachineNodeRef}&type=deploy";
			sUrl = YAHOO.lang.substitute(sUrl, {
				statemachineNodeRef: this.packageNodeRef
			});
			this._showSplash();
			var callback = {
				success:function (oResponse) {
					oResponse.argument.parent._hideSplash();
				},
				argument:{
					parent: this
				},
				timeout: 20000
			};
			YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
		},

		_editStatus: function(nodeRef) {
			var templateUrl = Alfresco.constants.URL_SERVICECONTEXT + "components/form?itemKind={itemKind}&itemId={itemId}&destination={destination}&mode={mode}&submitType={submitType}&formId={formId}&showCancelButton=true";
			templateUrl = YAHOO.lang.substitute(templateUrl, {
				itemKind:"node",
				itemId: nodeRef,
				mode:"edit",
				submitType:"json",
				formId:"statemachine-editor-edit-status"
			});

			this._showSplash();

			new Alfresco.module.SimpleDialog("statemachine-editor-edit-status").setOptions({
				width:"50em",
				templateUrl:templateUrl,
				actionUrl:null,
				destroyOnHide:true,
				doBeforeDialogShow:{
					fn: function(p_form, p_dialog) {
						this._hideSplash();
						this._setFormDialogTitle(p_form, p_dialog);
					},
					scope: this
				},
				onSuccess:{
					fn:function (response) {
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
							timeout: 20000
						};
						YAHOO.util.Connect.asyncRequest('PUT', sUrl, callback);
					},
					scope:this
				}
			}).show();

		},

		_editStatemachine: function() {
			var templateUrl = Alfresco.constants.URL_SERVICECONTEXT + "components/form?itemKind={itemKind}&itemId={itemId}&destination={destination}&mode={mode}&submitType={submitType}&formId={formId}&showCancelButton=true";
			templateUrl = YAHOO.lang.substitute(templateUrl, {
				itemKind: "node",
				itemId: this.packageNodeRef,
				mode: "edit",
				submitType: "json",
				formId: "statemachine-editor-edit-statemachine"
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
						this._setFormDialogTitle(p_form, p_dialog);
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
			YAHOO.lang.later(1000, this.splashScreen, this.splashScreen.destroy);
		}

	});

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	Alfresco.widget.InsituEditor.statemachineEditActions = function (p_params) {
		this.params = YAHOO.lang.merge({}, p_params);

		// Create icons instances
		this.editIcon = new Alfresco.widget.InsituEditorUnitEdit(this, p_params);
		this.deleteIcon = new Alfresco.widget.InsituEditorUnitDelete(this, p_params);
		return this;
	};

	YAHOO.extend(Alfresco.widget.InsituEditor.statemachineEditActions, Alfresco.widget.InsituEditor.textBox,
		{
			doShow:function InsituEditor_textBox_doShow() {
				if (this.contextStyle === null)
					this.contextStyle = Dom.getStyle(this.params.context, "display");
				Dom.setStyle(this.params.context, "display", "none");
				Dom.setStyle(this.editForm, "display", "inline");
			},

			doHide:function InsituEditor_textBox_doHide(restoreUI) {
				if (restoreUI) {
					Dom.setStyle(this.editForm, "display", "none");
					Dom.setStyle(this.params.context, "display", this.contextStyle);
				}
			},

			_generateMarkup:function InsituEditor_textBox__generateMarkup() {
				return;
			}
		});

	Alfresco.widget.InsituEditorUnitEdit = function (p_editor, p_params) {
		this.editor = p_editor;
		this.params = YAHOO.lang.merge({}, p_params);
		this.disabled = p_params.disabled;

		this.editIcon = document.createElement("span");
		this.editIcon.title = Alfresco.util.encodeHTML(p_params.title);
		Dom.addClass(this.editIcon, "insitu-edit-unit");

		this.params.context.appendChild(this.editIcon, this.params.context);
		Event.on(this.params.context, "mouseover", this.onContextMouseOver, this);
		Event.on(this.params.context, "mouseout", this.onContextMouseOut, this);
		Event.on(this.editIcon, "mouseover", this.onContextMouseOver, this);
		Event.on(this.editIcon, "mouseout", this.onContextMouseOut, this);
	};

	YAHOO.extend(Alfresco.widget.InsituEditorUnitEdit, Alfresco.widget.InsituEditorIcon,
		{
			onIconClick:function InsituEditorUnitEdit_onIconClick(e, obj) {
				var context = obj.params;
				if (context.elementType == "status") {
					context.parent._editStatus(context.nodeRef);
				} else if (context.elementType == "action") {
					context.parent._editAction(context.nodeRef);
				}
			}
		});

	Alfresco.widget.InsituEditorUnitDelete = function (p_editor, p_params) {
		this.editor = p_editor;
		this.params = YAHOO.lang.merge({}, p_params);
		this.disabled = p_params.disabled;

		this.editIcon = document.createElement("span");
		this.editIcon.title = Alfresco.util.encodeHTML(p_params.title);
		Dom.addClass(this.editIcon, "insitu-delete-unit");

		this.params.context.appendChild(this.editIcon, this.params.context);
		Event.on(this.params.context, "mouseover", this.onContextMouseOver, this);
		Event.on(this.params.context, "mouseout", this.onContextMouseOut, this);
		Event.on(this.editIcon, "mouseover", this.onContextMouseOver, this);
		Event.on(this.editIcon, "mouseout", this.onContextMouseOut, this);
	};

	YAHOO.extend(Alfresco.widget.InsituEditorUnitDelete, Alfresco.widget.InsituEditorIcon,
		{
			onIconClick: function InsituEditorUnitDelete_onIconClick(e, obj) {
				var context = obj.params;
				if (context.elementType == "status") {
					Alfresco.util.PopupManager.displayPrompt(
						{
							title: "Удаление статуса",
							text: "Вы действительно хотите удалить статус \"" + context.elementName + "\"",
							buttons: [
								{
									text: "Удалить",
									handler: function dlA_onActionDelete_delete()
									{
										this.destroy();
										context.parent._deleteStatus(context.nodeRef);
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
				} else if (context.elementType == "action") {
					Alfresco.util.PopupManager.displayPrompt(
						{
							title: "Удаление действия",
							text: "Вы действительно хотите удалить действие \"" + context.elementName + "\"",
							buttons: [
								{
									text: "Удалить",
									handler: function dlA_onActionDelete_delete()
									{
										this.destroy();
										context.parent._deleteAction(context.nodeRef);
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
			}
		});

})();
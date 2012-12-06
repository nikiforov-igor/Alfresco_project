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
			this.onResize();
			YAHOO.util.Event.on(window, "resize", function(e)
			{
				this.onResize();
			}, this, true);

			this.layout = new YAHOO.widget.Layout(this.id, {
				units: [
					{ position: 'center', body: 'center1', scroll: true }
				]
			});
			this.layout.render();

			this._showSplash();
			var sUrl = Alfresco.constants.PROXY_URI + "lecm/statemachine/editor/actions";
			var callback = {
				success:function (oResponse) {
					oResponse.argument.parent._hideSplash();
					var oResults = eval("(" + oResponse.responseText + ")");
					oResponse.argument.parent.startActionsMenu = new YAHOO.widget.Menu("startActionsMenu");
					oResponse.argument.parent._addMenu(oResponse.argument.parent.startActionsMenu, oResults.start, "start");
					oResponse.argument.parent.userActionsMenu = new YAHOO.widget.Menu("userActionsMenu");
					oResponse.argument.parent._addMenu(oResponse.argument.parent.userActionsMenu, oResults.user, "user");
					oResponse.argument.parent.transitionActionsMenu = new YAHOO.widget.Menu("takeActionsMenu");
					oResponse.argument.parent._addMenu(oResponse.argument.parent.transitionActionsMenu, oResults.transition, "transition");
					oResponse.argument.parent.endActionsMenu = new YAHOO.widget.Menu("endActionsMenu");
					oResponse.argument.parent._addMenu(oResponse.argument.parent.endActionsMenu, oResults.end, "end");
					oResponse.argument.parent._redraw();
				},
				argument:{
					parent: this
				},
				timeout: 20000
			};
			YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);

		},

		_addMenu: function(menu, receivedItems, type) {
			var items = [];
			for (var i = 0; i < receivedItems.length; i++) {
				items.push({
					text: receivedItems[i].title,
					value: receivedItems[i].id
				});
			}
			menu.addItems(items);
			menu.render(document.body);
			menu.subscribe("click", function (p_sType, p_aArgs) {
				this._addAction(p_sType, p_aArgs, type);
			}.bind(this));
		},

		onResize: function() {
			Dom.setStyle(this.id, "height", "");
			var h = Dom.getXY("alf-ft")[1] - Dom.getXY("alf-hd")[1] - Dom.get("alf-hd").offsetHeight - 92;

			if (YAHOO.env.ua.ie === 6)
			{
				var hd = Dom.get("alf-hd"), tmpHeight = 0;
				for (var i = 0, il = hd.childNodes.length; i < il; i++)
				{
					tmpHeight += hd.childNodes[i].offsetHeight;
				}
				h = Dom.get("alf-ft").parentNode.offsetTop - tmpHeight;
			}
			if (h < 200) {
				h = 200;
			}
			Dom.setStyle(this.id, "height", h + "px");
		},

		_redraw: function() {
			var el = this.layout.getUnitByPosition('center').body.firstChild;
			el.innerHTML = "";

			this._showSplash();
			var sUrl = Alfresco.constants.PROXY_URI + "lecm/statemachine/editor/process?statemachineId=" + this.statemachineId;
			var callback = {
				success:function (oResponse) {
					oResponse.argument.parent._hideSplash();
					var oResults = eval("(" + oResponse.responseText + ")");
					oResponse.argument.parent.packageNodeRef = oResults.packageNodeRef;
					oResponse.argument.parent._drawElements(el, oResults.statuses);
				},
				argument:{
					parent: this
				},
				timeout: 20000
			};
			YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
		},

		_drawElements: function(rootElement, statusesModel) {
			var container = this._createContainer(rootElement, "addMenu");
			var addNew = document.createElement('a');
			addNew.id = "new-status";
			addNew.className = "add_status";
			addNew.innerHTML = "Добавить статус";
			container.appendChild(addNew);

			var addNewelement = new YAHOO.util.Element("new-status");
			addNewelement.on("click", this._createStatus.bind(this));

			var editStemachine = document.createElement('a');
			editStemachine.id = "edit-status";
			editStemachine.className = "add_status";
			editStemachine.innerHTML = "Свойства";
			container.appendChild(editStemachine);

			var editStemachineElement = new YAHOO.util.Element("edit-status");
			editStemachineElement.on("click", this._editStatemachine.bind(this));

			var deploy = document.createElement('a');
			deploy.id = "deploy";
			deploy.className = "add_status";
			deploy.innerHTML = "Задеплоить";
			container.appendChild(deploy);

			var deployElement = new YAHOO.util.Element("deploy");
			deployElement.on("click", this._deployStatemachine.bind(this));

			for (var i = 0; i < statusesModel.length; i++) {
				var id = "status-" + i;
				this._createElement(rootElement, id, statusesModel[i]);
			}

		},

		_createContainer: function(parent, containerId) {
			var container = document.createElement("div");
			container.id = containerId;
			parent.appendChild(container);
			Dom.addClass(containerId, "container");
			return container;
		},

		_createElement: function(parent, id, model) {
			var container = this._createContainer(parent, id);
			//status
			var status = document.createElement("div");
			status.id = id + "-status";
			container.appendChild(status);
			Dom.addClass(id + "-status", "status_dis");
			var statusHeader = document.createElement("div");
			statusHeader.innerHTML = "<b>Статус</b>";
			status.appendChild(statusHeader);
			var statusName = document.createElement("div");
			statusName.id = id + "-status-name";
			statusName.innerHTML = model.name;
			status.appendChild(statusName);
			Dom.addClass(statusName.id, "status_name");

			if (!model.editable) {
				return;
			}

			Alfresco.util.createInsituEditor(
				statusName.id,
				{
					showDelay: 300,
					hideDelay: 300,
					type: "statemachineEditActions",
					nodeRef: model.nodeRef,
					elementType: "status",
					elementName: model.name,
					parent: this
				},
				null
			);

			//actions container
			var actions = document.createElement("div");
			actions.id = id + "-actions";
			container.appendChild(actions);
			Dom.addClass(id + "-actions", "actions_cont");

			/************** start Actions *********************/
			this._createActionsElement(actions, id + "-start", model.startActions, this.startActionsMenu, this.msg("statemachine.execution.start"), model.nodeRef, false);

			/************** user Actions *********************/
			this._createActionsElement(actions, id + "-user", model.userActions, this.userActionsMenu, this.msg("statemachine.execution.user"), model.nodeRef, false);

			/************** transition Actions *********************/
			this._createActionsElement(actions, id + "-transition", model.transitionActions, this.transitionActionsMenu, this.msg("statemachine.execution.transition"), model.nodeRef, true);

			/************** end Actions *********************/
			this._createActionsElement(actions, id + "-end", model.endActions, this.endActionsMenu, this.msg("statemachine.execution.end"), model.nodeRef, false);
		},

		_createActionsElement: function (container, id, items, menu, title, statusNodeRef, transitionLabel) {
			//action container header
			var action = document.createElement("div");
			action.id = id + "-action-header";
			container.appendChild(action);
			Dom.addClass(action.id, "action_cont_header");
			//action_name header
			var actionName = document.createElement("div");
			actionName.id = id + "-action-name-header";
			actionName.innerHTML = "<b>" + title +"</b> <a id='" + actionName.id + "-add' class='add_action'>&nbsp;&nbsp;&nbsp;&nbsp;</a>";
			action.appendChild(actionName);
			Dom.addClass(actionName.id, "action_name_header");

			var addAction = new YAHOO.util.Element(actionName.id + "-add");
			addAction.on("click", function (event) {
				this.currentStatus = statusNodeRef;
				menu.moveTo(event.clientX, event.clientY);
				menu.show();
			}.bind(this));

			//action_results header
			var actionResults = document.createElement("div");
			actionResults.id = id + "-action-resilts-header";
			if (transitionLabel) {
				actionResults.innerHTML = "<b>Переход</b>";
			}
			action.appendChild(actionResults);
			Dom.addClass(actionResults.id, "action_results_cont");

			for (var i = 0; i < items.length; i++) {
				var actionModel = items[i];
				//action container
				var action = document.createElement("div");
				action.id = id + "-action-" + i;
				container.appendChild(action);
				Dom.addClass(action.id, "action_cont");
				//action_name
				var actionName = document.createElement("div");
				actionName.id = id + "-action-name-" + i;
				actionName.innerHTML = actionModel.actionName;
				action.appendChild(actionName);
				Dom.addClass(actionName.id, "action_name");

				Alfresco.util.createInsituEditor(
					actionName.id,
					{
						showDelay: 300,
						hideDelay: 300,
						type: "statemachineEditActions",
						nodeRef: actionModel.nodeRef,
						elementType: "action",
						elementName: actionModel.actionName,
						parent: this
					},
					null
				);

				//action_results
				var actionResults = document.createElement("div");
				actionResults.id = id + "-action-resilts-" + i;
				action.appendChild(actionResults);
				Dom.addClass(actionResults.id, "action_results_cont");
				//action result
				for (var j = 0; j < actionModel.transitions.length; j++) {
					var result = document.createElement("div");
					result.innerHTML = actionModel.transitions[j];
					actionResults.appendChild(result);
				}
			}
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
						this._redraw();
					},
					scope:this
				}
			}).show();
		},

		_addAction: function(p_sType, p_aArgs, type) {
			var oEvent = p_aArgs[0];    // DOM Event
			var oMenuItem = p_aArgs[1]; // YAHOO.widget.MenuItem instance
			//statusNodeRef
			//actionId
			var sUrl = Alfresco.constants.PROXY_URI + "/lecm/statemachine/editor/actions?statusNodeRef={statusNodeRef}&actionId={actionId}&type={type}";
			sUrl = YAHOO.lang.substitute(sUrl, {
				statusNodeRef: this.currentStatus,
				actionId: oMenuItem.value,
				type: type
			});
			this._showSplash();
			var callback = {
				success:function (oResponse) {
					oResponse.argument.parent._hideSplash();
					var oResults = eval("(" + oResponse.responseText + ")");
					oResponse.argument.parent._redraw();
				},
				argument:{
					parent: this
				},
				timeout: 20000
			};
			YAHOO.util.Connect.asyncRequest('PUT', sUrl, callback);
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
					oResponse.argument.parent._redraw();
				},
				argument:{
					parent: this
				},
				timeout: 20000
			};
			YAHOO.util.Connect.asyncRequest('DELETE', sUrl, callback);
		},

		_deployStatemachine: function(nodeRef) {
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
						var sUrl = Alfresco.constants.PROXY_URI + "/lecm/statemachine/editor/status?nodeRef={nodeRef}";
						sUrl = YAHOO.lang.substitute(sUrl, {
							nodeRef: nodeRef
						});
						this._showSplash();
						var callback = {
							success:function (oResponse) {
								oResponse.argument.parent._hideSplash();
								oResponse.argument.parent._redraw();
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

		_deleteAction: function(nodeRef) {
			var sUrl = Alfresco.constants.PROXY_URI + "lecm/statemachine/editor/actions?nodeRef={nodeRef}";
			sUrl = YAHOO.lang.substitute(sUrl, {
				nodeRef: nodeRef
			});
			this._showSplash();
			var callback = {
				success:function (oResponse) {
					oResponse.argument.parent._hideSplash();
					oResponse.argument.parent._redraw();
				},
				argument:{
					parent: this
				},
				timeout: 20000
			};
			YAHOO.util.Connect.asyncRequest('DELETE', sUrl, callback);
		},

		_editAction: function(nodeRef) {
			var templateUrl = Alfresco.constants.URL_SERVICECONTEXT + "components/form?itemKind={itemKind}&itemId={itemId}&destination={destination}&mode={mode}&submitType={submitType}&formId={formId}&showCancelButton=true&packageNodeRef={packageNodeRef}";
			templateUrl = YAHOO.lang.substitute(templateUrl, {
				itemKind:"node",
				itemId: nodeRef,
				mode:"edit",
				submitType:"json",
				formId:"statemachine-editor-edit-status",
				packageNodeRef: this.packageNodeRef
			});
			this._showSplash();
			new Alfresco.module.SimpleDialog("statemachine-editor-edit-status").setOptions({
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
						this._redraw();
					},
					scope:this
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
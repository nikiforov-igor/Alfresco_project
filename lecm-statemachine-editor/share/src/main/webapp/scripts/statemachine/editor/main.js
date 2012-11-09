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
	LogicECM.module.StatemachineEditor = function (htmlId) {
		return LogicECM.module.StatemachineEditor.superclass.constructor.call(
			this,
			"LogicECM.module.StatemachineEditor",
			htmlId,
			["button", "container", "connection", "json", "selector"]);
	};

	YAHOO.extend(LogicECM.module.StatemachineEditor, Alfresco.component.Base, {
		messages:null,
		statemachineId: null,
		packageNodeRef: null,
		layout: null,
		menu: null,
		currentStatus: null,
		options:{},

		setMessages:function (messages) {
			this.messages = messages;
		},

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
					{ position: 'bottom', height: 200, body: 'bottom1', resize: true, scroll: true },
					{ position: 'center', body: 'center1', scroll: true }
				]
			});
			this.layout.on('render', function() {
				this._redraw();
			}.bind(this));
			this.layout.render();

			this.menu = new YAHOO.widget.Menu("actionsmenu");
			this.menu.addItems([
				{ text: "Yahoo! Mail", value: "1" },
				{ text: "Yahoo! Address Book", value: "2" },
				{ text: "Yahoo! Calendar", value: "http://calendar.yahoo.com" },
				{ text: "Yahoo! Notepad",  value: "http://notepad.yahoo.com" }
			]);
			this.menu.render(document.body);
			this.menu.subscribe("click", this._addAction.bind(this));
		},

		onResize: function() {
			Dom.setStyle(this.id, "height", "");
			var h = Dom.getXY("alf-ft")[1] - Dom.getXY("alf-hd")[1] - Dom.get("alf-hd").offsetHeight - 50;

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

			var sUrl = Alfresco.constants.PROXY_URI + "lecm/statemachine/editor/process?statemachineId=" + this.statemachineId;
			var callback = {
				success:function (oResponse) {
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
			status.innerHTML = "<b>Статус</b><br/>" + model.name;
			container.appendChild(status);
			Dom.addClass(id + "-status", "status_dis");
			//actions container
			var actions = document.createElement("div");
			actions.id = id + "-actions";
			container.appendChild(actions);
			Dom.addClass(id + "-actions", "actions_cont");

			//action container header
			var action = document.createElement("div");
			action.id = id + "-action-header";
			actions.appendChild(action);
			Dom.addClass(action.id, "action_cont_header");
			//action_name header
			var actionName = document.createElement("div");
			actionName.id = id + "-action-name-header";
			actionName.innerHTML = "<b>Действия</b> <a id='" + actionName.id + "-add' class='add_action'>&nbsp;&nbsp;&nbsp;&nbsp;</a>";
			action.appendChild(actionName);
			Dom.addClass(actionName.id, "action_name");

			var addAction = new YAHOO.util.Element(actionName.id + "-add");
			addAction.on("click", function (event) {
				this.currentStatus = model.nodeRef;
				this.menu.moveTo(event.x, event.y);
				this.menu.show();
			}.bind(this));

			//action_results header
			var actionResults = document.createElement("div");
			actionResults.id = id + "-action-resilts-header";
			actionResults.innerHTML = "<b>Переход</b>";
			action.appendChild(actionResults);
			Dom.addClass(actionResults.id, "action_results_cont");

			for (var i = 0; i < model.actions.length; i++) {
				var actionModel = model.actions[i];
				//action container
				var action = document.createElement("div");
				action.id = id + "-action-" + i;
				actions.appendChild(action);
				Dom.addClass(action.id, "action_cont");
				//action_name
				var actionName = document.createElement("div");
				actionName.id = id + "-action-name-" + i;
				actionName.innerHTML = actionModel.action;
				action.appendChild(actionName);
				Dom.addClass(actionName.id, "action_name");
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
			new Alfresco.module.SimpleDialog("statemachine-editor-new-status").setOptions({
				width:"40em",
				templateUrl:templateUrl,
				actionUrl:null,
				destroyOnHide:true,
				onSuccess:{
					fn:function (response) {
						this._redraw();
					},
					scope:this
				}
			}).show();
		},
		_addAction: function(p_sType, p_aArgs) {
			var oEvent = p_aArgs[0],    // DOM Event
			oMenuItem = p_aArgs[1]; // YAHOO.widget.MenuItem instance
			alert("123");
		}
	});

})();
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
 * Experts module.
 *
 * @namespace LogicECM.module
 * @class LogicECM.module.Experts
 */
(function () {

	var Dom = YAHOO.util.Dom;

	LogicECM.module.Transitions = function (htmlId) {
		var module = LogicECM.module.Transitions.superclass.constructor.call(
			this,
			"LogicECM.module.Transitions",
			htmlId,
			["button", "container", "connection", "json", "selector"]);

		YAHOO.Bubbling.on("afterOptionsSet", module._afterOptionsSet, module);
		return module;
	};

	YAHOO.lang.extend(LogicECM.module.Transitions, Alfresco.component.Base, {
		table:null,
		button:null,
		actionNodeRef: null,
		globalDataCount:0,

		init: function (actionNodeRef) {
			this.actionNodeRef = actionNodeRef;

			var parent = Dom.get(this.id);

			var columnDefs = [
				{ key:"expression", label: this.msg("statemachine.editor.expression"), width: 150 },
				{ key:"transition", label: this.msg("statemachine.editor.transition"), width: 150 }
			];

			var initialSource = new YAHOO.util.DataSource([]);
			initialSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
			initialSource.responseSchema = {fields:["expression", "transition"]};

			this.table = new YAHOO.widget.DataTable(parent, columnDefs, initialSource, {initialLoad:false});

			this._loadTable();
		},

		_afterOptionsSet: function (layer, args) {
			args[1].eventGroup.options.parentNodeRef = "workspace://SpacesStore/467270f5-0e30-436d-ab09-c885c5570a83";
			args[1].eventGroup.options.itemType = "lecm-stmeditor:status";
		},

		_draw:function () {
			var context = this;

			this.button = new YAHOO.widget.Button({
				id:"getExperts",
				type:"button",
				label:this.msg("control.button.get.title"),
				container:"buttons"
			});

			var onButtonClick = function (e) {
				context.loadExperts();
			};

			this.button.on("click", this._addTransition.bind(this));
		},

		_addTransition: function () {
			var templateUrl = Alfresco.constants.URL_SERVICECONTEXT + "components/form?itemKind={itemKind}&itemId={itemId}&destination={destination}&mode={mode}&submitType={submitType}&formId={formId}&showCancelButton=true";
			templateUrl = YAHOO.lang.substitute(templateUrl, {
				itemKind:"type",
				itemId: "lecm-stmeditor:transition",
				destination: this.actionNodeRef,
				mode:"create",
				submitType:"json",
				formId:"statemachine-editor-new-transition"
			});
			new Alfresco.module.SimpleDialog("statemachine-editor-new-transition").setOptions({
				width:"40em",
				templateUrl:templateUrl,
				actionUrl:null,
				destroyOnHide:true,
				doBeforeDialogShow:{
					fn:this._setFormDialogTitle
				},
				onSuccess:{
					fn:function (response) {
						this._redraw();
					},
					scope:this
				}
			}).show();
		},

		_loadTable:function () {
			var sUrl = Alfresco.constants.PROXY_URI + "lecm/statemachine/editor/transitions";
			if (this.actionNodeRef != null) {
				sUrl += "?actionNodeRef=" + encodeURI(this.actionNodeRef);
			}
			var callback = {
				success:function (oResponse) {
					var oResults = eval("(" + oResponse.responseText + ")");
					oResponse.argument.context.table.deleteRows(0);
					oResponse.argument.context.table.addRows(oResults, 0);
					oResponse.argument.context.table.render();
					oResponse.argument.context._draw();
				},
				failure:function (oResponse) {
					alert("Failed to load experts. " + "[" + oResponse.statusText + "]");
				},
				argument:{
					context:this
				}
			};
			YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
		}
	});
})();
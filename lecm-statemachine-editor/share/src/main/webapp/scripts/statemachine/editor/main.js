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
		layout: null,
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
					{ position: 'left', width: 300, body: 'left1', resize: true }, //Width required here
					{ position: 'bottom', height: 100, body: 'bottom1', resize: true }, //Height required here
					{ position: 'center', body: 'center1' }
				]
			});
			this.layout.on('render', function() {
				//var el = this.getUnitByPosition('top').body.firstChild;
			});
			this.layout.render();
		},

		onResize: function() {
			Dom.setStyle(this.id, "height", "");
			var h = Dom.getXY("alf-ft")[1] - Dom.getXY("alf-hd")[1] - Dom.get("alf-hd").offsetHeight - 48;

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
		}
	});

})();
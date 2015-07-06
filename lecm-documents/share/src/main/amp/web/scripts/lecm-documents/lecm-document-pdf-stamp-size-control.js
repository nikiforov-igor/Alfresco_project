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
 * LogicECM top-level control namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.control
 */
LogicECM.module = LogicECM.module || {};

LogicECM.module.Documents = LogicECM.module.Documents || {};

(function () {
	var Dom = YAHOO.util.Dom,
		Event = YAHOO.util.Event;

	LogicECM.module.Documents.StampSizeControl = function (fieldHtmlId) {
		LogicECM.module.Documents.StampSizeControl.superclass.constructor.call(this, "LogicECM.module.Documents.StampSizeControl", fieldHtmlId, [ "container"]);

		return this;
	};

	YAHOO.extend(LogicECM.module.Documents.StampSizeControl, Alfresco.component.Base,
		{
			options: {
				htmlid: "",
			},

			onReady:function () {
				var field = Dom.get(this.options.htmlid + "_prop_lecm-document-stamp_width");
				Event.on(field, 'keyup', this.validate.bind(this));
				field = Dom.get(this.options.htmlid + "_prop_lecm-document-stamp_height");
				Event.on(field, 'keyup', this.validate.bind(this));
				this.validate();
			},

			validate: function() {
				var width = parseInt(Dom.get(this.options.htmlid + "_prop_lecm-document-stamp_width").value);
				var height = parseInt(Dom.get(this.options.htmlid + "_prop_lecm-document-stamp_height").value);
				if (!isNaN(width) && !isNaN(height)) {
					var w96dpi = Math.round(96 * width / 25.4);
					var h96dpi = Math.round(96 * height / 25.4);
					var w300dpi = Math.round(300 * width / 25.4);
					var h300dpi = Math.round(300 * height / 25.4);
					Dom.get(this.id + "-display").innerHTML = w96dpi + "x" + h96dpi + "px";
					Dom.get(this.id + "-print").innerHTML = w300dpi + "x" + h300dpi + "px";
				} else {
					Dom.get(this.id + "-display").innerHTML = "<неопределено>";
					Dom.get(this.id + "-print").innerHTML = "<неопределено>";
				}
			}
		});
})();
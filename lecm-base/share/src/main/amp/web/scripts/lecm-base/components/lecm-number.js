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

/**
 * Number component.
 *
 * @namespace LogicECM
 * @class LogicECM.module.Number
 */
(function ()
{
	/**
	 * YUI Library aliases
	 */
	var Dom = YAHOO.util.Dom,
			Event = YAHOO.util.Event;

	/**
	 * Alfresco Slingshot aliases
	 */
	var $html = Alfresco.util.encodeHTML;

	/**
	 * Number constructor.
	 *
	 * @param {String} htmlId The HTML id of the control element
	 * @return {LogicECM.module.Number} The new Number instance
	 * @constructor
	 */
	LogicECM.module.Number = function (htmlId)
	{
		LogicECM.module.Number.superclass.constructor.call(this, "LogicECM.module.Number", htmlId);

		this.controlId = htmlId;
		YAHOO.Bubbling.on("readonlyControl", this.onReadonlyControl, this);
		YAHOO.Bubbling.on("disableControl", this.onDisableControl, this);
		YAHOO.Bubbling.on("enableControl", this.onEnableControl, this);
		return this;
	};

	YAHOO.extend(LogicECM.module.Number, Alfresco.component.Base,
			{
				controlId:null,
				readonly: false,
				options: {
					formId: null,
					fieldId: null,
                    disabled: false
				},
				onReadonlyControl: function (layer, args) {
					var input, fn;
					if (this.options.formId === args[1].formId && this.options.fieldId === args[1].fieldId) {
						this.readonly = args[1].readonly;
						input = Dom.get(this.controlId);
						if (input) {
							fn = args[1].readonly ? input.setAttribute : input.removeAttribute;
							fn.call(input, "readonly", "");
						}
					}
				},
				onDisableControl: function (layer, args) {
					if (this.options.formId === args[1].formId && this.options.fieldId === args[1].fieldId) {
						var input = Dom.get(this.controlId);
						if (input) {
							input.disabled = true;
							input.value = "";
						}
					}
				},
				onEnableControl: function (layer, args) {
					if (this.options.formId === args[1].formId && this.options.fieldId === args[1].fieldId) {
						if (!this.options.disabled) {
							var input = Dom.get(this.controlId);
							if (input) {
								input.disabled = false;
							}
						}
					}
				},
				onReady: function () {
					LogicECM.module.Base.Util.createComponentReadyElementId(this.id, this.options.formId, this.options.fieldId);
				}

			});
})();

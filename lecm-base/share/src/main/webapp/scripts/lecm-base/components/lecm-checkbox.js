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

(function()
{
	var Dom = YAHOO.util.Dom;

	LogicECM.module.Checkbox = function (fieldHtmlId)
	{
		LogicECM.module.Checkbox.superclass.constructor.call(this, "LogicECM.module.Checkbox", fieldHtmlId, [ "container", "datasource"]);
		this.checkboxId = fieldHtmlId + "-entry";
		return this;
	};

	YAHOO.extend(LogicECM.module.Checkbox, Alfresco.component.Base,
		{
			options:
			{
				fieldId: null,

				disabled: false,

				mode: false,

				defaultValueDataSource: null
			},

			checkboxId: null,

			checkbox: null,

			setOptions: function (obj)
			{
				LogicECM.module.Checkbox.superclass.setOptions.call(this, obj);
				YAHOO.Bubbling.fire("afterOptionsSet",
					{
						eventGroup: this
					});
				return this;
			},

			onReady: function ()
			{
				if (!this.options.disabled && this.options.mode == "create") {
					this.checkbox = Dom.get(this.checkboxId);
					if (this.checkbox) {
						this.loadDefaultValue();
					}
				}
			},

			loadDefaultValue: function AssociationSelectOne__loadDefaultValue() {
				if (this.options.defaultValueDataSource != null) {
					var me = this;
					Alfresco.util.Ajax.request(
						{
							url: Alfresco.constants.PROXY_URI + this.options.defaultValueDataSource,
							successCallback: {
								fn: function (response) {
									var oResults = eval("(" + response.serverResponse.responseText + ")");
									if (oResults != null && oResults.checked != null ) {
										me.checkbox.checked = oResults.checked == "true";
									}
								}
							},
							failureMessage: "message.failure"
						});
				}
			}
		});
})();
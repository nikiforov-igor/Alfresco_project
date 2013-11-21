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

LogicECM.module.FormsEditor = LogicECM.module.FormsEditor || {};

(function() {
	LogicECM.module.FormsEditor.FieldComponent = function (fieldHtmlId)
	{
		LogicECM.module.FormsEditor.FieldComponent.superclass.constructor.call(this, "LogicECM.module.FormsEditor.FieldComponent", fieldHtmlId, [ "container"]);
		this.formIds = [];
		return this;
	};

	YAHOO.extend(LogicECM.module.FormsEditor.FieldComponent, Alfresco.component.Base, {
		options: {
			disabled: false,
			mandatory: false,
			itemNodeRef: null,
			value: null
		},

		targetType: null,
		config: null,

		onReady: function() {
			this.getTargetType();
		},

		getTargetType: function() {
			Alfresco.util.Ajax.jsonGet({
				url:  Alfresco.constants.PROXY_URI + "/lecm/docforms/attribute?nodeRef=" + encodeURIComponent(this.options.itemNodeRef),
				successCallback: {
					fn: function (response) {
						var oResults = response.json;
						if (oResults != null && oResults.targetType != null) {
							this.targetType = oResults.targetType;

							this.loadConfig()
						}
					},
					scope: this
				}
			});
		},

		loadConfig: function() {
			Alfresco.util.Ajax.jsonGet({
				url: Alfresco.constants.URL_SERVICECONTEXT + "/lecm/forms/getConfig?action=getControlsById&typeId=" + encodeURIComponent(this.targetType),
				successCallback: {
					fn: function (response) {
						var oResults = response.json;
						if (oResults != null) {
							this.config = oResults;
							this.applyConfig();
						}
					},
					scope: this
				}
			});
		},

		applyConfig: function() {
			if (this.config != null) {

			}
		}
	});
})();
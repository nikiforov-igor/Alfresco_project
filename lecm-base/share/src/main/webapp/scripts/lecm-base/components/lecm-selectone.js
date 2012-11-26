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

(function() {

    var Dom = YAHOO.util.Dom;
    LogicECM.module.SelectOne = function LogicECM_module_SelectOne(fieldHtmlId)
    {
		LogicECM.module.SelectOne.superclass.constructor.call(this, "LogicECM.module.SelectOne", fieldHtmlId, [ "container", "resize"]);
        return this;
    };

    YAHOO.extend(LogicECM.module.SelectOne, Alfresco.component.Base, {
		options: {
			controlId: null,
			selectedValue: null,
			webscriptType: null,
			webscript: null,
			mandatory: false,
			currentNodeRef: null,
			destination: null
		},

		onReady: function SelectOne_onReady() {
			YAHOO.util.Event.on(this.id, "change", this.onSelectChange, this, true);
			var url;
			if (this.options.webscriptType != null && this.options.webscriptType == "server") {
				url = Alfresco.constants.PROXY_URI;
			} else {
				url = Alfresco.constants.URL_SERVICECONTEXT;
			}
			url += this.options.webscript;
			if (this.options.destination != null && this.options.destination != "" && this.options.destination != "{destination}") {
				url += "?nodeRef=" + this.options.destination + "&type=create";
			} else if (this.options.currentNodeRef != null) {
				url += "?nodeRef=" + this.options.currentNodeRef + "&type=edit";
			}
			Alfresco.util.Ajax.jsonGet({
				url: url,
				successCallback:
				{
					fn: function (response) {
						var oResults = response.json;
						if (oResults != null) {
							var select = document.getElementById(this.id);
							for (var i = 0; i < oResults.data.length; i++) {
								var option = document.createElement("option");
								option.value = oResults.data[i].value;
								option.innerHTML = oResults.data[i].name;
								if (oResults.data[i].value == this.options.selectedValue) {
									option.selected = true;
								}
								select.appendChild(option);
							}
						}
					},
					scope: this
				}
			});
		},

		onSelectChange: function() {
			var select = document.getElementById(this.id);
			document.getElementById(this.options.controlId + "-removed").value = this.options.selectedValue;
			document.getElementById(this.options.controlId + "-added").value = select.value;
			if (this.options.mandatory) {
				YAHOO.Bubbling.fire("mandatoryControlValueUpdated", this);
			}
		}
	 });
})();
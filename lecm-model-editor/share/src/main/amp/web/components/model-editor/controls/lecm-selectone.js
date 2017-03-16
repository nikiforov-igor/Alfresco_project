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

(function() {

    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event,
        Bubbling = YAHOO.Bubbling;

    LogicECM.module.SelectOneME = function(fieldHtmlId) {
        LogicECM.module.SelectOneME.superclass.constructor.call(this, "LogicECM.module.SelectOneME", fieldHtmlId, [ "container", "datasource"]);

        Event.on(this.id, "change", this.onSelectChange, this, true);
        return this;
    };

    YAHOO.extend(LogicECM.module.SelectOneME, Alfresco.component.Base, {
        options: {
            selectedValue: null,
            withEmpty: null,
            mandatory: false,
            values: null
        },

        onReady: function() {
            this._init();
        },

        _init: function() {
        	var i, select, option, prop, oResults = this.options.values;
        	if (oResults) {
        		select = document.getElementById(this.id);
        		while (select.firstChild) {
        			select.removeChild(select.firstChild);
        		}
        		if (this.options.withEmpty) {
        			option = document.createElement("option");
        			option.value = '';
        			select.appendChild(option);
        		}
        		for (i in oResults) {
        			option = document.createElement("option");
        			option.value = oResults[i].value;
        			option.innerHTML = oResults[i].label;
        			if (oResults[i].value === this.options.selectedValue) {
        				option.selected = true;
        			}
        			select.appendChild(option);
        		}
        		if (this.options.mandatory) {
        			Bubbling.fire("mandatoryControlValueUpdated", this);
        		}
        	}
        },

        onSelectChange: function() {
            var select = document.getElementById(this.id);
            if (this.options.mandatory) {
                Bubbling.fire("mandatoryControlValueUpdated", this);
            }
        }
    });
})();

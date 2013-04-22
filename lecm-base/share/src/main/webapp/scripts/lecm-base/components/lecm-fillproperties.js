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
    LogicECM.module.FillProperties = function LogicECM_module_FillProperties(fieldHtmlId)
    {
		var module = LogicECM.module.FillProperties.superclass.constructor.call(this, "LogicECM.module.FillProperties", fieldHtmlId, [ "container", "resize"]);
        YAHOO.Bubbling.on("afterSetItems", module.onAfterSetItems, module);
        return module;
    };

    YAHOO.extend(LogicECM.module.FillProperties, Alfresco.component.Base, {
		options: {
            fieldName: null,
			properties: null,
            dateFormat: "mm.dd.yyyy"
		},

        setData: function setData_Function(metadata) {
            var exp = this.options.properties.split(",");
            var me = this;
            exp.forEach(function (item) {
                item = item.trim();
                var props = item.split("->");
                var fromProp = props[0].trim();
                var toProp = "prop_" + props[1].trim().replace(":", "_");
                var value = metadata.properties[fromProp];

                if (value == null) return;

                var formIdPrefix = me.id.replace(me.options.fieldName, "");
                var formField = document.getElementById(formIdPrefix + toProp + "-cntrl-date");
                if (formField != null) {
                    var date = new Date(value);
                    var dateString = date.toString(me.options.dateFormat);
                    formField.value = dateString;
                    formField = document.getElementById(formIdPrefix + toProp);
                    formField.value = date.toISOString();
                    return;
                }
                var formField = document.getElementById(formIdPrefix + toProp);
                if (formField != null) {
                    formField.value = value;
                }
            });
            YAHOO.Bubbling.fire("mandatoryControlValueUpdated", this);
        },

        onAfterSetItems: function SelectOne_onAfterSetItems(layer, args) {
            var me = this;
            var nodeRef = args[1].items;
            var url = Alfresco.constants.PROXY_URI + "/api/metadata?nodeRef={nodeRef}&shortQNames";
            url = YAHOO.lang.substitute(url, {
                nodeRef: nodeRef
            });
            callback = {
                success:function (oResponse) {
                    var oResults = eval("(" + oResponse.responseText + ")");
                    me.setData(oResults);
                },
                timeout:60000
            };
            YAHOO.util.Connect.asyncRequest('GET', url, callback);

        }
	 });
})();
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

LogicECM.module.FormsEditor = LogicECM.module.FormsEditor || {};

(function() {
	var Dom = YAHOO.util.Dom;

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
			YAHOO.util.Event.on(this.id + "-select", "change", this.onSelectChange, this, true);

			this.getTargetType();
		},

		getTargetType: function() {
			Alfresco.util.Ajax.jsonGet({
				url:  Alfresco.constants.PROXY_URI + "/lecm/docforms/attribute?nodeRef=" + encodeURIComponent(this.options.itemNodeRef),
				successCallback: {
					fn: function (response) {
						var oResults = response.json;
						if (oResults && oResults.targetType) {
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
				var select = Dom.get(this.id + "-select");

				for (var i = 0; i < this.config.length; i++) {
					var conf = this.config[i];

					var option = document.createElement("option");
					option.innerHTML = conf.displayName;
					if (this.options.value != null && this.options.value.id == conf.id) {
						option.selected = true;

						var valueParams = this.options.value.params;
						var confParams = conf.params;
						if (valueParams != null && confParams != null) {
							for (var j = 0; j < confParams.length; j++) {
								for (var k = 0; k < valueParams.length; k++) {
									if (valueParams[k].name == confParams[j].id) {
										confParams[j].value = valueParams[k].value;
									}
								}
							}
						}
					}
					select.appendChild(option);
				}
				this.onSelectChange();
			}
		},

		onSelectChange: function() {
			var select = Dom.get(this.id + "-select");
			if (select != null) {
				var tableParams = Dom.get(this.id + "-params");
				var hiddenParams = Dom.get(this.id + "-hidden-params");
				if (tableParams != null && hiddenParams != null) {
					tableParams.innerHTML = "";
					hiddenParams.innerHTML = "";

					var conf = this.config[select.selectedIndex -1];
					if (conf != null && conf.params != null) {
						for (var i = 0; i < conf.params.length; i++) {
							var param = conf.params[i];
							if (param.visible){
								var tr = document.createElement("tr");

								var td1 = document.createElement("td");
								td1.innerHTML = param.localName;
								if (param.mandatory) {
									td1.innerHTML += "<span class='mandatory-indicator'>*</span>";
								}
								tr.appendChild(td1);

								var td2 = document.createElement("td");
								td2.innerHTML = "<input type='text' id='" + param.id + "' value='" + param.value + "' class='formFieldControlParams'/>";
								tr.appendChild(td2);

								tableParams.appendChild(tr);

								YAHOO.util.Event.onAvailable(param.id, this.attachChangeParamsListener, param.id, this);
							} else {
								hiddenParams.innerHTML += "<input type='hidden' id='" + param.id + "' value='" + param.value + "' class='formFieldControlParams'>";
							}
						}
					}
				}
			}

			this.updateComponentValue();
		},

		attachChangeParamsListener: function(id)
		{
			YAHOO.util.Event.on(id, 'change', this.updateComponentValue, {}, this);
		},

		updateComponentValue: function() {
			var el = Dom.get(this.id);
			if (el != null) {
				var value = "";
				var select = Dom.get(this.id + "-select");
				if (select != null) {
					var conf = this.config[select.selectedIndex -1];
					if (conf != null) {
						var obj = {
							template: conf.templatePath,
							displayName: conf.displayName,
							id: conf.id,
							params: []
						};

						if (conf.params != null) {
							for (var i = 0; i < conf.params.length; i++) {
								var paramEl = Dom.get(conf.params[i].id);

								if (paramEl != null) {
									obj.params.push({
										name: conf.params[i].id,
										value: paramEl.value
									});
								}
							}
						}
						value = JSON.stringify(obj);
					}
				}
				el.value = value;
			}
		}
	});
})();
if (typeof LogicECM == 'undefined' || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.AssociationComplexControl = LogicECM.module.AssociationComplexControl || {};

(function () {

	LogicECM.module.AssociationComplexControl.ExtSearch = {
		extendQueryFunctions: [],
		extendArgsFunctions: [],

		FORM_ID: "ex-control-search",

		getExtSearchQuery: function (currentForm) {
			var exSearchFilter = '',
				propNamePrefix = '@',
				first = true;

			for (var i = 0; i < currentForm.elements.length; i++) {
				var element = currentForm.elements[i],
					propName = element.name,
					propValue = YAHOO.lang.trim(element.value);

				if (propName && propValue && propValue.length) {
					if (propName.indexOf("prop_") == 0) {
						propName = propName.substr(5);
						if (propName.indexOf("_") !== -1) {
							propName = propName.replace("_", ":");
							if (propName.match("-range$") == "-range") {
								var from, to, sepindex = propValue.indexOf("|");
								if (propName.match("-date-range$") == "-date-range") {
									propName = propName.substr(0, propName.length - "-date-range".length);
									from = (sepindex === 0 ? "MIN" : propValue.substr(0, 10));
									to = (sepindex === propValue.length - 1 ? "MAX" : propValue.substr(sepindex + 1, 10));
								} else {
									propName = propName.substr(0, propName.length - "-number-range".length);
									from = (sepindex === 0 ? "MIN" : propValue.substr(0, sepindex));
									to = (sepindex === propValue.length - 1 ? "MAX" : propValue.substr(sepindex + 1));
								}
								exSearchFilter += (first ? '' : ' AND ') + propNamePrefix + this.escape(propName) + ':"' + from + '".."' + to + '"';
								first = false;
							} else {
								exSearchFilter += (first ? '' : ' AND ') + propNamePrefix + this.escape(propName) + ':' + this.applySearchSettingsToTerm(propValue, 'MATCHES');
								first = false;
							}
						}
					} else if (propName.indexOf("assoc_") == 0) {
						var assocName = propName.substring(6);
						if (assocName.indexOf("_") !== -1) {
							assocName = assocName.replace("_", ":") + "-ref";
							exSearchFilter += (first ? '(' : ' AND (');
							var assocValues = propValue.split(",");
							var firstAssoc = true;
							for (var k = 0; k < assocValues.length; k++) {
								var assocValue = assocValues[k];
								if (!firstAssoc) {
									exSearchFilter += " OR ";
								}
								exSearchFilter += this.escape(assocName) + ':"' + this.applySearchSettingsToTerm(assocValue, 'CONTAINS') + '"';
								firstAssoc = false;
							}
							exSearchFilter += ") ";
							first = false;
						}
					}
				}
			}
			return exSearchFilter;
		},

		escape: function (value) {
			var result = "";

			for (var i = 0, c; i < value.length; i++) {
				c = value.charAt(i);
				if (!((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= 'а' && c <= 'я') || (c >= 'А' && c <= 'Я') || (c >= '0' && c <= '9') || c == '_')) {
					result += '\\';
				}

				result += c;
			}
			return result;
		},

		applySearchSettingsToTerm: function (searchTerm, searchSettings) {
			var decoratedTerm;

			searchTerm = this.escape(searchTerm);

			switch (searchSettings) {
				case 'BEGINS':
					decoratedTerm = searchTerm + '*';
					break;
				case 'ENDS':
					decoratedTerm = '*' + searchTerm;
					break;
				case 'CONTAINS':
					decoratedTerm = '*' + searchTerm + '*';
					break;
				case 'MATCHES':
					decoratedTerm = searchTerm;
					break;
				default:
					decoratedTerm = '*' + searchTerm + '*';
					break;
			}

			return decoratedTerm;
		},

		getInputValue: function (form, propName) {
			var value = null;
			var inputName = form[propName];
			if (inputName != null) {
				value = inputName.value;
				value = YAHOO.lang.trim(value);
			}
			return value;
		},

		getArgsFromForm: function (currentForm) {
			var args = {};
			for (var i = 0; i < currentForm.elements.length; i++) {
				var element = currentForm.elements[i],
					propName = element.name,
					propValue = YAHOO.lang.trim(element.value);

				if (propName && (propName.indexOf("prop_") == 0 || propName.indexOf("assoc_") == 0)) {
					if (propValue) {
						args[propName] = propValue;
					}
				}
			}
			return args;
		},

		registerQueryFunction: function (key, fn) {
			this.extendQueryFunctions[key] = fn;
		},

		registerArgsFunction: function (key, fn) {
			this.extendArgsFunctions[key] = fn;
		}
	};
})();

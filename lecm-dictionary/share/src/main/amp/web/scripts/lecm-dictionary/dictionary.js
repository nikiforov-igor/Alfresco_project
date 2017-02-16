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

(function () {

	var Dom = YAHOO.util.Dom;

	LogicECM.module.DictionaryMain = function () {
		LogicECM.module.DictionaryMain.superclass.constructor.call(
			this,
			"LogicECM.module.DictionaryMain",
			"lecm-dictionary",
			["button", "container", "connection", "json"]);

        return this;
	};

	YAHOO.lang.extend(LogicECM.module.DictionaryMain, Alfresco.component.Base, {
		options:
		{
			dictionaryName: "",
			plane: false
		},

		rootNode: null,

		onReady: function DictionaryMain_onReady()
		{
			this.loadDictionary();
		},

		loadDictionary: function DictionaryMain_loadDictionary() {
			if (this.options.dictionaryName) {
				var  sUrl = Alfresco.constants.PROXY_URI + "/lecm/dictionary/api/getDictionary?dicName=" + encodeURIComponent(this.options.dictionaryName);
				Alfresco.util.Ajax.jsonGet({
					url: sUrl,
					successCallback: {
						fn: function (response) {
							var oResults = response.json;
							if (oResults) {
								this.rootNode = oResults;
								if (this.options.plane) {
									this.loadDatagrid();
								}
							}
						},
						scope: this
					},
					failureMessage: this.msg('message.dictionary.loading.fail')
				});
			}
		},

		loadDatagrid: function DictionaryMain_loadDatagrid() {
			if (this.rootNode != null) {
				YAHOO.Bubbling.fire("activeGridChanged",
					{
						datagridMeta:{
							itemType: this.rootNode.itemType,
							nodeRef: this.rootNode.nodeRef
						},
						scrollTo:true
					});
			}
		}
	});
})();

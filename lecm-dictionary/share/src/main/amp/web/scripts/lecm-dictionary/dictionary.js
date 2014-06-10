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
			if (this.options.dictionaryName != "") {
				var me = this;
				var  sUrl = Alfresco.constants.PROXY_URI + "/lecm/dictionary/api/getDictionary?dicName=" + encodeURIComponent(this.options.dictionaryName);

				var callback = {
					success:function (oResponse) {
						var oResults = eval("(" + oResponse.responseText + ")");
						if (oResults != null) {
							me.rootNode = oResults;
							if (me.options.plane) {
								me.loadDatagrid();
							}
						}
					},
					failure:function (oResponse) {
						alert("Справочник не был загружен. Попробуйте обновить страницу.");
					},
					argument:{
					}
				};
				YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
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

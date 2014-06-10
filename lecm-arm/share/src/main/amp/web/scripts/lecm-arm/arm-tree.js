if (typeof LogicECM == "undefined" || !LogicECM) {
    var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.ARM = LogicECM.module.ARM|| {};

(function()
{

	LogicECM.module.ARM.Tree = function(htmlId)
	{
		return LogicECM.module.ARM.Tree.superclass.constructor.call(this, htmlId);
	};

	/**
	 * Extend from Alfresco.component.Base
	 */
	YAHOO.lang.extend(LogicECM.module.ARM.Tree, LogicECM.module.Dictionary.Tree);

	/**
	 * Augment prototype with main class implementation, ensuring overwrite is enabled
	 */
	YAHOO.lang.augmentObject(LogicECM.module.ARM.Tree.prototype,
		{
			_loadRootNode:function () {
				var me = this;
				var sUrl = Alfresco.constants.PROXY_URI + "/lecm/arm/settings/getDictionary";
				var callback = {
					success:function (oResponse) {
						var oResults = eval("(" + oResponse.responseText + ")");
						if (oResults != null && oResults.nodeRef != null) {
							nodeDictionary = oResults.nodeRef;
							me.rootNode = oResults;
						}

						me.draw();
					},
					failure:function (oResponse) {
						alert("Справочник не был загружен. Попробуйте обновить страницу.");
					},
					argument:{
					}
				};
				YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
			}
		}, true);
})();
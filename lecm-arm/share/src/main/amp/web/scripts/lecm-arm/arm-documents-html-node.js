if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.ARM = LogicECM.module.ARM|| {};

(function () {
	var Dom = YAHOO.util.Dom;

	LogicECM.module.ARM.HtmlNode = function (htmlId) {
		LogicECM.module.ARM.HtmlNode.superclass.constructor.call(this, "LogicECM.module.ARM.HtmlNode", htmlId);

		YAHOO.Bubbling.on("updateArmHtmlNode", this.onUpdateArmHtmlNode, this);
		return this;
	};

	YAHOO.extend(LogicECM.module.ARM.HtmlNode, Alfresco.component.Base,
		{
			onUpdateArmHtmlNode: function(layer, args) {
				var url = args[1].url;
				if (url !== null) {
					var container = Dom.get(this.id);

					if (container != null) {
						container.innerHTML = "";

						if (this.isIframePage(url)) {
							this.loadIframePage(url);
						} else {
							this.loadSharePage(url);
						}
					}
				}
			},

			isIframePage: function(url) {
				return url.indexOf("http://") == 0 || url.indexOf("https://") == 0;
			},

			loadSharePage: function(url) {
				var me = this;
				Alfresco.util.Ajax.request(
					{
						url: Alfresco.constants.URL_CONTEXT + url,
						dataObj: {
							htmlid: this.id
						},
						successCallback: {
							fn:function(response){
								Dom.get(me.id).innerHTML = response.serverResponse.responseText;
							},
							scope: this
						},
						failureMessage: this.msg("message.failure"),
						scope: this,
						execScripts: true
					});
			},

			loadIframePage: function(url) {
				var height = Dom.getY("lecm-content-ft") - Dom.getY(this.id);

				Dom.get(this.id).innerHTML = '<iframe style="width:100%; height:' + height + 'px;" src="' + url + '"></iframe>';
			}
		}, true);
})();
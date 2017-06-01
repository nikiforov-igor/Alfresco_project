if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.ARM = LogicECM.module.ARM|| {};

(function () {
	var Dom = YAHOO.util.Dom,
        Selector = YAHOO.util.Selector;

	LogicECM.module.ARM.HtmlNode = function (htmlId) {
		LogicECM.module.ARM.HtmlNode.superclass.constructor.call(this, "LogicECM.module.ARM.HtmlNode", htmlId);

		YAHOO.Bubbling.on("updateArmHtmlNode", this.onUpdateArmHtmlNode, this);
		return this;
	};

	YAHOO.extend(LogicECM.module.ARM.HtmlNode, Alfresco.component.Base,
		{
			onUpdateArmHtmlNode: function(layer, args) {
				var node = args[1].armNode;
				var url = node.data.htmlUrl;
				if (url !== null) {
					var container = Dom.get(this.id);

					if (container != null) {
						container.innerHTML = "";

						if (this.isIframePage(url)) {
							this.loadIframePage(url);
						} else {
							this.loadSharePage(url, node);
						}
					}
				}
			},

			isIframePage: function(url) {
				return url.indexOf("http://") == 0 || url.indexOf("https://") == 0;
			},

			loadSharePage: function(url, node) {
				var me = this;
				var dataObj = {
					htmlid: this.id
				};
				if (node.data.nodeRef != null) {
					dataObj.armSelectedNodeRef = node.data.nodeRef
				}
				if (node.data.runAs != null) {
					dataObj.runAs = node.data.runAs;
				}

				Alfresco.util.Ajax.request(
					{
						url: Alfresco.constants.URL_CONTEXT + encodeURI(url),
						dataObj: dataObj,
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
                Dom.get(this.id).innerHTML = '<iframe style="width:100%; height:' + height + 'px;" src="' + url + '"></iframe>'
                    // для адекватной работы сплиттера ифрэйм нужно "прикрыть", иначе он перехватывает события от сплиттера
                    + '<div class="iframe-cover"></div>';

                var iframeCover = Selector.query('div.iframe-cover', this.id, true);

                YAHOO.Bubbling.on("startSplitterMoving", function() {
                    if (iframeCover) {
                        Dom.setStyle(iframeCover, 'display', 'block');
                    }
                });
                YAHOO.Bubbling.on("endSplitterMoving", function() {
                    if (iframeCover) {
                        Dom.setStyle(iframeCover, 'display', 'none');
                    }
                });

            }
		}, true);
})();
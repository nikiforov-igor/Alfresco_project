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
 * LogicECM top-level control namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.control
 */
LogicECM.control = LogicECM.control || {};

(function () {
	var Dom = YAHOO.util.Dom,
		Event = YAHOO.util.Event;

	LogicECM.control.Preview = function (fieldHtmlId) {
		LogicECM.control.Preview.superclass.constructor.call(this, "LogicECM.control.Preview", fieldHtmlId, [ "container"]);

		YAHOO.Bubbling.on("showPreview", this.showPreview, this);
		YAHOO.Bubbling.on("hidePreview", this.hidePreview, this);

		return this;
	};

	YAHOO.extend(LogicECM.control.Preview, Alfresco.component.Base,
		{
			flashUploaderWasShow: true,

            currentPreviewElement: null,

			showPreview: function (event, args) {
				if (args != null && args[1] != null && args[1].nodeRef != null) {
					var me = this;
                    if (this.currentPreviewElement != null) {
                        Dom.get(me.id).innerHTML = "";
                    }
                    var previewElementId = this.id + new Date().getTime();
                    this.currentPreviewElement = document.createElement("div");
                    this.currentPreviewElement.id = previewElementId;
                    Dom.get(me.id).appendChild(this.currentPreviewElement);
					Alfresco.util.Ajax.request(
						{
							url: Alfresco.constants.URL_SERVICECONTEXT + "components/preview/web-preview",
							dataObj: {
								nodeRef: args[1].nodeRef,
								htmlid: previewElementId
							},
							successCallback: {
								fn: function (response) {
									Dom.get(previewElementId).innerHTML = response.serverResponse.responseText;
									var previewId = previewElementId + "-full-window-div";

									if (me.flashUploaderWasShow && Alfresco.WebPreview.prototype.Plugins.PdfJs == null) {
										me.flashUploaderWasShow = false;
										Event.onAvailable(previewId, function () {
											var preview = Dom.get(previewId);
											var container = Dom.get(previewElementId);
											container.innerHTML = "";

											preview.setAttribute("style", "");
											container.appendChild(preview);

											me.flashUploaderWasShow = true;
										}, {}, me);
									}
								},
								scope: this
							},
							failureMessage: this.msg("message.failure"),
							scope: this,
							execScripts: true
						});
				}
			},

			hidePreview: function () {
				Dom.get(this.id).innerHTML = "";
                this.currentPreviewElement = null;
			}
		});
})();
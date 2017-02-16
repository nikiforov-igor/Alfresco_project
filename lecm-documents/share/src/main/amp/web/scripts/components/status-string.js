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
 * DocumentHistory
 *
 * @namespace LogicECM
 * @class LogicECM.DocumentStatusString
 */
(function () {
	/**
	 * YUI Library aliases
	 */
	var Dom = YAHOO.util.Dom,
		Event = YAHOO.util.Event;
    var statusString;

	/**
	 * DocumentHistory constructor.
	 *
	 * @param {String} htmlId The HTML id of the parent element
	 * @return {LogicECM.DocumentStatusString} The new DocumentHistory instance
	 * @constructor
	 */
	LogicECM.DocumentStatusString = function DocumentStatusString_constructor(htmlId) {
		LogicECM.DocumentStatusString.superclass.constructor.call(this, "LogicECM.DocumentStatusString", htmlId);

		YAHOO.Bubbling.on("updateDocumentPage", this.updateDocumentPage, this);
		return this;
	};

	YAHOO.extend(LogicECM.DocumentStatusString, Alfresco.component.Base,
		{
			options: {
				nodeRef: null,
				propertyName: null
			},

			onReady: function () {
                statusString = Dom.get(this.id + "-body");

				this.widgets.submitButton = Alfresco.util.createYUIButton(this, "submit-button", this.onSubmit);
				Event.addListener(Dom.get(this.id + "-property-value"), "keypress", this.adjustTextareaHeight, this);
                Event.on(window, "resize", function() {
                    this.setStatusStringWidth();
                    this.fixStatusString();
                }, this, true);
                this.setStatusStringWidth();
                this.fixStatusString();
			},

			onSubmit: function () {
				Alfresco.util.Ajax.jsonPost({
					url: Alfresco.constants.PROXY_URI + "lecm/document/api/editDocument",
					dataObj: {
						nodeRef: this.options.nodeRef,
						properties: this.options.propertyName + "=" + Dom.get(this.id + "-property-value").value + "," + this.options.propertyName + "=" + Dom.get(this.id + "-property-value").value
					},
					successMessage: this.msg("message.submit.success"),
					failureMessage: this.msg("message.submit.failure")
				});
			},

			updateDocumentPage: function (layer, args) {
				if (args[1] != null && args[1].title != null) {
					Dom.get(this.id + "-page").innerHTML = args[1].title;
				}
			},

			adjustTextareaHeight: function (event, scope) {
				var textarea = event.currentTarget;

				setTimeout(function() {
					var dif = textarea.scrollHeight - textarea.clientHeight;
					if (dif) {
						if (isNaN(parseInt(textarea.style.height))) {
							textarea.style.height = textarea.scrollHeight + "px";
						} else {
							textarea.style.height = parseInt(textarea.style.height) + dif + "px";
						}
                        scope.setPlaceholderHeight();
                    }
				}, 1);
			},

            setStatusStringWidth: function() {
                var width = parseInt(Dom.getStyle("main-region", "width"));
                // придется сразу учитывать наличие скролла,
                // иначе бесчисленное множество мест, где может увеличиться высота страницы,
                // что повлечет за собой появление скролла
                var scrollbarWidth = 18;

                width = width - parseInt(Dom.getStyle(statusString, "border-right-width")) - scrollbarWidth;
                Dom.setStyle(statusString, "width", width + "px");
            },

            setPlaceholderHeight: function() {
                var height = Dom.get(statusString).offsetHeight;

                Dom.setStyle(this.id + "-placeholder", "height", height + "px");
            },

            fixStatusString: function() {
                var doc = Dom.get('doc-bd');
                var bottom = parseInt(Dom.getStyle(doc, 'margin-bottom'));

                Dom.setStyle(statusString, "bottom", bottom + "px");
                this.setPlaceholderHeight();
            }
		});
})();

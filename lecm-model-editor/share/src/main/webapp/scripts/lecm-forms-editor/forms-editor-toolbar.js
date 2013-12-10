/**
 * LogicECM root namespace.
 *
 * @namespace LogicECM
 */
// Ensure LogicECM root object exists
if (typeof LogicECM == "undefined" || !LogicECM) {
    var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.FormsEditor = LogicECM.module.FormsEditor || {};

(function () {
    LogicECM.module.FormsEditor.Toolbar = function (htmlId) {
        return LogicECM.module.FormsEditor.Toolbar.superclass.constructor.call(this, "LogicECM.module.FormsEditor.Toolbar", htmlId);
    };

    YAHOO.extend(LogicECM.module.FormsEditor.Toolbar, LogicECM.module.Base.Toolbar);

    YAHOO.lang.augmentObject(LogicECM.module.FormsEditor.Toolbar.prototype,
        {
            _initButtons: function () {
                this.toolbarButtons["defaultActive"].newRowButton = Alfresco.util.createYUIButton(this, "newFormButton", this.onNewRow,
                    {
                        value: "create"
                    });

	            this.toolbarButtons["defaultActive"].deployButton = Alfresco.util.createYUIButton(this, "generateFormsButton", this.generateModelForms);

	            this.toolbarButtons["defaultActive"].deployButton = Alfresco.util.createYUIButton(this, "deployFormsButton", this.deployForms);
            },

	        generateModelForms: function() {
		        var me = this;
		        Alfresco.util.Ajax.jsonGet(
			        {
				        url: Alfresco.constants.PROXY_URI + "/lecm/docforms/generate?modelName=" + encodeURIComponent(this.options.doctype),
				        successCallback: {
					        fn: function (response) {
						        var oResults = response.json;
						        if (oResults != null && oResults.success) {
							        Alfresco.util.PopupManager.displayMessage({
								        text: me.msg("message.generate.success")
							        });
						        }
					        },
					        scope: this
				        },
				        failureMessage: "message.failure"
			        });
	        },

	        deployForms: function() {
		        var me = this;
		        Alfresco.util.Ajax.request(
			        {
				        url:Alfresco.constants.URL_SERVICECONTEXT + "lecm/config/init?reset=true",
				        dataObj:{},
				        successCallback:{
					        fn:function (response) {
						        Alfresco.util.PopupManager.displayMessage({
							        text: me.msg("message.deploy.success")
						        });
					        }
				        },
				        failureMessage:"message.failure"
			        });

	        }
        }, true);
})();
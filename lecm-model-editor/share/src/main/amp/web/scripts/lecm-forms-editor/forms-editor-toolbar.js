/**
 * LogicECM root namespace.
 *
 * @namespace LogicECM
 */
// Ensure LogicECM root object exists
if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
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
	        fileUpload: null,

            _initButtons: function () {
                this.toolbarButtons["defaultActive"].newRowButton = Alfresco.util.createYUIButton(this, "newFormButton", this.onNewRow,
                    {
                        value: "create"
                    });

	            this.toolbarButtons["defaultActive"].deployButton = Alfresco.util.createYUIButton(this, "generateFormsButton", this.generateModelForms);

	            this.toolbarButtons["defaultActive"].deployButton = Alfresco.util.createYUIButton(this, "deployFormsButton", this.deployForms);

	            this.toolbarButtons["defaultActive"].downloadButton = Alfresco.util.createYUIButton(this, "downloadConfigButton", this.downloadConfig);

	            this.toolbarButtons["defaultActive"].uploadButton = Alfresco.util.createYUIButton(this, "uploadConfigButton", this.uploadConfig);
            },

	        generateModelForms: function() {
		        Alfresco.util.Ajax.jsonGet(
			        {
				        url: Alfresco.constants.PROXY_URI + "/lecm/docforms/generate?modelName=" + encodeURIComponent(this.options.doctype),
				        successCallback: {
					        fn: function (response) {
						        var oResults = response.json;
						        if (oResults != null && oResults.success) {
							        Alfresco.util.PopupManager.displayMessage({
								        text: this.msg("message.generate.success")
							        });
						        }
					        },
					        scope: this
				        },
				        failureMessage: "message.failure"
			        });
	        },

	        deployForms: function() {
		        Alfresco.util.Ajax.request(
			        {
				        url:Alfresco.constants.URL_SERVICECONTEXT + "lecm/config/init?reset=true",
				        dataObj:{},
				        successCallback:{
					        fn:function (response) {
						        Alfresco.util.PopupManager.displayMessage({
							        text: this.msg("message.deploy.success")
						        });
					        },
					        scope: this
				        },
				        failureMessage:"message.failure"
			        });

	        },

	        downloadConfig: function() {
                Alfresco.util.Ajax.jsonGet(
			        {
				        url: Alfresco.constants.PROXY_URI + "/lecm/docforms/config?modelName=" + encodeURIComponent(this.options.doctype),
				        successCallback: {
					        fn: function (response) {
						        var oResults = response.json;
						        if (oResults != null && oResults.nodeRef != null) {
							        window.open(Alfresco.constants.PROXY_URI + "api/node/content/" + oResults.nodeRef.replace("://", "/") + "/" + oResults.name + "?a=true", "_self");
						        } else {
							        Alfresco.util.PopupManager.displayMessage({
								        text: this.msg("message.downloadConfig.notFound")
							        });
						        }
					        },
					        scope: this
				        },
				        failureMessage: "message.failure"
			        });
	        },

	        uploadConfig: function() {
		        Alfresco.util.Ajax.jsonGet(
			        {
				        url: Alfresco.constants.PROXY_URI + "/lecm/docforms/config?modelName=" + encodeURIComponent(this.options.doctype),
				        successCallback: {
					        fn: function (response) {
						        var oResults = response.json;
						        if (oResults != null && oResults.nodeRef != null) {
							        if (this.fileUpload == null) {
								        this.fileUpload = Alfresco.getFileUploadInstance();
							        }

							        var uploadConfig = {
								        updateNodeRef: oResults.nodeRef,
								        updateFilename: oResults.name,
								        updateVersion: oResults.version,
								        overwrite: true,
								        filter: [
									        {
										        description: this.msg("label.filter-description", oResults.name),
										        extensions: "*.xml"
									        }],
								        mode: this.fileUpload.MODE_SINGLE_UPDATE,
								        thumbnails: "doclib"
							        };
							        this.fileUpload.show(uploadConfig);
						        } else {
							        Alfresco.util.PopupManager.displayMessage({
								        text: this.msg("message.uploadConfig.notFound")
							        });
						        }
					        },
					        scope: this
				        },
				        failureMessage: "message.failure"
			        });
	        }
        }, true);
})();
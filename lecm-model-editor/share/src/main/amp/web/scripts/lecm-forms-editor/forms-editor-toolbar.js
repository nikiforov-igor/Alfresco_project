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

            importInfoDialog: null,
            importErrorDialog: null,
            importFromDialog: null,

            _initButtons: function () {
                this.toolbarButtons["defaultActive"].newRowButton = Alfresco.util.createYUIButton(this, "newFormButton", this.onNewRow,
                    {
                        value: "create"
                    });

                this.toolbarButtons["defaultActive"].generateButton = Alfresco.util.createYUIButton(this, "generateFormsButton", this.generateModelForms);

                this.toolbarButtons["defaultActive"].deployButton = Alfresco.util.createYUIButton(this, "deployFormsButton", this.deployForms);

                this.toolbarButtons["defaultActive"].downloadButton = Alfresco.util.createYUIButton(this, "downloadConfigButton", this.downloadConfig);

                this.toolbarButtons["defaultActive"].uploadButton = Alfresco.util.createYUIButton(this, "uploadConfigButton", this.uploadConfig);

                this.toolbarButtons["defaultActive"].importButton = Alfresco.util.createYUIButton(this, "uploadImportButton", this.showImportDialog);

                // Finally show the component body here to prevent UI artifacts on YUI button decoration
                Dom.setStyle(this.id + "-body", "visibility", "visible");

                this.importInfoDialog = Alfresco.util.createYUIPanel(this.id + "-import-info-form",
                    {
                        width: "50em"
                    });

                Dom.removeClass(this.id + "-import-info-form", "hidden1");

                this.importErrorDialog = Alfresco.util.createYUIPanel(this.id + "-import-error-form",
                    {
                        width: "60em"
                    });

                Dom.removeClass(this.id + "-import-error-form", "hidden1");

                this.importFromDialog = Alfresco.util.createYUIPanel(this.id + "-import-form",
                    {
                        width: "50em"
                    });

                Dom.removeClass(this.id + "-import-form", "hidden1");
                this.submitButton = Alfresco.util.createYUIButton(this, "import-form-submit", this.onImportXML, {
                    disabled: true
                });
                var importXmlButton = Alfresco.util.createYUIButton(this, "import-form-cancel", this.hideImportDialog, {});

                YAHOO.util.Event.on(this.id + "-import-form-import-file", "change", this.checkImportFile, null, this);

                YAHOO.util.Event.on(this.id + "-import-error-form-show-more-link", "click", this.errorFormShowMore, null, this);
			},

	        generateModelForms: function() {
		        Alfresco.util.Ajax.jsonGet({
			        url: Alfresco.constants.PROXY_URI + "/lecm/docforms/generate",
			        dataObj: {
				        modelName: this.options.doctype
			        },
			        successCallback: {
				        scope: this,
				        fn: function (response) {
						    var oResults = response.json;
						    if (oResults && oResults.success) {
							    Alfresco.util.PopupManager.displayMessage({
								    text: this.msg("message.generate.success")
							    });
						    }
					    }
				    },
				    failureMessage: this.msg("message.failure")
			    });
	        },

	        deployForms: function() {
		        Alfresco.util.Ajax.jsonGet({
				    url:Alfresco.constants.URL_SERVICECONTEXT + "lecm/config/init",
				    dataObj: {
					    reset: true
				    },
				    successCallback:{
					    scope: this,
					    fn:function (response) {
						    Alfresco.util.PopupManager.displayMessage({
							    text: this.msg("message.deploy.success")
						    });
					    }
				    },
				    failureMessage: this.msg("message.failure")
			    });
	        },

	        downloadConfig: function() {
                Alfresco.util.Ajax.jsonGet({
				    url: Alfresco.constants.PROXY_URI + "/lecm/docforms/config",
	                dataObj: {
		                modelName: this.options.doctype
	                },
				    successCallback: {
					    scope: this,
					    fn: function (response) {
						    var oResults = response.json;
						    if (oResults && oResults.nodeRef) {
							    window.open(Alfresco.constants.PROXY_URI + "api/node/content/" + oResults.nodeRef.replace("://", "/") + "/" + oResults.name + "?a=true", "_self");
						    } else {
							    Alfresco.util.PopupManager.displayMessage({
								    text: this.msg("message.downloadConfig.notFound")
							    });
						    }
					    }
				    },
				    failureMessage: this.msg("message.failure")
			    });
	        },

	        uploadConfig: function() {
		        Alfresco.util.Ajax.jsonGet({
				    url: Alfresco.constants.PROXY_URI + "/lecm/docforms/config",
			        dataObj: {
				        modelName: this.options.doctype
			        },
				    successCallback: {
					    scope: this,
					    fn: function (response) {
						    var oResults = response.json;
						    if (oResults && oResults.nodeRef) {
							    if (!this.fileUpload) {
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
					    }
				    },
				    failureMessage: this.msg("message.failure")
			    });
	        },

			showImportDialog: function () {
				Dom.get(this.id + "-import-form-chbx-ignore").checked = false;
				Dom.get(this.id + "-import-form-import-file").value = "";
				this.importFromDialog.show();
			},

			hideImportDialog: function () {
				this.importFromDialog.hide();
			},

			checkImportFile: function (event) {
				this.submitButton.set("disabled", event.currentTarget.value == null || event.currentTarget.value.length == 0);
			},

			onImportXML: function() {
				var me = this;
				YAHOO.util.Connect.setForm(this.id + '-import-xml-form', true);
				var url = Alfresco.constants.URL_CONTEXT + "proxy/alfresco/lecm/dictionary/post/import?nodeRef=" + this.modules.dataGrid.datagridMeta.nodeRef;
				var callback = {
					upload: function (oResponse) {
						var oResults = YAHOO.lang.JSON.parse(oResponse.responseText);
						if (oResults[0] != null && oResults[0].text != null) {
							Dom.get(me.id + "-import-info-form-content").innerHTML = oResults[0].text;
							me.importInfoDialog.show();
						} else if (oResults.exception != null) {
							Dom.get(me.id + "-import-error-form-exception").innerHTML = oResults.exception.replace(/\n/g, '<br>').replace(/\r/g, '<br>');
							Dom.get(me.id + "-import-error-form-stack-trace").innerHTML = me.getStackTraceString(oResults.callstack);
							Dom.setStyle(me.id + "-import-error-form-more", "display", "none");
							me.importErrorDialog.show();
						}

						YAHOO.Bubbling.fire("datagridRefresh",
							{
								bubblingLabel: me.options.bubblingLabel
							});
					}
				};
				this.hideImportDialog();
				YAHOO.util.Connect.asyncRequest(Alfresco.util.Ajax.POST, url, callback);
			},

			getStackTraceString: function (callstack) {
				var result = "";
				if (callstack != null) {
					for (var i = 0; i < callstack.length; i++) {
						if (callstack[i].length > 0) {
							result += callstack[i] + "<br/>";
						}
					}
				}
				return result;
			},

			errorFormShowMore: function () {
				Dom.setStyle(this.id + "-import-error-form-more", "display", "block");
			}

		}, true);
})();
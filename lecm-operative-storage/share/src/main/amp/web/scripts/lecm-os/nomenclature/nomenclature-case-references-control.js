if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Nomenclature = LogicECM.module.Nomenclature || {};

(function() {
	var Dom = YAHOO.util.Dom;

	LogicECM.module.Nomenclature.CaseReferences = function(htmlId) {
		LogicECM.module.Nomenclature.CaseReferences.superclass.constructor.call(this, "LogicECM.module.Nomenclature.CaseReferences", htmlId);
		return this;
	};

	YAHOO.extend(LogicECM.module.Nomenclature.CaseReferences, Alfresco.component.Base, {
        options: {
            bubblingId: null,
            template: null,
            references: null
        },
        fileUpload: null,

		onReady: function() {
            Alfresco.util.createYUIButton(this, "load-template", this.onDownloadTemplate.bind(this), {});
            Alfresco.util.createYUIButton(this, "upload-reference", this.onUpload.bind(this), {});
		},

        onDownloadTemplate: function onDownloadTemplateFunction() {
            document.location.href = Alfresco.constants.PROXY_URI + "api/node/content/" + this.options.template.replace("://", "/") + "/справка-заместитель?a=true";
        },

        onUpload: function onUploadFunction(e, obj) {
            if (this.fileUpload == null)
            {
                this.fileUpload = Alfresco.getFileUploadInstance();
            }

            var me = this;
            var multiUploadConfig =
            {
                destination: this.options.references,
                filter: [],
                mode: this.fileUpload.MODE_MULTI_UPLOAD,
                thumbnails: "doclib",
                onFileUploadComplete:
                {
                    fn: function() {
                        YAHOO.Bubbling.fire("datagridRefresh",
                            {
                                bubblingLabel: me.options.bubblingId
                            });
                    },
                    scope: this
                },
                suppressRefreshEvent: true
            };
            this.fileUpload.show(multiUploadConfig);
            Event.preventDefault(e);
        }
    });
})();

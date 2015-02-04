if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Errands = LogicECM.module.Errands || {};
LogicECM.module.Errands.dashlet = LogicECM.module.Errands.dashlet || {};

(function () {
    LogicECM.module.Errands.dashlet.Errands = function Errands_constructor(htmlId) {
        LogicECM.module.Errands.dashlet.Errands.superclass.constructor.call(this, "LogicECM.module.Errands.dashlet.Errands", htmlId, ["button", "container"]);
        return this;
    };

    YAHOO.extend(LogicECM.module.Errands.dashlet.Errands, Alfresco.component.Base,
        {
            doubleClickLock: false,
            options: {
                itemType: "lecm-errands:document",
                destination: null,
                parentDoc: null
            },

            onAddErrandClick: function Errands_onAddErrandsClick(args) {
	            var url = Alfresco.constants.URL_PAGECONTEXT + "document-create?documentType=" + this.options.itemType;
	            var params = "documentType=" + this.options.itemType;
	            params += "&formId=" + "workflow-form";
	            if (args != null) {
		            for (var prop in args) {
			            if (args.hasOwnProperty(prop)) {
				            params += "&" + prop + "=" + args[prop];
			            }
		            }
	            }

	            window.location.href = url + "&" + LogicECM.module.Base.Util.encodeUrlParams(params);
            },

            createChildErrand: function Errands_onAddErrandsClick() {
                var limitElement = Dom.get("errandLimitationDate");
                var limitDate = "";
                if (limitElement){
                    limitDate = limitElement.value;
                }
                var args = {
                    parentDoc: this.options.parentDoc,
                    parentLimitationDate: limitDate
                };

                this.onAddErrandClick(args);
            },

            createReErrand: function Errands_onAddErrandsClick(parentDoc, limit) {
                if (parentDoc) {
                    var limitDate = "";
                    if (limit){
                        limitDate = limit;
                    }
                    var args = {
                        parentDoc: parentDoc,
                        parentLimitationDate: limitDate
                    };

                    this.onAddErrandClick(args);
                } else {
                    this.createChildErrand();
                }
            }
        });
})();
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
 * LogicECM top-level module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module
 */
LogicECM.validation = LogicECM.validation || {};

LogicECM.validation.uniquenessFieldObjects = [];

LogicECM.validation.uniquenessValidation = function() {
    var valid = true;
    var fields = LogicECM.validation.uniquenessFieldObjects;
    for (var i = 0; i < fields.length; i++) {
        valid = valid & fields[i].valid;
    }
    return valid;
};

(function () {

    var Dom = YAHOO.util.Dom;

    LogicECM.validation.Uniqueness = function (htmlId) {
        var model = LogicECM.validation.Uniqueness.superclass.constructor.call(
            this,
            "LogicECM.validation.Uniqueness",
            htmlId,
            ["connection", "json"]);

        model.htmlId = htmlId;
        YAHOO.util.Event.addListener(htmlId, "keyup", model.validate.bind(model));
        LogicECM.validation.uniquenessFieldObjects.forEach(function(model, index, array){
            if(model.htmlId == htmlId) {
                array.splice(index, 1);
            }
        });
        LogicECM.validation.uniquenessFieldObjects.push(model);
        return model;
    };

    YAHOO.extend(LogicECM.validation.Uniqueness, Alfresco.component.Base, {
        htmlId:null,
        uniqueValidationTimer:null,
        valid:false,

        options:{
            nodeRef:"",
            typeName:"",
            propertyName:"",
            storedValue:""
        },

        checkValue:function Uniqueness_checkValue() {
            var newValue = Dom.get(this.htmlId).value;
            if (newValue.length == 0) {
                this.processValidState(true);
                return;
            }

            if (newValue == this.options.storedValue) {
                this.processValidState(true);
                return;
            }

            var sUrl = Alfresco.constants.PROXY_URI + '/lecm/base/validation/uniqueness?newValue=' + YAHOO.lang.trim(newValue)
                + '&nodeRef=' + this.options.nodeRef + '&typeName=' + this.options.typeName + '&propertyName=' + this.options.propertyName;

            var callback = {
                success:function (res) {
                    try {
                        var response = YAHOO.lang.JSON.parse(res.responseText);
                        this.processValidState(response.isUnique);
                    }
                    catch (e) {
                        this.processValidState(false);
                    }
                }.bind(this),
                failure:function () {
                    this.processValidState(false);
                }.bind(this)
            };

            YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
        },

        validate:function setUniqueValidationTimer() {
            if (this.uniqueValidationTimer != null) {
                clearTimeout(this.uniqueValidationTimer);
            }

            this.uniqueValidationTimer = setTimeout(this.checkValue.bind(this), 500);
        },

        processValidState: function processValidState(isValid) {
            this.valid = isValid;
            Dom.get(this.htmlId).className = (isValid) ? "" : "error";
            YAHOO.Bubbling.fire("mandatoryControlValueUpdated", this);
        }

    });

})();

if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.Document = LogicECM.module.Document|| {};

(function () {

    var Dom = YAHOO.util.Dom;
    LogicECM.module.Document.SelectStatusCtrl = function LogicECM_module_SelectMany(fieldHtmlId) {
        LogicECM.module.Document.SelectStatusCtrl.superclass.constructor.call(this, "LogicECM.module.Document.SelectStatusCtrl", fieldHtmlId, [ "container", "resize"]);
        return this;
    };

    YAHOO.extend(LogicECM.module.Document.SelectStatusCtrl, Alfresco.component.Base, {
        options: {
            dcoType: null,
            controlId: null,
            selectedValue: null,
            mandatory: false,
            currentNodeRef: null,
            destination: null,
            updateOnAction: null,
            valuesDelimiter: ","
        },

        draw: function SelectMany_onReady() {
            YAHOO.util.Event.on(this.options.controlId, "change", this.onSelectChange, this, true);

            var url = Alfresco.constants.PROXY_URI + "lecm/statemachine/getStatuses?docType={docType}&active=true&final=true";
            url = YAHOO.lang.substitute(url, {
                docType: this.options.docType ? this.options.docType : ""
            });
            Alfresco.util.Ajax.jsonGet({
                url: url,
                successCallback: {
                    scope: this,
                    fn: function (response) {
                        var oResults = response.json;
                        if (oResults) {
                            oResults.sort(function(a,b) {
                                if (a.id < b.id){
                                    return -1;
                                } else if (a.id > b.id) {
                                    return 1;
                                }
                                return 0;
                            });
                            var select = document.getElementById(this.options.controlId);
                            for (var i = 0; i < oResults.length; i++) {
                                var option = document.createElement("option");
                                option.value = oResults[i].id;
                                option.innerHTML = oResults[i].id;
                                if (option.value == this.options.selectedValue) {
                                    option.selected = true;
                                }
                                select.appendChild(option);
                            }
                            if (oResults.length == 0) {
                                if (Dom.get(this.options.controlId).hasAttribute("multiple")) {
                                    Dom.get(this.options.controlId).removeAttribute("multiple");
                                }
                            }
                        }
                    }
                }
            });

            if (this.options.updateOnAction && this.options.updateOnAction.length) {
                var select = document.getElementById(this.options.controlId);
                if (select) {
                    select.setAttribute("disabled", "true");
                }
                YAHOO.Bubbling.on(this.options.updateOnAction, this.onUpdateSelect, this);
            }
        },

        onSelectChange: function () {
            var select = document.getElementById(this.options.controlId);
            if (select) {
                var values = [];
                for (var j = 0, jj = select.options.length; j < jj; j++) {
                    if (select.options[j].selected) {
                        values.push(select.options[j].value);
                    }
                }

                document.getElementById(this.id).value = values.join(this.options.valuesDelimiter);
                if (this.options.mandatory) {
                    YAHOO.Bubbling.fire("mandatoryControlValueUpdated", this);
                }
            }
        }
    });
})();
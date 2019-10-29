(function () {
    /**
     * Toolbar constructor.
     *
     * @param htmlId {String} The HTML id of the parent element
     * @return {LogicECM.module.Contracts.Toolbar} The new Toolbar instance
     * @constructor
     */
    LogicECM.module.Contracts.BasedOnDocumentSelection = function (htmlId) {
        return LogicECM.module.Contracts.BasedOnDocumentSelection.superclass.constructor.call(this, "LogicECM.module.Contracts.BasedOnDocumentSelection", htmlId, ["button", "container"]);
    };

    /**
     * Extend from Alfresco.component.Base
     */
    YAHOO.extend(LogicECM.module.Contracts.BasedOnDocumentSelection, Alfresco.component.Base);

    /**
     * Augment prototype with main class implementation, ensuring overwrite is enabled
     */
    YAHOO.lang.augmentObject(LogicECM.module.Contracts.BasedOnDocumentSelection.prototype,
        {
            data: null,
            /**
             * Object container for initialization options
             *
             * @property options
             * @type object
             */
            options: {
                controlId: null,
	            nodeRef: null
            },

            setData: function setData_Function(data) {
                var control = document.getElementById(this.options.controlId);
                if (control != null) {

                    var buttonName = this.id + "-button";
                    var table = document.createElement("table");
                    table.width = "100%";
                    table.cellPadding = 2;
                    table.cellSpacing = 1;
                    table.border = 0;

                    var tr = document.createElement("tr");
                    tr.style.cursor = "pointer";
                    var td = document.createElement("td");
                    td.style.width="100%";

                    var button1 = document.createElement("input");
                    button1.type = "radio";
                    button1.name = buttonName;
                    button1.checked = true;
                    button1.id = this.htmlId + "-link-" + buttonName;
                    button1.className = "lecm-radio";
                    td.appendChild(button1);
                    var label1 = document.createElement("label");
                    label1.innerHTML = this.msg("contract.contractTime.link");
                    label1.setAttribute("for", this.htmlId + "-link-" + buttonName);
                    td.appendChild(label1);
                    tr.appendChild(td);

                    YAHOO.util.Event.addListener(tr, "click", function() {
                        button1.checked = true;
                        this._drawData(data.links);
                    }.bind(this));

                    this._drawData(data.links);

                    table.appendChild(tr);

                    tr = document.createElement("tr");
                    tr.style.cursor = "pointer";
                    td = document.createElement("td");
                    td.style.width="100%";

                    var button2 = document.createElement("input");
                    button2.type = "radio";
                    button2.name = buttonName;
                    button2.checked = false;
                    button2.id = this.htmlId + "-attachment-" + buttonName;
                    button2.className = "lecm-radio";
                    td.appendChild(button2);
                    var label2 = document.createElement("label");
                    label2.innerHTML = this.msg("contract.contractTime.attachment");
                    label2.setAttribute("for", this.htmlId + "-attachment-" + buttonName);
                    td.appendChild(label2);
                    tr.appendChild(td);

                    YAHOO.util.Event.addListener(tr, "click", function() {
                        button2.checked = true;
                        this._drawAttachments(data.attachments);
                    }.bind(this));

                    tr.appendChild(td);

                    table.appendChild(tr);

                    control.appendChild(table);
                }
            },

            _drawData: function _drawData_Function(items) {
                var select = document.getElementById(this.id);
                select.onclick = function () {
                    YAHOO.Bubbling.fire("mandatoryControlValueUpdated", this);
                }.bind(this);
                select.innerHTML = "";
                select.value = "";
                for (var i = 0; i < items.length; i++) {
                    var option = document.createElement("option");
                    option.value = items[i].nodeRef;
                    option.innerHTML = items[i].label;
                    select.appendChild(option);
                }
                YAHOO.Bubbling.fire("mandatoryControlValueUpdated", this);
            },

            _drawAttachments: function _drawAttachments_Function(items) {
                var select = document.getElementById(this.id);
                select.onclick = function () {
                    YAHOO.Bubbling.fire("mandatoryControlValueUpdated", this);
                }.bind(this);
                select.innerHTML = "";
                select.value = "";
                for (var j = 0; j < items.length; j++) {
                    var category = items[j];
                    var optGroup = document.createElement("optgroup");
                    optGroup.label = category.name;
                    for (var i = 0; i < category.items.length; i++) {
                        var option = document.createElement("option");
                        option.value = category.items[i].nodeRef;
                        option.innerHTML = category.items[i].label;
                        option.title = category.items[i].label;
                        optGroup.appendChild(option);
                    }
                    select.appendChild(optGroup);
                }
                YAHOO.Bubbling.fire("mandatoryControlValueUpdated", this);
            },

	        onReady: function onAfterSetItems_Function () {
                var me = this;
                var url = Alfresco.constants.PROXY_URI + "lecm/contracts/basedOnDocuments?nodeRef={nodeRef}";
                url = YAHOO.lang.substitute(url, {
                    nodeRef: this.options.nodeRef
                });
                var callback = {
                    success:function (oResponse) {
                        var oResults = eval("(" + oResponse.responseText + ")");
                        me.setData(oResults)
                    },
                    timeout:60000
                };
                YAHOO.util.Connect.asyncRequest('GET', url, callback);
            }

        }, true);
})();
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
                    table.cellPadding = 3;
                    table.cellSpacing = 1;
                    table.border = 0;

                    var tr = document.createElement("tr");
                    tr.style.cursor = "pointer";
                    var td = document.createElement("td");

                    var button1 = document.createElement("input");
                    button1.type = "radio";
                    button1.name = buttonName
                    button1.checked = true;
                    td.appendChild(button1);

                    tr.appendChild(td);

                    YAHOO.util.Event.addListener(tr, "click", function() {
                        button1.checked = true;
                        this._drawData(data.links);
                    }.bind(this));

                    this._drawData(data.links);

                    td = document.createElement("td");
                    td.innerHTML = this.msg("contract.contractTime.link");
                    td.width = "100%";
                    tr.appendChild(td);

                    table.appendChild(tr);

                    var tr = document.createElement("tr");
                    tr.style.cursor = "pointer";
                    var td = document.createElement("td");
                    var button2 = document.createElement("input");
                    button2.type = "radio";
                    button2.name = buttonName
                    td.appendChild(button2);

                    YAHOO.util.Event.addListener(tr, "click", function() {
                        button2.checked = true;
                        this._drawAttachments(data.attachments);
                    }.bind(this));

                    tr.appendChild(td);

                    td = document.createElement("td");
                    td.innerHTML = this.msg("contract.contractTime.attachment");
                    td.width = "100%";
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
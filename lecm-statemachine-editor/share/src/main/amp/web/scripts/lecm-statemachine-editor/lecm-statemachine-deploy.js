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
LogicECM.module = LogicECM.module || {};


/**
 * LogicECM StatemachineEditor module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module.StatemachineEditor.StatemachineEditor
 */
LogicECM.module.StatemachineEditor = LogicECM.module.StatemachineEditor || {};

/**
 * StatemachineEditor module.
 *
 * @namespace LogicECM.module
 * @class LogicECM.module.StatemachineEditor
 */
(function () {

    var Dom = YAHOO.util.Dom;
    var Event = YAHOO.util.Event;

    LogicECM.module.StatemachineEditor.Deploy = function (htmlId) {
        return LogicECM.module.StatemachineEditor.Deploy.superclass.constructor.call(
            this,
            "LogicECM.module.StatemachineEditor.Deploy", htmlId,[]);
    };

    YAHOO.extend(LogicECM.module.StatemachineEditor.Deploy, Alfresco.component.Base, {
        data: [],
        currentData: [],
        currentIndex: -1,
        button: null,
        onReady: function () {
            Alfresco.util.Ajax.jsonRequest(
                {
                    url: Alfresco.constants.PROXY_URI + "lecm/statemachine/editor/list",
                    method: "GET",

                    successCallback:
                    {
                        fn: function(response) {
                            var data = eval('(' + response.serverResponse.responseText + ')');
                            this.drawTable(data);
                        },
                        scope: this
                    }
                });
        },

        drawTable: function drawTable_function (data) {
            this.data = data;
            var table = document.createElement("table");
            document.getElementById(this.id).appendChild(table);
            var head = table.createTHead();
            var row = head.insertRow();
            var cell = row.insertCell(-1);
            cell.innerHTML = "<input id='" + this.id + "-select-all' type='checkbox' value='-1' />";
            cell = row.insertCell(-1);
            cell.innerHTML = "Машина состояний";
            cell = row.insertCell(-1);
            cell.innerHTML = "Восстановлена";
            cell = row.insertCell(-1);
            cell.innerHTML = "Развернута";

            for (var i in this.data) {
                var item = data[i];
                var row = table.insertRow();
                var cell = row.insertCell(-1);
                cell.innerHTML = "<input type='checkbox' value='" + i + "' />";
                cell = row.insertCell(-1);
                cell.innerHTML = item.title;
                cell = row.insertCell(-1);
                cell.id = item.id + "_restore";
                cell.innerHTML = "";
                cell = row.insertCell(-1);
                cell.id = item.id + "_deploy";
                cell.innerHTML = "";
            }

            var selectAll = document.getElementById(this.id + "-select-all");
            selectAll.onclick = function(el) {
                this.selectAll(el);
            }.bind(this);

            this.button = Alfresco.util.createYUIButton(this, this.id + "-button", this.start, {}, Dom.get(this.id + "-button"));
        },

        selectAll: function(el) {
            var value = el.target.checked;
            var checkboxes = Dom.getElementsBy(function(el) {return true;}, "input", this.id);
            for (var i in checkboxes) {
                checkboxes[i].checked = value;
            }

        },

        start: function() {
            for (var i in this.data) {
                var id = this.data[i].id;
                document.getElementById(id + "_restore").innerHTML = "";
                document.getElementById(id + "_deploy").innerHTML = "";
            }
            var checkboxes = Dom.getElementsBy(function(el) {return true;}, "input", this.id);
            this.currentData = [];
            for (var i in checkboxes) {
                if (checkboxes[i].checked) {
                    var index = parseInt(checkboxes[i].value);
                    if (index >= 0) {
                        this.currentData.push(this.data[index]);
                    }
                }
            }
            this.button.set("disabled", true);
            for (var i in checkboxes) {
                checkboxes[i].disabled = true;
            }
            this.run();
        },

        run: function() {
            this.currentIndex++;
            if (this.currentData[this.currentIndex] != null) {
                var statemachineId = this.currentData[this.currentIndex].id;
                var me = this;
                var sUrl = Alfresco.constants.PROXY_URI + "/lecm/statemachine/editor/import?default=true&stateMachineId={statemachineId}";
                sUrl = YAHOO.lang.substitute(sUrl, {
                    statemachineId: statemachineId
                });
                document.getElementById(statemachineId + "_restore").innerHTML = "Wait";
                $.ajax({
                    url: sUrl,
                    async: true,
                    success: function (result) {
                        var packageNodeRef = result.packageNodeRef;
                        document.getElementById(statemachineId + "_restore").innerHTML = "OK";
                        var sUrl = Alfresco.constants.PROXY_URI + "lecm/statemachine/editor/diagram";
                        var data = "statemachineNodeRef={statemachineNodeRef}&type=deploy&comment={comment}";
                        data = YAHOO.lang.substitute(data, {
                            statemachineNodeRef: packageNodeRef,
                            comment: "Восстановление машины состояний по умолчанию"
                        });
                        sUrl += "?" + encodeURI(data);
                        document.getElementById(statemachineId + "_deploy").innerHTML = "Wait";
                        $.ajax({
                            url: sUrl,
                            async: true,
                            success: function (result) {
                                document.getElementById(statemachineId + "_deploy").innerHTML = "OK";
                                me.run();
                            },
                            error: function () {
                                document.getElementById(statemachineId + "_deploy").innerHTML = "Error";
                                me.run();
                            }
                        });

                    },
                    error: function () {
                        document.getElementById(statemachineId + "_restore").innerHTML = "Error";
                        me.run();
                    }
                });
            } else {
                this.button.set("disabled", false);
                var checkboxes = Dom.getElementsBy(function(el) {return true;}, "input", this.id);
                for (var i in checkboxes) {
                    checkboxes[i].disabled = false;
                }
                this.currentIndex = -1;
            }
        }
    });
})();

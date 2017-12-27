if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.EDS = LogicECM.module.EDS || {};

(function () {
    var Dom = YAHOO.util.Dom;

    LogicECM.module.EDS.CheckboxSet = function (htmlId) {
        LogicECM.module.EDS.CheckboxSet.superclass.constructor.call(this, "LogicECM.module.EDS.CheckboxSet", htmlId);
        return this;
    };

    YAHOO.extend(LogicECM.module.EDS.CheckboxSet, Alfresco.component.Base,
        {
            controlId: null,

            options: {
                valueSet: {},
                formArguments: {},
                fieldId: null,
                nameColumnId: "label.column.name"
            },

            onReady: function () {
                this.controlId = this.id + "-cntrl";
                if (this.options.valueSet && this.options.valueSet.length) {
                    this.renderTable();
                }
            },

            fillDefaultFromArgs: function (config, args) {
                for (var i = 0; i < config.length; i++) {
                    var option = config[i];
                    var optionKey = this.options.fieldId + '-' + option["key"];
                    if (args[optionKey]) {
                        option["value"] = args[optionKey];
                    }
                }
            },

            renderTable: function() {
                this.fillDefaultFromArgs(this.options.valueSet, this.options.formArguments);

                this.widgets.dataSource = new YAHOO.util.DataSource(this.options.valueSet);
                this.widgets.dataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
                this.widgets.dataSource.responseSchema = {
                    fields: ["key", "name", "value"]
                };

                var columnDefinitions = [
                    {
                        key: "name",
                        label: this.msg(this.options.nameColumnId),
                        sortable: false,
                        formatter: this.fnRenderCellName.bind(this),
                        maxAutoWidth: 400,
                        resizeable: false
                    },
                    {
                        key: "value",
                        label: "<div class='centered'><input name='-' type='checkbox' id='" + this.id + "-select-all-records'></div>",
                        sortable: false,
                        formatter: this.fnRenderCellSelected.bind(this),
                        maxAutoWidth: 100,
                        width: 100,
                        resizeable: false
                    }
                ];

                this.widgets.dataTable = new YAHOO.widget.DataTable(this.id + "-dataTable", columnDefinitions, this.widgets.dataSource);

                YAHOO.util.Event.onAvailable(this.id + "-select-all-records", function () {
                    YAHOO.util.Event.on(this.id + "-select-all-records", 'click', this.selectAllClick, this, true);
                    /*initial state*/
                    this.setSelectAllCheckboxState();
                }, this, true);

                Dom.setStyle(this.id + "-body", "visibility", "visible");

                this.widgets.dataTable.subscribe("checkboxClickEvent", function (e) {
                    var inputId = e.target.id + '-hidden';
                    Dom.get(inputId).value = e.target.checked;

                    this.setSelectAllCheckboxState();
                }, this, true);
            },

            fnRenderCellName: function (elCell, oRecord, oColumn, oData) {
                var nameKey = this.options.fieldId + '-' + oRecord.getData("key");
                var nameFromProperties = this.msg(nameKey);
                elCell.innerHTML = nameFromProperties != nameKey ? nameFromProperties : oRecord.getData("name");
            },

            fnRenderCellSelected: function (elCell, oRecord, oColumn, oData) {
                Dom.setStyle(elCell, "width", oColumn.width + "px");
                Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

                var hiddenHtml =
                    "<input id='" + oRecord.getId() + "-hidden' type='hidden' name='" + this.options.fieldId + "-" + oRecord.getData("key") + "' value='" + oRecord.getData("value") + "'/>";
                var checkboxHtml =
                    "<input id='" + oRecord.getId() + "' type='checkbox' name='-' " + (oRecord.getData("value") == "true" ? " checked='checked'>" : ">");

                elCell.innerHTML = "<div class='centered'>" + (hiddenHtml + checkboxHtml) + "</div>";
            },

            selectAllClick: function () {
                var selectAllElement = Dom.get(this.id + "-select-all-records");
                if (selectAllElement.checked) {
                    this.selectItems("selectAll");
                } else {
                    this.selectItems("selectNone");
                }
            },

            selectItems: function (p_selectType) {
                var fnCheck;
                switch (p_selectType) {
                    case "selectAll":
                        fnCheck = function (checkbox) {
                            if (!checkbox.checked) {
                                checkbox.click();
                            }
                        };
                        break;
                    case "selectNone":
                        fnCheck = function (checkbox) {
                            if (checkbox.checked) {
                                checkbox.click();
                            }
                        };
                        break;
                    default:
                        fnCheck = function (checkbox) {
                            checkbox.click();
                        };
                }

                var checkboxes = Selector.query('input[type="checkbox"]', this.widgets.dataTable.getTbodyEl());
                for (var i = 0; i < checkboxes.length; i++) {
                    fnCheck(checkboxes[i]);
                }
            },

            setSelectAllCheckboxState: function () {
                var selectAllCheckBox = Dom.get(this.id + "-select-all-records");
                if (selectAllCheckBox) {
                    var checkboxes = Selector.query('input[type="checkbox"]', this.widgets.dataTable.getTbodyEl());
                    var allChecked = (checkboxes.length > 0);
                    for (var i = 0; i < checkboxes.length; i++) {
                        allChecked = allChecked && checkboxes[i].checked;
                    }
                    selectAllCheckBox.checked = allChecked;
                }
            }
        });
})();
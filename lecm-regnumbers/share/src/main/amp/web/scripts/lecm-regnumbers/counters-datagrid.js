if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Counters = LogicECM.module.Counters || {};

(function() {

    /**
     * YUI Library aliases
     */
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event,
        Selector = YAHOO.util.Selector,
        Bubbling = YAHOO.Bubbling;

    /**
     * Alfresco Slingshot aliases
     */
    var $html = Alfresco.util.encodeHTML,
        $links = Alfresco.util.activateLinks,
        $combine = Alfresco.util.combinePaths,
        $userProfile = Alfresco.util.userProfileLink;

    LogicECM.module.Counters.DataGrid = function(containerId) {
        LogicECM.module.Counters.DataGrid.superclass.constructor.call(this, containerId);
        return this;
    };

    YAHOO.lang.extend(LogicECM.module.Counters.DataGrid, LogicECM.module.Base.DataGrid);

    YAHOO.lang.augmentObject(LogicECM.module.Counters.DataGrid.prototype, {
        getCustomCellFormatter: function (grid, elCell, oRecord, oColumn, oData) {
            var html = "";
            // Populate potentially missing parameters
            if (!oRecord) {
                oRecord = this.getRecord(elCell);
            }
            if (!oColumn) {
                oColumn = this.getColumn(elCell.parentNode.cellIndex);
            }

            if (oRecord && oColumn) {
                if (!oData) {
                    oData = oRecord.getData("itemData")[oColumn.field];
                }

                if (oData) {
                    var datalistColumn = grid.datagridColumns[oColumn.key];
                    if (datalistColumn) {
                        oData = YAHOO.lang.isArray(oData) ? oData : [oData];
                        for (var i = 0, ii = oData.length, data; i < ii; i++) {
                            data = oData[i];

                            var columnContent = "";
                            switch (datalistColumn.name) { //  меняем отрисовку для конкретных колонок
                                case "lecm-regnum:doctype":
                                    if (data.value && (("" + data.value) != "false")) {
                                        columnContent += (data.value + " (" + grid.getDisplayedType(data.value) + ")");
                                    }
                                    break;
                                default:
                                    break;
                            }

                            if (i < ii - 1) {
                                html += "<br />";
                            }

                            html += columnContent;
                        }
                    }
                }
            }
            return html.length > 0 ? html : null;  // возвращаем NULL чтобы выызвался основной метод отрисовки
        },

        getDisplayedType: function(docType) {
            if (docType && this.options.typesTitles) {
                for (key in this.options.typesTitles) {
                    if (key == docType) {
                        return this.options.typesTitles[key];
                    }
                }
            }
            return '';
        }
    }, true);

    LogicECM.module.Counters.DataGrid.createDatagrid = function (containerId, options, datagridMetadata, messages) {
        var datagrid = new LogicECM.module.Counters.DataGrid(containerId);
        datagrid.setOptions(options);
        datagrid.setMessages(messages);

        // Получение title-ов из моделей типов через api-сервис Alfresco (тип 'lecm-document:base' и его дочерние типы)
        Alfresco.util.Ajax.jsonGet({
            url: Alfresco.constants.PROXY_URI + 'api/classes/lecm-document_base/subclasses',
            successCallback: {
                fn: function (response) {
                    var typesTitles = response.json.reduce(function (previousObject, currentValue) {
                        var attr = {};
                        attr[currentValue.name] = currentValue.title;
                        return YAHOO.lang.merge(previousObject, attr);
                    }, {});
                    datagrid.options.typesTitles = typesTitles;
                    Bubbling.fire('activeGridChanged', {
                        bubblingLabel: options.bubblingLabel,
                        datagridMeta: datagridMetadata
                    });
                }
            },
            failureMessage: datagrid.msg('message.failure')
        });
    };

})();

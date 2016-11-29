if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.SearchQueries = LogicECM.module.SearchQueries || {};


(function () {
    var Dom = YAHOO.util.Dom,
        Bubbling = YAHOO.Bubbling,
        Event = YAHOO.util.Event;

    LogicECM.module.SearchQueries.QueryEditor = function (htmlId) {
        LogicECM.module.SearchQueries.QueryEditor.superclass.constructor.call(this, "LogicECM.module.SearchQueries.QueryEditor", htmlId, ["container", "json"]);

        Bubbling.on("searchQueryChangeDocType", this.onChangeDocType, this);
        Bubbling.on("addNewSearchRow", this.onAddNewRows, this);
        Bubbling.on("updateQueryRow", this.onUpdateQueryRow, this);
        Bubbling.on("searchByConfig", this.onSearchByConfig, this);
        Bubbling.on("deleteAllSearchRows", this.onDeleteAllSearchRows, this);
        Bubbling.on("saveSearchConfigForUser", this.onSaveSearchConfigForUser, this);
        Bubbling.on("docFieldsSet", this.onDocFieldsSet, this);

        this.queryNodeRef = null;
        this.currentDocType = null;
        this.currentDocTypeFields = [];

        return this;
    };

    YAHOO.extend(LogicECM.module.SearchQueries.QueryEditor, Alfresco.component.Base,
        {
            currentDocType: null,
            currentDocTypeFields: [],

            queryNodeRef: null,

            PREFERENCE_KEY: "ru.it.lecm.search-editor.state.",
            
            dataTable: null,

            storeRoot: null,

            saveDialogOpening: false,

            deferredRestoreEvents: null,

            casesMap: {
                "date" : ['EQL', 'BEF', 'AFT'],
                "datetime" : ['EQL', 'BEF', 'AFT'],
                "int" : ['EQL', 'BEQL', 'LEQL'],
                "long" : ['EQL', 'BEQL', 'LEQL'],
                "float" : ['EQL', 'BEQL', 'LEQL'],
                "double" : ['EQL', 'BEQL', 'LEQL'],
                "text" : ['EQL', 'BEGN', 'ENDS', "CONT"],
                "lecm" : ['EQL_ASSOC', 'NEQL_ASSOC'],
                "boolean" : ['EQL', 'NEQL']
            } ,

            preferencesDialog: null,

            operatorKey: "OPERATOR",

            options: {
                restoreFromCookie: false,
                resetCookieOnChange:false,
                bubblingLabel: null
            },
            
            setRoot: function(root) {
                this.storeRoot = root;
            },

            onReady: function () {
                this.setupDataTable();

                if (this.options.restoreFromCookie) {
                    this.deferredRestoreEvents = new Alfresco.util.Deferred(["onChangeDocType"],
                        {
                            fn: this.restoreFromCookie,
                            scope: this
                        });    
                }
            },

            onChangeDocType: function (layer, args) {
                this.currentDocType = args[1].selectedItem;

                this._resetDatagrid();

                if (this.currentDocType != null && this.currentDocType.length > 0) {
                    this.updateDocTypeData();
                }
                if (this.options.restoreFromCookie && this.deferredRestoreEvents) {
                    this.deferredRestoreEvents.fulfil("onChangeDocType");
                }
            },

            setupDataTable: function () {
                var columnDefinitions = this.getDataTableColumnDefinitions();
                if (!this.dataTable) {
                    this.dataTable = new YAHOO.widget.DataTable(this.id + "-grid", columnDefinitions, new YAHOO.util.LocalDataSource([]),
                        {
                            initialLoad: false,
                            dynamicData: false,
                            "MSG_EMPTY": "",
                            "MSG_ERROR": this.msg("message.error"),
                            "MSG_LOADING": this.msg("message.loading")
                        });
                }
            },

            updateDocTypeData: function () {
                Alfresco.util.Ajax.jsonGet(
                    {
                        url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/type/fields?itemType=" + this.currentDocType,
                        successCallback:
                        {
                            fn:function (response) {
                                this.currentDocTypeFields = response.json.fields;
                                this.currentDocTypeFields.sort(function(left, right) {
                                    if (left.label < right.label) {
                                        return -1;
                                    }
                                    if (left.label > right.label) {
                                        return 1;
                                    }
                                    return 0;
                                });
                                Bubbling.fire("docFieldsSet");
                            },
                            scope:this
                        },
                        failureCallback:
                        {
                            fn:function () {
                                Alfresco.util.PopupManager.displayMessage(
                                    {
                                        text:this.msg("message.failure")
                                    });
                            },
                            scope:this
                        }
                    });
            },

            getDataTableColumnDefinitions:function () {
                var columnDefinitions = [];

                columnDefinitions.push({
                    key: "checkbox",
                    label: "",
                    sortable: false,
                    formatter: this.fnRenderCellDeleted (),
                    width: 24
                });

                columnDefinitions.push({
                    key:"selector",
                    label:"",
                    sortable:false,
                    formatter:this.fnRenderCellSelector(),
                    className: 'centered'
                });

                columnDefinitions.push({
                    key:"case",
                    label:"",
                    sortable:false,
                    formatter:this.fnRenderCellCase(),
                    className: 'centered',
                    width:100
                });

                columnDefinitions.push({
                    key:"control",
                    label:"",
                    sortable:false,
                    formatter:this.fnRenderCellControl(),
                    className: 'centered control-width-limited'
                });

                return columnDefinitions;
            },

            buildSearchConfig: function (isEncode) {
                var config = {
                    docType: this.currentDocType,
                    attributes: []
                };
                if (this.currentDocType && this.currentDocType.length > 0) {
                    var recordSet = this.dataTable.getRecordSet();
                    if (recordSet.getLength() > 0) {
                        for (var i = 0; i <= recordSet.getLength(); i++) {
                            var record = recordSet.getRecord(i);
                            if (record) {
                                var fieldId = this._getFieldIdByRecord(record);
                                var attrConf = {
                                    id: !record.getData().isOperator ? fieldId : this.operatorKey,
                                    type: !record.getData().isOperator ? this._getTypeByFieldId(fieldId, false) : this.operatorKey,
                                    "case": !record.getData().isOperator ? this._getCaseByRecord(record) : this.operatorKey,
                                    value: !record.getData().isOperator ? (isEncode ? encodeURIComponent(this._getValueByRecord(record).trim()) : this._getValueByRecord(record)): this._getOperatorByRecord(record)
                                };
                                config.attributes.push(attrConf);
                            }
                        }
                    }
                } else {
                    Alfresco.util.PopupManager.displayMessage(
                        {
                            text:this.msg("msg.editor.not_select_doc_type")
                        });
                }
                return config;
            },

            fnRenderCellDeleted: function () {
                var scope = this;

                return function (elCell, oRecord, oColumn, oData) {
                    Dom.setStyle(elCell, "width", oColumn.width + "px");
                    Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");
                    var html = '';
                    if (!oRecord.getData().isOperator) {
                        html = '<div class="onActionDelete" id="onActionDelete-' +
                            oRecord.getId() + '"><a id="' + oRecord.getId() +'-delete" rel="edit" class="datagrid-action-link" title="' +
                            scope.msg("msg.action.delete") + '"></a></div>';
                    }
                    elCell.innerHTML = html;

                    if (!oRecord.getData().isOperator) {
                        YAHOO.util.Event.onContentReady(oRecord.getId() + '-select', function() {
                            var a = Dom.get(oRecord.getId() + '-delete');
                            Event.on(
                                a, 'click', function (e) {
                                    scope.onDelete([oRecord]);
                                });
                        });
                    }
                };
            },

            fnRenderCellSelector: function ()
            {
                var scope = this;

                return function (elCell, oRecord, oColumn, oData)
                {
                    Dom.setStyle(elCell, "width", oColumn.width + "px");
                    Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");
                    var html = '';
                    if (!oRecord.getData().isOperator) {
                        html = '<select name="' + oRecord.getId() + '-select' +'" id="'
                            + oRecord.getId() + '-select' + '" class="fld-slt">';
                        for (var i = 0; i < scope.currentDocTypeFields.length; i++) {
                            html += '<option value="' + scope.currentDocTypeFields[i].name + '"' +
                            ((oRecord.getData().selector && oRecord.getData().selector == scope.currentDocTypeFields[i].name) ? ' selected="selected"' : '') +
                                '>';
                            html += scope.currentDocTypeFields[i].label;
                            html += '</option>';
                        }
                        html += '</select>';
                    } else {
                        html = '<select name="' + oRecord.getId() + '-case' +'" id="'
                            + oRecord.getId() + '-operator' + '" class="case-oper">';
                        html += '<option value="OR"' +
                        ((oRecord.getData().selector && oRecord.getData().selector == 'OR') ? ' selected="selected"' : '') +
                            '>';
                        html += scope.msg("msg.operator.OR");
                        html += '</option>'
                        ;
                        html += '<option value="AND"' +
                        ((oRecord.getData().selector && oRecord.getData().selector == 'AND') ? ' selected="selected"' : '') +
                            '>';
                        html += scope.msg("msg.operator.AND");
                        html += '</option>';

                        html += '</select>';
                    }

                    elCell.innerHTML = html;

                    if (!oRecord.getData().isOperator) {
                        YAHOO.util.Event.onContentReady(oRecord.getId() + '-select', function() {
                            var select = Dom.get(oRecord.getId() + '-select');
                            Bubbling.fire("updateQueryRow", {record: oRecord, fieldId: select.value});
                            Event.on(
                                select, 'change', function (e) {
                                    Bubbling.fire("updateQueryRow", {record: oRecord, fieldId: select.value})
                                });
                        });
                    }
                };
            },

            fnRenderCellCase: function () {
                // фиктивный рендеринг - актуальный в updateQueryRow
                return function (elCell, oRecord, oColumn, oData) {
                    Dom.setStyle(elCell, "width", oColumn.width + "px");
                    Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

                    var html = '';
                    if (!oRecord.getData().isOperator) {
                        html = '<select name="' + oRecord.getId() + '-case' + '" id="' + oRecord.getId() + '-case' + '" class="case-slt"';
                        html += '</select>';
                    }

                    elCell.innerHTML = html;
                };
            },

            fnRenderCellControl: function () {
                // фиктивный рендеринг - актуальный в updateQueryRow
                return function (elCell, oRecord, oColumn, oData) {
                    Dom.setStyle(elCell, "width", oColumn.width + "px");
                    Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

                    var html = '';
                    if (!oRecord.getData().isOperator) {
                        html = elCell.innerHTML = '<div class="form-container">'
                            + '<div id="' + oRecord.getId() + '-control">' +
                            '<div class="value-div">' +
                            '<input id="' + oRecord.getId() + '"/>' +
                            '</div>' +
                            '</div>' +
                            '</div>';
                    }

                    elCell.innerHTML = html;
                };
            },
            
            onAddNewRows: function () {
                if (this.dataTable && this.currentDocType !== null && this.currentDocType.length > 0) {
                    //+ operator
                    if (this.dataTable.getRecordSet() && this.dataTable.getRecordSet().getLength() > 0) {
                        this.dataTable.addRow({
                            checkbox: this.operatorKey,
                            selector: this.operatorKey,
                            "case": this.operatorKey,
                            control: this.operatorKey,
                            isOperator: true
                        })
                    }
                    //field
                    this.dataTable.addRow({
                        checkbox: "",
                        selector: "",
                        "case": "",
                        control: "",
                        isOperator: false
                    });
                    Dom.setStyle(this.id + "-body", "visibility", "visible");
                } else {
                    Alfresco.util.PopupManager.displayMessage(
                        {
                            text:this.msg("msg.editor.not_select_doc_type")
                        });
                }
            },

            onSearchByConfig: function () {
                var config = this.buildSearchConfig(true);
                var me = this;
                if (config.docType && config.attributes) {
                    Alfresco.util.Ajax.request(
                        {
                            method: "POST",
                            url: Alfresco.constants.PROXY_URI  + "lecm/components/search/query/byConfig",
                            dataObj: {
                                config: typeof  config == "object" ? YAHOO.lang.JSON.stringify(config) : config
                            },
                            successCallback: {
                                fn: function (response) {
                                    var queryObj = eval("(" + response.serverResponse.responseText + ")");
                                    config.query = queryObj.query;
                                    config.queryNodeRef = me.queryNodeRef;
                                    YAHOO.Bubbling.fire("activeGridChanged",
                                        {
                                            datagridMeta: {
                                                itemType: config.docType,
                                                useFilterByOrg: true,
                                                searchConfig: {
                                                    filter: decodeURIComponent(queryObj.query)
                                                }
                                            },
                                            config:config,
                                            bubblingLabel: me.options.bubblingLabel
                                        });
                                }
                            },
                            failureMessage: "message.failure",
                            execScripts: true
                        });
                }
            },

            onSaveSearchConfigForUser: function () {
                if (this.saveDialogOpening) return;

                if(this.currentDocType == null || this.currentDocType == '') {
                    Alfresco.util.PopupManager.displayMessage(
                        {
                            text:this.msg("msg.editor.not_select_doc_type")
                        });
                    return;
                }

                this.saveDialogOpening = true;
                var me = this;
                if (this.storeRoot && this.storeRoot != '') {
                    var isCreate = (this.queryNodeRef == null);
                    // Intercept before dialog show
                    var doBeforeDialogShow = function DataGrid_onActionEdit_doBeforeDialogShow(p_form, p_dialog) {
                        var contId = p_dialog.id + "-form-container";
                        Alfresco.util.populateHTML(
                            [contId + "_h", isCreate ? me.msg("msg.editor.new_query") :  me.msg("msg.editor.edit_query") ]
                        );
                        me.saveDialogOpening = false;
                        p_dialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, {moduleId: p_dialog.id}, this);
                    };

                    var templateUrl = Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form";
                    var templateRequestParams =  {
                        itemKind: isCreate ? "type" : "node",
                        itemId: isCreate ? "lecm-search-queries:dic" : this.queryNodeRef,
                        destination:me.storeRoot,
                        mode: isCreate ? "create" : "edit",
                        formId: "",
                        submitType:"json",
                        showCancelButton: true
                    };

                    var createDetails = new Alfresco.module.SimpleDialog(this.id + "-createDetails");
                    createDetails.setOptions(
                        {
                            width:"50em",
                            templateUrl:templateUrl,
                            templateRequestParams:templateRequestParams,
                            actionUrl:null,
                            destroyOnHide:true,
                            doBeforeDialogShow:{
                                fn:doBeforeDialogShow,
                                scope:this
                            },
                            doBeforeAjaxRequest: {
                                fn: function (form) {
                                    var dataObj = form.dataObj;
                                    dataObj['prop_lecm-search-queries_query-setting'] = YAHOO.lang.JSON.stringify(me.buildSearchConfig(false));
                                    return true;
                                }
                            },
                            onSuccess:{
                                fn:function DataGrid_onActionCreate_success(response) {
                                    Alfresco.util.PopupManager.displayMessage(
                                        {
                                            text:me.msg("msg.editor.query.save")
                                        });
                                    me.saveDialogOpening = false;
                                    Bubbling.fire("armRefreshParentSelectedTreeNode");
                                },
                                scope:this
                            },
                            onFailure:{
                                fn:function DataGrid_onActionCreate_failure(response) {
                                    Alfresco.util.PopupManager.displayMessage(
                                        {
                                            text:me.msg("msg.editor.query.not.saved")
                                        });
                                    me.saveDialogOpening = false;
                                },
                                scope: createDetails
                            }
                        }).show();
                } else {
                    Alfresco.util.PopupManager.displayMessage(
                        {
                            text:me.msg("msg.editor.root.not.found")
                        });
                }
            },

            restoreFromCookie: function () {
                // load from Cookie -> load data from Doc Type -> restore Rows
                var cookieConfig = LogicECM.module.Base.Util.getCookie(this.PREFERENCE_KEY  + Alfresco.constants.USERNAME);
                if (cookieConfig !== null) {
                    var config = YAHOO.lang.JSON.parse(cookieConfig);
                    if (config.docType && config.attributes) {
                        this.deferredRestoreEvents = new Alfresco.util.Deferred(["onDocFieldsSet"],
                            {
                                fn: function () {
                                    this.queryNodeRef = config.queryNodeRef;
                                    return this._restoreTableData(config.attributes);
                                },
                                scope: this
                            });

                        this._changeType(config.docType); // fire event onDocFieldsSet
                    }
                }
            },

            _restoreTableData: function(rows) {
                this.deferredRestoreEvents = null;

                this._resetDatagrid();

                var docTypeSelect = Dom.get(this.id + "_searchQuery-selectType");
                if (docTypeSelect) {
                    docTypeSelect.value = this.currentDocType;
                }

                for (var i = 0; i < rows.length; i++) {
                    var row = rows[i]; // Пример, {"id":"lecm-errands:on-control","type":"d:boolean","case":"EQL","value":"true"}
                    this.dataTable.addRow({
                        checkbox: "",
                        selector: row.id != this.operatorKey ? row.id : row.value,
                        "case": row["case"],
                        control: decodeURIComponent(row.value),
                        type: row.type,
                        isOperator: row.id == this.operatorKey
                    });
                }
                if (rows.length > 0) {
                    Dom.setStyle(this.id + "-body", "visibility", "visible");
                }
                if(this.options.resetCookieOnChange) {
                    LogicECM.module.Base.Util.setCookie(this.PREFERENCE_KEY  + Alfresco.constants.USERNAME, '{}', {});
                }
            },

            onDocFieldsSet: function() {
                if (this.deferredRestoreEvents) {
                    this.deferredRestoreEvents.fulfil("onDocFieldsSet");
                }
            },

            onUpdateQueryRow : function (layer, args) {
                var record = args[1].record;
                if (record !== null) {
                    record.getData().type = this._getTypeByFieldId(args[1].fieldId, true);
                    // обновить условие
                    var casesArray = [];
                    if (this.casesMap[record.getData().type] == null) {
                        casesArray = this.casesMap["lecm"];
                    } else {
                        casesArray = this.casesMap[record.getData().type];
                    }

                    var select = document.getElementById(record.getId() + '-case');
                    if (select) {
                        while (select.firstChild) {
                            select.removeChild(select.firstChild);
                        }
                        for (var i = 0; i < casesArray.length; i++) {
                            var option = document.createElement("option");
                            option.value = casesArray[i];
                            option.innerHTML = this.msg("msg.case." + casesArray[i]);
                            option.selected = (casesArray[i] == record.getData()["case"]);
                            select.appendChild(option);
                        }
                    }

                    // обновить контрол
                    var params = {
                        defaultValue: record.getData().control ? record.getData().control : '',
                        docType: this.currentDocType,
                        endpointMany: true,
                        showCreateNewButton: false,
                        showCreateNewLink: false
                    };

                    // раз мы изменили выбранное поле - надо сбросить значение в контроле
                    record.getData().control = '';

                    var fieldObj = this._getFieldObjById(args[1].fieldId);
                    if (fieldObj != null) {
                        if (fieldObj.control && fieldObj.control.params) {
                            for (var p = 0; p < fieldObj.control.params.length; p++) {
                                var par = fieldObj.control.params[p];
                                params[par.name] = par.value;
                            }
                        }
                    }
                    var isAssocType = this.casesMap[record.getData().type] == null;

                    var dataObj = {
                        fieldId: args[1].fieldId.split(':').join('_'),
                        labelId: args[1].fieldId.split(':').join('_'),
                        type: isAssocType ? record.getData().type : ("d:" + record.getData().type),
                        params: YAHOO.lang.JSON.stringify(params),
                        htmlid: this.id + '-' + record.getId() + '-ctrl'
                    };
                    if (fieldObj.control != null && fieldObj.control.template != null) {
                        dataObj.template = fieldObj.control.template;
                    }
                    Alfresco.util.Ajax.request(
                        {
                            url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/control",
                            dataObj: dataObj,
                            successCallback: {
                                fn: function (response) {
                                    var container = Dom.get(record.getId() + '-control');
                                    if (container != null) {
                                        container.innerHTML = response.serverResponse.responseText;
                                    }
                                }
                            },
                            failureMessage: "message.failure",
                            execScripts: true
                        });
                }
            },

            onDelete: function DataGridActions_onDelete(p_items){
                var items = YAHOO.lang.isArray(p_items) ? p_items : [p_items];

                if (items.length == 0) {
                    Alfresco.util.PopupManager.displayMessage(
                        {
                            text:this.msg("msg.editor.not_select_rows")
                        });
                    return;
                }
                var recordFound, el,
                    fnCallback = function (record) {
                        return function DataGrid_onDataItemsDeleted_anim() {
                            var rowIndex = this.dataTable.getRecordIndex(record);

                            var prevRecord = this.dataTable.getRecordSet().getRecords()[rowIndex - 1];
                            var nextRecord = rowIndex == 0 ? this.dataTable.getRecordSet().getRecords()[rowIndex + 1] : null;

                            this.dataTable.deleteRow(rowIndex); // текущая строка

                            if (prevRecord && prevRecord.getData().isOperator) {
                                this.dataTable.deleteRow(prevRecord);
                            }
                            if (nextRecord && nextRecord.getData().isOperator) {
                                this.dataTable.deleteRow(nextRecord);
                            }

                            if (this.dataTable.getRecordSet().getLength() == 0) {
                                Dom.setStyle(this.id + "-body", "visibility", "hidden");
                            }
                        };
                    };

                for (var i = 0, ii = items.length; i < ii; i++) {
                    recordFound = this._findRecordById(items[i].getId());
                    if (recordFound !== null)
                    {
                        el = this.dataTable.getTrEl(recordFound);
                        Alfresco.util.Anim.fadeOut(el,
                            {
                                callback: fnCallback(recordFound),
                                scope: this
                            });
                    }
                }
            },

            _getTypeByFieldId: function (fieldId, realTypeForAssoc) {
                if (fieldId) {
                    var fieldObj = this._getFieldObjById(fieldId);
                    if (fieldObj != null) {
                        if (!realTypeForAssoc && fieldObj.type == "association") {
                            return fieldObj.type;
                        } else {
                            return fieldObj.dataType;
                        }
                    }
                }
                return null;
            },

            _getFieldObjById: function (fieldId) {
                if (fieldId) {
                    for (var i = 0, ii = this.currentDocTypeFields.length; i < ii; i++) {
                        var docTypeField = this.currentDocTypeFields[i];
                        if (docTypeField.name == fieldId) {
                            return docTypeField;
                        }
                    }
                }
                return null;
            },

            onDeleteAllSearchRows: function () {
                this._resetDatagrid();
                if(this.options.resetCookieOnChange) {
                    LogicECM.module.Base.Util.setCookie(this.PREFERENCE_KEY  + Alfresco.constants.USERNAME, '{}', {});
                }
            },

            _findRecordById: function (p_value) {
                var recordSet = this.dataTable.getRecordSet();
                var index = 0;
                for (var i = index, j = recordSet.getLength(); i < j; i++) {
                    if (recordSet.getRecord(i).getId() == p_value) {
                        return recordSet.getRecord(i);
                    }
                }
                return null;
            },

            _getFieldIdByRecord: function(record){
                var select = Dom.get(record.getId() + '-select');
                if (select) {
                    return select.value;
                }
                return null;
            },

            _getCaseByRecord: function(record) {
                var select = Dom.get(record.getId() + '-case');
                if (select) {
                    return select.value;
                }
                return null;
            },

            _getValueByRecord: function (record) {
                var input = Dom.get(this.id + '-' + record.getId() + '-ctrl_' + this._getFieldIdByRecord(record).split(':').join('_'));
                if (input) {
                    return input.value;
                }
                return null;
            },

            _getOperatorByRecord: function(record) {
                var select = Dom.get(record.getId() + '-operator');
                if (select) {
                    return select.value;
                }
                return null;
            },

            _changeType: function(docType){
                var args = [];
                args.push({});
                args.push({selectedItem: docType});

                this.onChangeDocType(null, args);
            },
            
            _resetDatagrid: function() {
                if (this.dataTable && this.dataTable.getRecordSet() && this.dataTable.getRecordSet().getLength() > 0) {
                    this.dataTable.getRecordSet().reset();
                    this.dataTable.render();

                    Dom.setStyle(this.id + "-body", "visibility", "hidden");
                }

                YAHOO.Bubbling.fire("resetDataGrid",
                    {
                        bubblingLabel: this.options.bubblingLabel
                    });
            }
        });
})();

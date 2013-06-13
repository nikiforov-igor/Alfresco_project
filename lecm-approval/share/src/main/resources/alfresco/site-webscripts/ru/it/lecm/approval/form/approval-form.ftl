<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign htmlId = args.htmlid>

<#assign formId = htmlId + "-form">
<#assign controlId = htmlId + "-cntrl">
<#assign datagridId = htmlId + "-datagrid">
<#assign selectId = "assoc_lecmApprove_assigneesListAssoc_added">
<#assign formContainerId = htmlId + "-container">

<#assign addAssigneeButtonId = htmlId + "-add-assignee-button">
<#assign newAssigneesListButtonId = htmlId + "-save-assignees-list-button">
<#assign deleteAssigneesListButtonId = htmlId + "-delete-assignees-list-button">

<div id="${formContainerId}">
<#if formUI == "true">
    <@formLib.renderFormsRuntime formId = formId />
</#if>

<@formLib.renderFormContainer formId = formId>

    <!-- Тип согласования (default: SEQUENTIAL) -->
    <input id="prop_lecmApprove_approvalType" name="prop_lecmApprove_approvalType" value="SEQUENTIAL" type="hidden"/>

    <div class="set">
        <div class="set-panel">
            <div class="set-panel-heading">Тип и срок</div>
            <div class="set-panel-body">
                <div class="form-field">
                    <label for="approval-type-radio-buttons-container">Тип согласования:</label>
                    <div id="approval-type-radio-buttons-container"></div>
                </div>

                <!-- Срок согласования -->
                <@formLib.renderField field = form.fields["prop_bpm_workflowDueDate"] />
            </div>
        </div>
    </div>

    <div class="set">
        <div class="set-panel">
            <div class="set-panel-heading">Согласующие</div>
            <div class="set-panel-body">
                <div class="form-field">
                    <label for="${selectId}">Список:</label>
                    <select id="${selectId}" style="width: 28em;"></select>
                    <span class="create-new-button">
                        <button id="${newAssigneesListButtonId}"></button>
                    </span>
                    <span class="delete-button">
                        <button id="${deleteAssigneesListButtonId}"></button>
                    </span>
                </div>

                <button id="${addAssigneeButtonId}"></button>
                <div class="form-field with-grid">
                    <@grid.datagrid datagridId false />
                </div>
            </div>
        </div>
    </div>

    <!-- Магические поля -->
    <@formLib.renderField field = form.fields["assoc_packageItems"] /><!-- Hidden! -->
    <@formLib.renderField field = form.fields["prop_bpm_workflowDescription"] /><!-- Hidden! -->
</@>
</div>

<script>
(function() {
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event,
        Bubbling = YAHOO.Bubbling,
        ComponentManager = Alfresco.util.ComponentManager;








    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // AssigneesGrid ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    LogicECM.module.AssigneesGrid = function( htmlId ) {
        LogicECM.module.AssigneesGrid.superclass.constructor.call( this, htmlId );
        this.options.approvalType = "sequential";
        this.options.currentList = "";

        return this;
    };

    YAHOO.lang.extend( LogicECM.module.AssigneesGrid, LogicECM.module.Base.DataGrid );

    YAHOO.lang.augmentObject( LogicECM.module.AssigneesGrid.prototype, {

        /**
         * Обновляет таблицу
         *
         * @method AssigneesGrid_refreshDatagrid
         */
        refreshDatagrid: function AssigneesGrid_refreshDatagrid( response ) {
            YAHOO.Bubbling.fire( "activeGridChanged", {
                bubblingLabel: "${datagridId}-label",

                datagridMeta: {
                    nodeRef: response.json.assigneesListNodeRef,
                    itemType: "lecm-al:assignees-item",
                    sort: "lecm-al:assignees-item-order|true",
                    actionsConfig: {
                        fullDelete: true,
                        targetDelete: true
                    }
                }
            });
        },

        /**
         * Перемещает согласующего вверх по списку и обновляет таблицу
         *
         * @method AssigneesGrid_onMoveUp
         */
        onMoveUp: function AssigneesGrid_onMoveUp( items ) {

            var currentNodeRef = items.nodeRef,
                    dataObj = { "assigneeItemNodeRef": currentNodeRef, "moveDirection": "up" };

            Alfresco.util.Ajax.request({
                method: "POST",
                url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/approval/changeOrder",
                dataObj: dataObj,
                requestContentType: "application/json",
                responseContentType: "application/json",
                successCallback: {
                    fn: this.refreshDatagrid
                },
                failureCallback: {
                    fn: function() {
                        Alfresco.util.PopupManager.displayMessage({
                            text: "Не удалось переместить согласующего вверх по списку"
                        });
                    }
                }
            });

        },

        /**
         * Перемещает согласующего вниз по списку и обновляет таблицу
         *
         * @method AssigneesGrid_onMoveDown
         */
        onMoveDown: function AssigneesGrid_onMoveDown( items ) {

            var currentNodeRef = items.nodeRef, // {String}
                    dataObj = { "assigneeItemNodeRef": currentNodeRef, "moveDirection": "down" };

            Alfresco.util.Ajax.request({
                method: "POST",
                url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/approval/changeOrder",
                dataObj: dataObj,
                requestContentType: "application/json",
                responseContentType: "application/json",
                successCallback: {
                    fn: this.refreshDatagrid
                },
                failureCallback: {
                    fn: function () {
                        Alfresco.util.PopupManager.displayMessage({
                            text: "Не удалось переместить согласующего вниз по списку"
                        });
                    }
                }
            });
        }
    }, true);
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // AssigneesGrid ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////








    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ApprovalForm ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    LogicECM.module.ApprovalForm = function( htmlId, currentValueHtmlId ) {
        // Обязательные свойства
        this.name = "LogicECM.module.ApprovalForm";
        this.id = htmlId;
        this.currentValueHtmlId = currentValueHtmlId;

        /* Регистрируем компонент */
        Alfresco.util.ComponentManager.register( this );

        // Инициализация свойств прототипа
        this.widgets = {};

        this.options.dates = {
            startDate: new Date(),
            endDate: new Date( 2013, 11, 31 ), // 31.12.2013
            restrictedDates: [ new Date( 2013, 5, 8 ), new Date( 2013, 5, 11 ), new Date( 2013, 5, 13 ) ]
        };

        this.init();

        return this;
    };

    LogicECM.module.ApprovalForm.prototype =
    {
        options: {
            dates: null,
            currentList: null
        },

        widgets: null,

        constants: {
            URL_FORM: "lecm/components/form",
            LISTS_FOLDER_REF: null
        },

        _addBubblingLayers: function approvalForm_addBubblingLayers() {
            Bubbling.on( "registerValidationHandler", this._customizeCalendar, this );
        },

        handlers: {

            _onNewAssigneesListButtonClick: function approvalForm_onNewAssigneesListButtonClick() {

                debugger;

                if ( this.constants.LISTS_FOLDER_REF === null ) {
                    return false;
                }

                this.widgets.addAssigneesListForm = new Alfresco.module.SimpleDialog( "${formId}-form-add-assignees-list" );

                this.widgets.addAssigneesListForm.setOptions({
                    width: "50em",
                    templateUrl: Alfresco.constants.URL_SERVICECONTEXT + this.constants.URL_FORM,
                    templateRequestParams: {
                        itemKind: "type",
                        itemId: "lecm-al:assignees-list",
                        destination: this.constants.LISTS_FOLDER_REF,
                        mode: "create",
                        submitType: "json",
                        showCancelButton: "true"
                    },
                    destroyOnHide: true,
                    onSuccess: {
                        fn: this.fillApprovalListMenu,
                        scope: this
                    },
                    onFailure: {
                        fn: function() {
                            Alfresco.util.PopupManager.displayMessage({
                                text: "Не удалось создать новый список согласующих, попробуйте переоткрыть форму"
                            });
                        },
                        scope: this
                    }
                });

                debugger;

                this.widgets.addAssigneesListForm.show();

                return true;
            },

            /**
             * Обрабатывает смену типа согласования, в зависимости от типа показывает/скрывает необходимые столбцы в таблице
             *
             * @method approvalForm_onApprovalTypeChange
             */
            _onApprovalTypeChange: function approvalForm_onApprovalTypeChange( event ) {
                var dataTable = this.widgets.assigneesDatagrid.widgets.dataTable,
                        approvalTypeInput = Dom.get( "prop_lecmApprove_approvalType" );

                if( event.newValue.get( "value" ) === "parallel" ) {
                    approvalTypeInput.value = "PARALLEL";

                    this.widgets.assigneesDatagrid.setOptions({ approvalType: "parallel" });
                    dataTable.hideColumn( 0 );
                    dataTable.hideColumn( 1 );
                } else {
                    approvalTypeInput.value = "SEQUENTIAL";

                    this.widgets.assigneesDatagrid.setOptions({ approvalType: "sequential" });
                    dataTable.showColumn( 0 );
                    dataTable.showColumn( 1 );
                }
            },

            /**
             * Обрабатывает нажатие на кнопку 'Добавить согласующего'
             *
             * @method approvalForm_onApprovalTypeChange
             */
            _onAddAssigneeButtonClick: function approvalForm_onAddAssigneeButtonClick() {
                this.widgets.addAssigneeForm = new Alfresco.module.SimpleDialog( "${formId}-form-add-assignee" );

                this.widgets.addAssigneeForm.setOptions({
                    width: "50em",
                    templateUrl: Alfresco.constants.URL_SERVICECONTEXT + this.constants.URL_FORM,
                    templateRequestParams: {
                        itemKind: "type",
                        itemId: "lecm-al:assignees-item",
                        destination: this.widgets.$assigneesListSelectElem.val(),
                        mode: "create",
                        submitType: "json",
                        showCancelButton: "true",
                        ignoreNodes: this.getCurrentNodeRefs( "employee" )
                    },
                    destroyOnHide: true,
                    onSuccess: {
                        fn: this.refreshDatagrid,
                        scope: this
                    },
                    onFailure: {
                        fn: function() {
                            Alfresco.util.PopupManager.displayMessage({
                                text: "При добавлении согласующего произошла ошибка, попробуйте переоткрыть форму. Код ошибки: addAssigneeForm.onFailure"
                            });
                        },
                        scope: this
                    }
                });

                debugger;

                this.widgets.addAssigneeForm.show();
            },

            /**
             * Обрабатывает нажатие на кнопку 'Удалить список согласующих'
             *
             * @method approvalForm_onВeleteAssigneesListButtonClick
             */
            _onDeleteAssigneesListButtonClick: function approvalForm_onDeleteAssigneesListButtonClick( event ) {
                var currentSelectedListRef = this.widgets.$assigneesListSelectElem.attr( "value" );

                debugger;

                if( currentSelectedListRef.indexOf( "workspace://" ) === -1 ) {
                    return false;
                }

                Alfresco.util.Ajax.request({
                    method: "POST",
                    url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/approval/delete",
                    dataObj: { "nodeRef": currentSelectedListRef },
                    requestContentType: "application/json",
                    responseContentType: "application/json",
                    successCallback: {
                        fn: function() {
                            Alfresco.util.PopupManager.displayMessage({
                                text: "Список удалён"
                            });

                            this.fillApprovalListMenu();
                        },
                        scope: this
                    },
                    failureCallback: {
                        fn: function () {
                            Alfresco.util.PopupManager.displayMessage({
                                text: "Не удалось удалить список, попробуйте ещё раз"
                            });
                        }
                    }
                });

                return true;
            }
        },

        /**
         * Возвращает nodeRef'ы всех согласующих из текущего списка (datagrid'а) согласующих
         *
         * @method approvalForm_getCurrentNodeRefs
         */
        getCurrentNodeRefs: function approvalForm_getCurrentNodeRefs( nodeRefType ) {
            var datagrid = this.widgets.assigneesDatagrid,
                dataTable = datagrid.widgets.dataTable,
                recordsSet = dataTable.getRecordSet(),
                records = recordsSet.getRecords(),
                result = [],
                i;

            if( records.length === 0 ) {
                return "";
            }

            for( i = 0; i < records.length; ++i ) {
                switch ( nodeRefType ) {
                    case "employee":
                        result.push( records[ i ].getData( "itemData" )[ "assoc_lecm-al_assignees-item-employee-assoc" ].value );
                        break;
                    case "assignee":
                        result.push( records[ i ].getData( "nodeRef" ) );
                        break;
                }
            }

            return result.join();
        },

        refreshDatagrid: function approvalForm_refreshDatagrid() {
            YAHOO.Bubbling.fire( "activeGridChanged", {
                bubblingLabel: "${datagridId}-label",

                datagridMeta: {
                    itemType: "lecm-al:assignees-item",
                    nodeRef: this.widgets.$assigneesListSelectElem.val(),
                    sort: "lecm-al:assignees-item-order|true",
                    actionsConfig: {
                        fullDelete: true,
                        targetDelete: true
                    }
                }
            });
        },

        /**
         * Получает ссылку на корневую папку списков согласования
         */
        _getListsFolderRef: function() {
            function onSuccess( response ) {
                debugger;
                this.constants.LISTS_FOLDER_REF = response.json.approvalListFolderRef;
            }

            function onFailure() {
                Alfresco.util.PopupManager.displayMessage({
                    text: "Не удалось получить ссылку на корневую папку списков согласования"
                });
            }

            Alfresco.util.Ajax.request({
                method: "POST",
                url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/approval/getCurrentUserApprovalListFolder",
                requestContentType: "application/json",
                responseContentType: "application/json",
                successCallback: {
                    fn: onSuccess,
                    scope: this
                },
                failureCallback: {
                    fn: onFailure
                }
            });
        },

        /**
         * Заполняет dropDownList перечнем сохранённых списков согласования
         *
         * @method sortFunction
         * @param a {object} Sort record a
         * @param b {object} Sort record b
         * @param desc {boolean} Ascending/descending flag
         * @param field {String} Field to sort by
         */
        fillApprovalListMenu: function approvalForm_fillApprovalListMenu( listToSelect ) {

            debugger;

            if ( listToSelect && YAHOO.lang.isString( listToSelect ) === false ) {
                listToSelect = listToSelect.json.persistedObject;
            }

            function onGetAllListsSuccess( response ) {

                var assigneesLists = response.json.lists,
                        defaultListRef = response.json.defaultListRef,
                        $selectElem = this.widgets.$assigneesListSelectElem,
                        selectElem = this.widgets.assigneesListSelectElem,
                        i = 0,
                        optionElemAttributes;

                if ( selectElem.get( "options" ).length > 0 ) {
                    // To avoid memory leaks, jQuery removes other constructs such as data and event handlers from the
                    // child elements before removing the elements themselves. http://api.jquery.com/empty/
                    $selectElem.empty();
                }

                for( ; i < assigneesLists.length; ++i ) {
                    if ( assigneesLists[ i ].nodeRef === defaultListRef ) {
                        optionElemAttributes = {
                            value: assigneesLists[ i ].nodeRef,
                            text: assigneesLists[ i ].listName,
                            selected: true
                        }
                        $( "<option/>", optionElemAttributes ).appendTo( $selectElem );

                        assigneesLists.splice( i, 1 );

                        break;
                    }
                }

                for ( i = 0; i < assigneesLists.length; ++i ) {
                    optionElemAttributes = {
                        value: assigneesLists[ i ].nodeRef,
                        text: assigneesLists[ i ].listName,
                        selected: assigneesLists[ i ].nodeRef === ( listToSelect || defaultListRef )
                    };

                    $( "<option/>", optionElemAttributes ).appendTo( $selectElem );
                }

                this.refreshDatagrid();
            }

            Alfresco.util.Ajax.request({
                method: "POST",
                url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/approval/getAllLists",
                requestContentType: "application/json",
                responseContentType: "application/json",
                successCallback: {
                    fn: onGetAllListsSuccess,
                    scope: this
                },
                failureCallback: {
                    fn: function() {
                        Alfresco.util.PopupManager.displayMessage({
                            text: "Не удалось получить перечень списков согласующих"
                        });
                    },
                    scope: this
                }
            });
        },

        /**
         * События
         *
         * @method approvalForm_initAddAssigneeButton
         */
        _initEvents: function approvalForm_initEvents() {
            this.widgets.approvalTypeRadioButtons.subscribe( "checkedButtonChange", this.handlers._onApprovalTypeChange, null, this );

            this.widgets.addAssigneeButton.subscribe( "click", this.handlers._onAddAssigneeButtonClick, null, this );
            this.widgets.deleteAssigneesListButton.subscribe( "click", this.handlers._onDeleteAssigneesListButtonClick, null, this );
            this.widgets.newAssigneesListButton.subscribe( "click", this.handlers._onNewAssigneesListButtonClick, null, this );

            this.widgets.assigneesListSelectElem.subscribe( "change", this.refreshDatagrid, null, this );
        },

        /**
         * Создаёт кнопку 'Создать новый список'
         *
         * @method approvalForm_initSelectApprovalListMenu
         */
        _initNewAssigneesListButton: function() {
            this.widgets.newAssigneesListButton = Alfresco.util.createYUIButton( this, "save-assignees-list-button", null, { label: "", title: "Создать новый список" }, "${newAssigneesListButtonId}" );
            this.widgets.newAssigneesListButton.setStyle( "margin-left", "10px" );
        },

        /**
         * Создаёт кнопку 'Удалить выбранный список'
         *
         * @method approvalForm_initSelectApprovalListMenu
         */
        _initDeleteAssigneesListButton: function() {
            this.widgets.deleteAssigneesListButton = Alfresco.util.createYUIButton( this, "delete-assignees-list-button", null, { label: "", title: "Удалить выбранный список" }, "${deleteAssigneesListButtonId}" );
            this.widgets.deleteAssigneesListButton.setStyle( "margin-left", "0" );
        },

        /**
         * Создаёт {YAHOO.util.Element} и {JQuery} адаптеры для select-элемента
         *
         * @method approvalForm_initSelectApprovalListMenu
         */
        _initSelectApprovalListMenu: function approvalForm_initSelectApprovalListMenu() {
            this.widgets.assigneesListSelectElem = new YAHOO.util.Element( "${selectId}" );
            this.widgets.$assigneesListSelectElem = ${"$"}( this.widgets.assigneesListSelectElem.get( "element" ) );
        },

        /**
         * Создаёт кнопку 'Добавить согласующего'
         *
         * @method approvalForm_initAddAssigneeButton
         */
        _initAddAssigneeButton: function approvalForm_initAddAssigneeButton() {
            this.widgets.addAssigneeButton = Alfresco.util.createYUIButton( this, "add-assignee-button", null, { label: "Добавить согласующего", title: "Добавить согласующего в выбранный список" }, "${addAssigneeButtonId}" );
            this.widgets.addAssigneeButton.setStyle( "margin", "0 0 5px 1px" );
        },

        /**
         * Создаёт радио-кнопки для выбора типа согласования
         *
         * @method approvalForm_initApprovalTypeRadioButtons
         */
        _initApprovalTypeRadioButtons: function approvalForm_initApprovalTypeRadioButtons() {
            this.widgets.approvalTypeRadioButtons = new YAHOO.widget.ButtonGroup({
                id: "approval-type-radio-buttons",
                name: "approval-type-radio-buttons",
                container: "approval-type-radio-buttons-container" });

            this.widgets.approvalTypeRadioButtons.addButtons([
                { label: "Последовательное", value: "sequential", checked: true },
                { label: "Параллельное", value: "parallel" }
            ]);

            this.widgets.approvalTypeRadioButtons.getButton( 0 ).setStyle( "margin-left", "1px" );
        },

        /**
         * Создаёт таблицу
         *
         * @method approvalForm_initDatagrid
         */
        _initDatagrid: function approvalForm_initDatagrid() {

            this.widgets.assigneesDatagrid = new LogicECM.module.AssigneesGrid( "${datagridId}" ).setOptions({

                //allowCreate: true,
                bubblingLabel: "${datagridId}-label",
                usePagination: false,
                showExtendSearchBlock: false,
                showCheckboxColumn: false,
                searchShowInactive: false,
                forceSubscribing: true,
                showActionColumn: true,
                overrideSortingWith: false ,

                // From Constructor...
                approvalType: "sequential",

                actions: [{
                    type: "datagrid-action-link-${datagridId}-label",
                    id: "onMoveUp",
                    permission: "edit",
                    label: "Переместить вверх",
                    evaluator: function() {
                        return this.options.approvalType === "sequential";
                    }
                },
                {
                    type: "datagrid-action-link-${datagridId}-label",
                    id: "onMoveDown",
                    permission: "edit",
                    label: "Переместить вниз",
                    evaluator: function() {
                        return this.options.approvalType === "sequential";
                    }
                },
                {
                    type: "datagrid-action-link-${datagridId}-label",
                    id: "onActionDelete",
                    permission: "delete",
                    label: "Удалить согласующего"
                }],

                datagridMeta: {
                    itemType: "lecm-al:assignees-item",
                    nodeRef: "", // Пусто блять...
                    sort: "lecm-al:assignees-item-order|true",
                    actionsConfig: {
                        fullDelete: true,
                        targetDelete: true
                    }
                }
            });

            this.widgets.assigneesDatagrid.draw();
        },

        /**
         * Находит модуль datepicker для поля даты общего срока согласования, достаёт из него календарь, сохраняет
         * datepicker и calendar в this.widgets, устанавливает минимальную и максимальну дату, подсвечивает даты,
         * которые лучше не выбирать.
         *
         * @method approvalForm_customizeCalendar
         */
        _customizeCalendar: function approvalForm_customizeCalendar( layer, args ) {
            var i;

            if( args[ 1 ].fieldId === "workflow-form_prop_bpm_workflowDueDate-cntrl-date" ) {
                this.widgets.datepicker = this.widgets.datepicker || ComponentManager.get( "workflow-form_prop_bpm_workflowDueDate-cntrl" );
                this.widgets.calendar = this.widgets.calendar || ComponentManager.get( "workflow-form_prop_bpm_workflowDueDate-cntrl" ).widgets.calendar;

                this.widgets.calendar.cfg.setProperty( "mindate", this.options.dates.startDate );
                this.widgets.calendar.cfg.setProperty( "maxdate", this.options.dates.endDate );

                for( i = 0; i < this.options.dates.restrictedDates.length; ++i ) {
                    this.widgets.calendar.addRenderer( ( this.options.dates.restrictedDates[ i ].getMonth() + 1 ) + "/" +
                            this.options.dates.restrictedDates[ i ].getDate() + "/" +
                            this.options.dates.restrictedDates[ i ].getFullYear(),
                            this.widgets.calendar.renderCellStyleHighlight3 );
                }
            }
        },

        init: function approvalForm_onComponentsLoaded() {
            Event.onContentReady( "${formContainerId}" /*this.currentValueHtmlId*/, this.onReady, this, true );
        },

        onReady: function approvalForm_onReady() {
            Alfresco.util.PopupManager.zIndex = 9000;

            this._getListsFolderRef();

            this._addBubblingLayers();

            // Инициализация кнопок
            this._initApprovalTypeRadioButtons();
            this._initAddAssigneeButton();
            this._initDeleteAssigneesListButton();
            this._initNewAssigneesListButton();

            // Инициализация меню выбора списка согласующих
            this._initSelectApprovalListMenu();

            // Инициализация таблицы
            this._initDatagrid();

            // Подписки
            this._initEvents();

            this.fillApprovalListMenu();
        }
    };
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ApprovalForm ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    new LogicECM.module.ApprovalForm( "${controlId}", "${htmlId}" );

})();
</script>
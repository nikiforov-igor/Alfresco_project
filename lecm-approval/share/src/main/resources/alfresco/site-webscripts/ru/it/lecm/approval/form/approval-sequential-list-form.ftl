<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid />

<#assign selectId = formId + "-list-select">
<#assign enterAssigneesListNameDialogId = formId + "-enter-assignees-list-name-dialog">

<#assign addAssigneeButtonId = formId + "-add-assignee-button">
<#assign saveAssigneesListButtonId = formId + "-save-assignees-list-button">
<#assign deleteAssigneesListButtonId = formId + "-delete-assignees-list-button">
<#assign calculateDueDatesButtonId = formId + "-calculate-due-dates-button">

<#assign formWrapperId = formId + "-container">
<#assign datagridId = formId + "-datagrid">

<#assign showActions = true>

<div id="${formWrapperId}">

<#if formUI == "true">
    <@formLib.renderFormsRuntime formId = formId />
</#if>

<@formLib.renderFormContainer formId = formId>

    <div class="set">
        <div class="yui-g">
            <div class="yui-u first">
                <@formLib.renderField field = form.fields["prop_bpm_workflowDueDate"] />
            </div>
            <div class="yui-u">
                <div class="form-field">
                    <div id="${calculateDueDatesButtonId}"></div>
                </div>
            </div>
        </div>
    </div>

    <@formLib.renderField field = form.fields["assoc_packageItems"] />
    <@formLib.renderField field = form.fields["prop_bpm_workflowDescription"] /><!-- Hidden! -->

    <div id="${addAssigneeButtonId}"></div>

    <div class="form-field with-grid">
        <@grid.datagrid datagridId false />
    </div>

    <div class="form-field">
        <label for="${selectId}">Загрузить из списка:</label>
        <select id="${selectId}" style="width: 28em;"></select>
        <div id="${deleteAssigneesListButtonId}"></div>
    </div>

    <div style="padding-left: 160px; overflow: hidden;">
        <div id="${saveAssigneesListButtonId}"></div>
    </div>

    <input type="hidden" id="workflow-form_assoc_lecmSeq_assigneeAssoc-cntrl-added" name="assoc_lecmSeq_assigneeAssoc_added"/>
    <input type="hidden" id="workflow-form_assoc_lecmSeq_assigneeAssoc-cntrl-removed" name="assoc_lecmSeq_assigneeAssoc_removed"/>
    <input type="hidden" id="workflow-form_assoc_lecmSeq_assigneeAssoc-cntrl-selectedItems" />
</@>

</div>



<script>//<![CDATA[
(function() {

    var variables = {
        defaultDestination: "",
        currentDestination: ""
    };

    var objects = {
        assigneesDatagrid: null,
        selectElement: null
    }

    // Constants
    function constants() {
        return {
            URL_GET_DEFAULT_SEQUENTIAL_FOLDER_REF: "lecm/approval/getdefaultsequentialfolderref",
            URL_FORM: "lecm/components/form?itemKind={itemKind}&itemId={itemId}&destination={destination}&mode={mode}&submitType={submitType}&showCancelButton={showCancelButton}",

            STRING_ADD_ASSIGNEE_BUTTON_LABEL: "Добавить согласующего",
            STRING_SAVE_ASSIGNEES_LIST_BUTTON_LABEL: "Сохранить список согласующих",
            STRING_DELETE_ASSIGNEES_LIST_BUTTON_LABEL: "X",
            STRING_CALCULATE_DUE_DATES_BUTTON_LABEL: "Посмотреть сроки согласования"
        }
    }

    // Ajax.request Tiny Facade
    function ajax( type, url, successCallback, failureCallback, dataObj, callbacksScope, callbackParams ) {
        Alfresco.util.Ajax.request({
            method: type,
            url: Alfresco.constants.PROXY_URI_RELATIVE + url,
            dataObj: dataObj,
            requestContentType: "application/json",
            responseContentType: "application/json",
            successCallback: {
                fn: successCallback,
                obj: callbackParams,
                scope: callbacksScope
            },
            failureCallback: {
                fn: failureCallback,
                obj: callbackParams,
                scope: callbacksScope
            }
        });
    }

    // AssigneesGrid Constructor
    function AssigneesGrid( containerId ) {
        return AssigneesGrid.superclass.constructor.call( this, containerId );
    }

    YAHOO.lang.extend( AssigneesGrid, LogicECM.module.Base.DataGrid );

    YAHOO.lang.augmentObject( AssigneesGrid.prototype, {

        onMoveUp: function( p_items, owner, actionsConfig, fnDeleteComplete, fnPrompt ) {

            var currentNodeRef = p_items.nodeRef, // {String}
                dataObj = { "assigneeItemNodeRef": currentNodeRef,
                            "moveDirection": "up" };

            Alfresco.util.Ajax.request({
                method: "POST",
                url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/approval/changeOrder",
                dataObj: dataObj,
                requestContentType: "application/json",
                responseContentType: "application/json",
                successCallback: {
                    fn: function() {
                        YAHOO.Bubbling.fire( "datagridRefresh", {
                            datagridMeta: {
                                itemType: "lecm-al:assignees-item",
                                nodeRef: variables.currentDestination,
                                sort: "lecm-al:assignees-item-order|true",
                                actionsConfig: {
                                    fullDelete: true,
                                    targetDelete: true
                                }
                            },
                            bubblingLabel: "workflow-form-form-datagrid-label"
                        });
                    },
                    scope: this
                },
                failureCallback: {
                    fn: function() {
                        Alfresco.util.PopupManager.displayMessage({
                            text: "Не удалось переместить объект вверх по списку"
                        });
                    }
                }
            });

        },

        onMoveDown: function( p_items, owner, actionsConfig, fnDeleteComplete, fnPrompt ) {

            var currentNodeRef = p_items.nodeRef, // {String}
                dataObj = { "assigneeItemNodeRef": currentNodeRef,
                            "moveDirection": "down" };

            Alfresco.util.Ajax.request({
                method: "POST",
                url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/approval/changeOrder",
                dataObj: dataObj,
                requestContentType: "application/json",
                responseContentType: "application/json",
                successCallback: {
                    fn: function( response ) {
                        YAHOO.Bubbling.fire( "datagridRefresh", {
                            datagridMeta: {
                                itemType: "lecm-al:assignees-item",
                                nodeRef: variables.currentDestination,
                                sort: "lecm-al:assignees-item-order|true",
                                actionsConfig: {
                                    fullDelete: true,
                                    targetDelete: true
                                }
                            },
                            bubblingLabel: "workflow-form-form-datagrid-label"
                        });
                    },
                    scope: this
                },
                failureCallback: {
                    fn: function () {
                        Alfresco.util.PopupManager.displayMessage({
                            text: "Не удалось переместить объект вниз по списку"
                        });
                    }
                }
            });
        }
    }, true);

    YAHOO.util.Event.onContentReady( "${formWrapperId}", function() {
        // Спасаем "тонущие" всплывающие сообщения.
        Alfresco.util.PopupManager.zIndex = 9000;

        YAHOO.Bubbling.on( "dataItemsDeleted", actions().refreshDatagrid );

        ajax( "POST", constants().URL_GET_DEFAULT_SEQUENTIAL_FOLDER_REF, handlers().onGetDefaultSequentialFolderRefSuccess );
    });

    function actions() {

        function refreshDatagrid() {
            YAHOO.Bubbling.fire( "activeGridChanged", {
                datagridMeta: {
                    itemType: "lecm-al:assignees-item",
                    nodeRef: variables.currentDestination,
                    sort: "lecm-al:assignees-item-order|true",
                    actionsConfig: {
                        fullDelete: true,
                        targetDelete: true
                    }
                },
                bubblingLabel: "workflow-form-form-datagrid-label"
            });
        }

        function initSelectElement() {
            objects.selectElement = YAHOO.util.Dom.get( "${selectId}" )
            YAHOO.util.Event.on( objects.selectElement, "change", function( event /*, that*/ ) {


                Alfresco.util.Ajax.request({
                    method: "POST",
                    url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/approval/clearList",
                    dataObj: { "assigneesListNodeRef": variables.defaultDestination },
                    requestContentType: "application/json",
                    responseContentType: "application/json",
                    failureCallback: {
                        fn: function () {
                            Alfresco.util.PopupManager.displayMessage({
                                text: "Не удалось очистить список по умолчанию"
                            });
                        }
                    }
                });

                if( this.value === "" ) {

                    variables.currentDestination = variables.defaultDestination;
                    actions().refreshDatagrid();

                    event.stopPropagation();
                    event.preventDefault();

                    return false;
                }

                variables.currentDestination = this.value;
                actions().refreshDatagrid();

                return true;
            });
        }

        function initDatagrid() {

            objects.assigneesDatagrid = new AssigneesGrid( "${datagridId}" ).setOptions({

                bubblingLabel: "${datagridId}-label",
                usePagination: false,
                showExtendSearchBlock: false,
                showCheckboxColumn: false,
                searchShowInactive: false,
                forceSubscribing: true,
                showActionColumn: ${showActions?string},

            <#if showActions>
                actions: [{
                    type: "datagrid-action-link-${datagridId}-label",
                    id: "onMoveUp",
                    permission: "edit",
                    label: "Переместить вверх"
                },
                {
                    type: "datagrid-action-link-${datagridId}-label",
                    id: "onMoveDown",
                    permission: "edit",
                    label: "Переместить вниз"
                },
                {
                    type: "datagrid-action-link-${datagridId}-label",
                    id: "onActionDelete",
                    permission: "delete",
                    label: "Удалить согласующего"
                }],
            </#if>

                datagridMeta: {
                    itemType: "lecm-al:assignees-item",
                    nodeRef: variables.currentDestination,
                    sort: "lecm-al:assignees-item-order|true",
                    actionsConfig: {
                        fullDelete: true,
                        targetDelete: true
                    }
                }
            });

            objects.assigneesDatagrid.draw();
        }

        function initButtons() {

            var putAddAssigneeButtonIn = YAHOO.util.Dom.get( "${addAssigneeButtonId}" );
            var putSaveButtonIn = YAHOO.util.Dom.get( "${saveAssigneesListButtonId}" );
            var putDeleteButtonIn = YAHOO.util.Dom.get( "${deleteAssigneesListButtonId}" );
            var putCalculateDueDatesButtonIn = YAHOO.util.Dom.get( "${calculateDueDatesButtonId}" );

            var addAssigneeButton = Alfresco.util.createYUIButton( this, "${addAssigneeButtonId}", handlers().onAddAssigneeButtonClicked, { label: constants().STRING_ADD_ASSIGNEE_BUTTON_LABEL }, putAddAssigneeButtonIn );
            var saveButton = Alfresco.util.createYUIButton( this, "${saveAssigneesListButtonId}", handlers().onSaveAssigneesListButtonClick, { label: constants().STRING_SAVE_ASSIGNEES_LIST_BUTTON_LABEL }, putSaveButtonIn );
            var deleteButton = Alfresco.util.createYUIButton( this, "${deleteAssigneesListButtonId}", handlers().onDeleteAssigneesListButtonClick, { label: constants().STRING_DELETE_ASSIGNEES_LIST_BUTTON_LABEL }, putDeleteButtonIn );
            var calculateDueDatesButton = Alfresco.util.createYUIButton( this, "${calculateDueDatesButtonId}", handlers().onCalculateDueDatesButtonClick, { label: constants().STRING_CALCULATE_DUE_DATES_BUTTON_LABEL }, putCalculateDueDatesButtonIn );

            addAssigneeButton.setStyle( "margin", "0 0 5px 1px" );
            saveButton.setStyle( "margin", "0 0 5px 1px" );
            deleteButton.setStyle( "margin-left", "10px" );
            calculateDueDatesButton.setStyle( "margin", "0 0 5px 1px" );
        }

        function hookDatagridRecordSet() {
            var currentRecordSet = objects.assigneesDatagrid.widgets.dataTable.getRecordSet();

            currentRecordSet.createEvent( "recordAddEvent" );
            currentRecordSet.createEvent( "recordSetEvent" );
            currentRecordSet.createEvent( "recordDeleteEvent" );
            currentRecordSet.createEvent( "recordsAddEvent" );
            currentRecordSet.createEvent( "recordsSetEvent" );
            currentRecordSet.createEvent( "recordsDeleteEvent" );

            currentRecordSet.subscribe( "recordAddEvent", actions().updateFormFields );
            currentRecordSet.subscribe( "recordSetEvent", actions().updateFormFields );
            currentRecordSet.subscribe( "recordDeleteEvent", actions().updateFormFields );
            currentRecordSet.subscribe( "recordsAddEvent", actions().updateFormFields );
            currentRecordSet.subscribe( "recordsSetEvent", actions().updateFormFields );
            currentRecordSet.subscribe( "recordsDeleteEvent", actions().updateFormFields );
        }

        function subscribeToformContainerDestroy() {

            var destroyEvent = LogicECM.CurrentModules.WorkflowForm.dialog.destroyEvent;

            destroyEvent.subscribe( function() {
                var currentRecordSet = objects.assigneesDatagrid.widgets.dataTable.getRecordSet();

                currentRecordSet.unsubscribe( "recordAddEvent", actions().updateFormFields );
                currentRecordSet.unsubscribe( "recordSetEvent", actions().updateFormFields );
                currentRecordSet.unsubscribe( "recordDeleteEvent", actions().updateFormFields );
                currentRecordSet.unsubscribe( "recordsAddEvent", actions().updateFormFields );
                currentRecordSet.unsubscribe( "recordsSetEvent", actions().updateFormFields );
                currentRecordSet.unsubscribe( "recordsDeleteEvent", actions().updateFormFields );
            });
        }

        function fillDropDownList( listName ) {
            ajax( "POST",
                  "lecm/approval/getAllLists",
                  handlers().fillDropDownListFromResponse,
                  function () {
                      Alfresco.util.PopupManager.displayMessage({
                          text: "Не удалось получить перечень списков согласующих."
                      });
                  },
                  { "approvalType": "seq" }, null, { "listName": listName } );
        }

        function updateFormFields() {

            var addedInputElement = YAHOO.util.Dom.get( "workflow-form_assoc_lecmSeq_assigneeAssoc-cntrl-added" ),

                currentRecordSet = objects.assigneesDatagrid.widgets.dataTable.getRecordSet(),
                currentRecords = currentRecordSet.getRecords(),

                i,
                tail = currentRecords.length - 1;

            addedInputElement.value = "";

            for( i = 0; i < currentRecords.length; ++i ) {
                addedInputElement.value += currentRecords[ i ].getData( "nodeRef" );

                if( i < tail ) {
                    addedInputElement.value += ",";
                }
            }
        }

        return {
            initDatagrid: initDatagrid,
            initButtons: initButtons,
            initSelectElement: initSelectElement,
            subscribeToformContainerDestroy: subscribeToformContainerDestroy,
            hookDatagridRecordSet: hookDatagridRecordSet,
            updateFormFields: updateFormFields,
            fillDropDownList: fillDropDownList,
            refreshDatagrid: refreshDatagrid
        }
    }

    function handlers() {

        function onCalculateDueDatesButtonClick() {

            var i,

                dueDateObject,
                daysDiff,
                period,

                utcToday,
                utcDueDate,

                previousDate,
                currentDate,

                today = new Date(),
                dueDateInputValue = YAHOO.util.Dom.get( "workflow-form_prop_bpm_workflowDueDate" ).value,

                currentRecordSet = objects.assigneesDatagrid.widgets.dataTable.getRecordSet(),
                currentRecords = currentRecordSet.getRecords();

            if( dueDateInputValue === "" ) {

                if( currentRecords.length === 0 ) {
                    Alfresco.util.PopupManager.displayMessage({ text: "Введите дату согласования и добавьте хотя бы одного согласующего" });
                } else {
                    Alfresco.util.PopupManager.displayMessage({ text: "Введите дату согласования" });
                }

                return false;

            } else {

                if( currentRecords.length === 0 ) {
                    Alfresco.util.PopupManager.displayMessage({ text: "Добавьте хотя бы одного согласующего" });

                    return false;
                }
            }

            dueDateObject = new Date( dueDateInputValue );

            utcToday = Date.UTC( today.getFullYear(), today.getMonth(), today.getDate(), today.getHours(), today.getMinutes(), today.getSeconds(), today.getMilliseconds() );
            utcDueDate = Date.UTC( dueDateObject.getFullYear(), dueDateObject.getMonth(), dueDateObject.getDate(), dueDateObject.getHours(), dueDateObject.getMinutes(), dueDateObject.getSeconds(), dueDateObject.getMilliseconds() );

            daysDiff = ( utcDueDate - utcToday ) / 86400000;
            period = Math.round( daysDiff / currentRecords.length );

            console.log( "daysDiff: " + daysDiff );
            console.log( "period: " + period );

            today.setDate( today.getDate() + period );

            today.setHours( 0 );
            today.setMinutes( 0 );
            today.setSeconds( 0 );
            today.setMilliseconds( 0 );

            if( !( currentRecords[ 0 ].getData( "itemData" )[ "prop_lecm-al_assignees-item-due-date" ] ) ) {
                currentRecords[ 0 ].getData( "itemData" )[ "prop_lecm-al_assignees-item-due-date" ] = {};
            }

            currentRecords[ 0 ].getData( "itemData" )[ "prop_lecm-al_assignees-item-due-date" ].value = Alfresco.util.toISO8601( today );
            currentRecords[ 0 ].getData( "itemData" )[ "prop_lecm-al_assignees-item-due-date" ].displayValue = Alfresco.util.toISO8601( today );
            for( i = 1; i < currentRecords.length; ++i ) {

                if( !( currentRecords[ i ].getData( "itemData" )[ "prop_lecm-al_assignees-item-due-date" ] ) ) {
                    currentRecords[ i ].getData( "itemData" )[ "prop_lecm-al_assignees-item-due-date" ] = {};
                }

                previousDate = new Date( currentRecords[ i - 1 ].getData( "itemData" )[ "prop_lecm-al_assignees-item-due-date" ].value );
                previousDate.setDate( previousDate.getDate() + period );

                currentDate = previousDate;

                currentRecords[ i ].getData( "itemData" )[ "prop_lecm-al_assignees-item-due-date" ].value = Alfresco.util.toISO8601( currentDate );
                currentRecords[ i ].getData( "itemData" )[ "prop_lecm-al_assignees-item-due-date" ].displayValue = Alfresco.util.toISO8601( currentDate );
            }

            handlers().onEnterAssigneesListNameDialogSuccess( null, { "listName": objects.selectElement.options[ objects.selectElement.selectedIndex ].text, "records": currentRecords } );

            return true;
        }

        function onDeleteAssigneesListButtonClick() {

            if( objects.selectElement.value === "" ) {

                event.stopPropagation();
                event.preventDefault();

                return false;
            }

            Alfresco.util.Ajax.request({
                method: "POST",
                url: Alfresco.constants.PROXY_URI_RELATIVE + "/lecm/approval/delete",
                dataObj: { "nodeRef": objects.selectElement.value },
                requestContentType: "application/json",
                responseContentType: "application/json",
                successCallback: {
                    fn: function() {
                        Alfresco.util.PopupManager.displayMessage({
                            text: "Список удалён"
                        });

                        actions().fillDropDownList( "Без списка (список не выбран)" );
                    }
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

        function onEnterAssigneesListNameDialogSuccess( formHtmlElem, params ) {

            var i,
                dataObjArrayItem,
                dataObj = {},
                nameInputElementId = "workflow-form-form-enter-assignees-list-name-dialog_prop_cm_name",
                nameInputElement = YAHOO.util.Dom.get( nameInputElementId );

            if( this.form && this.form.setAJAXSubmit ) {
                this.form.setAJAXSubmit( false, null );
            }

            dataObj.approvalType = "seq";
            dataObj.listName = params.listName || nameInputElement.value;
            dataObj.listItems = [];

            for( i = 0; i < params.records.length; ++i ) {
                dataObjArrayItem = {};

                dataObjArrayItem.order = params.records[ i ].getData( "itemData" )[ "prop_lecm-al_assignees-item-order" ].value;
                if( params.records[ i ].getData( "itemData" )[ "prop_lecm-al_assignees-item-due-date" ] ) {
                    dataObjArrayItem.dueDate = params.records[ i ].getData( "itemData" )[ "prop_lecm-al_assignees-item-due-date" ].value || Alfresco.util.toISO8601( new Date() );
                } else {
                    dataObjArrayItem.dueDate = Alfresco.util.toISO8601( new Date() );
                }
                dataObjArrayItem.nodeRef = params.records[ i ].getData( "itemData" )[ "assoc_lecm-al_assignees-item-employee-assoc" ].value;

                dataObj.listItems.push( dataObjArrayItem );
            }

            Alfresco.util.Ajax.request({
                method: "POST",
                url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/approval/save",
                dataObj: dataObj,
                requestContentType: "application/json",
                responseContentType: "application/json",
                successCallback: {
                    fn: function () {

                        actions().fillDropDownList( dataObj.listName );

                        if( this.form ) { // Если сохраняем через кнопку, то выводим сообщение.
                            Alfresco.util.PopupManager.displayMessage({
                                text: "Список успешно сохранён"
                            });
                        }

                        Alfresco.util.Ajax.request({
                            method: "POST",
                            url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/approval/clearList",
                            dataObj: { "assigneesListNodeRef": variables.defaultDestination },
                            requestContentType: "application/json",
                            responseContentType: "application/json",
                            failureCallback: {
                                fn: function () {
                                    Alfresco.util.PopupManager.displayMessage({
                                        text: "Не удалось очистить список по умолчанию"
                                    });
                                }
                            }
                        });
                    },
                    scope: this
                },
                failureCallback: {
                    fn: function () {
                        Alfresco.util.PopupManager.displayMessage({
                            text: "Не удалось сохранить список согласующих: данные были отправлены, но сервер ответил отрицательно"
                        });
                    }
                }
            });
        }

        function onSaveAssigneesListButtonClick() {

            var recordSet = objects.assigneesDatagrid.widgets.dataTable.getRecordSet(),
                records = recordSet.getRecords(),

                enterAssigneesListNameDialog = new Alfresco.module.SimpleDialog( "${enterAssigneesListNameDialogId}" ),

                url = "lecm/components/form" +
                      "?itemKind={itemKind}" +
                      "&itemId={itemId}" +
                      "&mode={mode}" +
                      "&submitType={submitType}" +
                      "&showCancelButton={showCancelButton}",

                templateUrl = YAHOO.lang.substitute( Alfresco.constants.URL_SERVICECONTEXT + url, {
                    itemKind: "type",
                    itemId: "lecm-al:assignees-list",
                    mode: "create",
                    submitType: "json",
                    showCancelButton: "true"
                });

            if( records.length === 0 ) {
                Alfresco.util.PopupManager.displayMessage({
                    text: "Нельзя сохранить пустой список. Пожалуйста, добавьте хотя бы одного согласующего и попробуйте ещё раз"
                });

                return;
            }

            enterAssigneesListNameDialog.setOptions({
                width: "500px",
                templateUrl: templateUrl,
                destroyOnHide: true,
                doBeforeFormSubmit: {
                    fn: handlers().onEnterAssigneesListNameDialogSuccess,
                    obj: { "records": records },
                    scope: enterAssigneesListNameDialog
                }
            });

            enterAssigneesListNameDialog.show();
        }

        function fillDropDownListFromResponse( response, params ) {

            var i,
                mustBeSelected,
                selectElement = YAHOO.util.Dom.get( "${selectId}" );

            selectElement.options.length = 0;

            for( i = 0; i < response.json.length; ++i ) {

                mustBeSelected = response.json[ i ].listName === params.listName;

                selectElement.options[ selectElement.options.length ] = new Option(
                        response.json[ i ].listName,
                        response.json[ i ].nodeRef,
                        mustBeSelected,
                        mustBeSelected
                );

                if( mustBeSelected ) {
                    variables.currentDestination = response.json[ i ].nodeRef;
                }
            }

            actions().refreshDatagrid();
        }

        function onGetDefaultSequentialFolderRefSuccess( response ) {

            //LogicECM.CurrentModules.WorkflowForm.form.doBeforeFormSubmit = {
            LogicECM.CurrentModules.WorkflowForm.form.doBeforeAjaxRequest = {
                fn: function( form, arg2 ) {

                    this.widgets.okButton.set( "disabled", true );
                    this.widgets.cancelButton.set( "disabled", true );


                    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                    var i,
                        today = new Date(),
                        daysDiff,
                        utcToday,
                        utcDueDate,
                        dueDateObject,
                        dataObjArrayItem,
                        period,
                        dataObj = {},
                        previousDate,
                        currentDate,

                        currentRecordSet = objects.assigneesDatagrid.widgets.dataTable.getRecordSet(),
                        currentRecords = currentRecordSet.getRecords(),
                        dueDateInputValue = YAHOO.util.Dom.get( "workflow-form_prop_bpm_workflowDueDate" ).value;

                    dueDateObject = new Date( dueDateInputValue );

                    utcToday = Date.UTC( today.getFullYear(), today.getMonth(), today.getDate(), today.getHours(), today.getMinutes(), today.getSeconds(), today.getMilliseconds() );
                    utcDueDate = Date.UTC( dueDateObject.getFullYear(), dueDateObject.getMonth(), dueDateObject.getDate(), dueDateObject.getHours(), dueDateObject.getMinutes(), dueDateObject.getSeconds(), dueDateObject.getMilliseconds() );

                    daysDiff = ( utcDueDate - utcToday ) / 86400000;
                    period = Math.round( daysDiff / currentRecords.length );

                    today.setDate( today.getDate() + period );

                    today.setHours( 0 );
                    today.setMinutes( 0 );
                    today.setSeconds( 0 );
                    today.setMilliseconds( 0 );

                    if( !( currentRecords[ 0 ].getData( "itemData" )[ "prop_lecm-al_assignees-item-due-date" ] ) ) {
                        currentRecords[ 0 ].getData( "itemData" )[ "prop_lecm-al_assignees-item-due-date" ] = {};
                    }

                    currentRecords[ 0 ].getData( "itemData" )[ "prop_lecm-al_assignees-item-due-date" ].value = Alfresco.util.toISO8601( today );
                    currentRecords[ 0 ].getData( "itemData" )[ "prop_lecm-al_assignees-item-due-date" ].displayValue = Alfresco.util.toISO8601( today );
                    for( i = 1; i < currentRecords.length; ++i ) {

                        if( !( currentRecords[ i ].getData( "itemData" )[ "prop_lecm-al_assignees-item-due-date" ] ) ) {
                            currentRecords[ i ].getData( "itemData" )[ "prop_lecm-al_assignees-item-due-date" ] = {};
                        }

                        previousDate = new Date( currentRecords[ i - 1 ].getData( "itemData" )[ "prop_lecm-al_assignees-item-due-date" ].value );
                        previousDate.setDate( previousDate.getDate() + period );

                        currentDate = previousDate;

                        currentRecords[ i ].getData( "itemData" )[ "prop_lecm-al_assignees-item-due-date" ].value = Alfresco.util.toISO8601( currentDate );
                        currentRecords[ i ].getData( "itemData" )[ "prop_lecm-al_assignees-item-due-date" ].displayValue = Alfresco.util.toISO8601( currentDate );
                    }

                    dataObj.approvalType = "seq";
                    dataObj.listName = objects.selectElement.options[ objects.selectElement.selectedIndex ].text;
                    dataObj.listItems = [];

                    for( i = 0; i < currentRecords.length; ++i ) {
                        dataObjArrayItem = {};

                        dataObjArrayItem.order = currentRecords[ i ].getData( "itemData" )[ "prop_lecm-al_assignees-item-order" ].value;
                        if (currentRecords[ i ].getData( "itemData" )[ "prop_lecm-al_assignees-item-due-date" ]) {
                            dataObjArrayItem.dueDate = currentRecords[ i ].getData( "itemData" )[ "prop_lecm-al_assignees-item-due-date" ].value || Alfresco.util.toISO8601( new Date() );
                        } else {
                            dataObjArrayItem.dueDate = Alfresco.util.toISO8601( new Date() );
                        }
                        dataObjArrayItem.nodeRef = currentRecords[ i ].getData( "itemData" )[ "assoc_lecm-al_assignees-item-employee-assoc" ].value;

                        dataObj.listItems.push( dataObjArrayItem );
                    }

                    jQuery.ajax({
                        url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/approval/save",
                        type: "POST",
                        timeout: 10000, // 30 секунд таймаута хватит всем!
                        async: false, // ничего не делаем, пока не отработал запром
                        dataType: "json",
                        contentType: "application/json",
                        data: YAHOO.lang.JSON.stringify( dataObj ), // jQuery странно кодирует данные. пусть YUI эаймеся преобразованием в JSON
                        processData: false, // данные не трогать, не кодировать вообще
                        success: function ( result, textStatus, jqXHR ) {
                            form.dataObj[ "assoc_lecmSeq_assigneeAssoc_added" ] = result.items;
                        },
                        error: function( jqXHR, textStatus, errorThrown ) {
                            Alfresco.util.PopupManager.displayMessage({
                                text: "ERROR: can not perform field validation"
                            });
                        }
                    });

                    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                    return true;

                },
                scope: LogicECM.CurrentModules.WorkflowForm
            }

            variables.defaultDestination = variables.currentDestination = response.json.defaultListFolderRef;

            YAHOO.Bubbling.on( "datagridVisible", actions().hookDatagridRecordSet /*, this*/ );

            actions().initDatagrid();
            actions().initButtons();
            actions().fillDropDownList();
            actions().subscribeToformContainerDestroy();
            actions().initSelectElement();
        }

        function onAddAssigneeButtonClicked() {

            var addAssigneeForm = new Alfresco.module.SimpleDialog( "${formId}-form-add-assignee" ),

                templateUrl = YAHOO.lang.substitute( Alfresco.constants.URL_SERVICECONTEXT + constants().URL_FORM, {
                    itemKind: "type",
                    itemId: "lecm-al:assignees-item",
                    destination: variables.currentDestination,
                    mode: "create",
                    submitType: "json",
                    showCancelButton: "true"
                });

            addAssigneeForm.setOptions({
                width: "680px",
                templateUrl: templateUrl,
                destroyOnHide: true,
                onSuccess: {
                    fn: function( response ) {
                        YAHOO.Bubbling.fire( "datagridRefresh", {
                            nodeRef: response.json.persistedObject,
                            bubblingLabel: "${datagridId}-label"
                        });
                    },
                    scope: this
                },
                onFailure: {
                    fn: function() {
                        window.alert( "onFailure" );
                    },
                    scope: this
                }
            });

            addAssigneeForm.show();
        }

        function dummy() {
            console.log( "hello!" );
        }

        return {
            onGetDefaultSequentialFolderRefSuccess: onGetDefaultSequentialFolderRefSuccess,
            onEnterAssigneesListNameDialogSuccess: onEnterAssigneesListNameDialogSuccess,

            onAddAssigneeButtonClicked: onAddAssigneeButtonClicked,
            onSaveAssigneesListButtonClick: onSaveAssigneesListButtonClick,
            onDeleteAssigneesListButtonClick: onDeleteAssigneesListButtonClick,
            onCalculateDueDatesButtonClick: onCalculateDueDatesButtonClick,

            fillDropDownListFromResponse: fillDropDownListFromResponse,

            dummy: dummy
        }
    }
})();
//]]>
</script>
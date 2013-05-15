<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid />

<#assign selectId = formId + "-list-select">
<#assign formWrapperId = formId + "-wrapper">
<#assign enterAssigneesListNameDialogId = formId + "-enter-assignees-list-name-dialog">
<#assign saveAssigneesListButtonId = formId + "-save-assignees-list-button">
<#assign deleteAssigneesListButtonId = formId + "-delete-assignees-list-button">

<div id="${formWrapperId}">

<#if formUI == "true">
    <@formLib.renderFormsRuntime formId = formId />
</#if>

<@formLib.renderFormContainer formId = formId>
    <@formLib.renderField field = form.fields["prop_bpm_workflowDueDate"] />
    <@formLib.renderField field = form.fields["assoc_lecmPar_employeesAssoc"] />
    <@formLib.renderField field = form.fields["assoc_packageItems"] />
    <@formLib.renderField field = form.fields["prop_bpm_workflowDescription"] /><!-- Hidden! -->

    <div class="form-field">
        <label for="${selectId}">Загрузить из списка:</label>
        <select id="${selectId}" style="width: 28em;"></select>
        <div id="${deleteAssigneesListButtonId}"></div>
    </div>

    <div style="padding-left: 160px; overflow: hidden;">
        <div id="${saveAssigneesListButtonId}"></div>
    </div>
</@>

</div>

<script>//<![CDATA[

(function() {
    YAHOO.util.Event.onContentReady( "${formWrapperId}", function() {

        var SAVE_BUTTON_LABEL = "Сохранить список согласующих",
            DELETE_BUTTON_LABEL = "Удалить текущий",
            CHOOSE_LABEL = "Выберите список согласующих...";

        // Спасаем "тонущие" всплывающие сообщения.
        Alfresco.util.PopupManager.zIndex = 9000;

        debugger;

        function handlers() {
            function saveAssigneesList() {

                var selectedItems = LogicECM.CurrentModules.employeesAssocTreeView.selectedItems,

                    selectedItemsArray = Object.keys( selectedItems ), // Chrome, FF 4+, IE 9+, Safari 5+

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

                if( selectedItemsArray.length === 0 ) {
                    Alfresco.util.PopupManager.displayMessage({
                        text: "Нельзя сохранить пустой список. Пожалуйста, добавьте хотя бы одного согласующего и попробуйте ещё раз."
                    });

                    return;
                }

                enterAssigneesListNameDialog.setOptions({
                    ajaxSubmit: false,
                    width: "500px",
                    templateUrl: templateUrl,
                    destroyOnHide: true,
                    doBeforeFormSubmit: {
                        fn: function() {

                            debugger;

                            var selectedItem,
                                dataObjArrayItem,
                                dataObj = {},
                                nameInputElementId = "workflow-form-form-enter-assignees-list-name-dialog_prop_cm_name",
                                nameInputElement = YAHOO.util.Dom.get( nameInputElementId );

                            this.form.setAJAXSubmit( false, null );

                            dataObj.approvalType = "par";
                            dataObj.listName = nameInputElement.value;
                            dataObj.listItems = [];

                            for( selectedItem in selectedItems ) {
                                dataObjArrayItem = {};

                                dataObjArrayItem.order = "";
                                dataObjArrayItem.dueDate = "";
                                dataObjArrayItem.nodeRef = selectedItem;

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

                                        actions().fillDropDownList();

                                        Alfresco.util.PopupManager.displayMessage({
                                            text: "Список успешно сохранён."
                                        });
                                    },
                                    scope: this
                                },
                                failureCallback: {
                                    fn: function () {
                                        Alfresco.util.PopupManager.displayMessage({
                                            text: "Не удалось сохранить список согласующих: данные были отправлены, но сервер ответил отрицательно."
                                        });
                                    }
                                }
                            });
                        },
                        scope: enterAssigneesListNameDialog
                    }
                });

                enterAssigneesListNameDialog.show();

                debugger;
            }

            function deleteAssigneesList() {

                if( selectElement.value === "" ) {

                    event.stopPropagation();
                    event.preventDefault();

                    return false;
                }

                Alfresco.util.Ajax.request({
                    method: "POST",
                    url: Alfresco.constants.PROXY_URI_RELATIVE + "/lecm/approval/delete",
                    dataObj: { "nodeRef": selectElement.value },
                    requestContentType: "application/json",
                    responseContentType: "application/json",
                    successCallback: {
                        fn: function() {
                            Alfresco.util.PopupManager.displayMessage({
                                text: "Список успешно удалён."
                            });

                            actions().fillDropDownList();
                        }
                    },
                    failureCallback: {
                        fn: function () {
                            Alfresco.util.PopupManager.displayMessage({
                                text: "Не удалось удалить список согласующих."
                            });
                        }
                    }
                });
            }

            function fillDropDownListFromResponse( response ) {

                debugger;

                var i;

                selectElement.options.length = 1; // FUTURE: JSHint-friendly...
                selectElement.options[ 0 ] = new Option( CHOOSE_LABEL, "", true, true );

                for( i = 0; i < response.json.length; ++i ) {
                    selectElement.options[ selectElement.options.length ] = new Option(
                        response.json[ i ].listName,
                        response.json[ i ].nodeRef,
                        false,
                        false
                    );
                }
            }

            function fillAssigneesListFromResponse( response ) {

                var i, j,

                    employeesAssocAutoComplete = LogicECM.CurrentModules.employeesAssocAutoComplete,

                    responseItemsArray = response.json.listItems,
                    dataArray = employeesAssocAutoComplete.dataArray,
                    selectedItems = employeesAssocAutoComplete.selectedItems;

                actions().clearAssigneesList();

                for( i = 0; i < responseItemsArray.length; ++i ) {
                    for( j = 0; j < dataArray.length; ++j ) {
                        if( responseItemsArray[ i ].nodeRef === dataArray[ j ].nodeRef ) {
                            selectedItems[ dataArray[j].nodeRef ] = dataArray[ j ];
                        }
                    }
                }

                employeesAssocAutoComplete.updateSelectedItems();
                employeesAssocAutoComplete.updateFormFields();
            }

            return {
                saveAssigneesList: saveAssigneesList,
                fillDropDownListFromResponse: fillDropDownListFromResponse,
                fillAssigneesListFromResponse: fillAssigneesListFromResponse,
                deleteAssigneesList: deleteAssigneesList
            }
        }

        function actions() {

            function fillDropDownList() {
                Alfresco.util.Ajax.request({
                    method: "POST",
                    url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/approval/getAllLists",
                    dataObj: { "approvalType": "par" },
                    requestContentType: "application/json",
                    responseContentType: "application/json",
                    successCallback: {
                        fn: handlers().fillDropDownListFromResponse,
                        scope: this
                    },
                    failureCallback: {
                        fn: function () {
                            Alfresco.util.PopupManager.displayMessage({
                                text: "Не удалось получить перечень списков согласующих."
                            });
                        }
                    }
                });
            }

            function fillAssigneesList( listNodeRef ) {
                Alfresco.util.Ajax.request({
                    method: "POST",
                    url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/approval/getListContents",
                    dataObj: { "nodeRef": listNodeRef },
                    requestContentType: "application/json",
                    responseContentType: "application/json",
                    successCallback: {
                        fn: handlers().fillAssigneesListFromResponse,
                        scope: this
                    },
                    failureCallback: {
                        fn: function () {
                            Alfresco.util.PopupManager.displayMessage({
                                text: "Не удалось получить перечень списков согласующих."
                            });
                        }
                    }
                });
            }

            function clearAssigneesList() {
                var employeesAssocAutoComplete = LogicECM.CurrentModules.employeesAssocAutoComplete,
                    currentSelectedItems = employeesAssocAutoComplete.selectedItems,

                    property;

                for ( property in currentSelectedItems ) {
                    if( currentSelectedItems.hasOwnProperty( property ) ) {
                        delete currentSelectedItems[ property ];
                    }
                }

                employeesAssocAutoComplete.updateSelectedItems();
                employeesAssocAutoComplete.updateFormFields();
            }

            return {
                fillDropDownList: fillDropDownList,
                fillAssigneesList: fillAssigneesList,
                clearAssigneesList: clearAssigneesList
            }
        }

        var selectElement = YAHOO.util.Dom.get( "${selectId}" ),

            putSaveButtonIn = YAHOO.util.Dom.get( "${saveAssigneesListButtonId}" ),
            putDeleteButtonIn = YAHOO.util.Dom.get( "${deleteAssigneesListButtonId}" ),

            saveButton = Alfresco.util.createYUIButton( this, "${saveAssigneesListButtonId}", handlers().saveAssigneesList, { label: SAVE_BUTTON_LABEL }, putSaveButtonIn ),
            deleteButton = Alfresco.util.createYUIButton( this, "${deleteAssigneesListButtonId}", handlers().deleteAssigneesList, { label: DELETE_BUTTON_LABEL }, putDeleteButtonIn );

        saveButton.setStyle( "margin", "0 0 5px 1px" );
        deleteButton.setStyle( "margin-left", "10px" );

        // Select Element Initialization
        YAHOO.util.Event.on( selectElement, "change", function( event /*, that*/ ) {

            if( this.value === "" ) {
                actions().clearAssigneesList();

                event.stopPropagation();
                event.preventDefault();

                return false;
            }

            actions().fillAssigneesList( this.value );
        });

        selectElement.options.length = 1;
        selectElement.options[ 0 ] = new Option( CHOOSE_LABEL, "", true );

        actions().fillDropDownList();
    });
})();

//]]>
</script>
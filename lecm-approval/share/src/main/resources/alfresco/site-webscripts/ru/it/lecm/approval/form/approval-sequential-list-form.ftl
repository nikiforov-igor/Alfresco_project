<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid />

<#assign datagridId = formId + "-datagrid">
<#assign addAssigneeButtonId = formId + "-btn-add-assignee">
<#assign formWrapperId = formId + "-wrapper">
<#assign showActions = true>

<div id="${formWrapperId}">

<#if formUI == "true">
    <@formLib.renderFormsRuntime formId = formId />
</#if>

<@formLib.renderFormContainer formId = formId>
    <@formLib.renderField field = form.fields["prop_bpm_workflowDueDate"] />
    <@formLib.renderField field = form.fields["prop_bpm_workflowDescription"] />
    <#--<@formLib.renderField field = form.fields["assoc_lecmPaWf_employeesAssoc"] />-->

    <div id="${addAssigneeButtonId}-wrapper">
        <div id="${addAssigneeButtonId}"></div>
    </div>

    <div class="form-field with-grid">
        <@grid.datagrid datagridId false />
    </div>

    <div>
        <p><strong>DEBUG MODE</strong></p>
        <p id="added-hidden-dbg"></p>
    </div>

    <input type="hidden" id="asdf-added" name="asdf_added"/>
    <input type="hidden" id="asdf-removed" name="asdf_removed"/>
    <input type="hidden" id="asdf" name="asdf" value="asdf"/>
</@>

</div>



<script>//<![CDATA[
(function() {

    var addedInputElement,
        datagridObj,
        addedAssignees = [];

    var currentRecordSet,
        currentRecords,
        currentRecordSetId;

    // ApproversGrid ==================================================================================================================
    function ApproversGrid( containerId ) {
        return ApproversGrid.superclass.constructor.call( this, containerId );
    }

    YAHOO.lang.extend( ApproversGrid, LogicECM.module.Base.DataGrid );
    // ================================================================================================================================

    function updateFormInputs() {

        debugger;

        currentRecordSet = datagridObj.widgets.dataTable.getRecordSet();
        currentRecords = currentRecordSet.getRecords();
        currentRecordSetId = currentRecordSet.getId();

        var i,
            tail = currentRecords.length - 1; // "tail" - хвост, необходим для правильного расставления запятых.

        addedInputElement.value = "";

        for( i = 0; i < currentRecords.length; ++i ) {
            addedInputElement.value += currentRecords[ i ].getData( "nodeRef" );

            if( i < tail ) {
                addedInputElement.value += ",";
            }
        }

        YAHOO.util.Dom.get( "added-hidden-dbg" ).innerHTML = addedInputElement.value;
    }

    // ================================================================================================================================


    // ================================================================================================================================


    // Запрашиваем nodeRef на "Список по умолчанию" для текущего пользователя, если ничего не получаем, то форму можно
    // не отоборжать.
    Alfresco.util.Ajax.request({
        method: "POST",
        url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/approval/getdefaultfolderref",
        requestContentType: "application/json",
        responseContentType: "application/json",
        successCallback: {
            fn: successCallback,
            scope: this
        },
        failureCallback: {
            fn: function () {
                Alfresco.util.PopupManager.displayMessage({
                    text: "Fail! :("//Alfresco.component.Base.prototype.msg("message.reassign-representative.failure")
                });
            }
        }
    });
    // ================================================================================================================================

    // ================================================================================================================================
    function successCallback( response ) {
        var defaultListFolderRef = response.json.defaultListFolderRef;

        function showAddAssigneeForm() {

            debugger;

            var addAssigneeForm = new Alfresco.module.SimpleDialog("${formId}-form-add-assignee"),

                url = "lecm/components/form" +
                      "?itemKind={itemKind}" +
                      "&itemId={itemId}" +
                      "&destination={destination}" +
                      "&mode={mode}" +
                      "&submitType={submitType}" +
                      "&showCancelButton={showCancelButton}",

                templateUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + url, {
                    itemKind: "type",
                    itemId: "lecm-al:assignees-item",
                    destination: defaultListFolderRef,
                    mode: "create",
                    submitType: "json",
                    showCancelButton: "true"
                });

            currentRecordSet = datagridObj.widgets.dataTable.getRecordSet();
            currentRecords = currentRecordSet.getRecords();
            currentRecordSetId = currentRecordSet.getId();

            currentRecordSet.createEvent( "recordAddEvent" );
            currentRecordSet.createEvent( "recordsAddEvent" );
            currentRecordSet.createEvent( "recordSetEvent" );
            currentRecordSet.createEvent( "recordsSetEvent" );
            currentRecordSet.createEvent( "recordDeleteEvent" );
            currentRecordSet.createEvent( "recordsDeleteEvent" );
            currentRecordSet.createEvent( "recordUpdateEvent" );

            currentRecordSet.subscribe( "recordAddEvent", updateFormInputs );
            currentRecordSet.subscribe( "recordsAddEvent", updateFormInputs );
            currentRecordSet.subscribe( "recordSetEvent", updateFormInputs );
            currentRecordSet.subscribe( "recordsSetEvent", updateFormInputs );
            currentRecordSet.subscribe( "recordDeleteEvent", updateFormInputs );
            currentRecordSet.subscribe( "recordsDeleteEvent", updateFormInputs );
            currentRecordSet.subscribe( "recordUpdateEvent", updateFormInputs );

            addAssigneeForm.setOptions({
                width: "680px",
                templateUrl: templateUrl,
                destroyOnHide: true,
                doBeforeFormSubmit: {
                    fn: function() {
                        window.alert( "doBeforeFormSubmit" );
                    },
                    scope: this
                },
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
        // ================================================================================================================================

        YAHOO.util.Event.onContentReady( "${formWrapperId}", function() {

            debugger;

            // Hook!
            var formCancelButton = YAHOO.util.Dom.get( "${formId}-cancel-button" );
            YAHOO.util.Event.addListener( "${formId}-cancel-button", "click", function() {
                window.alert("formCancelButton hooked, mthrfckr!");
            });

            addedInputElement = YAHOO.util.Dom.get( "asdf-added" );

            var button,
                putButtonIn = YAHOO.util.Dom.get( "${addAssigneeButtonId}" );

            if( putButtonIn !== null ) {
                button = Alfresco.util.createYUIButton( { id: "" }, "${addAssigneeButtonId}", showAddAssigneeForm, {
                    label: "Добавить согласущего"
                }, putButtonIn );

                button.setStyle("margin", "0 0 5px 1px");
            }

            datagridObj = new ApproversGrid("${datagridId}").setOptions({

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
                    id: "onActionDelete",
                    permission: "delete",
                    label: "Удалить согласующего"
                }],
            </#if>

                datagridMeta: {
                    itemType: "lecm-al:assignees-item",
                    nodeRef: defaultListFolderRef,
                    sort:"lecm-al:assignees-item-order|true",
                    actionsConfig: {
                        fullDelete: true,
                        targetDelete: true
                    }
                }
            });

            datagridObj.draw();
        });
    }
})();
//]]>
</script>
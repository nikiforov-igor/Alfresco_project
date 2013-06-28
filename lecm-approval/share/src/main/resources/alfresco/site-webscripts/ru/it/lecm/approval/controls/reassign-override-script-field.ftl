
<script type="text/javascript">//<![CDATA[

(function() {

    var grandPermissionsAndGetPerson = function (form, formToReportProperties){
        //debugger;
        var dataObj = {
            taskID : "",
            employeeNodeRef : form.dataObj.assoc_cm_contains
        };

        var searchString = location.search.replace("?","");
        var searchParams = searchString.split("&");
        var currentPair;
        for(var pairIndex in searchParams){
            currentPair = searchParams[pairIndex].split("=");
            if (currentPair[0] == "taskId"){
                dataObj.taskID = currentPair[1];
                break;
            }
        }

        var personLogin = null;

        jQuery.ajax({
            url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/approval/OverrideReassign",
            type: "GET",
            timeout: 30000, // 30 секунд таймаута хватит всем!
            async: false, // ничего не делаем, пока не отработал запром
            dataType: "json",
            contentType: "application/json",
            //data: YAHOO.lang.JSON.stringify(dataObj), // jQuery странно кодирует данные. пусть YUI эаймеся преобразованием в JSON
            data: dataObj, // jQuery странно кодирует данные. пусть YUI эаймеся преобразованием в JSON
            processData: true, // данные не трогать, не кодировать вообще
            success: function (result, textStatus, jqXHR) {
                //debugger;
                personLogin = result.personLogin;
            },
            error: function(jqXHR, textStatus, errorThrown) {
                Alfresco.util.PopupManager.displayMessage({
                    text: errorThrown
                });
                //debugger;
            }
        });

        // Start/stop inherit rules from parent folder
        Alfresco.util.Ajax.jsonPut(
                {
                    url: Alfresco.constants.PROXY_URI_RELATIVE + "api/task-instances/" + dataObj.taskID,
                    dataObj: {
                        cm_owner: personLogin
                    },
                    successCallback:
                    {
                        fn: function(response, action)
                        {
                            var data = response.json.data;
                            if (data)
                            {
                                Alfresco.util.PopupManager.displayMessage(
                                        {
                                            text: "Переназначено!"
                                        });

                                YAHOO.lang.later(3000, this, function(data)
                                {
                                    if (data.owner && data.owner.userName == Alfresco.constants.USERNAME)
                                    {
                                        // Let the user keep working on the task since he claimed it
                                        document.location.reload();
                                    }
                                    else
                                    {
                                        // Take the user to the most suitable place
                                        var taskEditdHtmlId = "${fieldHtmlId}";
                                        taskEditdHtmlId = taskEditdHtmlId.replace("form", "header");
                                        taskEditdHtmlId = taskEditdHtmlId.replace("_reassignScriptField", "");
                                        var taskEditModel = Alfresco.util.ComponentManager.get(taskEditdHtmlId);
                                        taskEditModel.navigateForward(true);
                                    }
                                }, data);

                            }
                        },
                        obj: null,
                        scope: this
                    },
                    failureCallback:
                    {
                        fn: function(response)
                        {
                            Alfresco.util.PopupManager.displayPrompt(
                                    {
                                        title: this.msg("message.failure"),
                                        text: this.msg("message." + "reassign" + ".failure")
                                    });
                        },
                        scope: this
                    }
                });


    };

    var payLoad = function(){
        var templateUrl = "lecm/components/form"
                + "?itemKind={itemKind}"
                + "&itemId={itemId}"
                + "&mode={mode}"
                + "&submitType={submitType}"
                + "&showCancelButton=true"
                + "&formId={formId}";


        var url = YAHOO.lang.substitute (Alfresco.constants.URL_SERVICECONTEXT + templateUrl, {
            itemKind: "type",
            itemId: "lecm-orgstr:employees",
            mode: "create",
            submitType: "json",
            formId: "reassign-override-form"
        });

        var editDetails = new Alfresco.module.SimpleDialog("${fieldHtmlId}-dialogOverride");
        editDetails.setOptions({
            width: "50em",
            templateUrl: url,
            actionUrl: null,
            destroyOnHide: true,
            doBeforeDialogShow: {
                fn: function ( p_form, p_dialog ) {
                    //debugger;
                    //editDetails.dialog.form.buttons.cancel.hide();
					p_dialog.dialog.setHeader( "Переназначение задачи" );
                    var frm = Alfresco.util.ComponentManager.get(this.dialog.form.id);
                    var btn = frm.buttons.cancel;
                    btn._button.style.display = "none";
                    btn._button.hidden = "hidden";
                    LogicECM.CurrentModules.employeesAssocAutoComplete.setOptions( { multipleSelectMode: false } );
                    LogicECM.CurrentModules.employeesAssocTreeView.setOptions( { multipleSelectMode: false } );
                },
                scope: editDetails
            },
            doBeforeAjaxRequest: {
                fn: grandPermissionsAndGetPerson,
                scope : this
            },
            doBeforeFormSubmit: {
                fn: function () {
                },
                scope: this
            },
            onSuccess: {
                fn: function DataGrid_onActionEdit_success(response) {
                },
                scope: this
            },
            onFailure:{
                fn:function DataGrid_onActionEdit_failure(response) {

                },
                scope:this
            }
        }).show();
    };

    var reassignContentReady = function (reassignHtmlId){
        var btn = YAHOO.widget.Button.getButton(reassignHtmlId);
        btn.removeListener();
        btn.on("click", payLoad);
    };


    var fieldHtmlId = "${fieldHtmlId}";
    var resignButtonHtmlId = fieldHtmlId
            .replace("form", "header")
            .replace("_reassignScriptField", "-reassign");
    //Alfresco.util.ComponentManager.get("page_x002e_data-header_x002e_task-edit_x0023_default")
    //page_x002e_data-header_x002e_task-edit_x0023_default-reassign

    YAHOO.util.Event.onContentReady(resignButtonHtmlId + "-button", reassignContentReady, resignButtonHtmlId);
})();



//]]></script>
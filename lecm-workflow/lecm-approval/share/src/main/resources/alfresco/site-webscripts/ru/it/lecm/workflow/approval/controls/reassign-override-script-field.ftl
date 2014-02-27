<script type="text/javascript">//<![CDATA[

(function() {

    var doubleClickLock = false;

    var grandPermissionsAndGetPerson = function (form, formToReportProperties){
        var dataObj = {
            taskID : "",
            employeeNodeRef : form.dataObj["reassign-to-employee"]
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
            data: dataObj, // jQuery странно кодирует данные. пусть YUI эаймеся преобразованием в JSON
            processData: true, // данные не трогать, не кодировать вообще
            success: function (result, textStatus, jqXHR) {
                personLogin = result.personLogin;
            },
            error: function(jqXHR, textStatus, errorThrown) {
                Alfresco.util.PopupManager.displayMessage({
                    text: errorThrown
                });
            }
        });

        Alfresco.util.Ajax.jsonPut({
			url: Alfresco.constants.PROXY_URI_RELATIVE + "api/task-instances/" + dataObj.taskID,
			dataObj: {
				cm_owner: personLogin
			},
			successCallback: {
				fn: function(response, action) {
					var data = response.json.data;
					if (data) {
						Alfresco.util.PopupManager.displayMessage( {
							text: "Переназначено!"
						});

						YAHOO.lang.later(3000, this, function(data) {
							if (data.owner && data.owner.userName == Alfresco.constants.USERNAME) {
								document.location.reload();
							} else {
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
			failureCallback: {
				fn: function(response) {
					Alfresco.util.PopupManager.displayPrompt({
						title: this.msg("Ошибка при переназначении задачи"),
						text: this.msg("Не удалось переназначить задачу на другого пользователя, попробуйте еще раз")
					});
				},
				scope: this
			}
		});
	};

    var payLoad = function(){
        if (doubleClickLock) return;
        doubleClickLock = true;
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

        var me = this;
        var editDetails = new Alfresco.module.SimpleDialog("${fieldHtmlId}-dialogOverride");
        editDetails.setOptions({
            width: "50em",
            templateUrl: url,
            actionUrl: null,
            destroyOnHide: true,
            doBeforeDialogShow: {
                fn: function ( p_form, p_dialog ) {
					p_dialog.dialog.setHeader( "Переназначение задачи" );
                    var frm = Alfresco.util.ComponentManager.get(this.dialog.form.id);
                    frm.formsRuntime.addValidation("${fieldHtmlId}-dialogOverride_reassign-to-employee-cntrl-currentValueDisplay", validateField, null, "DOMSubtreeModified");
                    this.dialog.validate = validateField;
                    var btn = frm.buttons.cancel;
                    btn._button.style.display = "none";
                    btn._button.hidden = "hidden";
                    LogicECM.CurrentModules.employeesAssocAutoComplete.setOptions( { multipleSelectMode: false } );
                    LogicECM.CurrentModules.employeesAssocTreeView.setOptions( { multipleSelectMode: false } );
                    me.doubleClickLock = false;
                },
                scope: editDetails
            },
            doBeforeAjaxRequest: {
                fn: grandPermissionsAndGetPerson,
                scope : this
            },
            onSuccess:{
                fn:function (response) {
                    this.doubleClickLock = false;
                },
                scope:this
            },
            onFailure: {
                fn:function (response) {
                    this.doubleClickLock = false;
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

    var validateField = function(field, args,  event, form, silent, message){
        if(!field) {
            field = document.getElementById("${fieldHtmlId}-dialogOverride_reassign-to-employee-cntrl-currentValueDisplay");
        }
        var selected = field.getElementsByClassName("association-auto-complete-selected-item");
        if(selected != null && selected.length > 0){
            return true;
        }
        return false;
    }

    var fieldHtmlId = "${fieldHtmlId}";
    var resignButtonHtmlId = fieldHtmlId
            .replace("form", "header")
            .replace("_reassignScriptField", "-reassign");

    YAHOO.util.Event.onContentReady(resignButtonHtmlId + "-button", reassignContentReady, resignButtonHtmlId);
})();

//]]></script>
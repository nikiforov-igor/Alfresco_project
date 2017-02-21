<#include "/org/alfresco/components/form/controls/common/utils.inc.ftl" />
<#assign employeeRef = form.arguments.itemId/>
<#assign  id = args.htmlid/>

<script type="text/javascript">//<![CDATA[
(function () {
    var Dom = YAHOO.util.Dom;
    var dataRef;

    function drawForm(nodeRef) {
        dataRef = nodeRef;
        Alfresco.util.Ajax.request(
                {
                    url: Alfresco.constants.URL_SERVICECONTEXT + "components/form",
                    dataObj: {
                        htmlid: "${id}" + "-personal-data",
                        itemKind: "node",
                        itemId: nodeRef,
                        mode: "view",
                        showSubmitButton: "false"
                    },
                    successCallback: {
                        fn: function (response) {
                            var formEl = document.getElementById("${id}-contentPersonalData");
                            formEl.innerHTML = response.serverResponse.responseText;
                        }
                    },
                    failureMessage: "message.failure",
                    execScripts: true
                });
    }

    function showDialogCreate(nodeRef) {
        // Intercept before dialog show
        var doBeforeDialogShow = function BeforeDialogShow(p_form, p_dialog) {
            Alfresco.util.populateHTML(
                    [ "${id}-dialogTitle", "Personal Data" ]
            );
            var contId = p_dialog.id + "-form-container";
            Alfresco.util.populateHTML(
                    [contId + "_h", "${msg('info.personal-info')}" ]
            );
            p_dialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, {moduleId: p_dialog.id}, this);
        };

        // Using Forms Service, so always create new instance
        var templateUrl = Alfresco.constants.URL_SERVICECONTEXT + "components/form";
        var templateRequestParams = {
            itemKind: "type",
            itemId: "lecm-orgstr:personal-data",
            destination: nodeRef,
            mode: "create",
            submitType: "json",
            showCancelButton: true
        };

        // Using Forms Service, so always create new instance
        var createDetails = new Alfresco.module.SimpleDialog("${id}-personalDataDialog");
        createDetails.setOptions(
                {
                    width: "50em",
                    templateUrl: templateUrl,
                    templateRequestParams: templateRequestParams,
                    actionUrl: null,
                    destroyOnHide: true,
                    doBeforeDialogShow: {
                        fn: doBeforeDialogShow,
                        scope: this
                    },
                    onSuccess: {
                        fn: function on_success(response) {
                            Alfresco.util.Ajax.jsonRequest(
                                    {
                                        url: Alfresco.constants.PROXY_URI + "lecm/base/createAssoc",
                                        method: "POST",
                                        dataObj: {
                                            source: "${employeeRef}",
                                            target: response.json.persistedObject,
                                            assocType: "lecm-orgstr:employee-person-data-assoc"
                                        },
                                        successCallback: {
                                            fn: function () {
                                                Dom.removeClass("${id}-editPersonalData", 'hidden');
                                                Dom.addClass("${id}-createPersonalData", 'hidden');
                                                drawForm(response.json.persistedObject);
                                            },
                                            scope: this
                                        },
                                        failureCallback: {
                                            fn: function () {
                                                alert("${msg('message.association.creation.fail')}")
                                            },
                                            scope: this
                                        }
                                    });
                        },
                        scope: this
                    },
                    onFailure: {
                        fn: function DataGrid_onActionCreate_failure() {
                            Alfresco.util.PopupManager.displayMessage(
                                    {
                                        text: "${msg('message.data.load.fail')}"
                                    });
                        },
                        scope: this
                    }
                }).show();
    }

    function showDialogEdit(nodeRef) {
        // Intercept before dialog show
        var doBeforeDialogShow = function BeforeDialogShow(p_form, p_dialog) {
            Alfresco.util.populateHTML(
                    [ "${id}-dialogTitle", "Personal Data" ]
            );
            var contId = p_dialog.id + "-form-container";
            Alfresco.util.populateHTML(
                    [contId + "_h", "${msg('info.personal-info')}" ]
            );

            p_dialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, {moduleId: p_dialog.id}, this);
        };

        // Using Forms Service, so always create new instance
        var templateUrl = Alfresco.constants.URL_SERVICECONTEXT + "components/form";
        var templateRequestParams = {
            itemKind: "node",
            itemId: nodeRef,
            mode: "edit",
            submitType: "json",
            showCancelButton: true
        };

        // Using Forms Service, so always create new instance
        var editDetails = new Alfresco.module.SimpleDialog("${id}-personalDataDialog");
        editDetails.setOptions(
                {
                    width: "50em",
                    templateUrl: templateUrl,
                    templateRequestParams: templateRequestParams,
                    actionUrl: null,
                    destroyOnHide: true,
                    doBeforeDialogShow: {
                        fn: doBeforeDialogShow,
                        scope: this
                    },
                    onSuccess: {
                        fn: function on_success() {
                            drawForm(nodeRef)
                        },
                        scope: this
                    },
                    onFailure: {
                        fn: function DataGrid_onActionCreate_failure() {
                            Alfresco.util.PopupManager.displayMessage(
                                    {
                                        text: "${msg('message.data.load.fail')}"
                                    });
                        },
                        scope: this
                    }
                }).show();
    }

    function editPersonalData() {
        showDialogEdit(dataRef);
    }

    function createPersonalData() {
        var sUrl = Alfresco.constants.PROXY_URI + "/lecm/orgstructure/api/createEmployeePersonData";
        var callback = {
            success: function (oResponse) {
                var oResults = eval("(" + oResponse.responseText + ")");
                if (oResults != null) {
                    showDialogCreate(oResults.nodeRef);
                }
            },
            failure: function () {
                alert("${msg('message.personal-data.load.fail')}");
            },
            argument: {
            }
        };
        YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
    }

    function initialize() {
        new YAHOO.widget.Button("${id}-editPersonalData", { onclick: { fn: editPersonalData} });
        new YAHOO.widget.Button("${id}-createPersonalData", { onclick: { fn: createPersonalData} });
		Alfresco.util.Ajax.jsonGet({
			url: Alfresco.constants.PROXY_URI + "lecm/orgstructure/api/getEmployeePersonalData",
			dataObj: {
				nodeRef: "${employeeRef}"
			},
			successCallback: {
				fn: function (oResponse) {
					if (oResponse.json && oResponse.json.nodeRef) {
                        drawForm(oResults.nodeRef);
                        // скрываем кнопку создать
                        Dom.removeClass("${id}-editPersonalData", 'hidden');
					} else {
                        // скрываем кнопку редактировать
                        Dom.removeClass("${id}-createPersonalData", 'hidden');
					}
				}
			},
			failureMessage: "${msg('message.personal-data.load.fail')}"
		});
    }

    YAHOO.util.Event.onContentReady("${id}-buttonPersonalData", initialize, this);
})();
//]]></script>

<div class="control view-personal-data viewmode">
    <div class="container">
        <div class="value-div">
            <div id="${id}">
                <div id="${id}-contentPersonalData"></div>
                <div id="${id}-buttonPersonalData">
                <#if form.mode == "edit" || form.mode == "create">
                    <span id="${id}-createPersonalData" class="yui-button yui-push-button hidden">
                       <span class="first-child">
                          <button type="button">${msg('button.create')}</button>
                       </span>
                    </span>
                    <span id="${id}-editPersonalData" class="yui-button yui-push-button hidden">
                       <span class="first-child">
                          <button type="button">${msg('button.edit')}</button>
                       </span>
                    </span>
                </#if>
                </div>
            </div>
        </div>
    </div>
</div>
<div class="clear"></div>

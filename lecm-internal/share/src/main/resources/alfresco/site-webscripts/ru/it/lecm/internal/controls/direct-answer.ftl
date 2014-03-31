<#include "/org/alfresco/components/form/controls/common/utils.inc.ftl" />
<#include "/ru/it/lecm/base-share/components/controls/lecm-dnd-uploader-container.ftl">

<#assign htmlid=args.htmlid?html>
<#assign hideValue = false>
<#assign typicalId = fieldHtmlId + "-typical-control">
<#assign uploaderId = fieldHtmlId + "-upload-control">
<#assign fieldId=field.id!"">

<div class="form-field">

<script type="text/javascript">//<![CDATA[

/**
 * LogicECM root namespace.
 *
 * @namespace LogicECM
 */
// Ensure LogicECM root object exists
if (typeof LogicECM == "undefined" || !LogicECM) {
    var LogicECM = {};
}

/**
 * LogicECM top-level module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module
 */
LogicECM.module = LogicECM.module || {};


(function () {

    /**
     * YUI Library aliases
     */
    var Dom = YAHOO.util.Dom,
            Event = YAHOO.util.Event,
            UA = YAHOO.util.UserAction,
            Connect = YAHOO.util.Connect;

    /**
     * Toolbar constructor.
     *
     * @param htmlId {String} The HTML id of the parent element
     * @return {Alfresco.component.AllDictToolbar} The new Toolbar instance
     * @constructor
     */
    LogicECM.module.DirectAnswer = function (htmlId) {
        LogicECM.module.DirectAnswer.superclass.constructor.call(this, "LogicECM.module.DirectAnswer", htmlId, ["button", "container"]);
        return this;
    };

    /**
     * Extend from Alfresco.component.Base
     */
    YAHOO.extend(LogicECM.module.DirectAnswer, Alfresco.component.Base);

    /**
     * Augment prototype with main class implementation, ensuring overwrite is enabled
     */
    YAHOO.lang.augmentObject(LogicECM.module.DirectAnswer.prototype,
            {
                addDocumentButton: null,
                answerTextField: null,
                typicalAnswerField: null,
                destination: null,
                persistedObject: "",
                uploader: null,
                uploaderResult: "",
                initFields: "${form.fields["prop_internalDirect_formData"].value?js_string}",

                /**
                 * Object container for initialization options
                 *
                 * @property options
                 * @type object
                 */
                options: {

                },

                onReady: function Toolbar_onReady() {
                    var me = this;
                    var url = Alfresco.constants.PROXY_URI + "lecm/document-type/settings?docType=lecm-internal:document";
                    var callback = {
                        success: function (oResponse) {
                            var oResults = eval("(" + oResponse.responseText + ")");
                            me.destination = oResults.nodeRef;
                        },
                        argument: {
                            parent: this
                        },
                        timeout: 60000
                    };
                    YAHOO.util.Connect.asyncRequest('GET', url, callback);

                    YAHOO.util.Event.on(this.id + "-first-selector", "click", this.onFirst, null, this);
                    YAHOO.util.Event.on(this.id + "-second-selector", "click", this.onSecond, null, this);
                    YAHOO.util.Event.on(this.id + "-third-selector", "click", this.onThird, null, this);
                    this.answerTextField = Dom.get(this.id + "-answer-text");
                    YAHOO.util.Event.on(this.id + "-answer-text", "keyup", this.onThirdChanged, null, this);
                    YAHOO.util.Event.on(this.id + "-typical-control", "change", this.onThirdChanged, null, this);
                    YAHOO.util.Event.on("${uploaderId}-added", "click", this.onThirdChanged, null, this);
                    YAHOO.util.Event.on("${uploaderId}-removed", "click", this.onThirdChanged, null, this);

                    this.typicalAnswerField = Dom.get(this.id + "-typical-control");
                    this.uploaderResult = Dom.get("${uploaderId}-added")
                    this.addDocumentButton = Alfresco.util.createYUIButton(this, "addDocument", this.addDocument, {
                        disabled: true
                    });
                },
                addDocument: function addDocement_function() {
                    var templateUrl = Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form";
	                var templateRequestParams = {
		                itemKind: "type",
		                destination: this.destination,
		                itemId: "lecm-internal:document",
		                mode: "create",
		                submitType: "json",
		                formId: "workflow-form",
		                args: this.initFields,
		                showCancelButton: true
	                };

                    var me = this;
                    LogicECM.CurrentModules = {};
                    LogicECM.CurrentModules.WorkflowForm = new Alfresco.module.SimpleDialog("workflow-form").setOptions({
                        width: "84em",
                        templateUrl: templateUrl,
	                    templateRequestParams: templateRequestParams,
                        actionUrl: null,
                        destroyOnHide: true,
                        doBeforeDialogShow: {
                            fn: function (p_form, p_dialog) {
                                var contId = p_dialog.id + "-form-container";
                                var dialogName = this.msg("logicecm.workflow.runAction.label", "Ответ");
                                Alfresco.util.populateHTML(
                                        [contId + "_h", dialogName]
                                );

	                            p_dialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, {moduleId: p_dialog.id}, this);

	                            Dom.addClass(contId, "metadata-form-edit");
                                Dom.addClass(contId, "lecm-internal_document");
                                me.doubleClickLock = false;
                            }
                        },
                        onSuccess: {
                            fn: function (response) {
                                me.persistedObject = response.json.persistedObject;
                                me.saveDocumentAnswer();
                                Dom.get("${htmlid}-form-submit").click();
                            },
                            scope: this
                        }
                    }).show();
                },
                onFirst: function onFirst_function() {
                    this.clearAnswer();
                    this.disableSecond();
                    this.disableThird();
                    var answer = {
                        type: "takeNote"
                    }
                    this.saveAnswer(answer);
                },
                onSecond: function onSecond_function() {
                    this.clearAnswer();
                    this.disableThird();
                    this.saveDocumentAnswer();
                    this.addDocumentButton.set("disabled", false);

                },
                onThird: function onThird_function() {
                    this.clearAnswer();
                    this.disableSecond();
                    this.answerTextField.disabled = false;
                    this.typicalAnswerField.disabled = false;
                    this.uploader.enable();
                    this.onThirdChanged();

                },
                onThirdChanged: function onThird_function() {
                    this.clearAnswer();
                    var typical = this.typicalAnswerField.value;
                    var manual = this.answerTextField.value;
                    var files = this.uploaderResult.value;
                    if (typical != "" || manual != "") {
                        var answer = {
                            type: "answer",
                            typical: typical,
                            manual: manual,
                            files: files
                        };
                        this.saveAnswer(answer);
                    }
                },
                saveDocumentAnswer: function saveDocumentAnswer_function() {
                    if (this.persistedObject != "") {
                        var answer = {
                            type: "document",
                            nodeRef: this.persistedObject
                        };
                        this.saveAnswer(answer);
                    }
                },
                saveAnswer: function saveResponse_function(answer) {
                    Dom.get(this.id).value = JSON.stringify(answer);
                    YAHOO.Bubbling.fire("mandatoryControlValueUpdated", this);
                },
                clearAnswer: function clearResponse_function() {
                    Dom.get(this.id).value = "";
                    YAHOO.Bubbling.fire("mandatoryControlValueUpdated", this);
                },
                disableSecond: function disableSecond_function() {
                    this.addDocumentButton.set("disabled", true);
                },
                disableThird: function disableThird_function() {
                    this.answerTextField.disabled = true;
                    this.typicalAnswerField.disabled = true;
                    this.uploader.disable();
                },
                setUploader: function setUploader_function(uploader) {
                    this.uploader = uploader;
                }
            });

    YAHOO.util.Event.onDOMReady(function () {
        var directAnswer = new LogicECM.module.DirectAnswer("${fieldHtmlId}");
        var control = new LogicECM.module.AssociationSelectOne("${typicalId}").setMessages(${messages});
        control.setOptions(
                {
                    disabled: false,
                    parentNodeRef: "",
                    startLocation: "/app:company_home/cm:Business_x0020_platform/cm:LECM/cm:Сервис_x0020_Справочники/lecm-dic:Типовые_x0020_ответы",
                    mandatory: false,
                    itemType: "lecm-internal-typical-answer:dictionary",
                    itemFamily: "node",
                    maxSearchResults: '1000',
                    oldValue: "",
                    selectedValueNodeRef: "",
                    nameSubstituteString: "{cm:name}",
                    showCreateNewButton: false,
                    notSelectedOptionShow: true,
                    notSelectedText: "",
                    fieldId: "${fieldId}"
                });
        var uploader = new LogicECM.control.DndUploader("${uploaderId}").setMessages(${messages});
        uploader.setOptions(
                {
                    uploadDirectoryPath: "{usertemp}",
                    disabled: false,
                    multipleMode: false,
                    autoSubmit: false,
                    showUploadNewVersion: false,
                    directoryName: "Ответы",
                    checkRights: false,
                    itemNodeRef: "",
                    currentValue: "",
                    suppressRefreshEvent: true,
                    useDnD: false
                });
        uploader.disable();
        directAnswer.setUploader(uploader);
    });

})();
//]]></script>

<input type="hidden" id="${fieldHtmlId}" name="${field.name}" tabindex="0"/>

<table style="width: 100%">
    <tr>
        <td valign="middle"><input type="radio" name="direct-type" id="${fieldHtmlId}-first-selector"/></td>
        <td valign="middle">Принять к сведению</td>
    </tr>
    <tr>
        <td valign="middle"><input type="radio" name="direct-type" id="${fieldHtmlId}-second-selector"/></td>
        <td valign="middle">
            <table cellpadding="0">
                <tr>
                    <td nowrap="nowrap" valign="middle">Создать ответный документ</td>
                    <td valign="middle" style="width: 100%">
                        <span id="${fieldHtmlId}-addDocument" class="yui-button yui-push-button">
                            <span class="first-child">
                                <button type="button"
                                        title="Создать внутренний документ">+
                                </button>
                            </span>
                        </span>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
    <tr>
        <td valign="top"><input type="radio" name="direct-type" id="${fieldHtmlId}-third-selector"/></td>
        <td>Создать ответ комментарий
            <input type="hidden" id="${typicalId}-removed" name="${field.name}_typical_removed"/>
            <input type="hidden" id="${typicalId}-added" name="${field.name}_typical_added"/>

            <div id="${typicalId}-controls" class="selectone-control">
                <select id="${typicalId}" name="${field.name}_typical" tabindex="0" disabled="true">
                    <option value="">Типовой ответ</option>
                </select>
            </div>
            <textarea id="${fieldHtmlId}-answer-text" rows="20" style="width: 100%; margin-top:10px;"
                      disabled="true"></textarea>


            <div class="form-field dnd-uploader">
                <input id="${uploaderId}" type="hidden" class="autocomplete-input" name="${field.name}_uploader" value="${field.value?html}"/>
                <input type="hidden" id="${uploaderId}-removed" name="${field.name}_removed"/>
                <input type="hidden" id="${uploaderId}-added" name="${field.name}_added"/>

                <ul id="${uploaderId}-attachments" class="attachments-list" style="margin-top: 13px; width: 397px; margin-bottom: 12px;"></ul>
                <div id="${uploaderId}-uploader-block" class="uploader-block" style="margin-left: 0">
                    <fieldset>
                        <legend>${msg("label.add-file")}</legend>
                        <img id="${uploaderId}-uploader-button" src="/share/res/images/lecm-base/components/plus.png" alt="" class="uploader-button">  <br/>
                        <span class="drag-tip">${msg("label.drag-file")}</span>
                    </fieldset>
                </div>
            </div>
        </td>
    </tr>
</table>
</div>
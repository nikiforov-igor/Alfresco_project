<#include "/org/alfresco/components/form/controls/common/utils.inc.ftl" />
<#include "/ru/it/lecm/base-share/components/controls/lecm-dnd-uploader-container.ftl">

<#assign htmlid=args.htmlid?html>
<#assign hideValue = false>
<#assign typicalId = fieldHtmlId + "-typical-control">
<#assign uploaderId = fieldHtmlId + "-upload-control">
<#assign fieldId=field.id!"">

<div class="direct-answer">

<script type="text/javascript">//<![CDATA[

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
                    var url = Alfresco.constants.PROXY_URI + "lecm/document-type/settings";
                    Alfresco.util.Ajax.jsonGet({
                        url: url,
                        dataObj: {
                            docType: "lecm-internal:document"
                        },
                        successCallback: {
                            scope: this,
                            fn: function (response) {
                                if (response.json && response.json.nodeRef) {
                                    this.destination = response.json.nodeRef;
                                }
                            }
                        }
                    });

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
                    var url =  Alfresco.constants.URL_PAGECONTEXT + "document-create?documentType=" + "lecm-internal:document";

                    var params = "documentType=" + "lecm-internal:document";
                    params += "&formId=" + "workflow-form";
                    params += "&workflowTask=" + "${form.arguments.itemId!""}";

                    if (this.initFields != null && this.initFields != "") {
                        var fields = eval("(" + this.initFields + ")");
                        for (var prop in fields) {
                            if (fields.hasOwnProperty(prop)) {
                                params += "&" + prop + "=" + fields[prop];
                            }
                        }
                    }

                    window.location.href = url + "&" + LogicECM.module.Base.Util.encodeUrlParams(params);
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

    function init() {
        LogicECM.module.Base.Util.loadResources([
	            'scripts/lecm-base/components/lecm-dnd-uploader.js',
	            'scripts/lecm-base/components/lecm-uploader-initializer.js',
	            'scripts/lecm-base/components/lecm-dnd-uploader-control.js',
	            'scripts/lecm-base/components/lecm-association-selectone.js'],
	        [
		        'css/lecm-internal/direct-answer.css'
	        ],
            initControl,
            ["container", "datasource"]);
    }

    function initControl() {
        var directAnswer = new LogicECM.module.DirectAnswer("${fieldHtmlId}");
        var control = new LogicECM.module.AssociationSelectOne("${typicalId}").setMessages(${messages});
        control.setOptions(
                {
                    disabled: false,
                    parentNodeRef: "",
                    startLocation: "/app:company_home/cm:Business_x0020_platform/cm:LECM/cm:????????????_x0020_??????????????????????/lecm-dic:??????????????_x0020_????????????",
                    mandatory: false,
                    itemType: "lecm-internal-typical-answer:dictionary",
                    itemFamily: "node",
                    maxSearchResults: '1000',
                    oldValue: "",
                    selectedValueNodeRef: "",
                    nameSubstituteString: "{cm:title}",
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
                    directoryName: "????????????",
                    checkRights: false,
                    itemNodeRef: "",
                    currentValue: "",
                    suppressRefreshEvent: true,
                    useDnD: true
                });
        uploader.disable();
        directAnswer.setUploader(uploader);
    }

    YAHOO.util.Event.onDOMReady(init);

})();
//]]></script>

    <input type="hidden" id="${fieldHtmlId}" name="${field.name}" tabindex="0"/>

    <div>
        <input type="radio" name="direct-type" id="${fieldHtmlId}-first-selector" class="lecm-radio"/>
        <label for="${fieldHtmlId}-first-selector">?????????????? ?? ????????????????</label>
    </div>
    <div class="with-button">
        <input type="radio" name="direct-type" id="${fieldHtmlId}-second-selector" class="lecm-radio"/>
        <label for="${fieldHtmlId}-second-selector">?????????????? ???????????????? ????????????????</label>
        <span id="${fieldHtmlId}-addDocument" class="yui-button yui-push-button">
            <span class="first-child">
                <button type="button" title="?????????????? ???????????????????? ????????????????"> + </button>
            </span>
        </span>
    </div>
    <div>
        <input type="radio" name="direct-type" id="${fieldHtmlId}-third-selector" class="lecm-radio"/>
        <label for="${fieldHtmlId}-third-selector">?????????????? ??????????-??????????????????????</label>

        <input type="hidden" id="${typicalId}-removed" name="${field.name}_typical_removed"/>
        <input type="hidden" id="${typicalId}-added" name="${field.name}_typical_added"/>

        <div class="answer-comment">
            <div id="${typicalId}-controls" class="selectone-control">
                <select id="${typicalId}" name="${field.name}_typical" tabindex="0" disabled="true">
                    <option value="">?????????????? ??????????</option>
                </select>
            </div>
            <textarea id="${fieldHtmlId}-answer-text" class="answer-text" rows="20" disabled="true"></textarea>

            <div class="control dnd-uploader editmode">
                <input id="${uploaderId}" type="hidden" class="autocomplete-input" name="${field.name}_uploader" value="${field.value?html}"/>
                <input type="hidden" id="${uploaderId}-removed" name="${field.name}_removed"/>
                <input type="hidden" id="${uploaderId}-added" name="${field.name}_added"/>

                <div class="container">
                    <div id="${uploaderId}-uploader-block" class="uploader-block">
                        <fieldset>
                            <legend align="center">${msg("label.add-file")}</legend>
                            <div class="uploader-container">
                                <div class="uploader-button-block">
                                    <div id="${uploaderId}-uploader-button" class="uploader-button"></div>
                                </div>
                                <div class="uploader-content middle-centring">
                                    <div id="${uploaderId}-drag-file">
                                        <span class="drag-tip">${msg("label.drag-file")}</span>
                                    </div>
                                    <div class="load-attachments">
                                        <div class="value-div">
                                            <ul id="${uploaderId}-attachments" class="attachments-list" tabindex="1"></ul>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </fieldset>
                    </div>
                </div>
            </div>
            <div class="clear"></div>
        </div>
    </div>
</div>
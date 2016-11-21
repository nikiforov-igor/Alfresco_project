<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>
<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<@markup id="css">
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-group-actions/group-actions.css" />
</@>

<#assign id = "group-actions-import">
<#assign importFormId = id + "-import-form">
<#assign importInfoFormId = id + "-import-info-form">
<#assign importErrorFormId = id + "-import-error-form">

<#assign gridId = "group-actions"/>
<#assign controlId = gridId + "-cntrl">
<#assign containerId = gridId + "-container">

<#assign showSearchBlock=true/>
<#assign showExSeacrhBtn=false/>

<#assign idX = args.htmlid>

<script type="text/javascript">//<![CDATA[
(function(){
	function createToolbar() {
	    new LogicECM.module.GroupActions.Toolbar("${idX}").setOptions({
	    }).setMessages(${messages});
	}

    function init() {
        LogicECM.module.Base.Util.loadResources([
            'scripts/lecm-base/components/lecm-toolbar.js',
            'scripts/lecm-group-actions/group-actions-toolbar.js'
        ], [
            'components/data-lists/toolbar.css',
            'css/lecm-group-actions/group-actions-toolbar.css'
        ], createToolbar);
    }

    YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>

<@comp.baseToolbar idX true showSearchBlock showExSeacrhBtn>
</@comp.baseToolbar>

<div class="form-field with-grid group-actions-grid" id="group-actions-${controlId}">
<div>
<div id="${id}-script" title="${msg('label.script')}" class="left-button-container">
    <span id="${id}-scriptButton" class="yui-button yui-push-button">
        <span class="first-child">
            <button type="button" title="${msg('label.script')}">${msg('label.script')}</button>
        </span>
    </span>
</div>

<div id="${id}-workflow" title="${msg('label.workflow')}" class="left-button-container">
    <span id="${id}-workflowButton" class="yui-button yui-push-button">
        <span class="first-child">
            <button type="button" title="${msg('label.workflow')}">${msg('label.workflow')}</button>
        </span>
    </span>
</div>

<div id="${id}-document" title="${msg('label.document')}" class="left-button-container">
    <span id="${id}-documentButton" class="yui-button yui-push-button">
        <span class="first-child">
            <button type="button" title="${msg('label.document')}">${msg('label.document')}</button>
        </span>
    </span>
</div>

<div id="${id}-import-xml" class="import-xml right-button-container" title="${msg('button.import-xml')}">
    <span id="${id}-importXmlButton" class="yui-button yui-push-button">
        <span class="first-child">
            <button type="button" title="${msg('button.import-xml')}">${msg('button.import-xml')}</button>
        </span>
    </span>
</div>
</div>

<div id="${importInfoFormId}" class="yui-panel hidden1">
    <div id="${importInfoFormId}-head" class="hd">${msg("title.import.info")}</div>
    <div id="${importInfoFormId}-body" class="bd">
        <div id="${importInfoFormId}-content" class="import-info-content"></div>
    </div>
</div>

<div id="${importErrorFormId}" class="yui-panel hidden1">
    <div id="${importErrorFormId}-head" class="hd">${msg("title.import.info")}</div>
    <div id="${importErrorFormId}-body" class="bd">
        <div id="${importErrorFormId}-content" class="import-info-content">
            <div class="import-error-header">
                <h3>${msg("import.failure")}</h3>
                <a href="javascript:void(0);"
                   id="${importErrorFormId}-show-more-link">${msg("import.failure.showMore")}</a>
            </div>
            <div id="${importErrorFormId}-more" class="import-error-more">
                <div class="import-error-exception">
                ${msg("import.failure.exception")}:
                    <div class="import-error-exception-content" id="${importErrorFormId}-exception">
                    </div>
                </div>
                <div class="import-error-stack-trace">
                ${msg("import.failure.stack-trace")}:
                    <div class="import-error-stack-trace-content" id="${importErrorFormId}-stack-trace">
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<div id="${importFormId}" class="yui-panel hidden1">
    <div id="${importFormId}-head" class="hd">${msg("title.import")}</div>
    <div id="${importFormId}-body" class="bd">
        <div id="${importFormId}-content">
            <form method="post" id="${id}-import-xml-form" enctype="multipart/form-data"
                  action="${url.context}/proxy/alfresco/lecm/dictionary/post/import">
                <ul class="import-form">
                    <li>
                        <label for="${importFormId}-import-file">${msg("label.import-file")}*</label>
                        <input id="${importFormId}-import-file" type="file" name="file"
                               accept=".xml,application/xml,text/xml">
                    </li>
                    <li>
                        <label for="${importFormId}-chbx-ignore">${msg("label.ignore-errors")}</label>
                        <input id="${importFormId}-chbx-ignore" type="checkbox" name="ignoreErrors" value="true"/>
                    </li>
                </ul>
                <div class="bdft">
                    <button id="${importFormId}-submit" disabled="true"
                            tabindex="0">${msg("button.import-xml")}</button>
                    <button id="${importFormId}-cancel" tabindex="1">${msg("button.cancel")}</button>
                </div>
            </form>
        </div>
    </div>
</div>

<@grid.datagrid containerId false gridId+"form">
<script type="text/javascript">//<![CDATA[

/**
 * LogicECM root namespace.
 *
 * @namespace LogicECM
 */
// Ensure LogicECM root object exists
if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

/**
 * LogicECM top-level module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module
 */
LogicECM.module = LogicECM.module || {};


/**
 * LogicECM Dictionary module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module.Dictionary.Dictionary
 */
LogicECM.module.GroupActions = LogicECM.module.GroupActions || {};

(function () {

    /**
     * YUI Library aliases
     */
    var Dom = YAHOO.util.Dom,
            Event = YAHOO.util.Event,
            UA = YAHOO.util.UserAction,
            Connect = YAHOO.util.Connect,
            Bubbling = YAHOO.Bubbling;

    /**
     * Toolbar constructor.
     *
     * @param htmlId {String} The HTML id of the parent element
     * @return {Alfresco.component.AllDictToolbar} The new Toolbar instance
     * @constructor
     */
    LogicECM.module.GroupActions.Import = function (htmlId) {
        LogicECM.module.GroupActions.Import.superclass.constructor.call(this, "LogicECM.module.GroupActions.Import", htmlId, ["button", "container"]);
        return this;
    };

    /**
     * Extend from Alfresco.component.Base
     */
    YAHOO.extend(LogicECM.module.GroupActions.Import, Alfresco.component.Base);

    /**
     * Augment prototype with main class implementation, ensuring overwrite is enabled
     */
    YAHOO.lang.augmentObject(LogicECM.module.GroupActions.Import.prototype,
            {
                /**
                 * Object container for initialization options
                 *
                 * @property options
                 * @type object
                 */
                options: {},

                importFromDialog: null,

                importInfoDialog: null,

                submitButton: null,

                editDialogOpening: false,

                /**
                 * Fired by YUI when parent element is available for scripting.
                 *
                 * @method onReady
                 */
                show: function Toolbar_onReady() {
                    // Import XML
                    var importXmlButton = Alfresco.util.createYUIButton(this, "importXmlButton", this.showImportDialog, {});

                    this.submitButton = Alfresco.util.createYUIButton(this, "import-form-submit", this.onImportXML, {
                        disabled: true
                    });
                    var importXmlButton = Alfresco.util.createYUIButton(this, "import-form-cancel", this.hideImportDialog, {});

                    Event.on(this.id + "-import-form-import-file", "change", this.checkImportFile, null, this);

                    Event.on(this.id + "-import-error-form-show-more-link", "click", this.errorFormShowMore, null, this);

                    // Finally show the component body here to prevent UI artifacts on YUI button decoration
                    Dom.setStyle(this.id + "-body", "visibility", "visible");

                    this.importInfoDialog = Alfresco.util.createYUIPanel(this.id + "-import-info-form",
                            {
                                width: "50em"
                            });

                    Dom.removeClass(this.id + "-import-info-form", "hidden1");

                    this.importErrorDialog = Alfresco.util.createYUIPanel(this.id + "-import-error-form",
                            {
                                width: "60em"
                            });

                    Dom.removeClass(this.id + "-import-error-form", "hidden1");

                    this.importFromDialog = Alfresco.util.createYUIPanel(this.id + "-import-form",
                            {
                                width: "50em"
                            });

                    Dom.removeClass(this.id + "-import-form", "hidden1");

                    var scriptButton = Alfresco.util.createYUIButton(this, "scriptButton", this.showCreateScript, {});
                    var workflowButton = Alfresco.util.createYUIButton(this, "workflowButton", this.showCreateWorkflow, {});
                    var documentButton = Alfresco.util.createYUIButton(this, "documentButton", this.showCreateDocument, {});
                },

                showCreateScript: function showCreateScript_function() {
                    this._showCreateDialog("lecm-group-actions:script-action");
                },

                showCreateWorkflow: function showCreateWorkflow_function() {
                    this._showCreateDialog("lecm-group-actions:workflow-action");
                },

                showCreateDocument: function showCreateDocument_function() {
                    this._showCreateDialog("lecm-group-actions:document-action");
                },

                _showCreateDialog: function _showCreateDialog_function(type) {
                    if (this.editDialogOpening) return;
                    this.editDialogOpening = true;
                    var me = this;
                    // Intercept before dialog show
                    var doBeforeDialogShow = function DataGrid_onActionEdit_doBeforeDialogShow(p_form, p_dialog) {
                        var contId = p_dialog.id + "-form-container";
                        Alfresco.util.populateHTML(
                                [contId + "_h", "${msg("lecm.gracts.creation")}"]
                        );
                        me.editDialogOpening = false;
                        p_dialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, {moduleId: p_dialog.id}, this);
                    };

                    var templateUrl = Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form";
                    var templateRequestParams = {
                        itemKind: "type",
                        itemId: type,
                        destination: "${nodeRef!""}",
                        mode: "create",
                        submitType: "json",
                        showCancelButton: true
                    };

                    // Using Forms Service, so always create new instance
                    var createDetails = new Alfresco.module.SimpleDialog(this.id + "-createDetails");
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
                                    fn: function DataGrid_onActionCreate_success(response) {
                                        YAHOO.Bubbling.fire("nodeCreated",
                                                {
                                                    nodeRef: response.json.persistedObject,
                                                    bubblingLabel: "group-actions-bubbling-label"
                                                });
                                        YAHOO.Bubbling.fire("dataItemCreated", // обновить данные в гриде
                                                {
                                                    nodeRef: response.json.persistedObject,
                                                    bubblingLabel: "group-actions-bubbling-label"
                                                });
                                        this.editDialogOpening = false;
                                    },
                                    scope: this
                                },
                                onFailure: {
                                    fn: function DataGrid_onActionCreate_failure(response) {
                                        LogicECM.module.Base.Util.displayErrorMessageWithDetails(this.msg("logicecm.base.error"), this.msg("message.save.failure"), response.json.message);
                                        this.editDialogOpening = false;
                                    },
                                    scope: this
                                }
                            }).show();
                },

                showImportDialog: function () {
                    Dom.get(this.id + "-import-form-chbx-ignore").checked = false;
                    Dom.get(this.id + "-import-form-import-file").value = "";
                    this.importFromDialog.show();
                },

                hideImportDialog: function () {
                    this.importFromDialog.hide();
                },

                checkImportFile: function (event) {
                    this.submitButton.set("disabled", event.currentTarget.value == null || event.currentTarget.value.length == 0);
                },

                /**
                 * On "submit"-button click.
                 */
                onImportXML: function () {
                    var me = this;
                    Connect.setForm(this.id + '-import-xml-form', true);
                    var url = Alfresco.constants.URL_CONTEXT + "proxy/alfresco/lecm/dictionary/post/import?nodeRef=${nodeRef!""}";
                    var callback = {
                        upload: function (oResponse) {
                            var oResults = YAHOO.lang.JSON.parse(oResponse.responseText);
                            if (oResults[0] != null && oResults[0].text != null) {
                                Dom.get(me.id + "-import-info-form-content").innerHTML = oResults[0].text;
                                me.importInfoDialog.show();
                            } else if (oResults.exception != null) {
                                Dom.get(me.id + "-import-error-form-exception").innerHTML = oResults.exception.replace(/\n/g, '<br>').replace(/\r/g, '<br>');
                                Dom.get(me.id + "-import-error-form-stack-trace").innerHTML = me.getStackTraceString(oResults.callstack);
                                Dom.setStyle(me.id + "-import-error-form-more", "display", "none");
                                me.importErrorDialog.show();
                            }

                            YAHOO.Bubbling.fire("datagridRefresh",
                                    {
                                        bubblingLabel: "group-actions-bubbling-label"
                                    });
                        }
                    };
                    this.hideImportDialog();
                    Connect.asyncRequest(Alfresco.util.Ajax.POST, url, callback);
                },

                getStackTraceString: function (callstack) {
                    var result = "";
                    if (callstack != null) {
                        for (var i = 0; i < callstack.length; i++) {
                            if (callstack[i].length > 0) {
                                result += callstack[i] + "<br/>";
                            }
                        }
                    }
                    return result;
                },

                errorFormShowMore: function () {
                    Dom.setStyle(this.id + "-import-error-form-more", "display", "block");
                }
            }, true);

    /* Экспорт в XML.
    *
    * @method onActionExportXML
    * @param items {Object} Object literal representing the Data Item to be actioned
    */
    LogicECM.module.Base.DataGrid.prototype.onActionExportXML = function (item) {
        document.location.href = Alfresco.constants.PROXY_URI + "lecm/dictionary/get/export?nodeRef=" + item.nodeRef;
    }

    YAHOO.util.Event.onDOMReady(function () {

        var importDialog = new LogicECM.module.GroupActions.Import("${id}");
        importDialog.show();

        var datagrid = new LogicECM.module.Base.DataGrid('${containerId}').setOptions({
            usePagination: true,
            pageSize: 10,
            showExtendSearchBlock: true,
            actions: [
                {
                    type: "datagrid-action-link-group-actions-bubbling-label",
                    id: "onActionEdit",
                    permission: "edit",
                    label: "${msg("actions.edit")}"
                },
                {
                    type: "datagrid-action-link-group-actions-bubbling-label",
                    id: "onActionDelete",
                    permission: "delete",
                    label: "${msg("actions.delete-row")}"
                },
                {
                    type: "datagrid-action-link-group-actions-bubbling-label",
                    id: "onActionExportXML",
                    permission: "edit",
                    label: "${msg("actions.export-xml")}"
                }
            ],
            datagridMeta: {
                useFilterByOrg: false,
                itemType: "lecm-group-actions:base-action",
                nodeRef: "${nodeRef!""}",
                datagridFormId: "datagrid",
                useChildQuery: true,
                actionsConfig: {
                    fullDelete: "true"
                },
                sort: "lecm-group-actions:order|true"
            },
            showActionColumn: true,
            showCheckboxColumn: false,
            bubblingLabel: "group-actions-bubbling-label",
            allowCreate: false
        });      
        datagrid.draw();
    });

})();
//]]></script>

</@grid.datagrid>
</div>

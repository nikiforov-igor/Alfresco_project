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
        Event = YAHOO.util.Event;

    /**
     * StartWorkflow constructor.
     *
     * @param {String} htmlId The HTML id of the parent element
     * @return {Alfresco.component.StartWorkflow} The new StartWorkflow instance
     * @constructor
     */
    LogicECM.module.StartWorkflow = function StartWorkflow_constructor(htmlId) {
        var module = LogicECM.module.StartWorkflow.superclass.constructor.call(this, "LogicECM.module.StartWorkflow", htmlId, ["button"]);
        YAHOO.Bubbling.on("objectFinderReady", module.onObjectFinderReady, module);
        YAHOO.Bubbling.on("formContentReady", module.onStartWorkflowFormContentReady, module);

        return module;
    };

    YAHOO.extend(LogicECM.module.StartWorkflow, Alfresco.component.ShareFormManager, {
        selectedItem: null,
        taskId: null,
        onObjectFinderReady:function StartWorkflow_onObjectFinderReady(layer, args) {
            var objectFinder = args[1].eventGroup;
            if (objectFinder.options.field == "assoc_packageItems") {
                objectFinder.selectItems(this.selectedItem);
            }
        },

        show: function showWorkflowForm(nodeRef, workflowId, taskId, actionId) {
            if (workflowId != null && workflowId != 'null') {
                this.selectedItem = nodeRef;
                var templateUrl = Alfresco.constants.URL_SERVICECONTEXT + "components/form?itemKind={itemKind}&itemId={itemId}&destination={destination}&mode={mode}&submitType={submitType}&formUI={formUI}&showCancelButton=true";
                templateUrl = YAHOO.lang.substitute(templateUrl, {
                    itemKind:"workflow",
                    itemId: workflowId,
                    mode:"create",
                    submitType:"json",
                    formUI: "true"
                });
                var htmlid = this.id + "-startWorkflowForm-" + Alfresco.util.generateDomId();
                new Alfresco.module.SimpleDialog("htmlid").setOptions({
                    width:"60em",
                    templateUrl:templateUrl,
                    actionUrl:null,
                    destroyOnHide:true,
                    onSuccess:{
                        fn:function (response) {
                            this._chooseState(taskId, response.json.persistedObject, actionId);
                        },
                        scope:this
                    }
                }).show();
            } else {
                this._chooseState(taskId, null, actionId);
            }
        },

        _chooseState: function (taskId, formResponse, actionId) {
            var url = Alfresco.constants.PROXY_URI + "lecm/statemachine/choosestate?taskId={taskId}&formResponse={formResponse}&actionId={actionId}";
            url = YAHOO.lang.substitute(url, {
                taskId: taskId,
                formResponse: encodeURI(formResponse),
                actionId: actionId
            });
            callback = {
                success:function (oResponse) {
                    alert("OK");
                },
                argument:{
                    contractsObject: this
                },
                timeout:7000
            };
            YAHOO.util.Connect.asyncRequest('GET', url, callback);
        },

        /**
         * Called when a workflow form has been loaded.
         * Will insert the form in the Dom.
         *
         * @method onWorkflowFormLoaded
         * @param response {Object}
         */
        onWorkflowFormLoaded:function StartWorkflow_onWorkflowFormLoaded(response) {
            var formEl = Dom.get(this.id + "-workflowFormContainer");
            Dom.addClass(formEl, "hidden");
            formEl.innerHTML = response.serverResponse.responseText;
        },

        /**
         * Event handler called when the "formContentReady" event is received
         */
        onStartWorkflowFormContentReady:function FormManager_onStartWorkflowFormContentReady(layer, args) {
            var formEl = Dom.get(this.id + "-workflowFormContainer");
            Dom.removeClass(formEl, "hidden");
        }

    });

})();

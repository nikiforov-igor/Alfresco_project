/**
 * LogicECM root namespace.
 *
 * @namespace LogicECM
 */
// Ensure LogicECM root object exists
if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.Errands = LogicECM.module.Errands|| {};

(function () {
    /**
     * YUI Library aliases
     */
    var Dom = YAHOO.util.Dom;

    LogicECM.module.Errands.Links = function ErrandsTasks_constructor(htmlId) {
        LogicECM.module.Errands.Links.superclass.constructor.call(this, "LogicECM.module.Errands.Links", htmlId, ["button", "container"]);
        return this;
    };

    YAHOO.extend(LogicECM.module.Errands.Links, Alfresco.component.Base,
        {
            options: {
                destination: null,
                totalLinks: 0,
                totalExecuteLinks: 0
            },

            /**
             * true - "К исполнению" ассоциация lecm-errands:links-assoc
             * false - "Работа над поручение" ассоциация lecm-errands:execution-links-assoc
             */
            isExecute: false,

            containerList: null,

            /**
             * html элемент в котрый помещаем результат
             */
            onReady: function () {
                this.widgets.linksButton = Alfresco.util.createYUIButton(this, "links-add", this.drawLinksForm,
                    {
                        disabled: false
                    });

                this.widgets.execLinksButton = Alfresco.util.createYUIButton(this, "exec-links-add", this.drawLinksExecuteForm,
                    {
                        disabled: false
                    });
            },

            drawLinksForm: function() {
                this.isExecute = false;
                this.containerList = Dom.get(this.id + "-links-list");
                this.drawForm();
            },

            drawLinksExecuteForm: function() {
                this.isExecute = true;
                this.containerList = Dom.get(this.id + "-execute-links-list");
                this.drawForm();
            },

            _formAddElemet: function(form, tag, nameId, value) {
                input = document.createElement(tag);
                input.setAttribute("id", this.id + "-createDetails-form-" + nameId);
                input.setAttribute("type", "hidden");
                input.setAttribute("name", nameId);
                input.setAttribute("value", value);
                form.appendChild(input);
            },

            drawForm: function () {
                var doBeforeDialogShow = function(p_form, p_dialog) {
                    Alfresco.util.populateHTML(
                        [ p_dialog.id + "-form-container_h", this.msg("label.connection.add.title") ]
                    );
                    var form = Dom.get(this.id + "-createDetails-form");
                    form.setAttribute("action", Alfresco.constants.PROXY_URI_RELATIVE + "lecm/errands/api/createLinks");
                    this._formAddElemet(form, "input", "isExecute", this.isExecute);
	                p_dialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, {moduleId: p_dialog.id}, this);
                };

                var templateUrl = Alfresco.constants.URL_SERVICECONTEXT + "components/form";
	            var templateRequestParams = {
                        itemKind:"type",
                        itemId:"lecm-links:link",
                        destination: this.options.destination,
                        mode:"create",
                        formId: this.id + "-create-form",
		            submitType:"json",
		            showCancelButton: true,
					showCaption: false
	            };

                var createDetails = new Alfresco.module.SimpleDialog(this.id + "-createDetails");
                createDetails.setOptions(
                    {
                        width:"50em",
                        templateUrl:templateUrl,
	                    templateRequestParams: templateRequestParams,
                        actionUrl:null,
                        destroyOnHide:true,
                        doBeforeDialogShow:{
                            fn:doBeforeDialogShow,
                            scope:this
                        },
                        onSuccess:{
                            fn:function (response) {
                                Alfresco.util.PopupManager.displayMessage(
                                    {
                                        text:this.msg("message.links.add.success")
                                    });
                                var results = response.json;
                                var details = "";
                                if (results.success) {
                                    details += "<li title='" + results.name + "'>";
                                    details += "<img src=" + Alfresco.constants.URL_CONTEXT + "res/components/images/filetypes/generic-file-16.png class='file-icon'/>";
                                    details += "<a href=" + ((results.url.match("://") == null) ? "http://" + results.url : results.url) + ">" + results.name + "</a>";
                                    details += "</li>";
                                    this.containerList.innerHTML += details;
                                    var count = 0;
                                    if (this.isExecute) {
                                        this.options.totalExecuteLinks++;
                                        Dom.get(this.id+"-execute-links-count").innerHTML = " ("+this.options.totalExecuteLinks+")";
                                    } else {
                                        this.options.totalLinks++;
                                        Dom.get(this.id+"-links-count").innerHTML = " ("+this.options.totalLinks+")";
                                    }
                                }

                            },
                            scope:this
                        },
                        onFailure:{
                            fn:function (response) {
                                Alfresco.util.PopupManager.displayMessage(
                                    {
                                        text:this.msg("message.links.add.failure")
                                    });
                            },
                            scope:this
                        }
                    }).show();
            }
        });
})();
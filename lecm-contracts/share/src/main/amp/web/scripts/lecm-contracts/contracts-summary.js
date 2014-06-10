if (typeof LogicECM == "undefined" || !LogicECM) {
    var LogicECM = {};
}
LogicECM.module = LogicECM.module || {};
LogicECM.module.Contracts = LogicECM.module.Contracts|| {};
LogicECM.module.Contracts.dashlet = LogicECM.module.Contracts.dashlet || {};

(function()
{
    var Dom = YAHOO.util.Dom;

    LogicECM.module.Contracts.dashlet.Summary = function Contracts_constructor(htmlId) {
        LogicECM.module.Contracts.dashlet.Summary.superclass.constructor.call(this, "LogicECM.module.Contracts.dashlet.Summary", htmlId, ["button", "container"]);

        return this;
    };

    YAHOO.extend(LogicECM.module.Contracts.dashlet.Summary, Alfresco.component.Base,
            {
                options:
                {
                    formId: null,
                    nodeRef: null
                },
                container: null,
                viewDialog: null,
                message:  {
                },
                onReady: function Contracts_onReady()
                {
                    this.container = Dom.get(this.id+'_results');
                    this.setMessage();
                    this.createDialog();
                    this.drawForm();
                },

                setMessage: function Set_Message(){
                    this.message = {
                        "Все": this.msg("label.info.allContracts"),
                        "Проекты": this.msg("label.info.contractsToDevelop"),
                        "Подписанные": this.msg("label.info.activeContracts"),
                        "Завершенные": this.msg("label.info.inactiveContracts"),
                        "Участники": this.msg("label.info.participants")
                    }
                },

                createRow: function Create_row(innerHtml)
                {
                    var div = document.createElement('div');
                    div.setAttribute('class', 'row summary');
                    if (innerHtml) {
                        div.innerHTML = innerHtml;
                    }
                    if (this.container) {
                        this.container.appendChild(div);
                    }
                },

                drawForm: function Draw_form() {
                    Alfresco.util.Ajax.jsonGet(
                            {
                                url: Alfresco.constants.PROXY_URI + encodeURI("lecm/documents/summary?docType=lecm-contract:document&archive=false&skippedStatuses=Корзина&considerFilter=" + location.hash.replace(/#(\w+)=/, "")),
                                successCallback: {
                                    fn: function (response) {
                                        if (this.container != null) {
                                            this.container.innerHTML = '';
                                            if (response.json != null) {
                                                var list = response.json.list;
                                                var members = response.json.members;
                                                var innerHtml;
                                                for (var index in list) {
                                                    if (!(list[index].skip == "true")){
                                                        innerHtml = "<div class='column first" + (index == 0 ? " bold" : "") + "'>" + this.message[list[index].key] + ":" + "</div>" +
                                                                "<div class='column second'><a class=\"status-button text-cropped\" href=\"/share/page/contracts-list?query=" +
                                                                list[index].filter + "\">" + list[index].amount + "</a></div>";
                                                        this.createRow(innerHtml);
                                                    }
                                                }
                                                innerHtml = "<div class='column first bold'>" + this.message[members.key] + ":" + "</div>" +
                                                "<div class='column second'><a class=\"status-button text-cropped\" onclick=\"info.showDialog();\">" + members.amountMembers + "</a></div>";
                                                this.createRow(innerHtml);
                                            }
                                        }
                                    },
                                    scope: this
                                },
                                failureMessage: "message.failure"
                            });
                },

                createDialog: function createDialog() {
                    this.viewDialog = Alfresco.util.createYUIPanel(this.options.formId,
                            {
                                width: "50em",
                                close: false
                            });
                    this.hideViewDialog();
                },

                hideViewDialog: function hideViewDialog() {
                    if (this.viewDialog != null) {
                        this.viewDialog.hide();
                        Dom.setStyle(this.options.formId, "display", "none");
                    }
                },

                showDialog: function showViewDialog(){
                    var formEl = Dom.get(this.options.formId+"-content");
                    var id = Alfresco.util.generateDomId();
                    Alfresco.util.Ajax.request(
                            {
                                url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/dashlet/summary/members",
                                dataObj: {
                                    nodeRef: this.options.nodeRef,
                                    htmlid: this.id
                                },
                                successCallback: {
                                    fn: function (response) {
                                        var text = response.serverResponse.responseText;
                                        var formEl = Dom.get(this.options.formId+"-content");
                                        formEl.innerHTML = text;
                                    },
                                    scope: this
                                },
                                failureMessage: function () {
                                    alert("Данные не загружены");
                                },
                                scope: this,
                                execScripts: true
                            });
                    if (this.viewDialog != null) {
                        Dom.setStyle(this.options.formId, "display", "block");
                        this.viewDialog.show();
                    }
                }
            });
})();
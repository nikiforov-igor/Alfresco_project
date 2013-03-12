<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign aDateTime = .now>
<#assign controlId = fieldHtmlId + "-cntrl">
<#assign containerId = fieldHtmlId + "-container-" + aDateTime?iso_utc>

<div class="form-field with-grid" id="document-members-${controlId}">
    <input type="hidden" id="${fieldHtmlId}" name="${fieldHtmlId}" value="${field.value?html}"/>
    <label for="${controlId}">${msg("label.document.members")}:</label>

    <div class="add-member" style="float:none">
        <span id="${controlId}-add-member-button" class="yui-button yui-push-button">
           <span class="first-child">
              <button type="button" title="${msg("button.member.add")}">${msg("button.member.add")}</button>
           </span>
        </span>
    </div>

<@grid.datagrid containerId false>
    <script type="text/javascript">//<![CDATA[
    (function () {
        function init() {
            Alfresco.util.Ajax.request(
                    {
                        url: Alfresco.constants.PROXY_URI + "lecm/document/api/getMembersFolder",
                        dataObj: {
                            nodeRef: "${form.arguments.itemId}"
                        },
                        successCallback: {
                            fn: function (oResponse) {
                                var oResults = eval("(" + oResponse.serverResponse.responseText + ")");
                                if (oResults && oResults.nodeRef) {
                                    draw(oResults.nodeRef);
                                }
                            }
                        },
                        failureMessage: "message.failure"
                    });
        }

        function draw(folderRef) {
            var control = new LogicECM.module.Members.DocumentMembers("${fieldHtmlId}").setMessages(${messages});
            control.setOptions({
                documentNodeRef: "${form.arguments.itemId}",
                documentMembersFolderRef: folderRef,
                datagridBublingLabel: "${containerId}"
            });

            var datagrid = new LogicECM.module.Base.DataGrid('${containerId}').setOptions({
                usePagination: true,
                pageSize: 10,
                showExtendSearchBlock: false,
                actions: [
                    {
                        type: "datagrid-action-link-${containerId}",
                        id: "onActionDelete",
                        permission: "delete",
                        label: "${msg("actions.delete-row")}"
                    }
                ],

                datagridMeta: {
                    itemType: "lecm-doc-members:member",
                    nodeRef: folderRef,
                    actionsConfig: {
                        fullDelete: true
                    }
                },

                showActionColumn: true,
                showCheckboxColumn: false,
                bubblingLabel: "${containerId}"
            }).setMessages(${messages});

            datagrid.draw();
        }

        YAHOO.util.Event.onDOMReady(init);
    })();
    //]]></script>
</@grid.datagrid>
</div>
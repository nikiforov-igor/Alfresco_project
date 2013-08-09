<#import "/ru/it/lecm/base-share/components/view.lib.ftl" as view/>
<#import "/ru/it/lecm/documents/controls/history/status-history.lib.ftl" as historyStatus/>
<#include "/org/alfresco/include/alfresco-macros.lib.ftl" />
<#include "/ru/it/lecm/base-share/components/controls/lecm-dnd-uploader-container.ftl">

<#assign id = args.htmlid?js_string>
<#assign controlId = id + "-cntrl">
<#assign containerId = id + "-container">
<#assign viewFormId = id+ "-view">

<#if node??>
<#assign props = node.properties/>

<script type="text/javascript">
    //<![CDATA[
    (function () {
        var Dom = YAHOO.util.Dom,
            Event = YAHOO.util.Event;

        function init() {
            var links = new LogicECM.module.Errands.Links("${id}").setOptions(
                    {
                        destination: "${nodeRef}",
                        totalLinks: ${links.links?size},
                        totalExecuteLinks:${executeLinks.links?size}
                    }).setMessages(${messages});

            <#-- "time ago"  - start -->
            var date = "${props["cm:created"]["iso8601"]}";

            Dom.get("${id}-time-ago").innerHTML = Alfresco.util.relativeTime(new Date(date));
            <#-- "time ago"  - end -->

            <#-- Make twisters - start -->
            var blocks = Dom.getElementsByClassName("data-list-block", "div", "${id}_metadata");

            for (var i = 0; i < blocks.length; i++) {
                var block = blocks[i];
                var heading = Dom.getElementsByClassName("heading", "span", block)[0];
                var ul = Dom.getElementsByClassName("data-list", "ul", block)[0];

                Alfresco.util.createTwister(heading, "", {
                    panel: ul
                });
            }
            <#-- Make twisters - end -->

            <#-- Dnd uploader form - start -->
            drawDndForm("${nodeRef}", '${id}');
            <#-- Dnd uploader form - end -->
        }

        function drawDndForm(nodeRef, htmlId) {
	        var formId = htmlId + "-attachemnts";
            Alfresco.util.Ajax.request(
                    {
                        url: Alfresco.constants.URL_SERVICECONTEXT + "components/form",
                        dataObj: {
                            htmlid: formId,
                            itemKind: "node",
                            itemId: nodeRef,
                            formId: "errands-dnd",
                            mode: "edit",
	                        submitType:"json",
                            showSubmitButton: true,
                            showResetButton: false,
                            showCancelButton: false
                        },
                        successCallback: {
                            fn: function (response) {
                                var container = Dom.get('${id}-dnd');
                                if (container != null) {
                                    container.innerHTML = response.serverResponse.responseText;

	                                var form = new Alfresco.forms.Form(formId + '-form');
	                                form.setSubmitAsJSON(true);
	                                form.setAJAXSubmit(true,
			                                {
				                                successCallback:
				                                {
					                                fn: function(response) {
						                                window.location.reload(true);
					                                },
					                                scope: this
				                                }
			                                });
	                                form.init();

	                                Dom.setStyle(formId + "-form-buttons", "visibility", "hidden");
                                }
                            }
                        },
                        failureMessage: "message.failure",
                        execScripts: true
                    });
        }

        Event.onDOMReady(init);
    })();
    //]]>
</script>


<div id="${id}_metadata" class="metadata-form errands-main-form">
    <div id="${id}-form-fields" class="form-fields">
        <div class="title">
            ${props["lecm-errands:title"]}
        </div>
        <div class="statuses">
            <#assign isImportantProp = props["lecm-errands:is-important"]/>
            <#assign isImportant = false/>
            <#if isImportantProp??>
                <#if isImportantProp?is_boolean>
                    <#assign isImportant=isImportantProp>
                <#elseif isImportantProp?is_string && isImportantProp == "true">
                    <#assign isImportant=true>
                </#if>
            </#if>
            <#if isImportant>
                <div class="form-field field-important">
                    <div class="read-only-important">
                        ${msg("form.control.field-important")}
                    </div>
                </div>
            </#if>

            <div class="form-field field-status">
                <div class="read-only-status">
                    <a onclick="showViewStatusDialog();" href="javascript:void(0);">${props["lecm-statemachine:status"]}</a>
                </div>
            </div>
            <@historyStatus.showDialog formId="form-errans-history-status" nodeRef="${nodeRef}" />
        </div>
        <div class="clear"></div>
        <div class="main-info">
            Поручение дал <a href="javascript:void(0);" onclick="viewAttributes('${props["lecm-errands:initiator-assoc-ref"]}', null, 'logicecm.employee.view')">${props["lecm-errands:initiator-assoc-text-content"]}</a>
            &nbsp;
            <span id="${id}-time-ago"></span>
            &nbsp;
            сотруднику <a href="javascript:void(0);" onclick="viewAttributes('${props["lecm-errands:executor-assoc-ref"]}', null, 'logicecm.employee.view')">${props["lecm-errands:executor-assoc-text-content"]}</a>
            <br/>
            <#if additionalDoc?? && additionalDoc.name??>
                На основании документа <a href="${siteURL("document?nodeRef=" + additionalDoc.nodeRef)}">${additionalDoc.name}</a>
            </#if>
        </div>
        <#assign content = props["lecm-errands:content"]/>
        <#if content?? && content != "">
            <div class="errand-content">${content}</div>
        </#if>
        <div class="times">
            <#assign viewFormat>${msg("form.control.date-picker.view.date.format")}</#assign>

            <#assign justInTimeProp = props["lecm-errands:just-in-time"],
                justInTime=false>
            <#if justInTimeProp??>
                <#if justInTimeProp?is_boolean>
                    <#assign justInTime=justInTimeProp>
                <#elseif justInTimeProp?is_string && justInTimeProp == "true">
                    <#assign justInTime=true>
                </#if>
            </#if>

            Срок исполнения <span>${limitDate?string("d MMM yyyy")}</span>
            <#if justInTime>
                &nbsp;исполнить <span>Точно в срок</span>
            </#if>
        </div>
        <div id="${id}-attachments" class="data-list-block">
            <span class="heading">Вложения<span class="count"> (${(attachments![])?size})</span></span>
            <ul class="data-list">
                <#if attachments?? && attachments?size gt 0>
                    <#list attachments as attachment>
                        <li title="${attachment.name!""}">
                            <img src="${url.context}/res/components/images/filetypes/generic-file-16.png" class="file-icon"/>
                            <a href="${url.context}/page/document-attachment?nodeRef=${attachment.nodeRef}">
                                ${attachment.name!""}
                            </a>
                        </li>
                    </#list>
                </#if>
            </ul>
        </div>
        <div id="${id}-links" class="data-list-block">
            <span class="heading">${msg("label.links.head")}<span class="count" id="${id}-links-count"> (${links.links?size})</span></span>
            <span id="${id}-links-add" class="yui-button yui-push-button">
                <span class="first-child">
                    <button type="button">${msg("label.links.button")}</button>
                </span>
            </span>
            <ul class="data-list" id="${id}-links-list">
                <#if links.links?? && (links.links?size > 0)>
                    <#list links.links as link>
                        <li title="${link.name!""}">
                            <img src="${url.context}/res/components/images/filetypes/generic-file-16.png" class="file-icon"/>
                            <a href="${url.context}/page/document-attachment?nodeRef=${link.nodeRef}">
                                ${link.name!""}
                            </a>
                            <#--<span class="descr">Описание какое-то, пока одно для всех</span>-->
                        </li>
                    </#list>
                </#if>
            </ul>
        </div>
        <div id="${id}-coexecs" class="data-list-block">
            <span class="heading">Соисполнители<span class="count"> (${(coexecs![])?size})</span></span>
            <ul class="data-list persons-list">
                <#if coexecs?? && coexecs?size gt 0>
                    <#list coexecs as coexec>
                        <li>
                            <div class="avatar">
                                <img src="${url.context}/proxy/alfresco/lecm/profile/employee-photo?nodeRef=${coexec.employeeRef}" alt="Avatar" />
                            </div>
                            <div class="person">
                                <div>${view.showViewLink(coexec.employeeName, coexec.employeeRef, "logicecm.employee.view")}</div>
                                <div class="position">${coexec.employeePosition}</div>
                                <div class="coexec-ref" style="display: none">${coexec.employeeRef}</div>
                            </div>
                        </li>
                    </#list>
                </#if>
            </ul>
        </div>
        <div class="line"></div>
        <#-- РАБОТА НАД ПОРУЧЕНИЕМ -->
        <div id="${id}-exec" class="block">
            <div id="${id}-dnd" class="dnd-uploader"></div>
            <div class="title">Работа над поручением</div>
            <div id="${id}-exec-attachments" class="data-list-block">
                <span class="heading">Вложения<span class="count"> (${(attachmentsExec![])?size})</span></span>
                <ul class="data-list">
                    <#if attachmentsExec?? && attachmentsExec?size gt 0>
                        <#list attachmentsExec as attachment>
                            <li title="${attachment.name!""}">
                                <img src="${url.context}/res/components/images/filetypes/generic-file-16.png" class="file-icon"/>
                                <a href="${url.context}/page/document-attachment?nodeRef=${attachment.nodeRef}">
                                    ${attachment.name!""}
                                </a>
                            </li>
                        </#list>
                    </#if>
                </ul>
            </div>
            <div id="${id}-exec-links" class="data-list-block">
                <span class="heading">${msg("label.links.head")}<span class="count" id="${id}-execute-links-count"> (${(executeLinks.links)?size})</span></span>
                <span id="${id}-exec-links-add" class="yui-button yui-push-button">
                    <span class="first-child">
                        <button type="button">${msg("label.links.button")}</button>
                    </span>
                </span>
                <ul class="data-list" id="${id}-execute-links-list">
                    <#if executeLinks.links?? && (executeLinks.links?size > 0)>
                        <#list executeLinks.links as link>
                            <li title="${link.name!""}">
                                <img src="${url.context}/res/components/images/filetypes/generic-file-16.png" class="file-icon"/>
                                <a href="${url.context}/page/document-attachment?nodeRef=${link.nodeRef}">
                                    ${link.name!""}
                                </a>
                            </li>
                        </#list>
                    </#if>
                </ul>
            </div>
            <div id="${id}-exec-child-errands" class="data-list-block">
                <#-- ЗДЕСЬ ДОЛЖНЫ БЫТЬ ДОЧЕРНИЕ ПОРУЧЕНИЯ! (соисполнители временно) -->
                <span class="heading">Дочерние поручения<span class="count"> (${(coexecs![])?size})</span></span>
                <span id="${id}-exec-child-errands-add" class="yui-button yui-push-button">
                    <span class="first-child">
                        <button type="button">Добавить поручение</button>
                    </span>
                </span>
                <ul class="data-list persons-list">
                    <#if coexecs?? && coexecs?size gt 0>
                        <#list coexecs as coexec>
                            <li>
                                <div class="avatar">
                                    <img src="${url.context}/proxy/alfresco/lecm/profile/employee-photo?nodeRef=${coexec.employeeRef}" alt="Avatar" />
                                </div>
                                <div class="person">
                                    <div>
                                        ${view.showViewLink(coexec.employeeName, coexec.employeeRef, "logicecm.employee.view")}
                                        получил поручение
                                        <span class="text-cropped"><a href="javascript:void(0);">Некое очень важное поручение</a></span>
                                    </div>
                                    <div class="descr">
                                        Срок исполнения <span>5 июня 2013г.</span>
                                    </div>
                                </div>
                            </li>
                        </#list>
                    </#if>
                </ul>
            </div>
            <div id="${id}-exec-report" class="exec-report">
                <form>
                    <span class="heading">Отчет об исполнении</span> <br/>
                    <div>
                        <textarea rows="" cols=""></textarea>
                    </div>
                    <div>
                        <span id="${id}-exec-child-errands-add" class="yui-button yui-push-button">
                            <span class="first-child">
                                <button type="submit">Сохранить</button>
                            </span>
                        </span>
                        <br/>
                        <span id="${id}-exec-child-errands-add" class="yui-button yui-push-button">
                            <span class="first-child">
                                <button type="reset">Очистить</button>
                            </span>
                        </span>
                    </div>
                </form>
            </div>
        </div>
        <div class="line"></div>
        <#-- КОНТРОЛЬ ИСПОЛНЕНИЯ -->
        <div id="${id}-contr" class="block">
            <div class="title">Контроль исполнения</div>
            <div id="${id}-contr-attachments" class="data-list-block">
                <span class="heading">Вложения<span class="count"> (${(attachmentsControl![])?size})</span></span>
                <ul class="data-list">
                    <#if attachmentsControl?? && attachmentsControl?size gt 0>
                        <#list attachmentsControl as attachment>
                            <li title="${attachment.name!""}">
                                <img src="${url.context}/res/components/images/filetypes/generic-file-16.png" class="file-icon"/>
                                <a href="${url.context}/page/document-attachment?nodeRef=${attachment.nodeRef}">
                                    ${attachment.name!""}
                                </a>
                            </li>
                        </#list>
                    </#if>
                </ul>
            </div>
        </div>
        <div class="line"></div>
    </div>
</div>
</#if>

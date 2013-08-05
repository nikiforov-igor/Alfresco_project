<#import "/ru/it/lecm/base-share/components/view.lib.ftl" as view/>
<#include "/ru/it/lecm/base-share/components/controls/lecm-dnd-uploader-container.ftl">

<#assign id = args.htmlid?js_string>

<#if node??>
<#assign props = node.properties/>

<script type="text/javascript">
    //<![CDATA[
    (function () {
        var Dom = YAHOO.util.Dom,
            Event = YAHOO.util.Event;

        function init() {
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

            <#-- Remove-icons init - start -->
            var removeIcons = Dom.getElementsByClassName("remove-icon", "img", "${id}_metadata");

            for (var j = 0; j < removeIcons.length; j++) {
                removeIcons[j].onclick = function() {
                    var icon = this;
                    var li = Dom.getAncestorByTagName(icon, "li");
                    var ul = Dom.getAncestorByTagName(li, "ul");
                    var block = Dom.getAncestorByTagName(ul, "div");
                    var countSpan = Dom.getElementsByClassName("count", "span", block)[0];

                    ul.removeChild(li);
                    countSpan.innerHTML = " (" + Dom.getChildren(ul).length + ")";

                    <#-- todo: Удалить из поручения! -->
                };
            }
            <#-- Remove-icons init - end -->

            <#-- Dnd uploader form - start -->
            drawDndForm("${nodeRef}", '${id}');
            <#-- Dnd uploader form - end -->
        }

        function drawDndForm(nodeRef, htmlId) {
            Alfresco.util.Ajax.request(
                    {
                        url: Alfresco.constants.URL_SERVICECONTEXT + "components/form",
                        dataObj: {
                            htmlid: htmlId,
                            itemKind: "node",
                            itemId: nodeRef,
                            formId: "errands-dnd",
                            mode: "edit",
                            showSubmitButton: false,
                            showResetButton: false,
                            showCancelButton: false
                        },
                        successCallback: {
                            fn: function (response) {
                                var container = Dom.get('${id}-dnd');
                                if (container != null) {
                                    container.innerHTML = response.serverResponse.responseText;
                                }
                            }
                        },
                        failureMessage: "message.failure",
                        execScripts: true,
                        htmlId: htmlId + nodeRef
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
                    ${props["lecm-statemachine:status"]}
                </div>
            </div>
        </div>
        <div class="clear"></div>
        <div class="main-info">
            Поручение дал <a href="javascript:void(0);" onclick="viewAttributes('${props["lecm-errands:initiator-assoc-ref"]}', null, 'logicecm.employee.view')">${props["lecm-errands:initiator-assoc-text-content"]}</a>
            &nbsp;
            <span id="${id}-time-ago"></span>
            &nbsp;
            сотруднику <a href="javascript:void(0);" onclick="viewAttributes('${props["lecm-errands:executor-assoc-ref"]}', null, 'logicecm.employee.view')">${props["lecm-errands:executor-assoc-text-content"]}</a>
            <br/>
            На основании документа <a href="javascript:void(0);">todo:документ-основание</a>
        </div>
        <div class="errand-content">${props["lecm-errands:content"]}</div>
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
                            <img src="${url.context}/res/components/images/delete-16.png" class="remove-icon"/>
                            <a href="${url.context}/page/document-attachment?nodeRef=${attachment.nodeRef}">
                                ${attachment.name!""}
                            </a>
                        </li>
                    </#list>
                </#if>
            </ul>
        </div>
        <div id="${id}-links" class="data-list-block">
            <#-- ЗДЕСЬ ДОЛЖНЫ БЫТЬ ССЫЛКИ! (вложения временно) -->
            <#assign links = attachments![]/>
            <span class="heading">Ссылки<span class="count"> (${(links![])?size})</span></span>
            <span id="${id}-links-add" class="yui-button yui-push-button">
                <span class="first-child">
                    <button type="button">Добавить ссылку</button>
                </span>
            </span>
            <ul class="data-list">
                <#if links?? && links?size gt 0>
                    <#list links as link>
                        <li title="${link.name!""}">
                            <img src="${url.context}/res/components/images/delete-16.png" class="remove-icon"/>
                            <a href="${url.context}/page/document-attachment?nodeRef=${link.nodeRef}">
                                ${link.name!""}
                            </a>
                            <span class="descr">Описание какое-то, пока одно для всех</span>
                        </li>
                    </#list>
                </#if>
            </ul>
        </div>
        <div id="${id}-coexecs" class="data-list-block">
            <#assign coexecList = []/>
            <#if coexecs?? && coexecs.items??>
                <#assign coexecList = coexecs.items/>
            </#if>
            <span class="heading">Соисполнители<span class="count"> (${coexecList?size})</span></span>
            <ul class="data-list persons-list">
                <#if coexecList?size gt 0>
                    <#list coexecList as coexec>
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
                                <img src="${url.context}/res/components/images/delete-16.png" class="remove-icon"/>
                                <a href="${url.context}/page/document-attachment?nodeRef=${attachment.nodeRef}">
                                    ${attachment.name!""}
                                </a>
                            </li>
                        </#list>
                    </#if>
                </ul>
            </div>
            <div id="${id}-exec-links" class="data-list-block">
                <#-- ЗДЕСЬ ДОЛЖНЫ БЫТЬ ССЫЛКИ! (вложения временно) -->
                <#assign links = attachments![]/>
                <span class="heading">Ссылки<span class="count"> (${(links![])?size})</span></span>
                <span id="${id}-exec-links-add" class="yui-button yui-push-button">
                    <span class="first-child">
                        <button type="button">Добавить ссылку</button>
                    </span>
                </span>
                <ul class="data-list">
                    <#if links?? && links?size gt 0>
                        <#list links as link>
                            <li title="${link.name!""}">
                                <img src="${url.context}/res/components/images/delete-16.png" class="remove-icon"/>
                                <a href="${url.context}/page/document-attachment?nodeRef=${link.nodeRef}">
                                    ${link.name!""}
                                </a>
                                <span class="descr">Описание какое-то, пока одно для всех</span>
                            </li>
                        </#list>
                    </#if>
                </ul>
            </div>
            <div id="${id}-exec-child-errands" class="data-list-block">
                <#assign coexecList = []/>
                <#-- ЗДЕСЬ ДОЛЖНЫ БЫТЬ ДОЧЕРНИЕ ПОРУЧЕНИЯ! (участники временно) -->
                <#if coexecs?? && coexecs.items??>
                    <#assign coexecList = coexecs.items/>
                </#if>
                <span class="heading">Дочерние поручения<span class="count"> (${coexecList?size})</span></span>
                <span id="${id}-exec-child-errands-add" class="yui-button yui-push-button">
                    <span class="first-child">
                        <button type="button">Добавить поручение</button>
                    </span>
                </span>
                <ul class="data-list persons-list">
                    <#if coexecList?size gt 0>
                        <#list coexecList as coexec>
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
                                <img src="${url.context}/res/components/images/delete-16.png" class="remove-icon"/>
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

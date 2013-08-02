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
            <#assign attsList = []/>
            <#if attachments?? && attachments.items?? && attachments.items?size gt 0>
                <#if attachments.items[0].attachments??>
                    <#assign attsList = attachments.items[0].attachments/>
                </#if>
            </#if>
            <span class="heading">Вложения<span class="count"> (${attsList?size})</span></span>
            <ul class="data-list">
                <#if attsList?size gt 0>
                    <#list attsList as attachment>
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
            <#assign linksList = []/>
            <#-- ЗДЕСЬ ДОЛЖНЫ БЫТЬ ССЫЛКИ! (вложения временно) -->
            <#if attachments?? && attachments.items?? && attachments.items?size gt 0>
                <#if attachments.items[0].attachments??>
                    <#assign linksList = attachments.items[0].attachments/>
                </#if>
            </#if>
            <span class="heading">Ссылки<span class="count"> (${linksList?size})</span></span>
            <span id="${id}-links-add" class="yui-button yui-push-button">
                <span class="first-child">
                    <button type="button">Добавить ссылку</button>
                </span>
            </span>
            <ul class="data-list">
                <#if linksList?size gt 0>
                    <#list linksList as link>
                        <li title="${link.name!""}">
                            <img src="${url.context}/res/components/images/delete-16.png" class="remove-icon"/>
                            <a href="${url.context}/page/document-attachment?nodeRef=${link.nodeRef}">
                                ${link.name!""}
                            </a>
                            <span>Описание какое-то, пока одно для всех</span>
                        </li>
                    </#list>
                </#if>
            </ul>
        </div>
        <div id="${id}-coexecs" class="data-list-block">
            <span class="heading">Соисполнители<span class="count"></span></span>
            <ul class="data-list"></ul>
        </div>
        <div class="line"></div>
        <#-- РАБОТА НАД ПОРУЧЕНИЕМ -->
        <div id="${id}-exec" class="block">
            <div class="title">Работа над поручением</div>
            <div id="${id}-exec-attachments" class="data-list-block">
                <#assign attsList = []/>
                <#if attachments?? && attachments.items?? && attachments.items?size gt 0>
                    <#if attachments.items[0].attachments??>
                        <#assign attsList = attachments.items[0].attachments/>
                    </#if>
                </#if>
                <span class="heading">Вложения<span class="count"> (${attsList?size})</span></span>
                <ul class="data-list">
                    <#if attsList?size gt 0>
                        <#list attsList as attachment>
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
                <#assign linksList = []/>
                <#-- ЗДЕСЬ ДОЛЖНЫ БЫТЬ ССЫЛКИ! (вложения временно) -->
                <#if attachments?? && attachments.items?? && attachments.items?size gt 0>
                    <#if attachments.items[0].attachments??>
                        <#assign linksList = attachments.items[0].attachments/>
                    </#if>
                </#if>
                <span class="heading">Ссылки<span class="count"> (${linksList?size})</span></span>
                <span id="${id}-exec-links-add" class="yui-button yui-push-button">
                    <span class="first-child">
                        <button type="button">Добавить ссылку</button>
                    </span>
                </span>
                <ul class="data-list">
                    <#if linksList?size gt 0>
                        <#list linksList as link>
                            <li title="${link.name!""}">
                                <img src="${url.context}/res/components/images/delete-16.png" class="remove-icon"/>
                                <a href="${url.context}/page/document-attachment?nodeRef=${link.nodeRef}">
                                    ${link.name!""}
                                </a>
                                <span>Описание какое-то, пока одно для всех</span>
                            </li>
                        </#list>
                    </#if>
                </ul>
            </div>

        </div>
        <div class="line"></div>
        <#-- КОНТРОЛЬ ИСПОЛНЕНИЯ -->
        <div id="${id}-contr" class="block">
            <div class="title">Контроль исполнения</div>
            <div id="${id}-contr-attachments" class="data-list-block">
                <#assign attsList = []/>
                <#if attachments?? && attachments.items?? && attachments.items?size gt 0>
                    <#if attachments.items[0].attachments??>
                        <#assign attsList = attachments.items[0].attachments/>
                    </#if>
                </#if>
                <span class="heading">Вложения<span class="count"> (${attsList?size})</span></span>
                <ul class="data-list">
                    <#if attsList?size gt 0>
                        <#list attsList as attachment>
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

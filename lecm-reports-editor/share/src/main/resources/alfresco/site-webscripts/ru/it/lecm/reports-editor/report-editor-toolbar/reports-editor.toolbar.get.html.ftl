<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>
<#assign id = args.htmlid>
<#assign toolbarId = "${id}-reports-editor-navigation-toolbar"/>

<script type="text/javascript">//<![CDATA[
function init() {
    var editor = new LogicECM.module.ReportsEditor.Footer("${toolbarId}").setMessages(${messages}).setOptions({
    <#if previousButton == true>
        previousButton: true,
    </#if>
    <#if nextButton == true>
        nextButton: true,
    </#if>
    <#if previousPage??>
        previousPage: "${previousPage}",
    </#if>
    <#if nextPage??>
        nextPage: "${nextPage}"
    </#if>
    });
    editor.setReportId("${page.url.args.reportId}");
}

YAHOO.util.Event.onDOMReady(init);
//]]></script>

<div id="${toolbarId}">
    <@comp.baseToolbar toolbarId true false false>
        <div class="previous-page">
            <span id="${toolbarId}-prevPageButton" class="yui-button yui-push-button">
                <span class="first-child">
                    <button type="button" title="Назад">${msg("label.prev.btn")}</button>
                </span>
            </span>
        </div>
        <div class="next-page">
            <span id="${toolbarId}-nextPageButton" class="yui-button yui-push-button">
                <span class="first-child">
                    <button type="button" title="Далее">${msg("label.next.btn")}</button>
                </span>
            </span>
        </div>
    </@comp.baseToolbar>
</div>
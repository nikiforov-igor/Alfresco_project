<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<#assign id = args.htmlid>
<div id="${id}-toolbar">
    <@comp.baseToolbar "${id}-toolbar" true false false>
        <div class="new-row">
            <span id="${id}-toolbar-newTemplateButton" class="yui-button yui-push-button">
                   <span class="first-child">
                      <button type="button" title="Загрузить">Загрузить</button>
                   </span>
            </span>
        </div>
        <div class="new-row">
            <span id="${id}-toolbar-newTemplateFromSourceButton" class="yui-button yui-push-button">
                   <span class="first-child">
                      <button type="button" title="Новый из набора данных">Новый из набора данных</button>
                   </span>
            </span>
        </div>
        <div class="new-row">
            <span id="${id}-toolbar-newTemplateSaveButton" class="yui-button yui-push-button">
                   <span class="first-child">
                      <button type="button" title="Сохранить как...">Сохранить как...</button>
                   </span>
            </span>
        </div>
        <div class="divider"></div>
        <div>
            <div id="${id}-export-template" class="export-template" title="${msg('button.export-template')}">
                    <span id="${id}-exportTemplateButton" class="yui-button yui-push-button">
                        <span class="first-child">
                            <button type="button" title="${msg('button.export-template')}">&nbsp;</button>
                        </span>
                    </span>
            </div>
        </div>
    </div>
</@comp.baseToolbar>
<script type="text/javascript">//<![CDATA[
function initEditor() {
    var reportsEditor = new LogicECM.module.ReportsEditor.TemplateEditToolbar("${id}");
    reportsEditor.setReportId("${page.url.args.reportId}");
    reportsEditor.setTemplateId("${activeTemplateId!""}");
    reportsEditor.setMessages(${messages});
    reportsEditor.markAsNewTemplate(!${existInRepo?string});
}
YAHOO.util.Event.onDOMReady(initEditor);
</script>
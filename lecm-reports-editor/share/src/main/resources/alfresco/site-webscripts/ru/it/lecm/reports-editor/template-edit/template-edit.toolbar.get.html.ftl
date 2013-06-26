<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<#assign id = args.htmlid>
 <#if page.url.args.reportId??>
 <script type="text/javascript">//<![CDATA[
 function init() {
     var toolbar = new LogicECM.module.ReportsEditor.TemplateEditToolbar("${id}")
             .setMessages(${messages})
             .setOptions({
                 bubblingLabel: "template-edit"
             });
     toolbar.setReportId("${page.url.args.reportId}")
 }
 YAHOO.util.Event.onDOMReady(init);
 //]]></script>

     <@comp.baseToolbar id true false false>
     <div class="new-row">
    <span id="${id}-newTemplateButton" class="yui-button yui-push-button">
           <span class="first-child">
              <button type="button" title="Новый">Новый</button>
           </span>
    </span>
     </div>
     <div class="new-row">
    <span id="${id}-newTemplateFromSourceButton" class="yui-button yui-push-button">
           <span class="first-child">
              <button type="button" title="Новый из набора данных">Новый из набора данных</button>
           </span>
    </span>
     </div>
     <div class="save-row">
    <span id="${id}-newTemplateSaveButton" class="yui-button yui-push-button">
           <span class="first-child">
              <button type="button" title="Сохранить как...">Сохранить как...</button>
           </span>
    </span>
     </div>

     <div class="prev-page">
    <span id="${id}-prevPageButton" class="yui-button yui-push-button">
           <span class="first-child">
              <button type="button" title="Назад">Назад</button>
           </span>
    </span>
     </div>
     <div class="next-page">
    <span id="${id}-nextPageButton" class="yui-button yui-push-button">
           <span class="first-child">
              <button type="button" title="Далее">Далее</button>
           </span>
    </span>
     </div>
     </@comp.baseToolbar>
 </#if>

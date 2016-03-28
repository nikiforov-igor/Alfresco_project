<@markup id="css">
    <@link rel="stylesheet" type="text/css" href="${url.context}/res/components/data-lists/toolbar.css" />
    <@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-forms-editor/form-editor-toolbar.css" />
    <@link rel="stylesheet" type="text/css" href="${url.context}/res/components/data-lists/toolbar.css" />
</@>
<@markup id="js">
    <@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/lecm-toolbar.js"></@script>
    <@script type="text/javascript" src="${url.context}/res/scripts/lecm-forms-editor/forms-editor-toolbar.js"></@script>
</@>
<#assign id = args.htmlid>

<#assign importFormId = id + "-import-form">
<#assign importInfoFormId = id + "-import-info-form">
<#assign importErrorFormId = id + "-import-error-form">

<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<script type="text/javascript">//<![CDATA[
function init() {
    new LogicECM.module.FormsEditor.Toolbar("${id}").setMessages(${messages}).setOptions({
        bubblingLabel: "${bubblingLabel!''}",
        doctype: "${doctype!''}",
        searchActive: true
    });
}
YAHOO.util.Event.onDOMReady(init);
//]]></script>
<@comp.baseToolbar id true false false>
    <div class="new-row">
            <span id="${id}-newFormButton" class="yui-button yui-push-button">
               <span class="first-child">
                  <button type="button" title="${msg("button.new-form")}">${msg("button.new-form")}</button>
               </span>
            </span>
    </div>
    <div class="divider"></div>
    <div class="generate-forms">
            <span id="${id}-generateFormsButton" class="yui-button yui-push-button">
               <span class="first-child">
                  <button type="button" title="${msg("button.generate-forms")}">${msg("button.generate-forms")}</button>
               </span>
            </span>
    </div>
    <div class="divider"></div>
    <div class="deploy-forms">
            <span id="${id}-deployFormsButton" class="yui-button yui-push-button">
               <span class="first-child">
                  <button type="button" title="${msg("button.deploy-forms")}">${msg("button.deploy-forms")}</button>
               </span>
            </span>
    </div>
    <div class="divider"></div>
    <div class="download-config">
            <span id="${id}-downloadConfigButton" class="yui-button yui-push-button">
               <span class="first-child">
                  <button type="button" title="${msg("button.download-config")}">${msg("button.download-config")}</button>
               </span>
            </span>
    </div>
    <div class="divider"></div>
    <div class="upload-config">
            <span id="${id}-uploadConfigButton" class="yui-button yui-push-button">
               <span class="first-child">
                  <button type="button" title="${msg("button.upload-config")}">${msg("button.upload-config")}</button>
               </span>
            </span>
    </div>
    <div class="divider"></div>
    <div class="upload-import">
                <span id="${id}-uploadImportButton" class="yui-button yui-push-button">
                   <span class="first-child">
                      <button type="button" title="${msg("button.upload-import")}">${msg("button.upload-import")}</button>
                   </span>
                </span>
    </div>

    <div id="${importInfoFormId}" class="yui-panel hidden1">
        <div id="${importInfoFormId}-head" class="hd">${msg("title.import.info")}</div>
        <div id="${importInfoFormId}-body" class="bd">
            <div id="${importInfoFormId}-content" class="import-info-content"></div>
        </div>
    </div>

    <div id="${importErrorFormId}" class="yui-panel hidden1">
        <div id="${importErrorFormId}-head" class="hd">${msg("title.import.info")}</div>
        <div id="${importErrorFormId}-body" class="bd">
            <div id="${importErrorFormId}-content" class="import-info-content">
                <div class="import-error-header">
                    <h3>${msg("import.failure")}</h3>
                    <a href="javascript:void(0);"
                       id="${importErrorFormId}-show-more-link">${msg("import.failure.showMore")}</a>
                </div>
                <div id="${importErrorFormId}-more" class="import-error-more">
                    <div class="import-error-exception">
                    ${msg("import.failure.exception")}:
                        <div class="import-error-exception-content" id="${importErrorFormId}-exception">
                        </div>
                    </div>
                    <div class="import-error-stack-trace">
                    ${msg("import.failure.stack-trace")}:
                        <div class="import-error-stack-trace-content" id="${importErrorFormId}-stack-trace">
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div id="${importFormId}" class="yui-panel hidden1">
        <div id="${importFormId}-head" class="hd">${msg("title.import")}</div>
        <div id="${importFormId}-body" class="bd">
            <div id="${importFormId}-content">
                <form method="post" id="${id}-import-xml-form" enctype="multipart/form-data"
                      action="${url.context}/proxy/alfresco/lecm/dictionary/post/import">
                    <ul class="import-form">
                        <li>
                            <label for="${importFormId}-import-file">${msg("label.import-file")}*</label>
                            <input id="${importFormId}-import-file" type="file" name="file"
                                   accept=".xml,application/xml,text/xml">
                        </li>
                        <li>
                            <label for="${importFormId}-chbx-ignore">${msg("label.ignore-errors")}</label>
                            <input id="${importFormId}-chbx-ignore" type="checkbox" name="ignoreErrors" value="true"/>
                        </li>
                    </ul>
                    <div class="bdft">
                        <button id="${importFormId}-submit" disabled="true"
                                tabindex="0">${msg("button.import-xml")}</button>
                        <button id="${importFormId}-cancel" tabindex="1">${msg("button.no")}</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

</@comp.baseToolbar>
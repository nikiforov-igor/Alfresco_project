<#include "/org/alfresco/components/component.head.inc">
<!-- Data List Toolbar -->
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/components/data-lists/toolbar.css" />
<@script type="text/javascript" src="${page.url.context}/scripts/lecm-arm/arm-documents-toolbar.js"></@script>

<#assign id = args.htmlid>
<script type="text/javascript">
(function(){
    function init() {
        new LogicECM.module.ARM.DocumentsToolbar("${id}").setMessages(${messages});
    }

    YAHOO.util.Event.onDOMReady(init);
})();
//<![CDATA[
//]]></script>

<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>
<@comp.baseToolbar id true false false>
<div class="new-row">
    <span id="${id}-newDocumentButton" class="yui-button yui-push-button">
       <span class="first-child">
          <button type="button">${msg("button.new-row")}</button>
       </span>
    </span>
</div>
<div class="filters">
        <span id="${id}-filtersButton" class="yui-button yui-push-button yui-menu-button">
           <span class="first-child">
              <button type="button" title="${msg("btn.filters")}">${msg("btn.filters")}</button>
           </span>
        </span>
    <div style="display: none;">
        <div id="filtersBlock" class="yui-panel">
            <div id="${id}-filters-head" class="hd">${msg("filters-block")}</div>
            <div id="${id}-filters-body" class="bd">
                <div id="${id}-filters-content">
                    <div id="filtersBlock-content" >
                        <div id="${id}-filtersContainer" class="filters">
                            <div id="filtersBlock-forms" class="forms-container form-fields"></div>
                        </div>
                    </div>
                    <div class="bdft">
                    <#-- Кнопка Применить -->
                        <div class="yui-u align-right">
                            <span id="filtersBlock-apply-button" class="yui-button yui-push-button filters-icon">
                                <span class="first-child">
                                    <button type="button">${msg('button.apply')}</button>
                                </span>
                            </span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<div class="group-actions">
    <span id="${id}-groupActionsButton" class="yui-button yui-push-button">
       <span class="first-child">
          <button type="button">${msg("button.group-actions")}</button>
       </span>
    </span>
</div>

</@comp.baseToolbar>

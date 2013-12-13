<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<#assign id = args.htmlid>
<#assign toolbarId = "${id}-columns-toolbar"/>

<div id="${toolbarId}">
    <@comp.baseToolbar toolbarId true false false>
        <div class="new-row">
                    <span id="${toolbarId}-newColumnButton" class="yui-button yui-push-button">
                        <span class="first-child">
                            <button type="button"
                                    title="${msg("label.new-column.btn")}">${msg("label.new-column.btn")}</button>
                        </span>
                    </span>
        </div>
        <div class="new-row">
                    <span id="${toolbarId}-saveAsButton" class="yui-button yui-push-button">
                        <span class="first-child">
                            <button type="button"
                                    title="${msg("label.save-as-source.btn")}">${msg("label.save-as-source.btn")}</button>
                        </span>
                    </span>
        </div>
        <div class="select-source">
                    <span id="${toolbarId}-selectSource" class="yui-button yui-push-button">
                        <span class="first-child">
                            <button type="button"
                                    title="${msg("label.select-source.btn")}">${msg("label.select-source.btn")}</button>
                        </span>
                    </span>
        </div>
        <div class="divider"></div>
        <div class="delete-row">
                    <span id="${toolbarId}-deleteColumnsBtn" class="yui-button yui-push-button">
                        <span class="first-child">
                            <button type="button"
                                    title="${msg("label.delete.btn")}">&nbsp;</button>
                        </span>
                    </span>
        </div>
    </@comp.baseToolbar>
</div>
<div id="selectSourcePanel" class="yui-panel" style="visibility:hidden">
    <div id="selectSourcePanel-select-head" class="hd">Выбрать</div>
    <div id="selectSourcePanel-select-body" class="bd">
        <div id="selectSourcePanel-select-content">
            <div id="selectSourcePanel-content" >
                <div id="selectSourcePanel-form"></div>
            </div>
            <div class="bdft">
            <#-- Кнопка Очистки -->
                <div class="yui-u align-right right">
                            <span id="selectSourcePanel-close-button" class="yui-button yui-push-button search-icon">
                                <span class="first-child">
                                    <button type="button">${msg('button.close')}</button>
                                </span>
                            </span>
                </div>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    function initEditToolbar() {
        var toolbar = new LogicECM.module.ReportsEditor.EditSourceToolbar("${id}").setMessages(${messages});
        toolbar.setReportId("${page.url.args.reportId}");
        toolbar.setDataSourceId("${activeSourceId!""}");
        toolbar.markAsNewSource(!${existInRepo?string});
    }

    YAHOO.util.Event.onDOMReady(initEditToolbar);
</script>
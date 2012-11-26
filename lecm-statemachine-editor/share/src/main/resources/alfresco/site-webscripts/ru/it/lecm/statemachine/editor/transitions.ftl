<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign controlId = fieldHtmlId + "-cntrl">
<#assign containerId = fieldHtmlId + "-container">
<div class="form-field">
    <div id="${controlId}">
        <label for="${controlId}">${field.label?html}:<#if field.endpointMandatory!false || field.mandatory!false>
            <span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
        <div id="${containerId}></div>
        <@grid.datagrid containerId>
            <script type="text/javascript">//<![CDATA[
                (function () {
                    function init() {
                        // EXTEND DATAGRID HERE
                        var datagrid = new LogicECM.module.Base.DataGrid('${containerId}').setOptions({
                            usePagination: false,
                            showExtendSearchBlock: false,

                        }).setMessages(${messages});

                        YAHOO.Bubbling.fire("activeGridChanged", {
                            datagridMeta: {
                                itemType: "${field.control.params.itemType!""}",
                                nodeRef: "${form.arguments.itemId}"
                            }
                        });

                        datagrid.onReady();
                    }

                    YAHOO.util.Event.onDOMReady(init);

                })();
            //]]></script>
        </@grid.datagrid>

    </div>
</div>

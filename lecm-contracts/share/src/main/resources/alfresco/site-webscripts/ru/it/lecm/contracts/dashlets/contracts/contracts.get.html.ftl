<#assign id = args.htmlid>
<#assign jsid = args.htmlid?js_string>

<#assign settingsObj = settings!""/>
<#assign CONTRACTS_REF = settingsObj.nodeRef!""/>

<script type="text/javascript">//<![CDATA[
    var contracts = new LogicECM.module.Contracts.dashlet.Contracts("${jsid}").setOptions(
            {
                regionId: "${args['region-id']?js_string}",
                destination: ("${CONTRACTS_REF}" != "") ? "${CONTRACTS_REF}" : null
            }).setMessages(${messages});

    new Alfresco.widget.DashletResizer("${jsid}", "${instance.object.id}");
    new Alfresco.widget.DashletTitleBarActions("${jsid}").setOptions(
            {
                actions: [
                    {
                        cssClass: "arm",
                        linkOnClick: window.location.protocol + "//" + window.location.host + Alfresco.constants.URL_PAGECONTEXT + "contracts-main",
                        tooltip: "${msg("dashlet.arm.tooltip")?js_string}"
                    }
                ]
            });
//]]></script>

<div class="dashlet contracts">
    <div class="title">${msg("header")}
        <span class="lecm-dashlet-actions">
            <a id="${id}-action-add" href="javascript:void(0);" onclick="contracts.onAddContractClick()" class="add" title="${msg("dashlet.add.tooltip")}">${msg("dashlet.add.contract")}</a>
         </span>
    </div>
    <div class="toolbar flat-button">
        <div class="hidden">
         <span class="align-left yui-button yui-menu-button" id="${id}-user">
            <span class="first-child">
               <button type="button" tabindex="0"></button>
            </span>
         </span>
            <select id="${id}-user-menu">
            <#list filterTypes as filter>
                <option value="${filter.type?html}">${msg("filter." + filter.label)}</option>
            </#list>
            </select>
         <span class="align-left yui-button yui-menu-button" id="${id}-range">
            <span class="first-child">
               <button type="button" tabindex="0"></button>
            </span>
         </span>
            <select id="${id}-range-menu">
            <#list filterRanges as filter>
                <option value="${filter.type?html}">${msg("filter." + filter.label)}</option>
            </#list>
            </select>

            <div class="clear"></div>
        </div>
    </div>
    <div id="${id}-contractsList" class="body scrollableList"
         <#if args.height??>style="height: ${args.height}px;"</#if>></div>
</div>

<#-- Empty results list template -->
<div id="${id}-empty" style="display: none">
    <div class="empty"><h3>${msg("empty.title")}</h3><span>${msg("empty.description")}</span></div>
</div>
<#assign id = args.htmlid>

<div id="contracts-filters">
    <div style="background-color: white; margin: 20px 0; padding: 0; position: relative;">
        <ul id="filter-groups-set">
        <#if statusesGroups??>
        ${msg("label.contracts")}
            <#list statusesGroups as group>
                <li style="padding-bottom: 0.4em;" class="text-broken">
                    <a href="${url.context}/page/contracts-list?query=${group.value}" class="text-cropped" title="${group.name}">${group.name}</a>
                </li>
            </#list>
        </#if>
        </ul>
    </div>
    <div style="background-color: white; margin: 20px 0; padding: 0; position: relative;">
        <ul id="filter-statuses-set">
        <#if statusesList??>
        ${msg("label.byStatus")}
            <#list statusesList as status>
                <li style="padding-bottom: 0.4em;" class="text-broken">
                    <a href="${url.context}/page/contracts-list?query=${status}" class="text-cropped" title="${status}">${status}</a>
                </li>
            </#list>
        </#if>
        </ul>
    </div>
</div>

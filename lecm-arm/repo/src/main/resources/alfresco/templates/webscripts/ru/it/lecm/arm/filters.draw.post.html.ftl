<#if filters??>
    <#assign aDateTime = .now>
    <#assign el="avaiableFilters"+ aDateTime?iso_utc/>

<form id="filersForm">
    <div id="${el}" class="filters-list">
        <div id="${el}-filters-list-container">
            <table class="filters-table">
                <tr class="detail-list-item" style="border: 1px solid;">
                    <#list filters as item>
                        <td id="${item.code}" class="filter">
                            <div style="font-weight: bold">${item.name}</div>
                            <br/>
                            <p>
                                <#if item.values??>
                                    <#list item.values as fValue>
                                        <#assign el="avaiableFilters"+ aDateTime?iso_utc/>
                                            <input type=<#if item.multiple>"checkbox"<#else>"radio"</#if>
                                                name="${item.code}"
                                                value="${fValue.code}"
                                                <#if fValue.checked>checked</#if>>${fValue.title}<br>
                                    </#list>
                                </#if>
                            </p>
                        </td>
                    </#list>
                </tr>
            </table>
            <div class="space"></div>
        </div>
    </div>
</form>
</#if>

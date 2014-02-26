<#if filters??>
    <#assign aDateTime = .now>
    <#assign el="avaiableFilters"+ aDateTime?iso_utc/>

<form id="filersForm">
    <div id="${el}" class="filters-list">
        <div id="${el}-filters-list-container" class="filters-table-container">
            <table class="filters-table">
	            <tr>
		            <#list filters as item>
			            <th id="${item.code}" class="filter-title" title="${item.name}">
				            ${item.name}
			            </th>
		            </#list>
	            </tr>
                <tr class="detail-list-item" style="border: 1px solid;">
                    <#list filters as item>
                        <td id="${item.code}" class="filter">
                                <#if item.values??>
                                    <#list item.values as fValue>
                                        <div>
                                            <input type=<#if item.multiple>"checkbox"<#else>"radio"</#if>
                                                name="${item.code}"
                                                value="${fValue.code}"
                                                <#if fValue.checked>checked</#if>>
                                            <label title="${fValue.title}">${fValue.title}</label>
                                        </div>
                                    </#list>
                                </#if>
                        </td>
                    </#list>
                </tr>
            </table>
            <div class="space"></div>
        </div>
    </div>
</form>
</#if>

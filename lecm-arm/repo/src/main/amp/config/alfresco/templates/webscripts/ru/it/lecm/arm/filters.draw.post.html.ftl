<#if filters??>
    <#assign aDateTime = .now>
    <#assign el="avaiableFilters"+ aDateTime?iso_utc/>

<form id="filtersForm">
    <div id="${el}" class="filters-list">
        <div id="${el}-filters-list-container" class="filters-table-container">
            <table class="filters-table" cellspacing="0">
	            <tr>
		            <#list filters as item>
			            <th id="${item.code}" class="filter-title <#if item_index == 0>first</#if>" title="${item.name}">
				            ${item.name}:
			            </th>
		            </#list>
	            </tr>
                <tr class="detail-list-item">
                    <#list filters as item>
                        <td id="${item.code}" class="filter <#if item_index == 0>first</#if>">
                            <#if item.values??>
                                <#list item.values as fValue>
                                    <div>
                                        <input type=<#if item.multiple>"checkbox"<#else>"radio"</#if>
                                            name="${item.code}"
                                            value="${fValue.code}"
                                            <#if fValue.checked>checked</#if>>
                                        <label title="${fValue.name}">${fValue.name}</label>
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

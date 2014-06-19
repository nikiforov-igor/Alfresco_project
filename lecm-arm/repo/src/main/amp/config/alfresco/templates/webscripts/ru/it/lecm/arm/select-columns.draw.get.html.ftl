<#if columns??>
    <#assign aDateTime = .now>
    <#assign el="avaiableColumns"/>

<form id="columnsForm">
    <div id="${el}" class="columns-list">
        <div id="${el}-columns-list-container" class="columns-table-container">
            <table class="filters-table" cellspacing="0">
                <tr class="detail-list-item">
                    <td  class="column first">
                        <#list columns as item>
                            <div>
                                <input type="checkbox"
                                       name="${item.fieldName}"
                                       value="${item.id}"
                                       <#if item.checked>checked</#if>>
                                <label title="${item.name}">${item.name}</label>
                            </div>
                        </#list>
                    </td>
                </tr>
            </table>
            <div class="space"></div>
        </div>
    </div>
</form>
</#if>

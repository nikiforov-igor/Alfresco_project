<#if columns??>
    <#assign aDateTime = .now>
    <#assign el="avaiableColumns"/>

<script type="text/javascript">
    if (typeof LogicECM == "undefined" || !LogicECM) {
        var LogicECM = {};
    }

    LogicECM.module = LogicECM.module || {};

    LogicECM.module.ARM = LogicECM.module.ARM || {};

</script>

<script type="text/javascript">//<![CDATA[

(function() {
    LogicECM.module.ARM = LogicECM.module.ARM || {};
    LogicECM.module.ARM.restoreColumns = function restoreColumns() {
        YAHOO.Bubbling.fire ("restoreDefaultColumns");
    }
})();
//]]></script>

<form id="columnsForm">
    <div id="${el}" class="columns-list">
        <div id="${el}-columns-list-container" class="columns-table-container">
            <table class="filters-table" cellspacing="0">
                <tr>
                    <th class="filter-title first" title="Восстановить по умолчанию">
                        <a href="#" onclick="LogicECM.module.ARM.restoreColumns()" title="Восстановить по умолчанию">Восстановить по умолчанию</a>
                    </th>
                </tr>
                <tr class="detail-list-item">
                    <td class="column">
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

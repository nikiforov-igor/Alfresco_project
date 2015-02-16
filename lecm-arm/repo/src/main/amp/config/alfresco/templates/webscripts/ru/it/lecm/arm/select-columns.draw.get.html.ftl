<#if columns??>
    <#assign aDateTime = .now>
    <#assign el="avaiableColumns"/>

<script type="text/javascript">//<![CDATA[

(function () {
    LogicECM.module.ARM.restoreColumns = function restoreColumns() {
        YAHOO.Bubbling.fire("restoreDefaultColumns");
    }

    LogicECM.module.ARM.moveUp = function moveUp(elem) {
        var previous = LogicECM.module.ARM.findPrevious(elem);
        if (previous) {
            elem.parentNode.insertBefore(elem, previous);
            LogicECM.module.ARM.updateElements(elem, previous);
        }
    }
    LogicECM.module.ARM.moveDown = function moveUp(elem) {
        var next = LogicECM.module.ARM.findNext(elem);
        if (next) {
            next.parentNode.insertBefore(next, elem);
            LogicECM.module.ARM.updateElements(next, elem);
        }
    }
    LogicECM.module.ARM.updateElements = function (first, second) {
        //верхний
        //вверх можно только если не верхний!
        var prevEl = LogicECM.module.ARM.findPrevious(first);
        var upRef = document.getElementById(first.id + "_moveUp");
        if (upRef != null) {
            upRef.className = prevEl == null ?
                    upRef.className + " hidden" :
                    upRef.className.replace("hidden", "");
        }
        // всегда можно двигать вниз!
        var downRef = document.getElementById(first.id + "_moveDown");
        if (downRef != null) {
            downRef.className = downRef.className.replace("hidden", "");
        }

        //нижний
        //всегда можно вверх!
        var upRef2 = document.getElementById(second.id + "_moveUp");
        if (upRef2 != null) {
            upRef2.className = upRef2.className.replace("hidden", "");
        }
        //вниз можно только если не нижний!
        var nextEl = LogicECM.module.ARM.findNext(second);
        downRef2 = document.getElementById(second.id + "_moveDown");
        if (downRef2 != null) {
            downRef2.className = nextEl == null ?
                    downRef2.className + " hidden" :
                    downRef2.className.replace("hidden", "");
        }
    }

    LogicECM.module.ARM.findPrevious = function findPrevious(elm) {
        do {
            elm = elm.previousSibling;
        } while (elm && elm.nodeType != 1);
        return elm;
    }
    LogicECM.module.ARM.findNext = function findNext(elm) {
        do {
            elm = elm.nextSibling;
        } while (elm && elm.nodeType != 1);
        return elm;
    }
})();
//]]></script>

<form id="columnsForm">
    <div id="${el}" class="columns-list">
    <div id="${el}-columns-list-container" class="columns-table-container">
    <table class="filters-table" cellspacing="0">
        <tr>
            <th class="filter-title first" title="${msg('lecm.arm.ttl.restore.default')}">
                <a href="#" onclick="LogicECM.module.ARM.restoreColumns()" title="${msg('lecm.arm.ttl.restore.default')}">${msg('lecm.arm.ttl.restore.default')}</a>
            </th>
        </tr>
    <tr class="detail-list-item">
    <td class="column">
        <#list columns as item>
            <div id="${item.fieldName}">
                <input type="checkbox"
                       name="${item.fieldName}"
                       value="${item.id}"
                       <#if item.checked>checked</#if>>
                <label class="column-label" title="${item.name}">${item.name}</label>
                <a id="${item.fieldName}_moveUp" class="moveUpColumn <#if item_index == 0>hidden</#if>"
                   title="${msg('lecm.arm.ttl.up')}" href="#" onclick="LogicECM.module.ARM.moveUp(this.parentElement)"><span></span></a>

                <a id="${item.fieldName}_moveDown" class="moveDownColumn <#if !item_has_next>hidden</#if>"
                   title="${msg('lecm.arm.ttl.down')}" href="#" onclick="LogicECM.module.ARM.moveDown(this.parentElement)"><span></span></a>
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

<#list set.children as item>
    <#if item.kind == "set">
        <#if (item_index % 2) == 0>
            <div class="yui-g 2-column-parent-set">
                <div class="yui-u first">
        <#else>
                <div class="yui-u">
        </#if>
        <@formLib.renderSet set=item />
                </div>
        <#if ((item_index % 2) != 0) || !item_has_next>
            </div>
        </#if>
    <#else>
        <@formLib.renderField field=form.fields[item.id] />
    </#if>
</#list>

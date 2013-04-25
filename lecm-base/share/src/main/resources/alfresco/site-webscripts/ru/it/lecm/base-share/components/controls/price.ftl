<#assign priceSet = item />
<div class="price-control">
    <#list priceSet.children as item>
        <#if (item_index < 2)>
            <#if item_index == 0>
                <div class="price-sum">
            <#elseif item_index == 1>
                <div class="price-currency">
            </#if>
            <@formLib.renderField field=form.fields[item.id] />
            </div>
        </#if>
    </#list>
</div>
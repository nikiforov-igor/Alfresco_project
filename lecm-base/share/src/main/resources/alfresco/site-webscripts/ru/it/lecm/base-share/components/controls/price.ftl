<#assign priceSet = item />
<div class="price-control">
    <#list priceSet.children as item>
        <#if item_index == 0>
            <div class="price-sum">
        <#else>
            <div class="price-currency">
        </#if>
            <@formLib.renderField field=form.fields[item.id] />
        </div>
    </#list>
</div>
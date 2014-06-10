<#assign priceSet = item />
<div class="price-control">
    <div class="price-currency">
        <#assign currency = priceSet.children[1]/>
        <@formLib.renderField field=form.fields[currency.id] />
    </div>
    <div class="price-sum">
        <#assign sum = priceSet.children[0]/>
        <@formLib.renderField field=form.fields[sum.id] />
    </div>

    <script>
        (function() {
            LogicECM.module.Base.Util.loadCSS([
                'css/lecm-base/components/price-control.css'
            ]);
        })();
    </script>
</div>
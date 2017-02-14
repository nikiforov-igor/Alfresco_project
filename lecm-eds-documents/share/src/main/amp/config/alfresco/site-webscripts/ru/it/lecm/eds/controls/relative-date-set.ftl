<#if item??>
    <#assign child = item />
<#else>
    <#assign child = set />
</#if >
<div class="relative-date-set">
    <div class="relative-date-type">
    <#assign currency = child.children[1]/>
        <@formLib.renderField field=form.fields[currency.id] />
    </div>
    <div class="relative-date-days">
    <#assign sum = child.children[0]/>
        <@formLib.renderField field=form.fields[sum.id] />
    </div>

    <script>
        (function() {
            LogicECM.module.Base.Util.loadCSS([
                'css/lecm-eds-documents/relative-date-set.css'
            ]);
        })();
    </script>
</div>
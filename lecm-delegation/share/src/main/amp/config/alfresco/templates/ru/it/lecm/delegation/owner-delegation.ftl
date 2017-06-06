<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>

<@bpage.basePageSimple>
<div id="delegation-page">
    <@region id="owner-content" scope="template"/>
</div>

<script type="text/javascript">
    LogicECM.module.Base.Util.loadCSS([
        'css/lecm-delegation/owner-delegation.css'
    ]);
</script>
</@bpage.basePageSimple>

<#assign el=args.htmlid?js_string/>
<div class="document-details-panel">
    <h2 id="${el}-heading" class="thin dark">${msg("heading")}</h2>
    <div>
    <#list  experts as ex>
        <a href="${ex.ulink}">${ex.fname}</a><img width="16" alt="" title="${msg("contact")}" src="/share/res/components/images/filetypes/generic-user-16.png"><br/>
    </#list>
    </div>

    <script type="text/javascript">//<![CDATA[
    Alfresco.util.createTwister("${el}-heading", "DocumentExperts");
    //]]></script>
</div>



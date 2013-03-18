<#assign el=args.htmlid/>
<#assign skipCount=5/>

<div class="document-details-panel">
    <h2 id="${el}-heading" class="thin dark">
    ${msg("heading")}
        <span class="alfresco-twister-actions">
            <a id="${el}-action-expand" href="javascript:void(0);" onclick="" class="expand"
               title="${msg("label.expand")}">&nbsp</a>
         </span>
    </h2>

    <div id="${el}-formContainer">
        <ul id="document-members-set" style="width: 100%">
        <hr>
        <#if members?? && members.items??>
            <#assign i=0/>
            <#list members.items as item>
                <#if i < skipCount>
                <li style="padding-bottom: 0.4em;">
                    <a href="${url.context}/page/view-metadata?nodeRef=${item.employeeRef}">${item.employeeName}</a><br/>
                    ${item.employeePosition}<br/>
                    <#assign i = i+1/>
                </li>
                </#if>
            </#list>
            <#if members.hasNext == "true">
                <li style="text-align: right; padding-right: 0.5em;">
                    <a id="${el}-link" href="javascript:void(0);" onclick="" class="edit"
                       title="${msg("label.members.more")}">${msg("label.members.more")}</a>
                </li>
            </#if>
        </#if>
        </ul>
    </div>

    <script type="text/javascript">
        var documentMembersComponent = null;
    </script>
    <script type="text/javascript">//<![CDATA[
    (function () {
        Alfresco.util.createTwister("${el}-heading", "DocumentMembers");

        function init() {
            documentMembersComponent = new LogicECM.DocumentMembers("${el}").setOptions(
                    {
                        nodeRef: "${nodeRef}",
                        title: "${msg('heading')}"
                    }).setMessages(${messages});
        }

        YAHOO.util.Event.onDOMReady(init);
    })();
    //]]>
    </script>
</div>
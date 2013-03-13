<#assign el=args.htmlid/>

<div class="document-details-panel">
    <h2 id="${el}-heading" class="thin dark">
    ${msg("heading")}
        <span class="alfresco-twister-actions">
            <a id="${el}-action-expand" href="javascript:void(0);" onclick="" class="expand" title="${msg("label.expand")}">&nbsp</a>
         </span>
    </h2>

    <div id="${el}-formContainer">
        <ul id="document-members-set" style="width: 100%">
        <#if members?? && members.items??>
            <#list members.items as item>
                <li>
                    ${item.employeePosition}<br/>
                    <a href="${url.context}/page/view-metadata?nodeRef=${item.employeeRef}">${item.employeeName}</a>
                    <hr>
                    <div class="member-ref" style="display: none">${item.employeeRef}</div>
                </li>
            </#list>
            <#if members.hasNext == "true">
                <li style="text-align: right">
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
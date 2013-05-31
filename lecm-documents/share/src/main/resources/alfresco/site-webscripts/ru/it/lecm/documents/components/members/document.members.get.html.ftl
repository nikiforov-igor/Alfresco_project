<#if members??>
    <#import "/ru/it/lecm/base-share/components/view.lib.ftl" as view/>

    <#assign el=args.htmlid/>
    <#assign skipCount=5/>

<script type="text/javascript">
    var documentMembersComponent = null;
</script>
<div class="widget-bordered-panel">
<div class="document-components-panel">
    <h2 id="${el}-heading" class="dark">
        ${msg("heading")}
        <span class="alfresco-twister-actions">
            <a id="${el}-action-expand" href="javascript:void(0);" onclick="documentMembersComponent.onExpand()" class="expand"
               title="${msg("label.expand")}">&nbsp</a>
         </span>
    </h2>

    <div id="${el}-formContainer">
        <@view.viewForm formId="${el}-view-node-form"/>
        <ul id="document-members-set" class="document-members-set document-right-set">
            <#if members?? && members.items??>
                <#assign i=0/>
                <#list members.items as item>
                    <#if i < skipCount>
                        <li style="padding-bottom: 0.4em;" class="text-broken">
                        ${view.showViewLink(item.employeeName, item.employeeRef, 'logicecm.employee.view')}<br/>
                        ${item.employeePosition}<br/>
                            <#assign i = i+1/>
                        </li>
                    </#if>
                </#list>
                <#if members.hasNext == "true">
                <li>
                    <div class="right-more-link-arrow" onclick="documentMembersComponent.onExpand();"></div>
                    <div class="right-more-link" onclick="documentMembersComponent.onExpand();">${msg('label.members.more')}</div>
                    <div style="clear:both;"></div>
                </li>
                </#if>
            </#if>
        </ul>
    </div>

    <script type="text/javascript">//<![CDATA[
    (function () {
        function init() {
            if (documentMembersComponent == null) {
                documentMembersComponent = new LogicECM.DocumentMembers("${el}").setOptions(
                        {
                            nodeRef: "${nodeRef}",
                            title: "${msg('heading')}"
                        }).setMessages(${messages});
            }
        }

        YAHOO.util.Event.onDOMReady(init);
    })();
    //]]>
    </script>
</div>
</div>
</#if>

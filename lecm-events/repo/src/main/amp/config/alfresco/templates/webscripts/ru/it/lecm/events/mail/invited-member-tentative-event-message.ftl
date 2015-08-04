<#assign typeText="мероприятии">
<#if type="lecm-meetings:document">
<#assign typeText="совещании">
</#if>

Приглашённый участник ${attendeeName!""} возможно будет участвовать в ${typeText} ${link!""}.
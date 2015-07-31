<#assign typeText="мероприятии">
<#if type="lecm-meetings:document">
<#assign typeText="совещании">
</#if>

Сотрудник ${attendeeLink!""} возможно будет участвовать в ${typeText} ${link!""}.
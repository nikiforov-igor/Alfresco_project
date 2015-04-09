<#assign el=args.htmlid?html>

<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/components/calendar/toolbar.css" group="calendar"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script type="text/javascript" src="${url.context}/res/scripts/lecm-events/lecm-calendar-toolbar.js" group="calendar"/>
</@>

<@markup id="widgets">
   <@createWidgets group="calendar"/>
   <@inlineScript group="calendar">
      Alfresco.util.relToTarget("${el}-body");
      Alfresco.util.addMessages(${messages}, "Alfresco.CalendarToolbar");
   </@>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <div id="${el}-body" class="toolbar calendar-toolbar theme-bg-2">
         <div class="yui-ge calendar-bar">
             <div class="leftContainer">
                <div class="yui-u flat-button addEventContainer">
                   <div id="${el}-viewButtons" class="addEvent">
                      <button id="${el}-addEvent-button" name="addEvent" title="${msg("button.add-event")}">${msg("button.add-event")}</button>
                   </div>
                </div>
             </div>
             <div class="restContainer">
                 <div class="rightContainer">
                     <#if viewToolbarNav >
                         <button id="${el}-prev-button" class="prev-button">${msg("button.previous")}</button>
                     </#if>
                     <button id="${el}-today-button" class="today-button">${msg("button.today")}</button>
                     <#if viewToolbarNav >
                         <button id="${el}-next-button">${msg("button.next")}</button>
                     </#if>
                 </div>
                 <div class="centerContainer">
                    <div class="yui-u first theme-bg-1">
                       <#if viewToolbarViewCount>
                          <div id="${el}-navigation" class="yui-buttongroup inline">
                             <#-- Don't insert linefeeds between these <input> tags -->
                               ${day!""}${week!""}${month!""}${agenda!""}
                          </div>
                       </#if>
                    </div>
                    <div class="flat-button work-hours">
                       <span id="${el}-workHours-button" class="yui-button yui-checkbox-button">
                          <span class="first-child">
                             <button name="workHours"></button>
                          </span>
                       </span>
                    </div>
                 </div>
             </div>
         </div>
         <div id="${el}-addEvent"></div>
      </div>
   </@>
</@>
<#assign formId=args.htmlid?js_string + "-form">

<script type="text/javascript">
    (function () {

        function init() {
            LogicECM.module.Base.Util.loadResources([
                'scripts/lecm-base/components/lecm-reiteration-control.js'
            ],
            [
                'css/lecm-base/components/reiteration-control.css'
            ], createControl);
        }

        function createControl() {
            var reiteration = new LogicECM.module.Base.Reiteration("${fieldHtmlId}").setOptions({
                value: <#if field.value == "">{}<#else>${field.value}</#if>
            });
            reiteration.setMessages(
                ${messages}
            );
        }

        YAHOO.util.Event.onDOMReady(init);

    })();
</script>

<div class="control date editmode">
    <div class="label-div">
        <label for="${fieldHtmlId}-date">
        ${field.label?html}:
        <#if field.mandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if>
        </label>
    </div>
    <div class="container">
        <div class="control-container" id="${fieldHtmlId}-reiteration-control-container">
            <div class="rule-body" id="${fieldHtmlId}-week-days-mode">
                <input type="checkbox" id="${fieldHtmlId}-week-days-mode-1" name="${fieldHtmlId}-week-days-mode-1" value="1"> ${msg("lable.day-of-week.monday")}<br/>
                <input type="checkbox" id="${fieldHtmlId}-week-days-mode-2" name="${fieldHtmlId}-week-days-mode-2" value="2"> ${msg("lable.day-of-week.tuesday")}<br/>
                <input type="checkbox" id="${fieldHtmlId}-week-days-mode-3" name="${fieldHtmlId}-week-days-mode-3" value="3"> ${msg("lable.day-of-week.wednesday")}<br/>
                <input type="checkbox" id="${fieldHtmlId}-week-days-mode-4" name="${fieldHtmlId}-week-days-mode-4" value="4"> ${msg("lable.day-of-week.thursday")}<br/>
                <input type="checkbox" id="${fieldHtmlId}-week-days-mode-5" name="${fieldHtmlId}-week-days-mode-5" value="5"> ${msg("lable.day-of-week.friday")}<br/>
                <input type="checkbox" id="${fieldHtmlId}-week-days-mode-6" name="${fieldHtmlId}-week-days-mode-6" value="6"> ${msg("lable.day-of-week.saturday")}<br/>
                <input type="checkbox" id="${fieldHtmlId}-week-days-mode-7" name="${fieldHtmlId}-week-days-mode-7" value="7"> ${msg("lable.day-of-week.sunday")}<br/>
            </div>
            <div class="rule-body hidden1" id="${fieldHtmlId}-month-days-mode">
                <table class="calendar-grid">
                    <tbody>
                    <tr><td>1</td><td>2</td><td>3</td><td>4</td><td>5</td><td>6</td><td>7</td></tr>
                    <tr><td>8</td><td>9</td><td>10</td><td>11</td><td>12</td><td>13</td><td>14</td></tr>
                    <tr><td>15</td><td>16</td><td>17</td><td>18</td><td>19</td><td>20</td><td>21</td></tr>
                    <tr><td>22</td><td>23</td><td>24</td><td>25</td><td>26</td><td>27</td><td>28</td></tr>
                    <tr><td>29</td><td>30</td><td>31</td></tr>
                    </tbody>
                </table>
                <input type="hidden" name="month-days" id="${fieldHtmlId}-month-days-input" value="">
            </div>

            <div class="rule-body hidden1" id="${fieldHtmlId}-shift-work-mode">
                <div class="shift-picker">
                ${msg("label.schedule.form.shift-work.working-days")}: <input name="working-days-amount" id="working-days" type="number" ><br/>
                ${msg("label.schedule.form.shift-work.non-working-days")}: <input name="working-days-interval" id="non-working-days" type="number">
                </div>
            </div>

            <div class="rule-control" id="${fieldHtmlId}-control-buttons">
                <input type="radio" name="reiteration-type" id="month-days" value="week-days" checked/> ${msg("label.schedule.form.week-days-type")}<br/>
                <input type="radio" name="reiteration-type" id="${fieldHtmlId}-control-month-days" value="month-days"/> ${msg("label.schedule.form.month-days-type")}<br/>
                <#--
                <input type="radio" name="reiteration-type" value="shift-work"/> ${msg("label.schedule.form.shift-work-type")}<br/>
                -->
            </div>

            <div id="${fieldHtmlId}-reiteration-rules-error-container" class="reiteration-rules-error-container"></div>
        </div>
    </div>
    <input id="${fieldHtmlId}" type="hidden" name="${field.name}" value="${field.value?html}"/>
</div>
<div class="clear"></div>
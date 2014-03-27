<#assign formId=args.htmlid?js_string + "-form">

<script type="text/javascript">
(function() {

	YAHOO.Bubbling.on("reiterationRulesUpdated", LogicECM.module.WCalendar.Schedule.reiterationRulesValidation);

	YAHOO.util.Event.onContentReady("control-buttons", function() {
		var elements = YAHOO.util.Selector.query("input", "control-buttons");
		for (var i = 0; i < elements.length; i++) {
			YAHOO.util.Event.addListener(elements[i], "click", onRadioClicked);
		}
	});

	YAHOO.util.Event.onContentReady("month-days-mode", function() {
		var elements = YAHOO.util.Selector.query("td", "month-days-mode");
		for (var i = 0; i < elements.length; i++) {
			YAHOO.util.Event.addListener(elements[i], "click", onDateClicked);
		}
	});

	YAHOO.util.Event.onContentReady("shift-work-mode", function() {
		YAHOO.util.Event.addListener("working-days", "change", fireElementChanged);
		YAHOO.util.Event.addListener("non-working-days", "change", fireElementChanged);
	});

	YAHOO.util.Event.onContentReady("week-days-mode", function() {
		var elements = YAHOO.util.Selector.query("input", "week-days-mode");
		for (var i = 0; i < elements.length; i++) {
			YAHOO.util.Event.addListener(elements[i], "click", fireElementChanged);
		}
	});

	YAHOO.util.Event.onContentReady("reiteration-control-container", fireElementChanged);

	function fireElementChanged() {
		YAHOO.Bubbling.fire("reiterationRulesUpdated", this);
	}

	function onRadioClicked() {
		var monthDays = new YAHOO.util.Element('month-days-mode');
		var weekDays = new YAHOO.util.Element('week-days-mode');
		var shiftWork = new YAHOO.util.Element('shift-work-mode');

		if (this.value === "week-days") {
			monthDays.setStyle("display", "none");
			weekDays.setStyle("display", "block");
			shiftWork.setStyle("display", "none");
		} else if (this.value === "month-days") {
			monthDays.setStyle("display", "block");
			weekDays.setStyle("display", "none");
			shiftWork.setStyle("display", "none");
		} else if (this.value === "shift-work") {
			monthDays.setStyle("display", "none");
			weekDays.setStyle("display", "none");
			shiftWork.setStyle("display", "block");
		} else {
			alert(this.value);
		}

		 fireElementChanged();
	}
	function onDateClicked() {
		var monthDaysInput = YAHOO.util.Dom.get("month-days-input");

		var selectedDates = monthDaysInput.value.split(",");
		dateSelected = this.textContent;
		// Дата, которую только что выбрали, уже есть в списке выбранных
		if (selectedDates.indexOf(dateSelected) > -1) {
			var newMonthDaysInput = "";
			this.removeAttribute("class", 0);
			for (var j = 0; j < selectedDates.length; j++) {
				if (selectedDates[j] === dateSelected) {
					continue;
				}
				if (newMonthDaysInput.length > 0) {
					newMonthDaysInput += ",";
				}
				newMonthDaysInput += selectedDates[j];
			}
			monthDaysInput.value = newMonthDaysInput;
		} else {
			if (monthDaysInput.value.length > 0) {
				monthDaysInput.value += "," + dateSelected;
			} else {
				monthDaysInput.value += this.textContent;
			}
			this.setAttribute("class", "selected-date");
		}

		fireElementChanged();
	}
})();
</script>

<div class="control-container" id="reiteration-control-container">
	<#-- TODO: добавить локализацию для дней недели -->
	<div class="rule-body" id="week-days-mode" style="display:block">
		<input type="checkbox" name="w1" value=true> Понедельник<br/>
		<input type="checkbox" name="w2" value=true> Вторник<br/>
		<input type="checkbox" name="w3" value=true> Среда<br/>
		<input type="checkbox" name="w4" value=true> Четверг<br/>
		<input type="checkbox" name="w5" value=true> Пятница<br/>
		<input type="checkbox" name="w6" value=true> Суббота<br/>
		<input type="checkbox" name="w7" value=true> Воскресенье<br/>
	</div>
	<div class="rule-body" id="month-days-mode" style="display:none">
		<table class="calendar-grid">
			<tbody>
				<tr><td>1</td><td>2</td><td>3</td><td>4</td><td>5</td><td>6</td><td>7</td></tr>
				<tr><td>8</td><td>9</td><td>10</td><td>11</td><td>12</td><td>13</td><td>14</td></tr>
				<tr><td>15</td><td>16</td><td>17</td><td>18</td><td>19</td><td>20</td><td>21</td></tr>
				<tr><td>22</td><td>23</td><td>24</td><td>25</td><td>26</td><td>27</td><td>28</td></tr>
				<tr><td>29</td><td>30</td><td>31</td></tr>
			</tbody>
		</table>
		<input type="hidden" name="month-days" id="month-days-input" value="">
	</div>

	<div class="rule-body" id="shift-work-mode" style="display:none">
		<div class="shift-picker">
			 ${msg("label.schedule.form.shift-work.working-days")}: <input name="working-days-amount" id="working-days" type="number" ><br/>
			${msg("label.schedule.form.shift-work.non-working-days")}: <input name="working-days-interval" id="non-working-days" type="number">
		</div>
	</div>

	<div class="rule-control" id="control-buttons">
		<input type="radio" name="reiteration-type" value="week-days" checked/> ${msg("label.schedule.form.week-days-type")}<br/>
		<input type="radio" name="reiteration-type" value="month-days"/> ${msg("label.schedule.form.month-days-type")}<br/>
		<input type="radio" name="reiteration-type" value="shift-work"/> ${msg("label.schedule.form.shift-work-type")}<br/>
	</div>

	<div id="reiteration-rules-error-container"></div>
</div>

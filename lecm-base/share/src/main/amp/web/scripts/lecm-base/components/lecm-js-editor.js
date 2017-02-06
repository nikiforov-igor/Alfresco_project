if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

(function(){

	var defs = [],
	editor;

	function load(file, c) {
		var xhr = new XMLHttpRequest();
		xhr.open("get", file, true);
		xhr.send();
		xhr.onreadystatechange = function() {
			if (xhr.readyState == 4) c(xhr.responseText, xhr.status);
		};
	}

	function loadDefs(id) {
		var files = [
			Alfresco.constants.URL_RESCONTEXT + "scripts/tern/browser.json",
			Alfresco.constants.URL_RESCONTEXT + "scripts/tern/ecma5.json",
			Alfresco.constants.PROXY_URI_RELATIVE + "lecm/jshelper/completions"
		];
		var loaded = 0;
		for (var i = 0; i < files.length; ++i) (function(i) {
			load(files[i], function(json) {
				defs[i] = JSON.parse(json);
				if (++loaded == files.length) initEditor(id);
			});
		})(i);
	}

	function initEditor(id) {
		editor = CodeMirror.fromTextArea(document.getElementById(id), {
			lineNumbers: true,
			mode: "javascript",
			gutters: ["CodeMirror-lint-markers"],
			lint: true
		});

		server = new CodeMirror.TernServer({
			defs: defs,
			plugins: {doc_comment:true}
		});

		editor.setOption("extraKeys", {
			"Ctrl-Space": function(cm) { server.complete(cm); },
			"Ctrl-I": function(cm) { server.showType(cm); },
			"Alt-.": function(cm) { server.jumpToDef(cm); },
			"Alt-,": function(cm) { server.jumpBack(cm); },
			"Ctrl-Q": function(cm) { server.rename(cm); },
			"Ctrl-.": function(cm) { server.selectName(cm); },
			"F11": function(cm) {
				cm.setOption("fullScreen", !cm.getOption("fullScreen"));
			},
			"Esc": function(cm) {
				if (cm.getOption("fullScreen")) cm.setOption("fullScreen", false);
			}
		});

		editor.on("change", editor.save);
	}

	LogicECM.module.JSEditor = function LogicECM_module_JSEditor(fieldHtmlId) {
		loadDefs(fieldHtmlId);
		return editor;
	}

})();
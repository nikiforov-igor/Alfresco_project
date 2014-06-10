function main() {
	var result = [];
	var types = json.get("types");
	if (types != null) {
		for (var i = 0; i < types.length(); i++) {
			var type = "" + types.get(i);
			result.push({
				name: type,
				title: base.getTypeLabel(type)
			});
		}
	}

	model.types = result;
}

main();

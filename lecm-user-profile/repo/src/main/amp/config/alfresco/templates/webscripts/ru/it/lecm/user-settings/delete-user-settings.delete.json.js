var key = args["key"];

if (args["category"]) {
    key = args["category"] + "." + key;
}

model.success = userSettings.deleteSettings(key);

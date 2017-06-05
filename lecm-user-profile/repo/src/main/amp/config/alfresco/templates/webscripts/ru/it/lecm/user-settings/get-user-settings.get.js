var key = args["key"];

if (args["category"]) {
    key = args["category"] + "." + key;
}

model.result = userSettings.getSettings(key);

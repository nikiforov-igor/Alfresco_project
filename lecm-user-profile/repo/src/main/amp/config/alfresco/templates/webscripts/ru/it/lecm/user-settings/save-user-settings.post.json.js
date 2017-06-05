var key = json.get("key");
var value = json.get("value");

if (json.has("category")) {
    key = json.get("category") + "." + key;
}

model.success = userSettings.setSettings(key, value);

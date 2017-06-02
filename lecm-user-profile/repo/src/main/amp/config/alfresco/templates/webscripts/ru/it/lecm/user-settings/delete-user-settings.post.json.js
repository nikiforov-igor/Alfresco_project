var user = json.get("user");
var key = json.get("key");

if (json.has("category")) {
    key = json.get("category") + "." + key;
}

model.success = userSettings.deleteSettings(user, key);

var user = args["user"];
var key = args["key"];

if (args["category"]) {
    key = args["category"] + "." + key;
}

model.result = userSettings.getSettings(user, key);

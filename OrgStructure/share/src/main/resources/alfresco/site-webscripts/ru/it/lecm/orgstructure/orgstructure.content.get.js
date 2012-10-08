var full = 'true';
if (args["full"] == null || args["full"].length == 0) {
    full = 'false';
}else {
    full = args["full"];
}
var empty = 'true';
if (args["empty"] == null || args["empty"].length == 0) {
    empty = 'false';
} else {
    empty = args["empty"];
}

model.fullLoad = (full == 'true');
model.empty = (empty == 'true');


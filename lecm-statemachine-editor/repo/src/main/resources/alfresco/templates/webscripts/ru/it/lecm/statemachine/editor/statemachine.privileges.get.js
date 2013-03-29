var ctx = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
var privilegesService = ctx.getBean("lecmPermissionServiceBean");

var privileges = privilegesService.getPermGroups().toArray();

var result = [];

for each (var privilegy in privileges) {
    result.push({
        value: privilegy.getName(),
        label: privilegy.getLabel()
    });
}

model.result = result;


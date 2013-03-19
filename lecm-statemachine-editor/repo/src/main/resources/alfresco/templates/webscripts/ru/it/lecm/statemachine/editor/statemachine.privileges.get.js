var ctx = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
var privilegesService = ctx.getBean("statePrivileges");

var privileges = privilegesService.getPrivilegesSet().toArray();

var result = [];

for each (var privilegy in privileges) {
    result.push({
        value: privilegy,
        label: privilegesService.getLabel(privilegy)
    });
}

model.result = result;


function main()
{
    model.allowEdit = user.isAdmin || user.properties["alfUserGroups"].indexOf("DA_ENGINEER") !== -1
}

main();
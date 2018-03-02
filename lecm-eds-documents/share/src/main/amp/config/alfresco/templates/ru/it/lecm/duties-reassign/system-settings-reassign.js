var result = remote.connect("alfresco").get('/lecm/duties-reassign/isEnabled');
model.reassignIsEnabled = false;
if (result.status == 200) {
    model.reassignIsEnabled = JSON.parse(result).isEnabled;
}
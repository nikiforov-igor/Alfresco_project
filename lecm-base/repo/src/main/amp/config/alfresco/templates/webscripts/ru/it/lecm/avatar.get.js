function getPlaceholder(thumbnailName) {
    // Try and get the place holder resource for a png avatar.
    var phPath = thumbnailService.getMimeAwarePlaceHolderResourcePath(thumbnailName, "images/png");
    if (phPath == null) {
        // 404 since no thumbnail was found
        status.setCode(status.STATUS_NOT_FOUND, "Thumbnail was not found and no place holder resource set for '" + thumbnailName + "'");
        return;
    }

    return phPath;
}

function main() {
    var thumbnailName = args["thumbnailName"] || "avatar";
    var nodeRef = args["nodeRef"];
    var employee = search.findNode(nodeRef);
    if (employee == null) {
        // Stream the placeholder image
        model.contentPath = getPlaceholder(thumbnailName);
        return;
    }

    var photo = orgstructure.getEmployeePhoto(nodeRef);

    if (photo != null) {
            // Get the thumbnail
            var thumbnail = photo.getThumbnail(thumbnailName);
            if (thumbnail == null || thumbnail.size == 0) {
                // Remove broken thumbnail
                if (thumbnail != null) {
                    thumbnail.remove();
                }

                // Force the creation of the thumbnail
                thumbnail = photo.createThumbnail(thumbnailName, false);
                if (thumbnail != null) {
                    model.contentNode = thumbnail;
                    var phPath = thumbnailService.getMimeAwarePlaceHolderResourcePath(thumbnailName, photo.mimetype);
                    model.contentPath = phPath;
                    return;
                }
            }
            else {
                // Place the details of the thumbnail into the model, this will be used to stream the content to the client
                model.contentNode = thumbnail;
                return;
            }
    }

    // Stream the placeholder image
    model.contentPath = getPlaceholder(thumbnailName);
}

main();
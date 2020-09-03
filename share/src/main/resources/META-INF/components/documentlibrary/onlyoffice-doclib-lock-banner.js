(function() {
    if (Alfresco.DocumentList) {
        YAHOO.Bubbling.fire("registerRenderer", {
            propertyName: "onlyofficeLocked",
            renderer: function showMetadataDescription(record, label) {
                var properties = record.jsNode.properties,
                    bannerUser = properties.lockOwner || properties.workingCopyOwner,
                    bannerUserLink = Alfresco.DocumentList.generateUserLink(this, bannerUser);
                return this.msg("actions.document.onlyoffice.banner", bannerUserLink);
            }
        });
    }
})();
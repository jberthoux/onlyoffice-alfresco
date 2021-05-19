/*
    Copyright (c) Ascensio System SIA 2021. All rights reserved.
    http://www.onlyoffice.com
*/

Alfresco.WebPreview.prototype.Plugins.onlyoffice = function(wp, attributes) {
    this.wp = wp;
    this.attributes = YAHOO.lang.merge(Alfresco.util.deepCopy(this.attributes), attributes);
    return this;
};

Alfresco.WebPreview.prototype.Plugins.onlyoffice.prototype = {
    attributes: {},

    report: function() {
        return null;
    },

    display: function() {
        return "<iframe id='embeddedView' src='onlyoffice-edit?preview=true&nodeRef=" + this.attributes.nodeRef + "' style='height: 75vh; width: 100%;' frameborder='0' scrolling='no' allowtransparency></iframe>";
    }
};
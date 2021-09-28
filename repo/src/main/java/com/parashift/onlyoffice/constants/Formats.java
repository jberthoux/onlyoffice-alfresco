package com.parashift.onlyoffice.constants;

import java.util.*;

/*
    Copyright (c) Ascensio System SIA 2021. All rights reserved.
    http://www.onlyoffice.com
*/

public class Formats {
    public static final List<Format> formats = new ArrayList<Format>() {{
        add(new Format("djvu", Arrays.asList("bmp", "gif", "jpg", "png")));
        add(new Format("doc", Arrays.asList("bmp", "docx", "dotx", "epub", "fb2", "gif", "html", "jpg", "odt", "ott", "pdf", "pdfa", "png", "rtf", "txt")));
        add(new Format("docm", Arrays.asList("bmp", "docx", "dotx", "epub", "fb2", "gif", "html", "jpg", "odt", "ott", "pdf", "pdfa", "png", "rtf", "txt")));
        add(new Format("docx", Arrays.asList("bmp", "dotx", "epub", "fb2", "gif", "html", "jpg", "odt", "ott", "pdf", "pdfa", "png", "rtf", "txt")));
        add(new Format("dot", Arrays.asList("bmp", "docx", "dotx", "epub", "fb2", "gif", "html", "jpg", "odt", "ott", "pdf", "pdfa", "png", "rtf", "txt")));
        add(new Format("dotm", Arrays.asList("bmp", "docx", "dotx", "epub", "fb2", "gif", "html", "jpg", "odt", "ott", "pdf", "pdfa", "png", "rtf", "txt")));
        add(new Format("dotx", Arrays.asList("bmp", "docx", "epub", "fb2", "gif", "html", "jpg", "odt", "ott", "pdf", "pdfa", "png", "rtf", "txt")));
        add(new Format("epub", Arrays.asList("bmp", "docx", "dotx", "fb2", "gif", "html", "jpg", "odt", "ott", "pdf", "pdfa", "png", "rtf", "txt")));
        add(new Format("fb2", Arrays.asList("bmp", "docx", "dotx", "epub", "gif", "html", "jpg", "odt", "ott", "pdf", "pdfa", "png", "rtf", "txt")));
        add(new Format("fodt", Arrays.asList("bmp", "docx", "dotx", "epub", "fb2", "gif", "html", "jpg", "odt", "ott", "pdf", "pdfa", "png", "rtf", "txt")));
        add(new Format("html", Arrays.asList("bmp", "docx", "dotx", "epub", "fb2", "gif", "jpg", "odt", "ott", "pdf", "pdfa", "png", "rtf", "txt")));
        add(new Format("mht", Arrays.asList("bmp", "docx", "dotx", "epub", "fb2", "gif", "jpg", "odt", "ott", "pdf", "pdfa", "png", "rtf", "txt")));
        add(new Format("odt", Arrays.asList("bmp", "docx", "dotx", "epub", "fb2", "gif", "html", "jpg", "ott", "pdf", "pdfa", "png", "rtf", "txt")));
        add(new Format("ott", Arrays.asList("bmp", "docx", "dotx", "epub", "fb2", "gif", "html", "jpg", "odt", "pdf", "pdfa", "png", "rtf", "txt")));
        add(new Format("pdf", Arrays.asList("bmp", "gif", "jpg", "png")));
        add(new Format("rtf", Arrays.asList("bmp", "docx", "dotx", "epub", "fb2", "gif", "html", "jpg", "odt", "ott", "pdf", "pdfa", "png", "txt")));
        add(new Format("txt", Arrays.asList("bmp", "docx", "dotx", "epub", "fb2", "gif", "html", "jpg", "odt", "ott", "pdf", "pdfa", "png", "rtf")));
        add(new Format("xps", Arrays.asList("bmp", "gif", "jpg", "pdf", "pdfa", "png")));
        add(new Format("xml", Arrays.asList("bmp", "docx", "dotx", "epub", "fb2", "gif", "html", "jpg", "odt", "ott", "pdf", "pdfa", "png", "rtf", "txt")));

        add(new Format("csv", Arrays.asList("bmp", "gif", "jpg", "ods", "ots", "pdf", "pdfa", "png", "xlsx", "xltx")));
        add(new Format("fods", Arrays.asList("bmp", "csv", "gif", "jpg", "ods", "ots", "pdf", "pdfa", "png", "xlsx", "xltx")));
        add(new Format("ods", Arrays.asList("bmp", "csv", "gif", "jpg", "ots", "pdf", "pdfa", "png", "xlsx", "xltx")));
        add(new Format("ots", Arrays.asList("bmp", "csv", "gif", "jpg", "ods", "pdf", "pdfa", "png", "xlsx", "xltx")));
        add(new Format("xls", Arrays.asList("bmp", "csv", "gif", "jpg", "ods", "ots", "pdf", "pdfa", "png", "xlsx", "xltx")));
        add(new Format("xlsm", Arrays.asList("bmp", "csv", "gif", "jpg", "ods", "ots", "pdf", "pdfa", "png", "xlsx", "xltx")));
        add(new Format("xlsx", Arrays.asList("bmp", "csv", "gif", "jpg", "ods", "ots", "pdf", "pdfa", "png", "xltx")));
        add(new Format("xlt", Arrays.asList("bmp", "csv", "gif", "jpg", "ods", "ots", "pdf", "pdfa", "png", "xlsx", "xltx")));
        add(new Format("xltm", Arrays.asList("bmp", "csv", "gif", "jpg", "ods", "ots", "pdf", "pdfa", "png", "xlsx", "xltx")));
        add(new Format("xltx", Arrays.asList("bmp", "csv", "gif", "jpg", "ods", "ots", "pdf", "pdfa", "png", "xlsx")));

        add(new Format("fodp", Arrays.asList("bmp", "gif", "jpg", "odp", "otp", "pdf", "pdfa", "png", "potx", "pptx")));
        add(new Format("odp", Arrays.asList("bmp", "gif", "jpg", "otp", "pdf", "pdfa", "png", "potx", "pptx")));
        add(new Format("otp", Arrays.asList("bmp", "gif", "jpg", "odp", "pdf", "pdfa", "png", "potx", "pptx")));
        add(new Format("pot", Arrays.asList("bmp", "gif", "jpg", "odp", "otp", "pdf", "pdfa", "png", "potx", "pptx")));
        add(new Format("potm", Arrays.asList("bmp", "gif", "jpg", "odp", "otp", "pdf", "pdfa", "png", "potx", "pptx")));
        add(new Format("potx", Arrays.asList("bmp", "gif", "jpg", "odp", "otp", "pdf", "pdfa", "png", "pptx")));
        add(new Format("pps", Arrays.asList("bmp", "gif", "jpg", "odp", "otp", "pdf", "pdfa", "png", "potx", "pptx")));
        add(new Format("ppsm", Arrays.asList("bmp", "gif", "jpg", "odp", "otp", "pdf", "pdfa", "png", "potx", "pptx")));
        add(new Format("ppsx", Arrays.asList("bmp", "gif", "jpg", "odp", "otp", "pdf", "pdfa", "png", "potx", "pptx")));
        add(new Format("ppt", Arrays.asList("bmp", "gif", "jpg", "odp", "otp", "pdf", "pdfa", "png", "potx", "pptx")));
        add(new Format("pptm", Arrays.asList("bmp", "gif", "jpg", "odp", "otp", "pdf", "pdfa", "png", "potx", "pptx")));
        add(new Format("pptx", Arrays.asList("bmp", "gif", "jpg", "odp", "otp", "pdf", "pdfa", "png", "potx")));
    }};

    public static List<Format> getSupportedFormats() {
        return formats;
    }
}

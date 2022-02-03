package com.parashift.onlyoffice.constants;

import java.util.*;

/*
    Copyright (c) Ascensio System SIA 2022. All rights reserved.
    http://www.onlyoffice.com
*/

public class Formats {
    public static final List<Format> formats = new ArrayList<Format>() {{
        add(new Format("djvu", Type.WORD, Arrays.asList("bmp", "gif", "jpg", "png")));
        add(new Format("doc", Type.WORD, Arrays.asList("bmp", "docx", "dotx", "epub", "fb2", "gif", "html", "jpg", "odt", "ott", "pdf", "pdfa", "png", "rtf", "txt")));
        add(new Format("docm", Type.WORD, Arrays.asList("bmp", "docx", "dotx", "epub", "fb2", "gif", "html", "jpg", "odt", "ott", "pdf", "pdfa", "png", "rtf", "txt")));
        add(new Format("docx", Type.WORD, true, Arrays.asList("bmp", "dotx", "docxf", "epub", "fb2", "gif", "html", "jpg", "odt", "ott", "pdf", "pdfa", "png", "rtf", "txt")));
        add(new Format("docxf", Type.FORM, true, Arrays.asList("bmp", "dotx", "oform", "epub", "fb2", "gif", "html", "jpg", "odt", "ott", "pdf", "pdfa", "png", "rtf", "txt")));
        add(new Format("oform", Type.WORD, true, Arrays.asList("bmp", "pdf")));
        add(new Format("dot", Type.WORD, Arrays.asList("bmp", "docx", "dotx", "epub", "fb2", "gif", "html", "jpg", "odt", "ott", "pdf", "pdfa", "png", "rtf", "txt")));
        add(new Format("dotm", Type.WORD, Arrays.asList("bmp", "docx", "dotx", "epub", "fb2", "gif", "html", "jpg", "odt", "ott", "pdf", "pdfa", "png", "rtf", "txt")));
        add(new Format("dotx", Type.WORD, Arrays.asList("bmp", "docx", "epub", "fb2", "gif", "html", "jpg", "odt", "ott", "pdf", "pdfa", "png", "rtf", "txt")));
        add(new Format("epub", Type.WORD, Arrays.asList("bmp", "docx", "dotx", "fb2", "gif", "html", "jpg", "odt", "ott", "pdf", "pdfa", "png", "rtf", "txt")));
        add(new Format("fb2", Type.WORD, Arrays.asList("bmp", "docx", "dotx", "epub", "gif", "html", "jpg", "odt", "ott", "pdf", "pdfa", "png", "rtf", "txt")));
        add(new Format("fodt", Type.WORD, Arrays.asList("bmp", "docx", "dotx", "epub", "fb2", "gif", "html", "jpg", "odt", "ott", "pdf", "pdfa", "png", "rtf", "txt")));
        add(new Format("html", Type.WORD, Arrays.asList("bmp", "docx", "dotx", "epub", "fb2", "gif", "jpg", "odt", "ott", "pdf", "pdfa", "png", "rtf", "txt")));
        add(new Format("mht", Type.WORD, Arrays.asList("bmp", "docx", "dotx", "epub", "fb2", "gif", "jpg", "odt", "ott", "pdf", "pdfa", "png", "rtf", "txt")));
        add(new Format("odt", Type.WORD, Arrays.asList("bmp", "docx", "dotx", "epub", "fb2", "gif", "html", "jpg", "ott", "pdf", "pdfa", "png", "rtf", "txt")));
        add(new Format("ott", Type.WORD, Arrays.asList("bmp", "docx", "dotx", "epub", "fb2", "gif", "html", "jpg", "odt", "pdf", "pdfa", "png", "rtf", "txt")));
        add(new Format("pdf", Type.WORD, Arrays.asList("bmp", "gif", "jpg", "png")));
        add(new Format("rtf", Type.WORD, Arrays.asList("bmp", "docx", "dotx", "epub", "fb2", "gif", "html", "jpg", "odt", "ott", "pdf", "pdfa", "png", "txt")));
        add(new Format("txt", Type.WORD, Arrays.asList("bmp", "docx", "dotx", "epub", "fb2", "gif", "html", "jpg", "odt", "ott", "pdf", "pdfa", "png", "rtf")));
        add(new Format("xps", Type.WORD, Arrays.asList("bmp", "gif", "jpg", "pdf", "pdfa", "png")));
        add(new Format("xml", Type.WORD, Arrays.asList("bmp", "docx", "dotx", "epub", "fb2", "gif", "html", "jpg", "odt", "ott", "pdf", "pdfa", "png", "rtf", "txt")));

        add(new Format("csv", Type.CELL, Arrays.asList("bmp", "gif", "jpg", "ods", "ots", "pdf", "pdfa", "png", "xlsx", "xltx")));
        add(new Format("fods", Type.CELL, Arrays.asList("bmp", "csv", "gif", "jpg", "ods", "ots", "pdf", "pdfa", "png", "xlsx", "xltx")));
        add(new Format("ods", Type.CELL, Arrays.asList("bmp", "csv", "gif", "jpg", "ots", "pdf", "pdfa", "png", "xlsx", "xltx")));
        add(new Format("ots", Type.CELL, Arrays.asList("bmp", "csv", "gif", "jpg", "ods", "pdf", "pdfa", "png", "xlsx", "xltx")));
        add(new Format("xls", Type.CELL, Arrays.asList("bmp", "csv", "gif", "jpg", "ods", "ots", "pdf", "pdfa", "png", "xlsx", "xltx")));
        add(new Format("xlsm", Type.CELL, Arrays.asList("bmp", "csv", "gif", "jpg", "ods", "ots", "pdf", "pdfa", "png", "xlsx", "xltx")));
        add(new Format("xlsx", Type.CELL, true, Arrays.asList("bmp", "csv", "gif", "jpg", "ods", "ots", "pdf", "pdfa", "png", "xltx")));
        add(new Format("xlt", Type.CELL, Arrays.asList("bmp", "csv", "gif", "jpg", "ods", "ots", "pdf", "pdfa", "png", "xlsx", "xltx")));
        add(new Format("xltm", Type.CELL, Arrays.asList("bmp", "csv", "gif", "jpg", "ods", "ots", "pdf", "pdfa", "png", "xlsx", "xltx")));
        add(new Format("xltx", Type.CELL, Arrays.asList("bmp", "csv", "gif", "jpg", "ods", "ots", "pdf", "pdfa", "png", "xlsx")));

        add(new Format("fodp", Type.SLIDE, Arrays.asList("bmp", "gif", "jpg", "odp", "otp", "pdf", "pdfa", "png", "potx", "pptx")));
        add(new Format("odp", Type.SLIDE, Arrays.asList("bmp", "gif", "jpg", "otp", "pdf", "pdfa", "png", "potx", "pptx")));
        add(new Format("otp", Type.SLIDE, Arrays.asList("bmp", "gif", "jpg", "odp", "pdf", "pdfa", "png", "potx", "pptx")));
        add(new Format("pot", Type.SLIDE, Arrays.asList("bmp", "gif", "jpg", "odp", "otp", "pdf", "pdfa", "png", "potx", "pptx")));
        add(new Format("potm", Type.SLIDE, Arrays.asList("bmp", "gif", "jpg", "odp", "otp", "pdf", "pdfa", "png", "potx", "pptx")));
        add(new Format("potx", Type.SLIDE, Arrays.asList("bmp", "gif", "jpg", "odp", "otp", "pdf", "pdfa", "png", "pptx")));
        add(new Format("pps", Type.SLIDE, Arrays.asList("bmp", "gif", "jpg", "odp", "otp", "pdf", "pdfa", "png", "potx", "pptx")));
        add(new Format("ppsm", Type.SLIDE, Arrays.asList("bmp", "gif", "jpg", "odp", "otp", "pdf", "pdfa", "png", "potx", "pptx")));
        add(new Format("ppsx", Type.SLIDE, Arrays.asList("bmp", "gif", "jpg", "odp", "otp", "pdf", "pdfa", "png", "potx", "pptx")));
        add(new Format("ppt", Type.SLIDE, Arrays.asList("bmp", "gif", "jpg", "odp", "otp", "pdf", "pdfa", "png", "potx", "pptx")));
        add(new Format("pptm", Type.SLIDE, Arrays.asList("bmp", "gif", "jpg", "odp", "otp", "pdf", "pdfa", "png", "potx", "pptx")));
        add(new Format("pptx", Type.SLIDE, true, Arrays.asList("bmp", "gif", "jpg", "odp", "otp", "pdf", "pdfa", "png", "potx")));
    }};

    public static List<Format> getSupportedFormats() {
        return formats;
    }
}

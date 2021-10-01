package com.parashift.onlyoffice.constants;

import java.util.List;

/*
    Copyright (c) Ascensio System SIA 2021. All rights reserved.
    http://www.onlyoffice.com
*/

public class Format {
    public String name;
    public List<String> convertTo;

    public Format(String name, List<String> convertTo) {
        this.name = name;
        this.convertTo = convertTo;
    }

    public String getName() {
        return name;
    }

    public List<String> getConvertTo() {
        return convertTo;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setConvertTo(List<String> convertTo) {
        this.convertTo = convertTo;
    }
}
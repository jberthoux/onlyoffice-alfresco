package com.parashift.onlyoffice.constants;

import java.util.List;

/*
    Copyright (c) Ascensio System SIA 2021. All rights reserved.
    http://www.onlyoffice.com
*/

public class Format {
    public String name;
    public Type type;
    public List<String> convertTo;

    public Format(String name, Type type, List<String> convertTo) {
        this.name = name;
        this.type = type;
        this.convertTo = convertTo;
    }

    public String getName() {
        return name;
    }

    public Type getType() { return type; }

    public List<String> getConvertTo() {
        return convertTo;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(Type type) { this.type = type; }

    public void setConvertTo(List<String> convertTo) {
        this.convertTo = convertTo;
    }
}
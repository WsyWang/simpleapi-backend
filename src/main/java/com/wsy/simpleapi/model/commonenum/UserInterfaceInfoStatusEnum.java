package com.wsy.simpleapi.model.commonenum;

public enum UserInterfaceInfoStatusEnum {
    USER_INTERFACE_INFO_STATUS_BAN(1, "封禁"),
    USER_INTERFACE_INFO_STATUS_NORMAL(0, "正常");

    private final Integer value;

    private final String text;

    UserInterfaceInfoStatusEnum(Integer value, String text) {
        this.value = value;
        this.text = text;
    }

    public Integer getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}

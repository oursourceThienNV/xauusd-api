package com.group.samrt.um.client.Common;

public interface Constant {
    public interface STATUS {
        String INACTICE = "00"; // cho duyet user
        String ACTIVE = "01"; // da khoa
        String DEATH = "02";// damat
    }
    public interface VERSION{
        String version="2.0";
    }
    public interface ERROR_CODE{
        String ERROR_SUCCESS="00";
        String ERROR_PASSWORD="01";
        String ERROR_OTHER="03";
        String ERROR_INACTIVE="04";
        String ERROR_VERSION="05";
    }
}


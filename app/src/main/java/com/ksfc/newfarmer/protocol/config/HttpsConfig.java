package com.ksfc.newfarmer.protocol.config;

import com.ksfc.newfarmer.MsgID;
import com.ksfc.newfarmer.protocol.ApiType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CAI on 2016/6/27.
 */
public class HttpsConfig {

    public static List<ApiType> httpsConfig() {
        List<ApiType> HttpsApis = new ArrayList<>();
        if (MsgID.IP.contains("api")){
            HttpsApis.add(ApiType.GET_ALI);
            HttpsApis.add(ApiType.GET_UNI);
            HttpsApis.add(ApiType.OFFLINE_PAY);
            HttpsApis.add(ApiType.EPOS_PAY);
            HttpsApis.add(ApiType.CONFIRM_OFFLINE_PAY);
        }
        return HttpsApis;
    }
}

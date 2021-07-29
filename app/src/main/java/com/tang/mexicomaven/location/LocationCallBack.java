package com.tang.mexicomaven.location;

public interface LocationCallBack {

    void onLocationGps(Double latitude, Double longitude);

    void onLocationGoogle(Double latitude, Double longitude);

}

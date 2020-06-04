package org.techtown.prototypeapplication.VO;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.techtown.prototypeapplication.MainActivity;

public class LatteMessage {
    private String deviceID;
    private String deviceType;
    private String dataType;
    private String jsonData;
    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").create();


    // constructor
    private LatteMessage() {
        this.deviceID = MainActivity.getDeviceId();
        this.deviceType = MainActivity.getDeviceType();
    }

    public LatteMessage(SensorData data) {
        this();
        this.dataType = "SensorData";
        this.jsonData = LatteMessage.gson.toJson(data);
    }

    public LatteMessage(Alert data) {
        this();
        this.dataType = "Alert";
        this.jsonData = LatteMessage.gson.toJson(data);
    }

    public LatteMessage(String id, String type, String data) {
        this.deviceID = id;
        this.dataType = type;
        this.jsonData = data;
    }

    public LatteMessage(String states, String stateDetail) {
        this();
        this.dataType = "Request";
        this.jsonData = LatteMessage.gson.toJson(new SensorData(states, stateDetail));
    }
    public LatteMessage(String SensorId) {
        this();
        this.dataType = "Request";
        this.jsonData = SensorId;
    }

    // get, set method
    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getDeviceType() { return deviceType; }

    public void setDeviceType(String deviceType) { this.deviceType = deviceType; }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String voType) {
        this.dataType = voType;
    }

    public String getJsonData() {
        return jsonData;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }

    @Override
    public String toString() {
        return "Message [deviceID=" + deviceID + ", voType=" + dataType + ", jsonData=" + jsonData + "]";
    }
}

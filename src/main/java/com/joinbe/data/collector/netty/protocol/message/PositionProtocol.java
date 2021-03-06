package com.joinbe.data.collector.netty.protocol.message;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.joinbe.data.collector.netty.protocol.annotation.DataOrder;

import java.lang.reflect.Field;
import java.util.List;

public class PositionProtocol extends ProtocolMessage{

    private static final Integer DEFAULT_DATA_LENGTH = 23;

    public PositionProtocol(String data) {
        super(data);
    }

    /**
     * 消息头：AT35
     */
    @DataOrder(order = 0)
    private String head;

    /**
     * 设备id
     */
    @DataOrder(order = 1)
    private String unitId;

    /**
     * 时间
     */
    @DataOrder(order = 2)
    private String dateTime;


    /**
     * 经度
     */
    @DataOrder(order = 3)
    private Double longitude;

    /**
     * 纬度
     */
    @DataOrder(order = 4)
    private Double latitude;

    /**
     * 速度
     */
    @DataOrder(order = 5)
    private Integer speed;

    /**
     * 方向
     */
    @DataOrder(order = 6)
    private Integer heading;
    /**
     * 海拔
     */
    @DataOrder(order = 7)
    private Integer altitude;

    /**
     * 卫星数量
     */
    @DataOrder(order = 8)
    private Integer satellites;

    /**
     * event id
     */
    @DataOrder(order = 9)
    private Integer eventId;

    /**
     * 里程
     */
    @DataOrder(order = 10)
    private Float mileage;

    /**
     * 输入状态
     */
    @DataOrder(order = 11)
    private Integer inputStatus;

    /**
     * 模拟输入1电压电平
     */
    @DataOrder(order = 12)
    private Float analogInput1;

    /**
     * 模拟输入2电压电平
     */
    @DataOrder(order = 13)
    private Float analogInput2;

    /**
     * 输出状态
     */
    @DataOrder(order = 14)
    private Integer outputStatus;

    /**
     * 漫游状态
     */
    @DataOrder(order = 15)
    private Integer roamingStatus;

    /**
     * GSM网络信号强度，CSQ数据
     */
    @DataOrder(order = 16)
    private Integer csq;


    /**
     * Communication system, 2=2G 3=3G 4=4G
     */
    @DataOrder(order = 17)
    private Integer commSys;

    /**
     * 跨国公司（移动国家代码）-跨国公司（移动网络代码）
     */
    @DataOrder(order = 18)
    private String mccMnc;


    /**
     * Location area code
     * 位置区号
     */
    @DataOrder(order = 19)
    private String lac;

    /**
     * cell id
     */
    @DataOrder(order = 20)
    private String cid;

    /**
     * Voltage level of backup battery
     * 备用电池电压等级
     */
    @DataOrder(order = 21)
    private Float voltageBB;

    /**
     * 当IBUTTON吸附时，AT35会发送事件ID=100至SERVER并将IBUTTON ID附加于信息最后
     * 当IBUTTON移除后，AT35会发送事件件ID=101至SERVER
     */
    @DataOrder(order = 22)
    private String ibuttonId;

    /**
     * 跨国公司（移动国家代码）
     */
    private String mcc;

    /**
     * 跨国公司（移动网络代码）
     */
    private String mnc;

    @Override
    public void processField(Field field, List<String> dataList) {
        DataOrder dataOrder = field.getAnnotation(DataOrder.class);
        if (dataOrder == null) {
            return;
        }
        String data = dataList.get(dataOrder.order());
        String methodName = "set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
        if (Integer.class.equals(field.getType())) {
            ReflectUtil.invoke(this, methodName, Integer.valueOf(data));
        } else if (Double.class.equals(field.getType())) {
            ReflectUtil.invoke(this, methodName, Double.valueOf(data));
        } else if (Float.class.equals(field.getType())) {
            ReflectUtil.invoke(this, methodName, Float.valueOf(data));
        } else if (String.class.equals(field.getType())) {
            ReflectUtil.invoke(this, methodName, data);
        }
    }

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Integer getSpeed() {
        return speed;
    }

    public void setSpeed(Integer speed) {
        this.speed = speed;
    }

    public Integer getHeading() {
        return heading;
    }

    public void setHeading(Integer heading) {
        this.heading = heading;
    }

    public Integer getAltitude() {
        return altitude;
    }

    public void setAltitude(Integer altitude) {
        this.altitude = altitude;
    }

    public Integer getSatellites() {
        return satellites;
    }

    public void setSatellites(Integer satellites) {
        this.satellites = satellites;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public Float getMileage() {
        return mileage;
    }

    public void setMileage(Float mileage) {
        this.mileage = mileage;
    }

    public Integer getInputStatus() {
        return inputStatus;
    }

    public void setInputStatus(Integer inputStatus) {
        this.inputStatus = inputStatus;
    }

    public Float getAnalogInput1() {
        return analogInput1;
    }

    public void setAnalogInput1(Float analogInput1) {
        this.analogInput1 = analogInput1;
    }

    public Float getAnalogInput2() {
        return analogInput2;
    }

    public void setAnalogInput2(Float analogInput2) {
        this.analogInput2 = analogInput2;
    }

    public Integer getOutputStatus() {
        return outputStatus;
    }

    public void setOutputStatus(Integer outputStatus) {
        this.outputStatus = outputStatus;
    }

    public Integer getRoamingStatus() {
        return roamingStatus;
    }

    public void setRoamingStatus(Integer roamingStatus) {
        this.roamingStatus = roamingStatus;
    }

    public Integer getCsq() {
        return csq;
    }

    public void setCsq(Integer csq) {
        this.csq = csq;
    }

    public Integer getCommSys() {
        return commSys;
    }

    public void setCommSys(Integer commSys) {
        this.commSys = commSys;
    }

    public String getMccMnc() {
        return mccMnc;
    }

    public void setMccMnc(String mccMnc) {
        this.mccMnc = mccMnc;
    }

    public String getLac() {
        return lac;
    }

    public void setLac(String lac) {
        this.lac = lac;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public Float getVoltageBB() {
        return voltageBB;
    }

    public void setVoltageBB(Float voltageBB) {
        this.voltageBB = voltageBB;
    }

    public String getMcc() {
        return mcc;
    }

    public void setMcc(String mcc) {
        this.mcc = mcc;
    }

    public String getMnc() {
        return mnc;
    }

    public void setMnc(String mnc) {
        this.mnc = mnc;
    }

    public String getIbuttonId() {
        return ibuttonId;
    }

    public void setIbuttonId(String ibuttonId) {
        this.ibuttonId = ibuttonId;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }
}

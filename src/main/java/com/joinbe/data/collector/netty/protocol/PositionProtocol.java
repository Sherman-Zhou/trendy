package com.joinbe.data.collector.netty.protocol;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.joinbe.data.collector.netty.protocol.annotation.DataOrder;
import lombok.Data;

import java.lang.reflect.Field;
import java.util.List;

@Data
public class PositionProtocol {

    private static final Integer DEFAULT_DATA_LENGTH = 22;

    /**
     * 设备id
     */
    @DataOrder(order = 0)
    private String unitId;

    /**
     * 时间
     */
    @DataOrder(order = 1)
    private String dateTime;


    /**
     * 经度
     */
    @DataOrder(order = 2)
    private Double longitude;

    /**
     * 纬度
     */
    @DataOrder(order = 3)
    private Double latitude;

    /**
     * 速度
     */
    @DataOrder(order = 4)
    private Integer speed;

    /**
     * 方向
     */
    @DataOrder(order = 5)
    private Integer heading;
    /**
     * 海拔
     */
    @DataOrder(order = 6)
    private Integer altitude;

    /**
     * 卫星数量
     */
    @DataOrder(order = 7)
    private Integer satellites;

    /**
     * event id
     */
    @DataOrder(order = 8)
    private Integer eventId;

    /**
     * 里程
     */
    @DataOrder(order = 9)
    private Float mileage;

    /**
     * 输入状态
     */
    @DataOrder(order = 10)
    private Integer inputStatus;

    /**
     * 模拟输入1电压电平
     */
    @DataOrder(order = 11)
    private Float analogInput1;

    /**
     * 模拟输入2电压电平
     */
    @DataOrder(order = 12)
    private Float analogInput2;

    /**
     * 输出状态
     */
    @DataOrder(order = 13)
    private Integer outputStatus;

    /**
     * 漫游状态
     */
    @DataOrder(order = 14)
    private Integer roamingStatus;

    /**
     * GSM网络信号强度，CSQ数据
     */
    @DataOrder(order = 15)
    private Integer csq;


    /**
     * Communication system, 2=2G 3=3G 4=4G
     */
    @DataOrder(order = 16)
    private Integer commSys;

    /**
     * 跨国公司（移动国家代码）-跨国公司（移动网络代码）
     */
    @DataOrder(order = 17)
    private String mccMnc;


    /**
     * Location area code
     * 位置区号
     */
    @DataOrder(order = 18)
    private String lac;

    /**
     * cell id
     */
    @DataOrder(order = 19)
    private String cid;

    /**
     * Voltage level of backup battery
     * 备用电池电压等级
     */
    @DataOrder(order = 20)
    private Float voltageBB;


    private String data;

    /**
     * 跨国公司（移动国家代码）
     */
    private String mcc;

    /**
     * 跨国公司（移动网络代码）
     */
    private String mnc;


    /**
     * 解析content内容
     */
    public void initData() {
        if (StrUtil.isEmpty(this.data)) {
            return;
        }
        if (data.startsWith("$OK")) {
            return;
        }
        List<String> d = StrUtil.split(this.data, StrUtil.C_COMMA);
        Field[] fields = ReflectUtil.getFields(PositionProtocol.class);
        List<Field> fieldList = CollUtil.newArrayList(fields);
        fieldList.forEach(field -> processField(field, d));

    }


    private void processField(Field field, List<String> dataList) {
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


    public PositionProtocol(String data) {
        this.data = data;
    }

    public PositionProtocol() {
    }

    public static void main(String[] args) {
        String data = "868020030100050,20190126000601,121.447943,25.167723,0,0,0,0,3,0.0,1,0.01,11.89,0,0,0,0,466-92,65534,28789517,0.01";
        PositionProtocol p = new PositionProtocol(data);
        p.initData();
        System.out.println(JSONUtil.toJsonStr(p));
    }

}

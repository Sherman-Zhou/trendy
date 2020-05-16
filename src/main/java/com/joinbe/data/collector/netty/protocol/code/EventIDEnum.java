package com.joinbe.data.collector.netty.protocol.code;

public enum EventIDEnum {

    /**
     * Logging position data
     */
    LOGGING_POSITION_DATA(1),
    /**
     * Position data
     */
    POSITION_DATA(2),
    /**
     * Track position data 轨道位置数据
     */
    TRACK_POSITION_DATA(3),
    /**
     * Main power low event
     */
    MAIN_POWER_LOW_EVENT(4),
    /**
     * Main power lost event
     */
    MAIN_POWER_LOST_EVENT(5),
    /**
     * Main power voltage low recover event
     * 主电源电压低恢复
     */
    MAIN_POWER_VOLTAGE_LOW_RECOVER_EVENT(6),
    /**
     * Main power voltage lost recover event
     * 主电源失压恢复事件
     */
    MAIN_POWER_VOLTAGE_LOST_RECOVER_EVENT(7),
    /**
     * Backup battery voltage low event
     * 备用电池电压低事件
     */
    BACKUP_BATTERY_VOLTAGE_LOW_EVENT(8),
    /**
     * Backup battery voltage low recover event
     * 备用电池电压低恢复事件
     */
    BACKUP_BATTERY_VOLTAGE_LOW_RECOVER_EVENT(9),
    /**
     * RFID read event
     * RFID读取事件
     */
    RFID_READ_EVENT(10),
    /**
     * Moving event
     * 移动事件
     */
    MOVING_EVENT(11),
    /**
     * Idle event
     * 空闲事件
     */
    IDLE_EVENT(12),
    /**
     * Tow event
     * 拖航事件
     */
    TOW_EVENT(13),
    /**
     * Over speed event
     * 超速事件
     */
    OVER_SPEED_EVENT(14),
    /**
     * Harsh break event
     * 严重中断事件
     */
    HARSH_BREAK_EVENT(15),
    /**
     * Harsh acceleration event
     * 剧烈加速事件
     */
    HARSH_ACCELERATION_EVENT(16),
    /**
     * Entering sleep mode event
     * 进入睡眠模式事件
     */
    ENTERING_SLEEP_MODE_EVENT(20),
    /**
     * Wake up from sleep mode event
     * 从睡眠模式事件中唤醒
     */
    WAKE_UP_FROM_SLEEP_MODE_EVENT(21),
    /**
     * Movement detect event
     * 运动检测事件
     */
    MOVEMENT_DETECT_EVENT(22),
    /**
     * Error for entering sleep mode
     * 进入睡眠模式时出错
     */
    ERROR_FOR_ENTERING_SLEEP_MODE(23),
    /**
     * Input 1 state changing event
     * 输入1状态更改事件
     */
    INPUT_1_STATE_CHANGING_EVENT(51),
    /**
     * Input 2 state changing event
     * 输入2状态更改事件
     */
    INPUT_2_STATE_CHANGING_EVENT(52),
    /**
     * Input 3 state changing event
     * 输入3状态更改事件
     */
    INPUT_3_STATE_CHANGING_EVENT(53),
    ;


    EventIDEnum(Integer eventId) {
        this.eventId = eventId;
    }

    public static EventIDEnum getCMDbyHex(Integer eventId) {
        EventIDEnum[] cmdEnums = values();
        for (EventIDEnum paramEnum : cmdEnums) {
            if (paramEnum.getEventId().equals(eventId)) {
                return paramEnum;
            }
        }
        return null;
    }

    private Integer eventId;


    public Integer getEventId() {
        return eventId;
    }

    public EventIDEnum setEventId(Integer eventId) {
        this.eventId = eventId;
        return this;
    }
}

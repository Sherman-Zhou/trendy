package com.joinbe.data.collector.netty.protocol.code;

public enum EventEnum {

    /**
     * Set device communication method
     */
    SCOM("SCOM"),
    /**
     * Set device basic setting
     */
    SCFG("SCFG"),
    /**
     * Reset device parameters to default
     */
    SDEF("SDEF"),
    /**
     * Check device firmware version
     */
    VER("VER"),
    /**
     * Reboot device
     */
    BOOT("BOOT"),
    /**
     * Get device system information
     */
    INFO("INFO"),
    /**
     * Set VIP phone numbers for user defined reports
     */
    SVIP("SVIP"),
    /**
     * Setting user define events
     */
    SEVT("SEVT"),
    /**
     * Clear user defined event setting
     */
    CEVT("CEVT"),
    /**
     * Set bit baud rate of serial port
     */
    SBBR("SBBR"),
    /**
     * Set device mileage value offset
     */
    SMIL("SMIL"),
    /**
     * Get device real time position event
     */
    GPOS("GPOS"),
    /**
     * Query device IMEI number
     */
    IMEI("IMEI"),
    /**
     * Query device SIM card identification number
     */
    ISIM("ISIM"),
    /**
     * Enable/Disable GPS NMEA output
     */
    NMEA("NMEA"),
    /**
     * Set device logging function
     */
    SLOG("SLOG"),
    /**
     * Download device logging data
     */
    DLOG("DLOG"),
    /**
     * Stop device download Logging data
     */
    ILOG("ILOG"),
    /**
     * Clear device all logging data
     */
    CLOG("CLOG"),
    /**
     * Set battery ON/OFF
     */
    SBAT("SBAT"),
    /**
     * Set output port
     */
    SGPO("SGPO"),
    /**
     * Set Input port
     */
    SGPI("SGPI"),
    /**
     * Clear all event queue
     */
    CREP("CREP"),
    /**
     * Set tracking report/ read time report
     */
    TPOS("TPOS"),
    /**
     * Set idle condition
     */
    IDLE("IDLE"),
    /**
     * Set tow function
     */
    STOW("STOW"),
    /**
     * Set speed alert function
     */
    SSPD("SSPD"),
    /**
     * Set harsh break alert function
     */
    SHBK("SHBK"),
    /**
     * Set harsh acceleration alert function
     */
    SHAC("SHAC"),
    /**
     * Enable/Disable RFID read function (*available in AT35 series only)
     */
    RFID("RFID"),

    /**
     * set token
     */
    SETKEY("SETKEY"),

    /**
     * 0-??????
     * 1-??????
     */
    DOOR("DOOR"),

    /**
     * ????????????
     */
    DOOR_QRY("DOOR_QRY"),

    /**
     * ??????????????????
     */
    BLENAME("BLENAME"),

    /**
     * ??????????????????
     */
    BLENAME_QRY("BLENAME_QRY"),

    /**
     * MAC??????
     */
    BMAC("BMAC"),

    /**
     * MAC0??????
     */
    BMAC0("BMAC0"),
    /**
     * DLFW
     */
    DLFW("DLFW"),
    /**
     * C_CMD
     */
    C_CMD("C_CMD"),
    ;

    EventEnum(String event) {
        this.event = event;
    }

    private String event;


    public String getEvent() {
        return event;
    }

    public EventEnum setEvent(String event) {
        this.event = event;
        return this;
    }}

package com.joinbe.data.collector.cmd.register.impl;

import cn.hutool.core.util.StrUtil;
import com.joinbe.data.collector.cmd.annotiation.CmdRegister;
import com.joinbe.data.collector.cmd.register.Cmd;
import com.joinbe.data.collector.netty.protocol.code.EventEnum;

import java.util.HashMap;

@CmdRegister(event = EventEnum.SEVT)
public class SetUserEventCmd extends AbstractCmdRegister implements Cmd {

    @Override
    public String initCmd(HashMap<String, String> params) {
        StringBuffer cmdBuild = super.initCmdHeader();
        cmdBuild.append(StrUtil.C_COMMA)
                //100
                .append(params.get(KEY_USER_ID))
                .append(StrUtil.C_COMMA)
                //3
                .append(params.get(KEY_ACTION))
                .append(StrUtil.C_COMMA)
                //1
                .append(params.get(KEY_SMS_VIP_MASK))
                .append(StrUtil.C_COMMA)
                //8
                .append(params.get(KEY_OUTPUT_CTRL_ID))
                .append(StrUtil.C_COMMA)
                //2
                .append(params.get(KEY_INPUT_MASK))
                .append(StrUtil.C_COMMA)
                //2
                .append(params.get(KEY_INPUT_CTRL))
                .append(StrUtil.C_COMMA)
                //0
                .append(params.get(KEY_SCHEDULE_ID))
                .append(StrUtil.C_COMMA)
                //0
                .append(params.get(KEY_ZONE_CTRL_ID))
                .append(StrUtil.C_COMMA)
                //0
                .append(params.get(KEY_EVENT_ID))
                .append(CMD_END)

        ;
        return cmdBuild.toString();
    }


    @Override
    String getEvent() {
        return EventEnum.SEVT.getEvent();
    }

    /**
     * The device supports up to 50 user defined events.
     * The user defined ID number is from 100~149.
     */
    public final static String KEY_USER_ID = "userId";
    /**
     * 1. Logging:
     * When all defined report conditions are true, log the most recent PGS position to memory.
     * 2. Polling:
     * When all defined report conditions are true, send the last GPS position to the base station.
     * 3. Logging and Polling:
     * When all defined report conditions are true, log the most recent GPS position to memory and send back to the base station as well.
     */
    public final static String KEY_ACTION = "actions";


    /**
     * If the event is triggered then the device could send a SMS alert to up to 5 different pre-defined SMS phone number.
     * The SMS VIP is defined in the $AVM,SVIP command.
     * The bitwise definition is following:
     * 0. Disable
     * 1. SMS VIP 1
     * 2. SMS VIP 2
     * 4. SMS VIP 3
     * 8. SMS VIP 4
     * 16. SMS VIP 5
     * Ex:
     * Set to 7 means enabled (SMS VIP 1 + SMS VIP 2 + SMS VIP 3)
     */
    public final static String KEY_SMS_VIP_MASK = "smsVipMask";

    /**
     * Select output control ID.
     * PID Range 1~10
     * PID is defined in the command $AVM,POUT
     */
    public final static String KEY_OUTPUT_CTRL_ID = "outputCtrlID";

    /**
     * Specify the input port is used for this specific report.
     * This setting is bitwise format.
     * 0. Disable
     * 1. Input 1
     * 2. Input 2 4. Input 3
     */
    public final static String KEY_INPUT_MASK = "=inputMask";
    /**
     * This parameter is used to specify the input port which defines in the “Input Used” parameters which
     * must be “on” state.
     * This setting is bitwise format. 0. Disable
     * 1. Input 1
     * 2. Input 2
     * 4. Input 3
     */
    public final static String KEY_INPUT_CTRL = "=inputCtrl";

    /**
     * Select Schedule ID.
     * SID Range 1~10
     * SID is defined in the command $AVM,SCLK
     */
    public final static String KEY_SCHEDULE_ID = "=scheduleId";
    /**
     * Select Zone ID.
     * ZID Range 1~10
     * ZID is defined in the command $AVM,SZON
     */
    public final static String KEY_ZONE_CTRL_ID = "=zoneCtrlId";
    /**
     * Default event IDs. Please see Event ID list.
     */
    public final static String KEY_EVENT_ID = "=eventID";


}

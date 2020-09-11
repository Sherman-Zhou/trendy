package com.joinbe.data.collector.cmd.register.impl;

import cn.hutool.core.util.StrUtil;
import com.joinbe.data.collector.cmd.annotiation.CmdRegister;
import com.joinbe.data.collector.cmd.register.Cmd;
import com.joinbe.data.collector.netty.protocol.code.EventEnum;

import java.util.HashMap;


@CmdRegister(event = EventEnum.SGPO)
public class LockCmd extends AbstractCmdRegister implements Cmd {

    @Override
    public String initCmd(HashMap<String, String> params) {
        StringBuffer cmdBuild = super.initCmdHeader();
        cmdBuild.append(StrUtil.C_COMMA)
                .append(params.get(KEY_OUTPUT_NUM))
                .append(StrUtil.C_COMMA)
                .append(params.get(KEY_OUTPUT_STATUS))
                .append(StrUtil.C_COMMA)
                .append(params.get(KEY_OUTPUT_ON_DURATION))
                .append(StrUtil.C_COMMA)
                .append(params.get(KEY_OUTPUT_OFF_DURATION))
                .append(StrUtil.C_COMMA)
                .append(params.get(KEY_OUTPUT_TIMES))
                .append(StrUtil.C_COMMA)
                .append(CMD_END)

        ;
        return cmdBuild.toString();
    }


    @Override
    String getEvent() {
        return EventEnum.SGPO.getEvent();
    }


    /**
     * 0: disable
     * 1: enable
     */
    public final static String KEY_OUTPUT_NUM = "outputNum";
    /**
     * 0. Disable
     * 1. Output 1
     * 2. Output 2
     * 3. Output 3
     * 4. Beep
     */
    public final static String KEY_OUTPUT_STATUS = "outputStatus";
    /**
     * Outpup OnDuration
     * <p>
     * To define the time interval of the specific output port staying in OFF state. Effective range: 0~65535 100ms
     * Ex:
     * 255 100ms = 25.5 seconds
     */
    public final static String KEY_OUTPUT_ON_DURATION = "outputOnDuration";
    /**
     * Output_OffDuration
     * To define the time interval of the specific output port staying in OFF state. Effective range: 0~65535 100ms
     * Ex:
     * 255 100ms = 25.5 seconds
     */
    public final static String KEY_OUTPUT_OFF_DURATION = "outputOffDuration";

    /**
     * To define the times of the specific output port changing from current state to alternative state and back to the original state after reaching the duration.
     * Effective range: 0~65535 times
     */
    public final static String KEY_OUTPUT_TIMES = "outputTimes";


}

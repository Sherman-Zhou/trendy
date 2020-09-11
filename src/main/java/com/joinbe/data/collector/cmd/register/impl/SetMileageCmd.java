package com.joinbe.data.collector.cmd.register.impl;

import cn.hutool.core.util.StrUtil;
import com.joinbe.data.collector.cmd.annotiation.CmdRegister;
import com.joinbe.data.collector.cmd.register.Cmd;
import com.joinbe.data.collector.netty.protocol.code.EventEnum;

import java.util.HashMap;

@CmdRegister(event = EventEnum.SMIL)
public class SetMileageCmd extends AbstractCmdRegister implements Cmd {

    @Override
    public String initCmd(HashMap<String, String> params) {
        StringBuffer cmdBuild = super.initCmdHeader();
        cmdBuild.append(StrUtil.C_COMMA)
                .append(params.get(MILEAGE_MODE))
                .append(StrUtil.C_COMMA)
                .append(params.get(MILEAGE_OFFSET))
                .append(StrUtil.C_COMMA)
                .append(params.get(MILEAGE_MULTIPLE))
                .append(CMD_END);
        return cmdBuild.toString();
    }

    @Override
    String getEvent() {
        return EventEnum.SMIL.getEvent();
    }


    /**
     * 0.Disable
     * 1. Mileage will be accumulated regardless the ACC status.
     * 2. Mileage will be accumulated only if the ACC is on.
     */
    public final static String MILEAGE_MODE = "mileageMode";

    /**
     * Initial the mileage value (Km).
     * Range is from 0.0~4294967.2
     */
    public final static String MILEAGE_OFFSET = "mileageOffset";

    /**
     * multiple mileage
     */
    public final static String MILEAGE_MULTIPLE = "mileageMultiple";
}

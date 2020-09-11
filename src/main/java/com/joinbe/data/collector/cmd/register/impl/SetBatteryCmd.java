package com.joinbe.data.collector.cmd.register.impl;

import cn.hutool.core.util.StrUtil;
import com.joinbe.data.collector.cmd.annotiation.CmdRegister;
import com.joinbe.data.collector.cmd.register.Cmd;
import com.joinbe.data.collector.netty.protocol.code.EventEnum;

import java.util.HashMap;

@CmdRegister(event = EventEnum.SBAT)
public class SetBatteryCmd extends AbstractCmdRegister implements Cmd {


    @Override
    public String initCmd(HashMap<String, String> params) {
        StringBuffer cmdBuild = super.initCmdHeader();
        cmdBuild.append(StrUtil.C_COMMA)
                .append(params.get(KEY_MODE))
                .append(CMD_END)

        ;
        return cmdBuild.toString();
    }


    @Override
    String getEvent() {
        return EventEnum.SBAT.getEvent();
    }


    /**
     * 0: disable
     * 1: enable
     */
    public final static String KEY_MODE = "mode";


}

package com.joinbe.data.collector.cmd.register.impl;

import cn.hutool.core.util.StrUtil;
import com.joinbe.data.collector.cmd.annotiation.CmdRegister;
import com.joinbe.data.collector.cmd.register.Cmd;
import com.joinbe.data.collector.netty.protocol.code.EventEnum;

import java.util.HashMap;

@CmdRegister(event = EventEnum.DLFW)
public class DLFWCmd extends AbstractCmdRegister implements Cmd {

    @Override
    public String initCmd(HashMap<String, String> params) {
        StringBuilder cmdBuild = super.initCmdHeader();
        cmdBuild.append(StrUtil.C_COMMA)
                .append(params.get(CMD))
                .append(CMD_END);
        return cmdBuild.toString();
    }

    @Override
    String getEvent() {
        return EventEnum.DLFW.getEvent();
    }


    /**
     * CMD
     */
    public final static String CMD = "cmd";


}

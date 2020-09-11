package com.joinbe.data.collector.cmd.register.impl;

import cn.hutool.core.util.StrUtil;
import com.joinbe.config.Constants;
import com.joinbe.data.collector.cmd.annotiation.CmdRegister;
import com.joinbe.data.collector.cmd.register.Cmd;
import com.joinbe.data.collector.netty.protocol.code.EventEnum;

import java.util.HashMap;

@CmdRegister(event = EventEnum.BLENAME_QRY)
public class BleNameQueryCmd extends AbstractCmdRegister implements Cmd {

    @Override
    public String initCmd(HashMap<String, String> params) {
        StringBuffer cmdBuild = super.initCmdHeader();
        cmdBuild.append(StrUtil.C_COMMA)
                .append(Constants.C_QUESTION_MARK)
                .append(CMD_END);
        return cmdBuild.toString();
    }

    @Override
    String getEvent() {
        return EventEnum.BLENAME.getEvent();
    }
}

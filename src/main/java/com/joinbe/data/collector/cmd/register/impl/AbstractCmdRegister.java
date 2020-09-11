package com.joinbe.data.collector.cmd.register.impl;
import cn.hutool.core.util.StrUtil;

public abstract class AbstractCmdRegister {
    /**
     * default password
     */
    protected static final String PASSWORD = "0000";
    /**
     * cmd head
     */
    protected static final String CMD_HEAD = "$AVM";
    /**
     * cmd end
     */
    protected static final String CMD_END = "\r\n";

    /**
     * 获取事件code
     *
     * @return string
     */
    abstract String getEvent();

    protected StringBuffer initCmdHeader() {
        StringBuffer cmdBuild = new StringBuffer();
        cmdBuild.append(CMD_HEAD)
                .append(StrUtil.C_COMMA)
                .append(getEvent())
                .append(StrUtil.C_COMMA)
                .append(PASSWORD);
        return cmdBuild;
    }
}

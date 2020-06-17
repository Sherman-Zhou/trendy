package com.joinbe.data.collector;

import cn.hutool.core.util.StrUtil;
import com.joinbe.data.collector.cmd.factory.CmdRegisterFactory;
import com.joinbe.data.collector.cmd.register.Cmd;
import com.joinbe.data.collector.cmd.register.impl.LockCmd;
import com.joinbe.data.collector.cmd.register.impl.SetBatteryCmd;
import com.joinbe.data.collector.cmd.register.impl.SetUserEventCmd;
import com.joinbe.data.collector.netty.handler.ServerHandler;
import com.joinbe.data.collector.netty.protocol.code.EventEnum;
import com.joinbe.web.rest.errors.BadRequestAlertException;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.HashMap;

@RestController
@RequestMapping("/api/internal/equipment")
public class EquipmentController {
    private final Logger logger = LoggerFactory.getLogger(EquipmentController.class);
    @Autowired
    ServerHandler serverHandler;

    @Autowired
    CmdRegisterFactory factory;

    /**
     *
     * @param deviceId
     * @return
     */
    @GetMapping("/setUserEvent")
    public String setUserEvent(@RequestParam String deviceId) {
        HashMap<String, String> params = new HashMap<>(16);
        params.put(SetUserEventCmd.KEY_USER_ID, "100");
        params.put(SetUserEventCmd.KEY_ACTION, "3");
        params.put(SetUserEventCmd.KEY_SMS_VIP_MASK, "1");
        params.put(SetUserEventCmd.KEY_OUTPUT_CTRL_ID, "8");
        params.put(SetUserEventCmd.KEY_INPUT_MASK, "2");
        params.put(SetUserEventCmd.KEY_INPUT_CTRL, "2");
        params.put(SetUserEventCmd.KEY_SCHEDULE_ID, "0");
        params.put(SetUserEventCmd.KEY_ZONE_CTRL_ID, "0");
        params.put(SetUserEventCmd.KEY_EVENT_ID, "0");

        Cmd cmd = factory.createInstance(EventEnum.SEVT.getEvent());
        if (cmd == null) {
            return "";
        }
        String str = cmd.initCmd(params);
        logger.debug("REST request for setUserEvent, command: {}", str);
        serverHandler.sendMessage(deviceId, str);
        return str;
    }

    /**
     *
     * @param deviceId
     * @param mode
     * @return
     */
    @GetMapping("/setBatteryEvent")
    public String setBatteryEvent(@RequestParam String deviceId, @RequestParam String mode) {
        HashMap<String, String> params = new HashMap<>(8);
        params.put(SetBatteryCmd.KEY_MODE, mode);

        Cmd cmd = factory.createInstance(EventEnum.SBAT.getEvent());
        if (cmd == null) {
            return "";
        }
        String str = cmd.initCmd(params);
        logger.debug("REST request for setBatteryEvent, command: {}", str);
        serverHandler.sendMessage(deviceId, str);
        return str;
    }

    /**
     *
     * @param deviceId
     * @param mode
     * @return
     */
    @GetMapping("/lock")
    public String lock(@RequestParam String deviceId, @RequestParam String mode) {
        HashMap<String, String> params = new HashMap<>(8);
        if ("open".equalsIgnoreCase(mode)) {
            params.put(LockCmd.KEY_OUTPUT_NUM, "2");
        } else {
            params.put(LockCmd.KEY_OUTPUT_NUM, "3");
        }
        params.put(LockCmd.KEY_OUTPUT_STATUS, "1");
        params.put(LockCmd.KEY_OUTPUT_ON_DURATION, "10");
        params.put(LockCmd.KEY_OUTPUT_OFF_DURATION, "10");
        params.put(LockCmd.KEY_OUTPUT_TIMES, "2");
        Cmd cmd = factory.createInstance(EventEnum.SGPO.getEvent());
        if (cmd == null) {
            return "Unimplemented command.";
        }
        String str = cmd.initCmd(params);
        logger.debug("REST request for lock/unlock, command: {}", str);
        serverHandler.sendMessage(deviceId, str);
        return str;
    }

    /**
     *
     * @param deviceId
     * @return
     */
    /*@GetMapping("/location")
    public DeferredResult<Object> getLocation(@RequestParam String deviceId) {
        HashMap<String, String> params = new HashMap<>(8);
        Cmd cmd = factory.createInstance(EventEnum.GPOS.getEvent());
        if (cmd == null) {
            throw new BadRequestAlertException("Unimplemented command", "equipmentCmd", "invalidCmd");
        }

        DeferredResult<Object> deferredResult = new DeferredResult<>(3000L, "Get Location time out");
        String str = cmd.initCmd(params);
        logger.debug("REST request for get location, command: {}", str);
        serverHandler.sendMessage(deviceId, str, deferredResult);

        return deferredResult;
    }*/
}

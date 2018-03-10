package cn.mageek.NetServer.vendor;

import cn.mageek.NetServer.handler.ClientHandler;
import cn.mageek.NetServer.pojo.WebMsgObject;
import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * @author Mageek Chiu
 * @date 2018/3/10 0010:19:37
 */
public class FastJSONTest {
    private static final Logger logger = LoggerFactory.getLogger(FastJSONTest.class);

    @Test
    public void objectToJSON() throws Exception {
        WebMsgObject webMsgObject = new WebMsgObject();
        webMsgObject.setClientId("00000000000000e0-00003578-00000001-98222fbc86d5659e-738d5a1a");
        webMsgObject.setCommand("81");
        webMsgObject.setData(new HashMap<>());
        webMsgObject.setRef("232323213");

        String webMsgString = JSON.toJSONString(webMsgObject);
        logger.debug("\n publish webMsg '"+webMsgString+"'");
//        测试web端推送的handler执行顺序
//        publish webMsg '{"clientId":"00000000000000e0-00004864-00000001-ad369ea559d01c55-44d3512e","command":"81","data":{},"ref":"232323213"}'
    }

    @Test
    public void JSONToObject() throws Exception {

    }

}
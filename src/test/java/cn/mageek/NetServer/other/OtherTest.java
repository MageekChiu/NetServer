package cn.mageek.NetServer.other;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * @author Mageek Chiu
 * @date 2018/3/10 0010:19:37
 */
public class OtherTest {
    private static final Logger logger = LoggerFactory.getLogger(OtherTest.class);

    @Test
    public void OtherTest() {
        String ab = "sadasad";
        logger.info("res:{}",ab.indexOf("ad"));

//        int a = 3;Integer b =3;Integer c=new Integer(3);
//        System.out.println(a==b);
//        System.out.println(b==c);

        long a =5;int b = 3;int c = 5;
        System.out.println(Math.ceil(a/b)+","+Math.ceil(c/b));
        System.out.println(Math.ceil((double)a/b)+","+Math.ceil(c/b));

    }
}
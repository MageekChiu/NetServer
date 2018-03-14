package cn.mageek.NetServer.other;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Mageek Chiu
 * @date 2018/3/10 0010:19:37
 */
public class SingletonTest {
    private static final Logger logger = LoggerFactory.getLogger(SingletonTest.class);

    @Test
    public void singletonestTest() {

        for (int i=0;i<8;i++)
            new Thread(new printSingle(),"Thread:"+i).start();
    }

    class printSingle implements  Runnable{
        @Override
        public void run() {
            Singleton.getSingleton().doSomething();
//            new Singleton().doSomething();
        }
    }


}
package cn.mageek.NetServer.res;

import cn.mageek.NetServer.command.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.print.URIException;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Command 工厂类
 * @author Mageek Chiu
 * @date 2018/3/13 0013:21:49
 */
public class CommandFactory {
    private static String packageDir = "cn/mageek/NetServer/command";
    private static String packagePrefix = "cn.mageek.NetServer.command.";
    private static final Logger logger = LoggerFactory.getLogger(CommandFactory.class);

    private static volatile Map<String,Command> commandMap ;

    public static void construct() throws Exception {
        if(commandMap==null){//volatile+双重检查来实现单例模式
            synchronized (CommandFactory.class){
                if (commandMap==null){
                    commandMap = new ConcurrentHashMap<>();
                    // Command 池 如果初始化不成功 整个程序就无法正常运转，所以不用try catch, 直接采用快速失败原则
                    getAllCommands(commandMap);
                    logger.info("Command pool initialized, number : {}",commandMap.size());
                }
            }
        }
    }

    public static Command getCommand(String commandId){
        return commandMap.get(commandId);
    }

    public static void destruct(){
        commandMap = null;
    }

    private static void getAllCommands(Map<String,Command> commandMap) throws Exception {
        Class clazz = Command.class;
        ClassLoader loader = clazz.getClassLoader();
        URL url = loader.getResource(packageDir);
        URI uri = url.toURI();
        File file = new File(uri);
        File[] files = file.listFiles();
        for (File f : files) {
            String fName = f.getName();
            fName = fName.substring(0, fName.length() - 6);//Command,Command11,Command81
            if(fName.length()>7){
                String commandId = fName.substring(7);
                String className = packagePrefix + fName ;
                clazz = Class.forName(className);
                logger.debug("Command class found: {},commandId: {}",clazz.getName(),commandId);
                commandMap.put(commandId,(Command)clazz.newInstance());
            }
        }
    }
}

package cn.mageek.NetServer.res;

import cn.mageek.NetServer.command.Command;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.print.URIException;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.Buffer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

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

//    private static void getAllCommands(Map<String,Command> commandMap) throws Exception {
//        Class clazz = Command.class;
//        ClassLoader loader = clazz.getClassLoader();
//        URL url = loader.getResource(packageDir);// 这个方式打包过后就不能用了，因为打包完后jar是一个整体的文件
//        URI uri = url.toURI();
//        File file = new File(uri);
//        File[] files = file.listFiles();
//        for (File f : files) {
//            String fName = f.getName();
//            fName = fName.substring(0, fName.length() - 6);//Command,Command11,Command81
//            if(fName.length()>7){
//                String commandId = fName.substring(7);
//                String className = packagePrefix + fName ;
//                clazz = Class.forName(className);
//                logger.debug("Command class found: {},commandId: {}",clazz.getName(),commandId);
//                commandMap.put(commandId,(Command)clazz.newInstance());
//            }
//        }
//    }

    private static void getAllCommands(Map<String,Command> commandMap) throws Exception {

        Reflections reflections = new Reflections(packagePrefix);

        Set<Class<? extends Command>> subTypes = reflections.getSubTypesOf(Command.class);

        int idStart = packagePrefix.length()+7;
        for(Class clazz : subTypes){
            String className = clazz.getName();
            String commandId = className.substring(idStart);
            logger.debug("Command class found: {} , Id: {}",clazz.getName(),commandId);
            commandMap.put(commandId,(Command)clazz.newInstance());
        }
    }

}

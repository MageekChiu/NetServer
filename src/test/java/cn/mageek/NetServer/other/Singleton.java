package cn.mageek.NetServer.other;

/**
 * @author Mageek Chiu
 * @date 2018/3/13 0013:22:02
 */

// 饿汉 基于classloder机制避免了多线程的同步问题
public class Singleton {

    private static final Singleton singleton = new Singleton();
    private Singleton(){ }
//    public Singleton(){ }

    public static Singleton getSingleton(){
        return singleton;
    }
    public void doSomething(){
//        System.out.println(Thread.currentThread().getName()+singleton.toString());
        System.out.println(Thread.currentThread().getName()+"_"+this.toString()+"_1_"+System.nanoTime());
        System.out.println(Thread.currentThread().getName()+"_"+this.toString()+"_2_"+System.nanoTime());
        System.out.println(Thread.currentThread().getName()+"_"+this.toString()+"_3_"+System.nanoTime());
    }
}
// 静态内部类 基于classloder机制避免了多线程的同步问题
//public class Singleton {
//    private static class SingletonHolder{
//        private static final SingletonDemo5 instance = new SingletonDemo5();
//    }
//    private Singleton(){}
//    public static final Singleton getInsatance(){
//        return SingletonHolder.instance;
//    }
//}

// 双重校验
//public class Singleton {
//    private volatile static Singleton singleton;
//    private Singleton(){}
//    public static Singleton getSingleton(){
//        if (singleton == null) {
//            synchronized (Singleton.class) {
//                if (singleton == null) {
//                    singleton = new Singleton();
//                }
//            }
//        }
//        return singleton;
//    }
//}
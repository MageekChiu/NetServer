package cn.mageek.NetServer.other;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Mageek Chiu
 * @date 2018/3/10 0010:19:37
 */
public class DeadLockTest {
    private static final Logger logger = LoggerFactory.getLogger(DeadLockTest.class);

    private static final String a ="a";
    private static final String b ="b";

    @Test
    public void deadLockTest() throws InterruptedException {

        Thread t1 = new Thread(() -> {
            synchronized (a){
                System.out.println(Thread.currentThread().getName()+"get a");
                synchronized (b){
                    System.out.println(Thread.currentThread().getName()+"get b");
                }
            }
        },"t1");
        Thread t2 = new Thread(() -> {
            synchronized (b){
                System.out.println(Thread.currentThread().getName()+"get b");
                synchronized (a){
                    System.out.println(Thread.currentThread().getName()+"get a");
                }
            }
        },"t2");
        t1.start();t2.start();
        t1.join();t2.join();
        System.out.println(Thread.currentThread().getName()+":done");
    }

//    这个程序偶尔会产生死锁，此时用jps 配合 jstack 呢个

//    Microsoft Windows [版本 10.0.17134.165]
//            (c) 2018 Microsoft Corporation。保留所有权利。
//
//   jps
//11984 JUnitStarter
//9456
//        13076 Launcher
//13268 RemoteMavenServer
//11864 Jps
//
//    jstack -l 11984
//            2018-07-14 22:52:29
//    Full thread dump Java HotSpot(TM) 64-Bit Server VM (25.121-b13 mixed mode):
//
//            "t2" #12 prio=5 os_prio=0 tid=0x000000001c534000 nid=0x18a0 waiting for monitor entry [0x000000001cd2f000]
//    java.lang.Thread.State: BLOCKED (on object monitor)
//    at cn.mageek.NetServer.other.DeadLockTest.lambda$deadLockTest$1(DeadLockTest.java:32)
//            - waiting to lock <0x0000000781e25640> (a java.lang.String)
//            - locked <0x0000000781e25670> (a java.lang.String)
//    at cn.mageek.NetServer.other.DeadLockTest$$Lambda$2/184966243.run(Unknown Source)
//    at java.lang.Thread.run(Thread.java:745)
//
//    Locked ownable synchronizers:
//            - None
//
//"t1" #11 prio=5 os_prio=0 tid=0x000000001c533800 nid=0x9cc waiting for monitor entry [0x000000001cc2f000]
//    java.lang.Thread.State: BLOCKED (on object monitor)
//    at cn.mageek.NetServer.other.DeadLockTest.lambda$deadLockTest$0(DeadLockTest.java:24)
//            - waiting to lock <0x0000000781e25670> (a java.lang.String)
//            - locked <0x0000000781e25640> (a java.lang.String)
//    at cn.mageek.NetServer.other.DeadLockTest$$Lambda$1/1286084959.run(Unknown Source)
//    at java.lang.Thread.run(Thread.java:745)
//
//    Locked ownable synchronizers:
//            - None
//
//"Service Thread" #10 daemon prio=9 os_prio=0 tid=0x000000001b732000 nid=0x3150 runnable [0x0000000000000000]
//    java.lang.Thread.State: RUNNABLE
//
//    Locked ownable synchronizers:
//            - None
//
//"C1 CompilerThread2" #9 daemon prio=9 os_prio=2 tid=0x000000001b71b000 nid=0x3874 waiting on condition [0x0000000000000000]
//    java.lang.Thread.State: RUNNABLE
//
//    Locked ownable synchronizers:
//            - None
//
//"C2 CompilerThread1" #8 daemon prio=9 os_prio=2 tid=0x000000001b6ae000 nid=0x2024 waiting on condition [0x0000000000000000]
//    java.lang.Thread.State: RUNNABLE
//
//    Locked ownable synchronizers:
//            - None
//
//"C2 CompilerThread0" #7 daemon prio=9 os_prio=2 tid=0x000000001b6ad000 nid=0x1e64 waiting on condition [0x0000000000000000]
//    java.lang.Thread.State: RUNNABLE
//
//    Locked ownable synchronizers:
//            - None
//
//"Monitor Ctrl-Break" #6 daemon prio=5 os_prio=0 tid=0x000000001a37d800 nid=0x1044 runnable [0x000000001bd7e000]
//    java.lang.Thread.State: RUNNABLE
//    at java.net.SocketInputStream.socketRead0(Native Method)
//    at java.net.SocketInputStream.socketRead(SocketInputStream.java:116)
//    at java.net.SocketInputStream.read(SocketInputStream.java:171)
//    at java.net.SocketInputStream.read(SocketInputStream.java:141)
//    at sun.nio.cs.StreamDecoder.readBytes(StreamDecoder.java:284)
//    at sun.nio.cs.StreamDecoder.implRead(StreamDecoder.java:326)
//    at sun.nio.cs.StreamDecoder.read(StreamDecoder.java:178)
//            - locked <0x0000000781ba6240> (a java.io.InputStreamReader)
//    at java.io.InputStreamReader.read(InputStreamReader.java:184)
//    at java.io.BufferedReader.fill(BufferedReader.java:161)
//    at java.io.BufferedReader.readLine(BufferedReader.java:324)
//            - locked <0x0000000781ba6240> (a java.io.InputStreamReader)
//    at java.io.BufferedReader.readLine(BufferedReader.java:389)
//    at com.intellij.rt.execution.application.AppMainV2$1.run(AppMainV2.java:64)
//
//    Locked ownable synchronizers:
//            - None
//
//"Attach Listener" #5 daemon prio=5 os_prio=2 tid=0x000000001b690800 nid=0x48c waiting on condition [0x0000000000000000]
//    java.lang.Thread.State: RUNNABLE
//
//    Locked ownable synchronizers:
//            - None
//
//"Signal Dispatcher" #4 daemon prio=9 os_prio=2 tid=0x000000000346e800 nid=0x3418 runnable [0x0000000000000000]
//    java.lang.Thread.State: RUNNABLE
//
//    Locked ownable synchronizers:
//            - None
//
//"Finalizer" #3 daemon prio=8 os_prio=1 tid=0x0000000003468800 nid=0x2fb0 in Object.wait() [0x000000001b67e000]
//    java.lang.Thread.State: WAITING (on object monitor)
//    at java.lang.Object.wait(Native Method)
//            - waiting on <0x0000000781988ec8> (a java.lang.ref.ReferenceQueue$Lock)
//    at java.lang.ref.ReferenceQueue.remove(ReferenceQueue.java:143)
//            - locked <0x0000000781988ec8> (a java.lang.ref.ReferenceQueue$Lock)
//    at java.lang.ref.ReferenceQueue.remove(ReferenceQueue.java:164)
//    at java.lang.ref.Finalizer$FinalizerThread.run(Finalizer.java:209)
//
//    Locked ownable synchronizers:
//            - None
//
//"Reference Handler" #2 daemon prio=10 os_prio=2 tid=0x0000000003463000 nid=0x26d8 in Object.wait() [0x000000001b57f000]
//    java.lang.Thread.State: WAITING (on object monitor)
//    at java.lang.Object.wait(Native Method)
//            - waiting on <0x0000000781986b68> (a java.lang.ref.Reference$Lock)
//    at java.lang.Object.wait(Object.java:502)
//    at java.lang.ref.Reference.tryHandlePending(Reference.java:191)
//            - locked <0x0000000781986b68> (a java.lang.ref.Reference$Lock)
//    at java.lang.ref.Reference$ReferenceHandler.run(Reference.java:153)
//
//    Locked ownable synchronizers:
//            - None
//
//"main" #1 prio=5 os_prio=0 tid=0x0000000003372800 nid=0x29ac in Object.wait() [0x0000000002dce000]
//    java.lang.Thread.State: WAITING (on object monitor)
//    at java.lang.Object.wait(Native Method)
//            - waiting on <0x00000007824c8258> (a java.lang.Thread)
//    at java.lang.Thread.join(Thread.java:1249)
//            - locked <0x00000007824c8258> (a java.lang.Thread)
//    at java.lang.Thread.join(Thread.java:1323)
//    at cn.mageek.NetServer.other.DeadLockTest.deadLockTest(DeadLockTest.java:37)
//    at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
//    at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
//    at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
//    at java.lang.reflect.Method.invoke(Method.java:498)
//    at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:45)
//    at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:15)
//    at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:42)
//    at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:20)
//    at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:263)
//    at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:68)
//    at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:47)
//    at org.junit.runners.ParentRunner$3.run(ParentRunner.java:231)
//    at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:60)
//    at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:229)
//    at org.junit.runners.ParentRunner.access$000(ParentRunner.java:50)
//    at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:222)
//    at org.junit.runners.ParentRunner.run(ParentRunner.java:300)
//    at org.junit.runner.JUnitCore.run(JUnitCore.java:157)
//    at com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:68)
//    at com.intellij.rt.execution.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:47)
//    at com.intellij.rt.execution.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:242)
//    at com.intellij.rt.execution.junit.JUnitStarter.main(JUnitStarter.java:70)
//
//    Locked ownable synchronizers:
//            - None
//
//"VM Thread" os_prio=2 tid=0x000000001a2e6000 nid=0x2254 runnable
//
//"GC task thread#0 (ParallelGC)" os_prio=0 tid=0x0000000003388800 nid=0x1738 runnable
//
//"GC task thread#1 (ParallelGC)" os_prio=0 tid=0x000000000338a000 nid=0x214c runnable
//
//"GC task thread#2 (ParallelGC)" os_prio=0 tid=0x000000000338b800 nid=0x3928 runnable
//
//"GC task thread#3 (ParallelGC)" os_prio=0 tid=0x000000000338d000 nid=0x33b4 runnable
//
//"VM Periodic Task Thread" os_prio=2 tid=0x000000001b785000 nid=0x3494 waiting on condition
//
//    JNI global references: 343
//
//
//    Found one Java-level deadlock:
//            =============================
//            "t2":
//    waiting to lock monitor 0x000000001a3105c8 (object 0x0000000781e25640, a java.lang.String),
//    which is held by "t1"
//            "t1":
//    waiting to lock monitor 0x000000001a310728 (object 0x0000000781e25670, a java.lang.String),
//    which is held by "t2"
//
//    Java stack information for the threads listed above:
//            ===================================================
//            "t2":
//    at cn.mageek.NetServer.other.DeadLockTest.lambda$deadLockTest$1(DeadLockTest.java:32)
//            - waiting to lock <0x0000000781e25640> (a java.lang.String)
//            - locked <0x0000000781e25670> (a java.lang.String)
//    at cn.mageek.NetServer.other.DeadLockTest$$Lambda$2/184966243.run(Unknown Source)
//    at java.lang.Thread.run(Thread.java:745)
//            "t1":
//    at cn.mageek.NetServer.other.DeadLockTest.lambda$deadLockTest$0(DeadLockTest.java:24)
//            - waiting to lock <0x0000000781e25670> (a java.lang.String)
//            - locked <0x0000000781e25640> (a java.lang.String)
//    at cn.mageek.NetServer.other.DeadLockTest$$Lambda$1/1286084959.run(Unknown Source)
//    at java.lang.Thread.run(Thread.java:745)
//
//    Found 1 deadlock.
//
//


}
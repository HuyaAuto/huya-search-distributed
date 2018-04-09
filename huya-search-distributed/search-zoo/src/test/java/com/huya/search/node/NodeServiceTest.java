package com.huya.search.node;

import com.huya.search.settings.ImmutableSettings;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhangyiqun1@yy.com on 2017/10/17.
 */
public class NodeServiceTest {

//    @Test
//    public void checkMasterTest() {
//        Map<String, Object> set1 = new HashMap<>();
//        Map<String, Object> set2 = new HashMap<>();
//        Map<String, Object> set3 = new HashMap<>();
//
//        set1.put("zkHostPort", "localhost:2181");
//        set1.put("servicePort", "30001");
//
//        set2.put("zkHostPort", "localhost:2181");
//        set2.put("servicePort", "30002");
//
//        set3.put("zkHostPort", "localhost:2181");
//        set3.put("servicePort", "30003");
//
//        RealNodeService service1 = new RealNodeService(new NodeTestSettings(set1, this.getClass().getClassLoader()));
//        RealNodeService service2 = new RealNodeService(new NodeTestSettings(set2, this.getClass().getClassLoader()));
//        RealNodeService service3 = new RealNodeService(new NodeTestSettings(set3, this.getClass().getClassLoader()));
//
//        service1.start();
//        service2.start();
//        service3.start();
//        try {
//            TimeUnit.SECONDS.sleep(10);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        System.out.println(service1.getMasterStates());
//        System.out.println(service2.getMasterStates());
//        System.out.println(service3.getMasterStates());
//
//        try {
//            TimeUnit.SECONDS.sleep(60);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        System.out.println(service1.getMasterStates());
//        System.out.println(service2.getMasterStates());
//        System.out.println(service3.getMasterStates());
//    }
//
//
//    class NodeTestSettings extends ImmutableSettings {
//
//        NodeTestSettings(Map<String, Object> settings, ClassLoader classLoader) {
//            super(settings, classLoader);
//        }
//    }
}

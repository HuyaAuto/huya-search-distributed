package com.huya.facing.convert;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Files;

/**
 * @author ZhangXueJun
 * @date 2018年03月26日
 */
public class FileChangeListenerTest {

    public static void main(String[] args) throws Exception {

        File file = new File("E:/other/huya-search/huya-search-distributed/facing-kafka/src/test/java/com/huya/facing/convert");
        FileAlterationObserver filealtertionObserver=new FileAlterationObserver(file, new FileChangeListenerTest.MyFileFilter());
        filealtertionObserver.addListener(new FileAlterationListenerAdaptor() {
            @Override
            public void onFileChange(File file) {
                super.onFileChange(file);
                System.out.println("文件变了!");
            }
        });

        FileAlterationMonitor filealterationMonitor=new FileAlterationMonitor(1000);
        filealterationMonitor.addObserver(filealtertionObserver);
        filealterationMonitor.start();
    }

    static class MyFileFilter implements FileFilter {

        @Override
        public boolean accept(File pathname) {
            if (pathname.getName().equals("test.sql")) {
                return true;
            }
            return false;
        }
    }
}

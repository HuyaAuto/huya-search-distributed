package com.huya.search.util;

import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;

/**
 * @author ZhangXueJun
 * @date 2018年03月26日
 */
public class FileAlterationUtil {

    private final static Logger LOG = LoggerFactory.getLogger(FileAlterationUtil.class);

    public interface callback {
        void callback();
    }

    public static final void monitorFileChange(String filterName, callback callback) throws Exception {
        File file = new File("conf");
        LOG.info("监听文件修改程序路径:" + file.getCanonicalPath());
        FileAlterationObserver observer = new FileAlterationObserver(file, pathname -> {
            if (pathname.getName().equals(filterName)) {
                return true;
            }
            return false;
        });
        observer.addListener(new FileAlterationListenerAdaptor() {
            @Override
            public void onFileChange(File file) {
                LOG.info("监听到文件[" + file.getName() + "]变更!");
                callback.callback();
            }
        });

        FileAlterationMonitor monitor = new FileAlterationMonitor(1000);
        monitor.addObserver(observer);
        monitor.start();
    }

}

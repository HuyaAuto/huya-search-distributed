package com.huya.search.index.lucene;

import com.huya.search.service.LifecycleListener;
import com.huya.search.util.PathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class RemoveHdfsListener extends LifecycleListener {

    public static final Logger LOG = LoggerFactory.getLogger(RemoveHdfsListener.class);

    private String table;

    public RemoveHdfsListener(String table) {
        this.table = table;
    }

    @Override
    public void afterClose() {
        try {
            removeHDFS();
        } catch (IOException | InterruptedException e) {
            LOG.error("removeHdfs error", e);
        }
    }

    private void removeHDFS() throws IOException, InterruptedException {
        Process p = Runtime.getRuntime().exec("hadoop fs -rm -r " +
                PathUtil.separator + LazyFileSystem.BASE_PATH + PathUtil.separator +
                table
        );
        String info = getStream(p.getInputStream());
        String error = getStream(p.getErrorStream());
        if (info.length() > 0) LOG.info(info);
        if (error.length() > 0) LOG.error(error);
        p.waitFor();
        p.destroy();
    }

    private String getStream(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder builder = new StringBuilder();
        String line;
        while((line = reader.readLine())!= null){
            builder.append(line).append("\n");
        }
        is.close();
        reader.close();
        return builder.toString();
    }
}

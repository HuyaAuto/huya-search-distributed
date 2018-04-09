package com.huya.search.index.lucene;

import org.junit.Test;

public class RemoveHdfsTest {

    @Test
    public void removeTest() {
        RemoveHdfsListener listener = new RemoveHdfsListener("test");
        listener.afterClose();
    }

}

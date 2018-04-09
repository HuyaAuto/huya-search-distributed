package com.huya.search.index.opeation;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.huya.search.index.meta.MetaService;
import com.huya.search.index.meta.RealMetaService;
import com.huya.search.index.meta.util.JsonMetaUtil;
import com.huya.search.inject.ModulesBuilder;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Created by zhangyiqun1@yy.com on 2017/10/27.
 */
public class KyroTranQueryContextTest {

    private static final String TEST_TABLE = "{\n" +
            "    \"table\" : \"test\",\n" +
            "    \"grain\" : 1,\n" +
            "    \"accuracy\" : 0.0000000001,\n" +
            "    \"2017-08-15 10:00:00\" : {\n" +
            "        \"level\" : {\n" +
            "            \"type\" : \"integer\",\n" +
            "            \"sort\" : \"true\"\n" +
            "        },\n" +
            "        \"content\" : {\n" +
            "            \"type\" : \"text\",\n" +
            "            \"sort\" : \"false\"\n" +
            "        },\n" +
            "        \"keyWord\" : {\n" +
            "            \"type\" : \"string\",\n" +
            "            \"sort\" : \"true\"\n" +
            "        },\n" +
            "        \"num\" : {\n" +
            "            \"type\" : \"float\",\n" +
            "            \"sort\" : \"true\"\n" +
            "        }\n" +
            "    }\n" +
            "}";

    @Test
    public void tranTest() {
        ModulesBuilder modules = ModulesBuilder.getInstance();
        modules.add(new MetaModule());
        MetaService metaService = ModulesBuilder.getInstance().createInjector().getInstance(MetaService.class);
        metaService.start();
        metaService.add(JsonMetaUtil.createTimelineMetaDefineFromStr(TEST_TABLE));
        QueryContext queryContext = OperationFactory.getQueryContext("select * from test where pr = '2017-10-10 20' limit 100");
        Kryo kryo = new Kryo();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Output output = new Output(byteArrayOutputStream);
        kryo.writeObject(output, queryContext);
        output.close();

        byte[] bytes = byteArrayOutputStream.toByteArray();

        System.out.println(bytes.length);
        Input input = new Input(new ByteArrayInputStream(bytes));
        DSLDefaultQueryContext kryoQueryContext = kryo.readObject(input, DSLDefaultQueryContext.class);
        input.close();
    }

    class MetaModule extends AbstractModule {

        @Override
        protected void configure() {
            bind(MetaService.class).to(RealMetaService.class).in(Singleton.class);
        }
    }
}

package com.huya.search.rpc;

import com.google.common.collect.Lists;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.*;

/**
 * java -jar avro-tools-1.8.2.jar compile schema user.avsc .
 *
 * @author ZhangXueJun
 * @date 2018年03月17日
 */
public class AvroProtocolTest {

    private static final String SAVE_PATH = "users.avro";

    private List<User> getUserList() {
        User user1 = new User();
        user1.setName("Alyssa");
        user1.setFavoriteNumber(256);
// Leave favorite color null

// Alternate constructor
        User user2 = new User("Ben", 7, "red");

// Construct via builder
        User user3 = User.newBuilder()
                .setName("Charlie")
                .setFavoriteColor("blue")
                .setFavoriteNumber(null)
                .build();
        return Lists.newArrayList(user1, user2, user3);
    }

    @Test
    public void serialize() throws Exception {
        List<User> userList = getUserList();

        // serialize user1, user2 and user3 to disk
        DatumWriter<User> userDatumWriter = new SpecificDatumWriter<User>(User.class);
        DataFileWriter<User> dataFileWriter = new DataFileWriter<User>(userDatumWriter);
        dataFileWriter.create(userList.iterator().next().getSchema(), new File(SAVE_PATH));
        dataFileWriter.append(userList.get(0));
        dataFileWriter.append(userList.get(1));
        dataFileWriter.append(userList.get(2));
        dataFileWriter.close();
    }

    @Test
    public void deserialize() throws Exception {
// Deserialize Users from disk
        DatumReader<User> userDatumReader = new SpecificDatumReader<User>(User.class);
        DataFileReader<User> dataFileReader = new DataFileReader<User>(new File(SAVE_PATH), userDatumReader);
        User user = null;
        while (dataFileReader.hasNext()) {
// Reuse user object by passing it to next(). This saves us from
// allocating and garbage collecting many objects for files with
// many items.
            user = dataFileReader.next(user);
            System.out.println(user);
        }
    }

}
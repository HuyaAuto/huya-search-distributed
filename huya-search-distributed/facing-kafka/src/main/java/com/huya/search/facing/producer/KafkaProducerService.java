package com.huya.search.facing.producer;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.huya.search.SearchException;
import com.huya.search.settings.Settings;
import org.apache.commons.cli.ParseException;

import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

@Singleton
public class KafkaProducerService extends ProducerService {

    private ProducerCommand command;

    private Properties properties;

    @Inject
    public KafkaProducerService(ProducerCommand command) {
        this.command = command;
    }

    @Override
    protected Properties getProducerSettings() {
        if (properties == null) {
            properties = settings.asProperties();
        }
        return properties;
    }

    @Override
    protected void doStart() throws SearchException {
        System.out.print("> ");
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String lineStr = scanner.nextLine();
            try {
                command.parse(lineStr);

            } catch (ParseException e) {
                e.printStackTrace();
            }
            try {
                TimeUnit.MILLISECONDS.sleep(300);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            System.out.print("> ");
        }
    }

    @Override
    protected void doStop() throws SearchException {
        //do nothing
    }

    @Override
    protected void doClose() throws SearchException {
        //do nothing
    }

    @Override
    public String getName() {
        return "ProducerService";
    }


}

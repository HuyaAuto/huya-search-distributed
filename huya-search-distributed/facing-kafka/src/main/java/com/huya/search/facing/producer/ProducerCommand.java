package com.huya.search.facing.producer;

import org.apache.commons.cli.*;

/**
 * Created by zhangyiqun1@yy.com on 2018/3/10.
 */
public abstract class ProducerCommand {

    private Option help = new Option("help", "print this message");

    private Option produce = Option.builder("produce")
            .numberOfArgs(3)
            .argName("script> <table> <num")
            .desc("produce data to kafka")
            .build();


    private Options options = new Options()
            .addOption(help)
            .addOption(produce);

    private CommandLineParser commandLineParser = new DefaultParser();

    public ProducerCommand() {

    }


    public void parse(String lineStr) throws ParseException {
        String[] args = lineStr.split(" ");
        CommandLine commandLine = commandLineParser.parse(options, args);
        if (commandLine.hasOption(help.getOpt())) {
            doHelp();
        }
        else if (commandLine.hasOption(produce.getOpt())) {
            doProduce(commandLine.getOptionValues(produce.getOpt()));
        }
    }

    protected abstract void doProduce(String[] args);

    private void doHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("ProducerCommand", options);
    }

}

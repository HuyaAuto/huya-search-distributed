#!/usr/bin/env bash

cd `dirname $0`
cd ..

CURRENT_HOME=`pwd .`

# setting java env

# using the JAVA_HOME installed
JAVA_HOME=/home/hadoop/jdk1.8.0_131

echo "Using JAVA_HOME: $JAVA_HOME"

# setting proc env
# CLASSPATH=$CURRENT_HOME/lib/xx.jar
LIB_PATH=${CURRENT_HOME}/lib
CLASSPATH=$(find ${LIB_PATH} -name '*.jar' | xargs echo | tr ' ' ':')
CLASSPATH=${CLASSPATH}:${CURRENT_HOME}/conf
MAIN_CLASS=com.huya.search.rpc.RealRpcService
APP_NAME=distributed-huya-search
JAVA_OPTS="-Xms2048m -Xmx4096m -Dfile.encoding=UTF8"

#JAVA_OPTS="-Xms2048m -Xmx4096m -Dfile.encoding=UTF8"

echo "Running ${JAVA_HOME}/bin/java ${JAVA_OPTS} -Dapp=${APP_NAME} -cp ${CLASSPATH} ${MAIN_CLASS} $1 $2 $3"

${JAVA_HOME}/bin/java ${JAVA_OPTS} -Dapp=${APP_NAME} -cp ${CLASSPATH} ${MAIN_CLASS} $1 $2 $3

#nohup ${JAVA_HOME}/bin/java ${JAVA_OPTS} -Dapp=${APP_NAME} -cp ${CLASSPATH} ${MAIN_CLASS} $1 $2 $3 > /dev/null 2>&1 &

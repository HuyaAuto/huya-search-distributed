#!/usr/bin/env bash

cd `dirname $0`
cd ..

CURRENT_HOME=`pwd .`

# setting java env

# using the JAVA_HOME installed
JAVA_HOME=/home/huya-search/jdk1.8.0_131

echo "Using JAVA_HOME: $JAVA_HOME"

# setting proc env
# CLASSPATH=$CURRENT_HOME/lib/xx.jar
LIB_PATH=${CURRENT_HOME}/lib
CLASSPATH=$(find ${LIB_PATH} -name '*.jar' | xargs echo | tr ' ' ':')
CLASSPATH=${CLASSPATH}:${CURRENT_HOME}/conf
MAIN_CLASS=com.huya.search.HuyaSearchSalver
APP_NAME=distributed-huya-search-salver
JAVA_OPTS="-Xms30420m -Xmx30420m -Dfile.encoding=UTF8 -Djava.security.auth.login.config=/home/huya-search/workspace/conf/kafka_client_jaas.conf"

echo "Running ${JAVA_HOME}/bin/java ${JAVA_OPTS} -Dapp=${APP_NAME} -cp ${CLASSPATH} ${MAIN_CLASS} $1 $2 $3"

#${JAVA_HOME}/bin/java ${JAVA_OPTS} -Dapp=${APP_NAME} -cp ${CLASSPATH} ${MAIN_CLASS} $1 $2 $3

nohup ${JAVA_HOME}/bin/java ${JAVA_OPTS} -Dapp=${APP_NAME} -cp ${CLASSPATH} ${MAIN_CLASS} $1 $2 $3 > /dev/null 2>&1 &

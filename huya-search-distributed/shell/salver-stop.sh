#!/usr/bin/env bash

echo "check already starting distributed-huya-search-salver process"

if [ `ps -ef | grep ex | awk '{ if($13=="-Dapp=distributed-huya-search-salver") print $0 }' | wc -l` -lt 1 ]; then
    echo "No found distributed-huya-search-salver process"
else
    for OLD_PORT in `ps -ef | grep ex | awk '{ if($13=="-Dapp=distributed-huya-search-salver") print $2 }'`
    do
        echo "kill distributed-huya-search-salver process for port $OLD_PORT"
        kill -15 $OLD_PORT
    done
fi

echo "shutdown distributed-huya-search-salver process successfully"

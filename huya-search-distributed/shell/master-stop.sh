#!/usr/bin/env bash

echo "check already starting distributed-huya-search-master process"

if [ `ps -ef | grep ex | awk '{ if($13=="-Dapp=distributed-huya-search-master") print $0 }' | wc -l` -lt 1 ]; then
    echo "No found distributed-huya-search-master process"
else
    for OLD_PORT in `ps -ef | grep ex | awk '{ if($13=="-Dapp=distributed-huya-search-master") print $2 }'`
    do
        echo "kill distributed-huya-search-master process for port $OLD_PORT"
        kill -15 $OLD_PORT
    done
fi

echo "shutdown distributed-huya-search-master process successfully"

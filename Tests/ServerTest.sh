#!/bin/bash

echo Log\ time\ at\ $(date +"%T") >> httplog.txt

while read var
do
	echo `http GET 127.0.0.1:8080/wc?url=$var Accept:text/plain` >> httplog.txt &

done

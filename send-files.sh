#!/bin/bash

export AWS_PAGER="" 
for i in $(seq 1 10)
do
    aws s3 cp build.gradle "s3://hdm-iot/build-${i}".gradle
done

# EOF
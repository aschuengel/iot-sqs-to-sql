#!/bin/bhash 

export AWS_PAGER="" 
for i in $(seq 1 10)
do
    aws sqs send-message \
        --queue-url https://sqs.eu-central-1.amazonaws.com/804362744279/test \
        --message-body "{\"type\": \"alert\", \"cloudId\": \"1221\", \"date\": \"$(date -R)\", \"payload\": {\"text\": \"Test text $i\"}, \"comment\": \"Test message $i\"}" \
        --output json
done

# EOF
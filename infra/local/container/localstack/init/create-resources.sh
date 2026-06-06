#!/bin/sh
aws --endpoint-url=http://localstack:4566 sqs create-queue --queue-name user-events-queue
aws --endpoint-url=http://localstack:4566 sns create-topic --name user-events-topic

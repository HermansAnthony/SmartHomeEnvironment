#!/bin/bash
# This script will compile the protocol which was passed as an argument ($1)

java -jar lib/avro-tools-1.7.7.jar compile protocol $1 src/

#!/bin/bash

# Check if the correct number of arguments are provided
if [ $# -ne 6 ]; then
    echo "Usage: $0 <JAR_FILE> <SCRIPT_FILE> <REMOTE_USER> <REMOTE_HOST> <REMOTE_PATH> <RASPI_PASSWORD>"
    exit 1
fi

# Arguments passed from deploy_and_execute.sh
JAR_FILE=$1
SCRIPT_FILE=$2
REMOTE_USER=$3
REMOTE_HOST=$4
REMOTE_PATH=$5
RASPI_PASSWORD=$6

# Check if the JAR file exists
if [ ! -f "$JAR_FILE" ]; then
    echo "Error: JAR file $JAR_FILE not found!"
    exit 1
fi

# Check if the script file exists
if [ ! -f "$SCRIPT_FILE" ]; then
    echo "Error: Script file $SCRIPT_FILE not found!"
    exit 1
fi

# Transfer the JAR file
echo "Transferring $JAR_FILE to $REMOTE_USER@$REMOTE_HOST:$REMOTE_PATH"
sshpass -p "$RASPI_PASSWORD" scp "$JAR_FILE" "$REMOTE_USER@$REMOTE_HOST:$REMOTE_PATH"

if [ $? -eq 0 ]; then
    echo "JAR file transfer completed successfully."
else
    echo "Error during JAR file transfer."
    exit 1
fi

# Transfer the script file
echo "Transferring $SCRIPT_FILE to $REMOTE_USER@$REMOTE_HOST:$REMOTE_PATH"
sshpass -p "$RASPI_PASSWORD" scp "$SCRIPT_FILE" "$REMOTE_USER@$REMOTE_HOST:$REMOTE_PATH"

if [ $? -eq 0 ]; then
    echo "Script file transfer completed successfully."
else
    echo "Error during script file transfer."
    exit 1
fi

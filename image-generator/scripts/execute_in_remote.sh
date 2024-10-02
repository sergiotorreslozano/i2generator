#!/bin/bash

# Check if the correct number of arguments are provided
if [ $# -ne 6 ]; then
    echo "Usage: $0 <JAR_NAME> <REMOTE_PATH> <OPENAI_API_KEY> <REMOTE_USER> <REMOTE_HOST> <RASPI_PASSWORD>"
    exit 1
fi

# Arguments passed from deploy_and_execute.sh
JAR_NAME=$1
REMOTE_PATH=$2
OPENAI_API_KEY=$3
REMOTE_USER=$4
REMOTE_HOST=$5
RASPI_PASSWORD=$6

# Execute the script remotely
echo "Executing remote script on $REMOTE_USER@$REMOTE_HOST"

sshpass -p "$RASPI_PASSWORD" ssh "$REMOTE_USER@$REMOTE_HOST" \
  "cd $REMOTE_PATH && chmod +x ./run_image_generator.sh && ./run_image_generator.sh '$JAR_NAME' '$REMOTE_PATH' '$OPENAI_API_KEY'"

# Check if the remote execution was successful
if [ $? -eq 0 ]; then
    echo "Remote script execution completed successfully."
else
    echo "Error during remote script execution."
    exit 1
fi

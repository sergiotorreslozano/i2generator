#!/bin/bash

# Ensure the correct number of arguments are passed
if [ $# -ne 3 ]; then
    echo "Usage: $0 <JAR_NAME> <REMOTE_PATH> <OPENAI_API_KEY>"
    exit 1
fi

JAR_NAME=$1
REMOTE_PATH=$2
OPENAI_API_KEY=$3

# Load secrets (reuse from copy_to_remote-bk.sh)
SECRETS_FILE="../../secrets.properties"

# Function to load the secrets file
load_file() {
    local FILE_PATH="$1"
    if [ ! -f "$FILE_PATH" ]; then
        echo "Error: File $FILE_PATH not found!"
        exit 1
    fi
    source "$FILE_PATH"
}

load_file "$SECRETS_FILE"

# Check if password is loaded
if [ -z "$RASPI_PASSWORD" ]; then
    echo "Error: RASPI_PASSWORD not found in $SECRETS_FILE"
    exit 1
fi

# Execute the script remotely
echo "Executing remote script $SCRIPT_FILE on $REMOTE_USER@$REMOTE_HOST"

sshpass -p "$RASPI_PASSWORD" ssh "$REMOTE_USER@$REMOTE_HOST" \
  "cd $REMOTE_PATH && chmod +x ./run_image_generator.sh && ./run_image_generator.sh '$JAR_NAME' '$REMOTE_PATH' '$OPENAI_API_KEY'"

# Check if the remote execution was successful
if [ $? -eq 0 ]; then
    echo "Remote script execution completed successfully."
else
    echo "Error during remote script execution."
    exit 1
fi

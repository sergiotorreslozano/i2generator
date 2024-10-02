#!/bin/bash

# Function to load and check external files
load_file() {
    local FILE_PATH="$1"

    # Check if the file exists
    if [ ! -f "$FILE_PATH" ]; then
        echo "Error: File $FILE_PATH not found!"
        exit 1
    fi

    # Source the file to load its variables
    source "$FILE_PATH"
    echo "File $FILE_PATH loaded successfully."
}

# Paths to configuration files (one directory up)
CONFIG_FILE="../../config.properties"
SECRETS_FILE="../../secrets.properties"

# Load configuration and secrets using the reusable function
load_file "$CONFIG_FILE"
load_file "$SECRETS_FILE"

# Check if the password is loaded
if [ -z "$RASPI_PASSWORD" ]; then
    echo "Error: RASPI_PASSWORD not found in $SECRETS_FILE"
    exit 1
fi

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

# Execute SCP command to transfer the JAR file using sshpass
echo "Transferring $JAR_FILE to $REMOTE_USER@$REMOTE_HOST:$REMOTE_PATH"
sshpass -p "$RASPI_PASSWORD" scp "$JAR_FILE" "$REMOTE_USER@$REMOTE_HOST:$REMOTE_PATH"

# Check if SCP was successful for the JAR file
if [ $? -eq 0 ]; then
    echo "JAR file transfer completed successfully."
else
    echo "Error during JAR file transfer."
    exit 1
fi

# Execute SCP command to transfer the script file using sshpass
echo "Transferring $SCRIPT_FILE to $REMOTE_USER@$REMOTE_HOST:$REMOTE_PATH"
sshpass -p "$RASPI_PASSWORD" scp "$SCRIPT_FILE" "$REMOTE_USER@$REMOTE_HOST:$REMOTE_PATH"

# Check if SCP was successful for the script file
if [ $? -eq 0 ]; then
    echo "Script file transfer completed successfully."
else
    echo "Error during script file transfer."
    exit 1
fi

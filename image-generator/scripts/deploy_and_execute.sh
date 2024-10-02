#!/bin/bash

# Function to load and check external files
load_file() {
    local FILE_PATH="$1"

    if [ ! -f "$FILE_PATH" ]; then
        echo "Error: File $FILE_PATH not found!"
        exit 1
    fi
    source "$FILE_PATH"
    echo "File $FILE_PATH loaded successfully."
}

# Paths to configuration files (one directory up)
CONFIG_FILE="../../config.properties"
SECRETS_FILE="../../secrets.properties"

# Load configuration and secrets
load_file "$CONFIG_FILE"
load_file "$SECRETS_FILE"

# Ensure essential variables are loaded from secrets
if [ -z "$RASPI_PASSWORD" ]; then
    echo "Error: RASPI_PASSWORD not found in $SECRETS_FILE"
    exit 1
fi
if [ -z "$OPENAI_API_KEY" ]; then
    echo "Error: OPENAI_API_KEY not found in $SECRETS_FILE"
    exit 1
fi

# Ensure essential variables are loaded from config
if [ -z "$JAR_NAME" ]; then
    echo "Error: JAR_NAME not found in $CONFIG_FILE"
    exit 1
fi
if [ -z "$REMOTE_PATH" ]; then
    echo "Error: REMOTE_PATH not found in $CONFIG_FILE"
    exit 1
fi
if [ -z "$REMOTE_USER" ]; then
    echo "Error: REMOTE_USER not found in $CONFIG_FILE"
    exit 1
fi
if [ -z "$REMOTE_HOST" ]; then
    echo "Error: REMOTE_HOST not found in $CONFIG_FILE"
    exit 1
fi
if [ -z "$JAR_FILE" ]; then
    echo "Error: JAR_FILE not found in $CONFIG_FILE"
    exit 1
fi
if [ -z "$SCRIPT_FILE" ]; then
    echo "Error: SCRIPT_FILE not found in $CONFIG_FILE"
    exit 1
fi

# Step 1: Build the application if necessary
# Function to check if build flag is present
build_with_maven=false

if [[ "$1" == "--build" ]]; then
    build_with_maven=true
fi

# Call build.sh if the --build flag is set
if $build_with_maven; then
    echo "Building project with build.sh..."
    ./build.sh
    if [ $? -ne 0 ]; then
        echo "Build failed, aborting deployment!"
        exit 1
    fi
fi

# Step 2: Call copy_to_remote-bk.sh to transfer files
echo "Calling copy_to_remote.sh to transfer files..."
./copy_to_remote.sh "$JAR_FILE" "$SCRIPT_FILE" "$REMOTE_USER" "$REMOTE_HOST" "$REMOTE_PATH" "$RASPI_PASSWORD"

if [ $? -ne 0 ]; then
    echo "Error during file transfer, aborting!"
    exit 1
fi

# Step 3: Call execute_in_remote.sh to execute the script remotely
echo "Calling execute_in_remote.sh to execute the script remotely..."
./execute_in_remote.sh "$JAR_NAME" "$REMOTE_PATH" "$OPENAI_API_KEY" "$REMOTE_USER" "$REMOTE_HOST" "$RASPI_PASSWORD"

if [ $? -ne 0 ]; then
    echo "Error during remote script execution, aborting!"
    exit 1
fi

echo "Deployment and execution completed successfully."

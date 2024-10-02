#!/bin/bash

# Function to display the usage of the script
usage() {
    echo "Usage: $0 <JAR_NAME> <WORK_DIR> <OPENAI_API_KEY>"
    echo "  JAR_NAME     : The name of the JAR file to be executed."
    echo "  WORK_DIR     : The directory where the JAR file is located."
    echo "  OPENAI_API_KEY : The Open API key required for the application."
    echo ""
    echo "Example:"
    echo "  $0 image-generator.jar /home/user-dir/working-dir sk-aaaaabbbbb"
    exit 1
}

# Check if the correct number of arguments are provided
if [ $# -ne 3 ]; then
    echo "Error: Invalid number of arguments."
    usage
fi

# Use the passed arguments instead of config files
JAR_NAME=$1
WORK_DIR=$2
OPENAI_API_KEY=$3

# Check if essential arguments are provided
if [ -z "$JAR_NAME" ]; then
    echo "Error: JAR_NAME is not provided."
    usage
fi

if [ -z "$WORK_DIR" ]; then
    echo "Error: WORK_DIR is not provided."
    usage
fi

if [ -z "$OPENAI_API_KEY" ]; then
    echo "Error: OPENAI_API_KEY is not provided."
    usage
fi

# Log file location
LOG_FILE="$WORK_DIR/image-generator.log"

# Change to the working directory
cd "$WORK_DIR" || { echo "Error: Unable to navigate to $WORK_DIR"; exit 1; }

# Check if the JAR file exists
if [ ! -f "$JAR_NAME" ]; then
    echo "Error: JAR file $JAR_NAME not found in $WORK_DIR!"
    exit 1
fi

# Check if the process is already running by filtering exact JAR file path match
RUNNING_PIDS=$(ps -ef | grep "[j]ava.*$WORK_DIR/$JAR_NAME" | awk '{print $2}')

if [ -n "$RUNNING_PIDS" ]; then
    echo "Process $JAR_NAME is currently running with PIDs: $RUNNING_PIDS. Stopping it..."

    # Kill each running process individually
    for PID in $RUNNING_PIDS; do
        echo "Stopping process with PID $PID..."
        kill "$PID"
        sleep 2  # Allow time for the process to stop
    done

    # Wait up to 10 seconds for the process to terminate
    for i in {1..5}; do
        if ! ps -p $RUNNING_PIDS > /dev/null 2>&1; then
            echo "Process $JAR_NAME stopped successfully."
            break
        fi
        echo "Waiting for the process to stop..."
        sleep 2
    done

    # Force kill if still running after 10 seconds
    if ps -p $RUNNING_PIDS > /dev/null 2>&1; then
        echo "Force stopping process with PID $RUNNING_PIDS..."
        kill -9 "$RUNNING_PIDS"
    fi
else
    echo "No running process found for $JAR_NAME."
fi

# Start the JAR in the background with the OPENAI_API_KEY environment variable
echo "Starting $JAR_NAME with OPENAI_API_KEY ..."
#nohup java -jar "$WORK_DIR/$JAR_NAME" "$OPENAI_API_KEY" > "$LOG_FILE" 2>&1 &
nohup java -Dspring.ai.openai.api-key="$OPENAI_API_KEY" -jar "$WORK_DIR/$JAR_NAME" > "$LOG_FILE" 2>&1 &


# Get the process ID (PID) of the last background job
NEW_PID=$!

# Print success message and new PID
echo "$JAR_NAME started successfully with PID $NEW_PID. Logs are being written to $LOG_FILE."

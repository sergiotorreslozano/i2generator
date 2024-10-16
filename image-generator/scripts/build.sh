#!/bin/bash

# Build script for the Spring Boot project

# Ensure the script is run from the correct directory
# Moving one directory up to locate the pom.xml
echo "Navigating to project root..."
cd "$(dirname "$0")"/.. || {
    echo "Error: Failed to navigate to project root!"
    exit 1
}

# Run Maven build
echo "Building project with Maven..."
mvn -q clean install

# Check if Maven build was successful
if [ $? -ne 0 ]; then
    echo "Maven build failed, aborting!"
    exit 1
fi

echo "Maven build completed successfully."

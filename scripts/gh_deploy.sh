#!/bin/bash

# Change to the build/libs directory
cd ./build/libs/

# Find the most recently modified JAR file
latest_jar=$(ls -t *.jar | head -n 1)

# Check if a JAR file was found
if [ -n "$latest_jar" ]; then
  # Run the JAR file
  java -jar "$latest_jar"
else
  echo "No JAR file found in ./build/libs/"
fi

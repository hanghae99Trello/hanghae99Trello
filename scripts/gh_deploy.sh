cd ../build/libs/

latest_jar=$(ls -t *.jar | head -n 1)

if [ -n "$latest_jar" ]; then
  nohup java -jar "$latest_jar" &
else
  echo "No JAR file found in ./build/libs/"
fi

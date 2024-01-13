cd ../build/libs/

latest_jar=$(ls -t *.jar | head -n 1)

if [ -n "$latest_jar" ]; then
  chmod +x "$latest_jar"
  nohup java -jar "$latest_jar" > output.log 2>&1 &
else
  echo "No JAR file found in ./build/libs/"
fi

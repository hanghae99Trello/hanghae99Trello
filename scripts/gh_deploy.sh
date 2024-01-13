cd ../build/libs/

CURRENT_PID=$(pgrep -fla java | grep hayan | awk '{print $1}')

echo "현재 구동 중인 애플리케이션 pid: $CURRENT_PID"

if [ -z "$CURRENT_PID" ]; then
  echo "현재 구동 중인 애플리케이션이 없으므로 종료하지 않습니다."
else
  echo "> kill -15 $CURRENT_PID"
  kill -15 $CURRENT_PID
  sleep 10
fi

latest_jar=$(ls -t *.jar | head -n 1)

if [ -n "$latest_jar" ]; then
  java -jar "$latest_jar"
else
  echo "No JAR file found in ../build/libs/"
fi

JAR_NAME=$(ls -tr ../build/libs/*SNAPSHOT.jar | tail -n 1)
chmod +x $JAR_NAME
nohup java -jar -Duser.timezone=Asia/Seoul $JAR_NAME >> ../build/libs/nohup.out 2>&1 &

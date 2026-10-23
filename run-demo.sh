#!/bin/sh
set -eu
: "${INFRAI_API_KEY:?set INFRAI_API_KEY}"
: "${DEMO_EMAIL_TO:?set DEMO_EMAIL_TO}"
rm -rf build
mkdir -p build/classes
javac -d build/classes $(find src/main/java -name '*.java')
java -cp build/classes com.example.edtech.DeadlineMailExample "$DEMO_EMAIL_TO"

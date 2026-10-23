#!/bin/sh
set -eu
rm -rf build
mkdir -p build/classes
javac -d build/classes $(find src/main/java src/test/java -name '*.java')
java -cp build/classes com.example.edtech.LifecycleMailServiceTest

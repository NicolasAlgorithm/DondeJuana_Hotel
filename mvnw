#!/bin/sh

set -e

BASEDIR=$(cd "$(dirname "$0")"; pwd)
WRAPPER_DIR="$BASEDIR/.mvn/wrapper"
WRAPPER_JAR="$WRAPPER_DIR/maven-wrapper.jar"
WRAPPER_URL="https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar"

mkdir -p "$WRAPPER_DIR"

if [ ! -f "$WRAPPER_JAR" ]; then
  echo "Downloading Maven Wrapper jar..."
  if command -v curl >/dev/null 2>&1; then
    curl -fsSL "$WRAPPER_URL" -o "$WRAPPER_JAR"
  elif command -v wget >/dev/null 2>&1; then
    wget -q "$WRAPPER_URL" -O "$WRAPPER_JAR"
  else
    echo "curl or wget is required to download Maven Wrapper jar" >&2
    exit 1
  fi
fi

if [ -n "$JAVA_HOME" ]; then
  JAVA_EXE="$JAVA_HOME/bin/java"
  if [ ! -x "$JAVA_EXE" ]; then
    echo "Java executable not found or not executable at JAVA_HOME: $JAVA_EXE" >&2
    exit 1
  fi
else
  JAVA_EXE="java"
  if ! command -v java >/dev/null 2>&1; then
    echo "Java executable not found in PATH" >&2
    exit 1
  fi
fi

exec "$JAVA_EXE" -classpath "$WRAPPER_JAR" "-Dmaven.multiModuleProjectDirectory=$BASEDIR" org.apache.maven.wrapper.MavenWrapperMain "$@"

#!/usr/bin/env sh

# Copy of Gradle 8.4 wrapper script

APP_HOME=$(cd "$(dirname "$0")" && pwd -P)

DEFAULT_JVM_OPTS=""

CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar

if [ -d "$JAVA_HOME" ]; then
  JAVA_HOME_BIN="$JAVA_HOME/bin"
  PATH="$JAVA_HOME_BIN:$PATH"
fi

JAVA_EXEC="java"

# Collect JVM_OPTS and arguments
JVM_OPTS=()
for arg in "$@"; do
  case "$arg" in
    -D*) JVM_OPTS=("${JVM_OPTS[@]}" "$arg") ;;
    *) APP_ARGS=("${APP_ARGS[@]}" "$arg") ;;
  esac
done

exec "$JAVA_EXEC" ${JVM_OPTS[@]} -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "${APP_ARGS[@]}"

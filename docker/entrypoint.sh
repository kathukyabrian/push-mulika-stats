#!/bin/sh

echo "Mulika Reporting Engine starting"

echo "Engine running with 'java ${JAVA_OPTS} -cp /app/resources:/app/classes:/app/libs/* tech.kitucode.App'"

exec java ${JAVA_OPTS} -cp /app/resources:/app/classes:/app/libs/* "tech.kitucode.App"
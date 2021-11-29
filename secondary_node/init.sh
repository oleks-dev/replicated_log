#!/bin/bash
for e in "${envs[@]}"; do
	file_env "$e"
	if [ -z "$haveConfig" ] && [ -n "${!e}" ]; then
		haveConfig=1
	fi
done

exec java ${JAVA_OPTS} -jar ${APP_JAR} "$@"
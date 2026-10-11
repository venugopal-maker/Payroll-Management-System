#!/bin/bash
# Usage: put mysql-connector-j-8.4.0.jar in lib/, set DB_USER / DB_PASS, then ./run.sh
mkdir -p out
javac -d out $(find src -name "*.java") || exit 1
java -cp "out:lib/*" com.payroll.app.Main

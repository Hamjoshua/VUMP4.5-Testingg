#!/bin/bash

set -e

if [ -e "access-control.jar" ]; then
    echo "There is access-contol.jar already"    
else
    echo "Build needed"
    ./build.sh
fi

echo "Launching tests via access-control.jar..."

# Счётчик успешных тестов
PASSED=0
TOTAL=10

# Функция запуска теста: ожидаемый код + описание + аргументы
run_test() {
    local expected_code=$1
    local description="$2"
    shift 2
    local actual_code

    echo -n "Test: $description ... "

    # Запускаем и ловим exit code
    if java -jar access-control.jar "$@" 2>/dev/null; then
        actual_code=0
    else
        actual_code=$?
    fi

    if [ "$actual_code" -eq "$expected_code" ]; then
        echo "OK"
        ((PASSED++))
    else
        echo "FAIL (expected $expected_code, got $actual_code)"
    fi
}

# --- Тест-кейсы: по одному на каждый код ответа + дополнительные для полноты ---

# 1. SUCCESS (0)
run_test 0 "Alice reads allowed resource A.B" -l alice -p password123 -r A.B -a READ -v 10
exit 1
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

# 2. AUTH_FAILED (2)
run_test 2 "Wrong password for alice" -l alice -p wrongpass -r A.B -a READ -v 1

# 3. PERMISSION_DENIED (3)
run_test 3 "Alice tries to WRITE on X (only READ allowed)" -l alice -p password123 -r X -a WRITE -v 1

# 4. RESOURCE_NOT_FOUND (4)
run_test 4 "Access non-existent resource Z" -l alice -p password123 -r Z -a READ -v 1

# 5. VOLUME_EXCEEDED (5)
run_test 5 "Bob exceeds volume on A.B.C (max=20)" -l bob -p qwerty -r A.B.C -a READ -v 25

# 6. HELP_REQUESTED (1) — вызов без аргументов
run_test 1 "Missing arguments → help requested"  # без параметров

# 7. SUCCESS: Charlie на A.B.D с EXECUTE
run_test 0 "Charlie executes A.B.D (allowed)" -l charlie -p pass123 -r A.B.D -a EXECUTE -v 0

# 8. PERMISSION_DENIED: Bob на A.B (нет прав на A.B, только на A.B.C)
run_test 3 "Bob accesses A.B (no permission)" -l bob -p qwerty -r A.B -a READ -v 1

# 9. AUTH_FAILED: несуществующий пользователь
run_test 2 "Non-existent user eve" -l eve -p pass -r A -a READ -v 1

# 10. VOLUME_EXCEEDED: попытка использовать 0 на ресурсе с maxVolume=0? Или просто другой кейс.
# Но у нас нет ресурса с maxVolume=0, так что возьмём X.Y (max=75) и запросим 80
run_test 5 "Exceed volume on X.Y (max=75)" -l bob -p qwerty -r X.Y -a WRITE -v 80

# --- Итог ---
echo ""
echo "✅ Tests passed: $PASSED/$TOTAL"

# Выход с кодом 0, если все тесты прошли — удобно для CI
if [ "$PASSED" -eq "$TOTAL" ]; then
    exit 0
else
    exit 1
fi
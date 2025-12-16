package org.example.AccessControl.entities

enum class StatusCode(val code: Int) {
    SUCCESS(0),
    HELP_REQUESTED(127),
    INVALID_PASSWORD(2),
    INVALID_LOGIN(3),
    UNKNOWN_ACTION(4),
    NO_ACCESS(5),
    NON_EXISTENT_RESOURCE(6),
    INVALID_FORMAT(7),
    VOLUME_EXCEEDED(8),
    DB_CONNECTION(code = 9),
    INVALID_SQL_QUERY(code = 10);

    override fun toString(): String = "StatusCode($code)"
}

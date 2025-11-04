package org.example
import kotlinx.cli.*
import org.example.entities.StatusCode
import org.example.services.AccessControlService
import org.example.validators.ValidationContext
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val parser = ArgParser("example")
    val login by parser.option(ArgType.String, shortName = "l", description = "User login").required()
    val password by parser.option(ArgType.String, shortName = "p", description = "User password").required()
    val resource by parser.option(ArgType.String, shortName = "r", description = "Path to resource").required()
    val action by parser.option(ArgType.String, shortName = "a", description = "Requested action").required()
    val volume by parser.option(ArgType.Int, shortName = "v", description = "Volume of used resource").required()
    try {
        parser.parse(args)
    } catch (e: Exception) {
        val helpArg = args + "-h"
        parser.parse(helpArg) // я буквально не придумал ничего лучше. Господи прости за это... да нормуль!!
        exitProcess(StatusCode.HELP_REQUESTED.code)
    }

    val context = ValidationContext(
        login = login,
        password = password,
        resourcePath = resource,
        action = action,
        volume = volume
    )

    exitProcess(AccessControlService().checkAccess(context).code)
}

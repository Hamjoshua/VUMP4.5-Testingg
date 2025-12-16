package org.example.AccessControl
import kotlinx.cli.*
import org.example.AccessControl.entities.StatusCode
import org.example.AccessControl.services.AccessControlService
import org.example.AccessControl.validators.ValidationContext
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Component
import kotlin.system.exitProcess

@SpringBootApplication
class SpringBootConsoleApplication: CommandLineRunner {
    private val logger = LoggerFactory.getLogger(AccessControlService::class.java)
    override fun run(vararg args: String) {
        val parser = ArgParser("example")
        val login by parser.option(ArgType.String, shortName = "l", description = "User login").required()
        val password by parser.option(ArgType.String, shortName = "p", description = "User password").required()
        val resource by parser.option(ArgType.String, shortName = "r", description = "Path to resource").required()
        val action by parser.option(ArgType.String, shortName = "a", description = "Requested action").required()
        val volume by parser.option(ArgType.Int, shortName = "v", description = "Volume of used resource").required()
        try {
            parser.parse(args)
        } catch (e: Exception) {
            logger.error("Ошибка: ${e.message}")
            // val helpArg = args + "-h"
            // parser.parse(helpArg) // я буквально не придумал ничего лучше. Господи прости за это... да нормуль!!
            // exitProcess(StatusCode.HELP_REQUESTED.code)
        }

        val context = ValidationContext(
            login = login,
            password = password,
            resourcePath = resource,
            action = action,
            volume = volume
        )
        // logger.info("while (True) \n print(\"Aboba\")")
        // print("while (True) \n print(\"Aboba\")") прикол от Стаса
        val code = AccessControlService().checkAccess(context).code
        logger.info(code.toString())
        exitProcess(code)
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            runApplication<SpringBootConsoleApplication>(*args)
        }
    }
}




pluginManagement {
    val springBootVersion: String by settings
    val springDependencyManagementVersion: String by settings
    plugins {
        id("org.springframework.boot") version springBootVersion
        id("io.spring.dependency-management") version springDependencyManagementVersion
    }
}

rootProject.name = "mini-pix"

include("pix-common")
include("pix-producer")
include("pix-validator")
include("pix-notificador")
include("pix-auditor")
include("pix-app")
include("pix-api")
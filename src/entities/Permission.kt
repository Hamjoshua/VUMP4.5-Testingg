package org.example.entities

data class Permission(val userLogin: String,
                      val resourcePath: String,
                      val actions: Set<ResourceAction>)
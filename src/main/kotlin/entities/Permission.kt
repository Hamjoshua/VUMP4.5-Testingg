package org.example.AccessControl.entities

data class Permission(val id: Int,
                      val userLogin: String,
                      val resourcePath: String,
                      val actions: Set<ResourceAction>)
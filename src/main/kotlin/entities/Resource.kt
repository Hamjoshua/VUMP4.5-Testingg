package org.example.AccessControl.entities

data class Resource(val id: Int,
                    val path: String,
                    val maxVolume: Int,
                    val parent: Resource? = null)
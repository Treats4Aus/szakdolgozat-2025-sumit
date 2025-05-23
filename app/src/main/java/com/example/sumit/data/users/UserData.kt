package com.example.sumit.data.users

data class UserData(
    val id: String = "",
    val email: String = "",
    val name: String = "",
    val username: String = "",
    val deviceTokens: List<String> = emptyList()
)

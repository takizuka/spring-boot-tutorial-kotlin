package com.example.tutorial.todo

import java.time.LocalDateTime


data class Todo(
    var todoId: Long? = null,
    var todoTitle: String = "",
    var finished: Boolean = false,
    var createdAt: LocalDateTime? = null,
)

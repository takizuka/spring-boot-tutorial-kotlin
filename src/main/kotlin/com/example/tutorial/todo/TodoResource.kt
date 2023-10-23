package com.example.tutorial.todo

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class TodoResource(
    var todoId: Long? = null,
    @NotEmpty
    @Size(max = 30)
    var todoTitle: String = "",
    var finished: Boolean = false,
    @JsonFormat(pattern = "uuuu/MM/dd HH:mm:ss")
    var createdAt: LocalDateTime? = null
)
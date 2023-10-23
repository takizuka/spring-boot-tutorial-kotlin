package com.example.tutorial.todo

interface TodoService {
    fun findOne(todoId: Long): Todo
    fun findAll(): Collection<Todo>
    fun create(todo: Todo): Todo
    fun finish(todoId: Long): Todo
    fun delete(todoId: Long)
}
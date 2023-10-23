package com.example.tutorial.todo

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("todos")
class TodoController {
    @Autowired
    private lateinit var todoService: TodoService

    @Autowired
    private lateinit var beanMapper: TodoMapper

    @GetMapping
    fun getTodos(): List<TodoResource> {
        return todoService.findAll().map(beanMapper::map)
    }

    @GetMapping("{todoId}")
    fun getTodo(@PathVariable("todoId") todoId: Long): TodoResource {
        return beanMapper.map(todoService.findOne(todoId))
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun postTodo(@RequestBody @Validated todoResource: TodoResource): TodoResource {
        return beanMapper.map(todoService.create(beanMapper.map(todoResource)))
    }

    @PutMapping("{todoId}")
    fun putTodo(@PathVariable("todoId") todoId: Long): TodoResource {
        return beanMapper.map(todoService.finish(todoId))
    }

    @DeleteMapping("{todoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteTodo(@PathVariable("todoId") todoId: Long) {
        todoService.delete(todoId)
    }
}
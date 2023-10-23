package com.example.tutorial.todo

import com.example.tutorial.common.exception.BusinessException
import com.example.tutorial.common.exception.ResourceNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

const val MAX_UNFINISHED_COUNT = 5

@Service
@Transactional
class TodoServiceImpl : TodoService {
    @Autowired
    lateinit var todoRepository: TodoRepository

    override fun findOne(todoId: Long): Todo {
        return todoRepository.findById(todoId)
            ?: throw ResourceNotFoundException(
                "The requested Todo is not found. (id=$todoId)"
            )
    }

    override fun findAll(): Collection<Todo> {
        return todoRepository.findAll()
    }

    override fun create(todo: Todo): Todo {
        val unfinishedTodo = todoRepository.countByFinished(false)
        if (unfinishedTodo >= MAX_UNFINISHED_COUNT) {
            throw BusinessException("The count of un-finished Todo must not be over $MAX_UNFINISHED_COUNT.")
        }

        val createdAt = LocalDateTime.now()
        todo.createdAt = createdAt
        todo.finished = false

        todoRepository.create(todo)

        return todo
    }

    override fun finish(todoId: Long): Todo {
        var todo = findOne(todoId)
        if (todo.finished) {
            throw BusinessException("The requested Todo is already finished. (id=$todoId)")
        }

        todo.finished = true
        todoRepository.updateById(todoId)

        return todo
    }

    override fun delete(todoId: Long) {
        findOne(todoId)
        todoRepository.deleteById(todoId)
    }
}
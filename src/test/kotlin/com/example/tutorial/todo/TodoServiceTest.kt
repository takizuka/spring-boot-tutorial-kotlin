package com.example.tutorial.todo

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.willDoNothing
import org.mockito.kotlin.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@SpringBootTest(classes = [TodoServiceImpl::class])
class TodoServiceTest {
    @Autowired
    private lateinit var todoService: TodoService

    @MockBean
    private lateinit var todoRepository: TodoRepository

    @Test
    @DisplayName("全Todoが取得できることを確認する(service)")
    fun testFindAll() {
        // setup
        val expectTodo1 = Todo(1L, "sample todo 1", false, LocalDateTime.parse("2019/09/19 01:01:01", DATETIME_FORMAT))
        val expectTodo2 = Todo(2L, "sample todo 2", true, LocalDateTime.parse("2019/09/19 02:02:02", DATETIME_FORMAT))
        val expectTodo3 = Todo(3L, "sample todo 3", false, LocalDateTime.parse("2019/09/19 03:03:03", DATETIME_FORMAT))

        // setup mocks
        given(todoRepository.findAll()).willReturn(listOf(expectTodo1, expectTodo2, expectTodo3))

        // run
        val actualTodos = todoService.findAll()

        // check
        then(todoRepository).should(times(1)).findAll()
        assertThat(actualTodos).containsExactly(expectTodo1, expectTodo2, expectTodo3)
    }

    @Test
    @DisplayName("todoIdに対応するTodoが取得できることを確認する(Service)")
    fun testFindOne() {
        // setup
        val expectTodo = Todo(1L, "sample todo 1", false, LocalDateTime.parse("2019/09/19 01:01:01", DATETIME_FORMAT))

        // setup mocks
        given(todoRepository.findById(1L)).willReturn(expectTodo)

        // run
        val actualTodo = todoService.findOne(1L)

        // check
        then(todoRepository).should(times(1)).findById(1L)
        assertThat(actualTodo).isEqualTo(expectTodo)
    }

    @Test
    @DisplayName("新たなTodoが作成できることを確認する(service)")
    fun testCreate() {
        // setup
        val expectTodo = Todo(null, "sample todo 4", false, null)

        // setup mocks
        willDoNothing().given(todoRepository).create(expectTodo)

        // run
        todoService.create(expectTodo)

        // check
        then(todoRepository).should(times(1)).create(
            argThat(fun(arg: Todo): Boolean {
                return (expectTodo.todoTitle == arg.todoTitle && !arg.finished && arg.createdAt != null)
            })
        )
    }

    @Test
    @DisplayName("todoId=1のfinishedがtrueになることを確認する(service)")
    fun testFinish() {
        // setup
        val expectTodo = Todo(1L, "sample todo 1", false, LocalDateTime.parse("2019/09/19 01:01:01", DATETIME_FORMAT))

        // setup mocks
        given(todoRepository.findById(1L)).willReturn(expectTodo)
        given(todoRepository.updateById(1L)).willReturn(1L)

        // run
        todoService.finish(1L)

        // check
        then(todoRepository).should(times(1)).findById(eq(1L))
        then(todoRepository).should(times(1)).updateById(eq(1L))
    }

    @Test
    @DisplayName("todoId=1がDeleteによって削除されることを確認する(service)")
    fun testDelete() {
        // setup
        val expectTodo = Todo(1L, "sample todo 1", false, LocalDateTime.parse("2019/09/19 01:01:01", DATETIME_FORMAT))

        // setup mocks
        given(todoRepository.findById(1L)).willReturn(expectTodo)
        given(todoRepository.deleteById(1L)).willReturn(1L)

        // run
        todoService.delete(1L)

        // check
        then(todoRepository).should(times(1))
            .findById(1L)
        then(todoRepository).should(times(1)).deleteById(eq(1L))
    }

    companion object {
        private val DATETIME_FORMAT = DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss")
    }
}
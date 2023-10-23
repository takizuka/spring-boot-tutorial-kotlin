package com.example.tutorial.todo

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.RequestEntity
import java.net.URI
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TodoControllerTest {
    companion object {
        private val DATETIME_FORMAT = DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss")
    }

    @Autowired
    private lateinit var testRestTemplate: TestRestTemplate

    @Autowired
    private lateinit var beanMapper: TodoMapper

    @MockBean
    private lateinit var todoService: TodoService

    @Test
    @DisplayName("GET Todosが正常に動作することを確認する(Controller)")
    fun getTodos() {
        val expectTodo1 = Todo(1L, "sample todo 1", false, LocalDateTime.parse("2019/09/19 01:01:01", DATETIME_FORMAT))
        val expectTodo2 = Todo(2L, "sample todo 2", true, LocalDateTime.parse("2019/09/19 02:02:02", DATETIME_FORMAT))
        val expectTodos: Collection<Todo> = listOf(expectTodo1, expectTodo2)
        val expectTodoResources = expectTodos.map(beanMapper::map).toTypedArray()

        given(todoService.findAll()).willReturn(expectTodos)

        val actualResponseEntity = testRestTemplate.getForEntity("/todos", Array<TodoResource>::class.java)

        then(todoService).should(times(1)).findAll()
        assertThat(actualResponseEntity.body).containsExactly(*expectTodoResources)
        assertThat(actualResponseEntity.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    @DisplayName("GET Todoが正常に動作することを確認する(Controller)")
    fun getTodo() {
        val expectTodo = Todo(1L, "sample todo 1", false, LocalDateTime.parse("2019/09/19 01:01:01", DATETIME_FORMAT))
        val expectTodoResource = beanMapper.map(expectTodo)

        given(todoService.findOne(1L)).willReturn(expectTodo)

        val actualResponseEntity = testRestTemplate.getForEntity("/todos/1", TodoResource::class.java)

        then(todoService).should(times(1)).findOne(eq(1L))
        assertThat(actualResponseEntity.body).isEqualTo(expectTodoResource)
        assertThat(actualResponseEntity.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    @DisplayName("POST Todoが正常に動作することを確認する(Controller)")
    fun postTodo() {
        val inputTodoResource = TodoResource().apply {
            todoTitle = "sample todo 4"
        }
        val inputTodo = beanMapper.map(inputTodoResource)
        val expectTodo = Todo(
            todoId = 4L,
            todoTitle = "sample todo 4",
            finished = false,
            createdAt = LocalDateTime.parse("2019/09/19 04:04:04", DATETIME_FORMAT)
        )
        val expectedTodoResource = beanMapper.map(expectTodo)

        given(todoService.create(any())).willReturn(expectTodo)

        val actualResponseEntity = testRestTemplate.postForEntity("/todos", inputTodoResource, TodoResource::class.java)

        then(todoService).should(times(1)).create(eq(inputTodo))
        assertThat(actualResponseEntity.body).isEqualTo(expectedTodoResource)
        assertThat(actualResponseEntity.statusCode).isEqualTo(HttpStatus.CREATED)
    }

    @Test
    @DisplayName("PUT Todoが正常に動作することを確認する(Controller)")
    fun putTodo() {
        val expectTodo = Todo(1L, "sample todo 1", true, LocalDateTime.parse("2019/09/19 01:01:01", DATETIME_FORMAT))
        val expectTodoResource = beanMapper.map(expectTodo)

        given(todoService.finish(1L)).willReturn(expectTodo)

        val actualRequestEntity = RequestEntity.put(URI.create("/todos/1")).body("")
        val actualResponseEntity = testRestTemplate.exchange(actualRequestEntity, TodoResource::class.java)

        then(todoService).should(times(1)).finish(eq(1L))
        assertThat(actualResponseEntity.body).isEqualTo(expectTodoResource)
        assertThat(actualResponseEntity.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    @DisplayName("DELETE Todoが正常に動作することを確認する(Controller)")
    fun deleteTodo() {
        val actualResponseEntity =
            testRestTemplate.exchange("/todos/1", HttpMethod.DELETE, HttpEntity.EMPTY, Void::class.java)

        then(todoService).should(times(1)).delete(eq(1L))

        assertThat(actualResponseEntity.body).isNull()
        assertThat(actualResponseEntity.statusCode).isEqualTo(HttpStatus.NO_CONTENT)
    }
}
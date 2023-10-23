package com.example.tutorial.todo

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.tuple
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@MybatisTest
class TodoRepositoryTest {
    companion object {
        val DATETIME_FORMAT = DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss")
    }

    @Autowired
    private lateinit var todoRepository: TodoRepository

    @Autowired
    private lateinit var jdbcOperations: NamedParameterJdbcOperations

    @Test
    @DisplayName("全Todoが取得できることを確認する(Repository)")
    fun testFindAll() {
        val actualTodos = todoRepository.findAll()

        // check
        assertThat(actualTodos)
            .extracting(Todo::todoId, Todo::todoTitle, Todo::finished, Todo::createdAt)
            .contains(
                tuple(1L, "sample todo 1", false, LocalDateTime.parse("2019/09/19 01:01:01", DATETIME_FORMAT)),
                tuple(2L, "sample todo 2", true, LocalDateTime.parse("2019/09/19 02:02:02", DATETIME_FORMAT)),
                tuple(3L, "sample todo 3", false, LocalDateTime.parse("2019/09/19 03:03:03", DATETIME_FORMAT))
            )
    }

    @Test
    @DisplayName("todoIdに対応するTodoが取得できることを確認する(Repository)")
    fun testFindById() {
        // run
        val actualTodo = todoRepository.findById(1L)

        // check
        assertThat(actualTodo)
            .isEqualTo(Todo(1L, "sample todo 1", false, LocalDateTime.parse("2019/09/19 01:01:01", DATETIME_FORMAT)))
    }

    @Test
    @DisplayName("新たなTodoが作成できることを確認する(Repository)")
    fun testCreate() {
        // setup
        val actualTodo = Todo(null, "sample todo 4", false, LocalDateTime.parse("2019/09/19 04:04:04", DATETIME_FORMAT))

        // run
        todoRepository.create(actualTodo)

        // check
        val todo = getLastTodo()
        assertThat(actualTodo)
            .hasNoNullFieldsOrProperties()
            .usingRecursiveComparison()
            .ignoringFields("finished")
            .isEqualTo(todo)
    }

    @Test
    @DisplayName("finishedをfalseからtrueに変更できることを確認する(Repository)")
    fun testUpdateById() {
        // setup
        val todo = getTodo(1L)

        // run
        val count = todoRepository.updateById(1L)
        val updated = getTodo(1L)

        // check
        assertThat(count).isEqualTo(1L)
        assertThat(updated)
            .hasFieldOrPropertyWithValue("finished", true)
            .usingRecursiveComparison()
            .ignoringFields("finished")
            .isEqualTo(todo)
    }

    @Test
    @DisplayName("todoId=1が削除できていることを確認する(Repository)")
    fun testDeleteById() {
        // run
        val count = todoRepository.deleteById(1L)

        // check
        assertThat(count).isEqualTo(1)
    }

    @Test
    @DisplayName("未完了 or 完了済のTodoの件数を取得できることを確認する(Repository)")
    fun testCountByFinished() {
        // run
        val unfinishedCount = todoRepository.countByFinished(false)
        val finishedCount = todoRepository.countByFinished(true)

        // check
        assertThat(unfinishedCount).isEqualTo(2)
        assertThat(finishedCount).isEqualTo(1)
    }

    private fun getLastTodo(): Todo? {
        val sql = "SELECT * FROM todo ORDER BY todo_id DESC LIMIT 1"
        val paramSource = EmptySqlParameterSource()
        val rowMapper = BeanPropertyRowMapper(Todo::class.java)
        return jdbcOperations.queryForObject(sql, paramSource, rowMapper)
    }

    private fun getTodo(todoId: Long): Todo? {
        val sql = "SELECT * FROM todo WHERE todo_id=:todoId"
        val paramSource = MapSqlParameterSource().addValue("todoId", todoId)
        val rowMapper = BeanPropertyRowMapper(Todo::class.java)
        return jdbcOperations.queryForObject(sql, paramSource, rowMapper)
    }
}
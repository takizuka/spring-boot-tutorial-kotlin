package com.example.tutorial.todo

import org.apache.ibatis.annotations.*

@Mapper
interface TodoRepository {
    @Select("SELECT todo_id, todo_title, finished, created_at FROM todo WHERE todo_id = #{todoId}")
    fun findById(todoId: Long): Todo?

    @Select("SELECT todo_id, todo_title, finished, created_at FROM todo")
    fun findAll(): Collection<Todo>

    @Insert("INSERT INTO todo(todo_title, finished, created_at) VALUES(#{todoTitle}, #{finished}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "todoId")
    fun create(todo: Todo)

    @Update("UPDATE todo SET finished = true WHERE todo_id = #{todoId}")
    fun updateById(todoId: Long): Long

    @Delete("DELETE FROM todo WHERE todo_id = #{todoId}")
    fun deleteById(todoId: Long): Long

    @Select("SELECT COUNT(*) FROM todo WHERE finished = #{finished}")
    fun countByFinished(finished: Boolean): Long
}
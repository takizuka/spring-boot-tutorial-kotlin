package com.example.tutorial.todo

import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(componentModel = "spring")
interface TodoMapper {
    fun map(todo: Todo): TodoResource

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "finished", ignore = true)
    fun map(todoResource: TodoResource): Todo
}
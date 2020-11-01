package com.netcracker.cats.dao;

import com.netcracker.cats.model.Cat;

import java.sql.SQLException;
import java.util.List;

public interface CatDao {

    Cat getById(Long id) throws SQLException;

    List<Cat> getByAge(int age) throws SQLException;

    List<Cat> getAll() throws SQLException;

    Cat create(Cat cat) throws SQLException;

    Cat update(Cat cat) throws SQLException;

    boolean deleteById(Long id) throws SQLException;

}

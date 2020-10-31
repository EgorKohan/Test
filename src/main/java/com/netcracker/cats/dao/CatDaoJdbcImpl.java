package com.netcracker.cats.dao;

import com.netcracker.cats.model.Cat;
import com.netcracker.cats.util.JdbcConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CatDaoJdbcImpl implements CatDao {

    private final Connection CONNECTION = new JdbcConnectionUtil().getConnection();

    //language=SQL
    private static final String SQL_GET_ALL_CATS = "SELECT * FROM netcracker.cat";
    //language=SQL
    private static final String SQL_GET_CAT_BY_ID =
            "SELECT * FROM netcracker.cat WHERE id = ?";
    //language=SQL
    private static final String SQL_CREATE_CAT =
            "INSERT INTO netcracker.cat(name, father_id, mother_id) VALUE (?, ?, ?)";
    //language=SQL
    private static final String SQL_UPDATE_CAT_NAME =
            "UPDATE netcracker.cat SET name = ? WHERE id = ?";
    //language=SQL
    private static final String SQL_DELETE_CAT_BY_ID =
            "DELETE FROM netcracker.cat WHERE id = ?";

    @Override
    public Cat getById(Long id) throws SQLException {
        PreparedStatement preparedStatement = CONNECTION.prepareStatement(SQL_GET_CAT_BY_ID);
        preparedStatement.setLong(1, id);
        final ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            return mapCat(resultSet);
        }
        return null;
    }

    @Override
    public List<Cat> getAll() throws SQLException {
        final List<Cat> cats = new ArrayList<>();
        ResultSet resultSet = CONNECTION.createStatement().executeQuery(SQL_GET_ALL_CATS);
        while (resultSet.next()) {
            cats.add(mapCat(resultSet));
        }
        return cats;
    }

    @Override
    public Cat create(Cat cat) throws SQLException {
        PreparedStatement preparedStatement = CONNECTION.prepareStatement(SQL_CREATE_CAT);
        preparedStatement.setString(1, cat.getName());
        preparedStatement.setLong(2, cat.getFatherId());
        preparedStatement.setLong(3, cat.getMotherId());
        if (preparedStatement.execute()) {
            return cat;
        }
        return null;
    }

    @Override
    public Cat update(Cat cat) throws SQLException {
        PreparedStatement preparedStatement = CONNECTION.prepareStatement(SQL_UPDATE_CAT_NAME);
        preparedStatement.setString(1, cat.getName());
        preparedStatement.setLong(2, cat.getId());
        preparedStatement.executeUpdate();
        return cat;
    }

    @Override
    public boolean deleteById(Long id) throws SQLException {
        PreparedStatement preparedStatement = CONNECTION.prepareStatement(SQL_DELETE_CAT_BY_ID);
        preparedStatement.setLong(1, id);
        return preparedStatement.execute();
    }

    private Cat mapCat(ResultSet resultSet) throws SQLException {
        return new Cat(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getLong("father_id"),
                resultSet.getLong("mother_id")
        );
    }

}

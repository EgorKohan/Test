package com.netcracker.cats.dao;

import com.netcracker.cats.model.Cat;
import com.netcracker.cats.model.Gender;
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
            "INSERT INTO netcracker.cat(name, father_id, mother_id, gender, color, age) " +
                    "VALUE (?, ?, ?, ?, ?, ?)";
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
        PreparedStatement preparedStatementForCat = CONNECTION.prepareStatement(SQL_CREATE_CAT);
        preparedStatementForCat.setString(1, cat.getName());
        preparedStatementForCat.setLong(2, cat.getFather().getId());
        preparedStatementForCat.setLong(3, cat.getMother().getId());
        preparedStatementForCat.setString(4, cat.getGender().toString());
        preparedStatementForCat.setString(5, cat.getColor());
        preparedStatementForCat.setInt(6, cat.getAge());


        if (preparedStatementForCat.execute()) {
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
        Cat cat = new Cat(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                getById(resultSet.getLong("father_id")),
                getById(resultSet.getLong("mother_id")),
                resultSet.getInt("age"),
                resultSet.getString("color"),
                Gender.valueOf(resultSet.getString("gender"))
        );

        Cat father = cat.getFather();
        if(father != null) {
            father.getChildren().add(cat);
        }
        Cat mother = cat.getMother();
        if(mother != null) {
            mother.getChildren().add(cat);
        }

        return cat;
    }

}

package com.netcracker.cats.dao;

import com.netcracker.cats.model.Cat;
import com.netcracker.cats.model.Gender;
import com.netcracker.cats.util.JdbcConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class CatDaoJdbcImpl implements CatDao {

    private final Map<Long, Cat> cache = new HashMap<>();
    private final Connection CONNECTION = new JdbcConnectionUtil().getConnection();

    //language=SQL
    private static final String SQL_GET_ALL_CATS =
            "SELECT * FROM netcracker.cats";
    //language=SQL
    private static final String SQL_GET_CAT_BY_ID =
            "SELECT * FROM netcracker.cats WHERE id = ?";
    //language=SQL
    private static final String SQL_CREATE_CAT =
            "INSERT INTO netcracker.cats(name, gender, color, age) " +
                    "VALUE (?, ?, ?, ?)";
    //language=SQL
    private static final String SQL_CREATE_CHILD_TO_FATHER_TABLE =
            "INSERT INTO netcracker.fathers(father_id, child_id) VALUE (?,?)";
    //language=SQL
    private static final String SQL_CREATE_CHILD_TO_MOTHER_TABLE =
            "INSERT INTO netcracker.mothers(mother_id, child_id) VALUE (?,?)";
    //language=SQL
    private static final String SQL_UPDATE_CAT_NAME =
            "UPDATE netcracker.cats SET name = ? WHERE id = ?";
    //language=SQL
    private static final String SQL_DELETE_CAT_BY_ID =
            "DELETE FROM netcracker.cats WHERE id = ?";
    //language=SQL
    private static final String SQL_GET_CHILDREN_BY_FATHER_ID =
            "SELECT * FROM netcracker.fathers WHERE father_id = ?";
    //language=SQL
    private static final String SQL_GET_CHILDREN_BY_MOTHER_ID =
            "SELECT * FROM netcracker.mothers WHERE mother_id = ?";
    //language=SQL
    private static final String SQL_FIND_FATHER_BY_ID =
            "SELECT * FROM netcracker.fathers WHERE child_id = ?";
    //language=SQL
    private static final String SQL_FIND_MOTHER_BY_ID =
            "SELECT * FROM netcracker.mothers WHERE child_id = ?";
    //language=SQL
    private static final String SQL_FIND_CAT_BY_AGE =
            "SELECT * FROM netcracker.cats WHERE age = ?";

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
    public List<Cat> getByAge(int age) throws SQLException {
        final List<Cat> cats = new ArrayList<>();
        PreparedStatement preparedStatement = CONNECTION.prepareStatement(SQL_FIND_CAT_BY_AGE);
        preparedStatement.setInt(1, age);
        final ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            cats.add(mapCat(resultSet));
        }
        return cats;
    }

    @Override
    public List<Cat> getAll() throws SQLException {
        final List<Cat> cats = new ArrayList<>();
        ResultSet resultSet = CONNECTION.createStatement().executeQuery(SQL_GET_ALL_CATS);
        while (resultSet.next()) {
            Cat newCat = mapCat(resultSet);
            cats.add(newCat);
        }
        return cats;
    }

    @Override
    public Cat create(Cat cat) throws SQLException {
        PreparedStatement preparedStatementForCat = CONNECTION.prepareStatement(SQL_CREATE_CAT);
        preparedStatementForCat.setString(1, cat.getName());
        preparedStatementForCat.setString(2, cat.getGender().toString());
        preparedStatementForCat.setString(3, cat.getColor());
        preparedStatementForCat.setInt(4, cat.getAge());

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
        Long catId = resultSet.getLong("id");
        if (cache.containsKey(catId)) {
            return cache.get(catId);
        }

        Cat cat = Cat.builder()
                .id(catId)
                .name(resultSet.getString("name"))
                .age(resultSet.getInt("age"))
                .color(resultSet.getString("color"))
                .gender(Gender.valueOf(resultSet.getString("gender")))
                .build();

        cache.put(cat.getId(), cat);
        cat.getChildren().addAll(
                (cat.getGender() == Gender.MALE) ?
                        findFatherChildrenByCatId(cat.getId()) :
                        findMotherChildrenByCatId(cat.getId())
        );

        cat.setFather(findFatherForCatById(cat.getId()));
        cat.setMother(findMotherForCatById(cat.getId()));

        return cat;
    }

    private List<Cat> findFatherChildrenByCatId(Long id) throws SQLException {
        return getCats(id, SQL_GET_CHILDREN_BY_FATHER_ID);
    }

    private List<Cat> findMotherChildrenByCatId(Long id) throws SQLException {
        return getCats(id, SQL_GET_CHILDREN_BY_MOTHER_ID);
    }

    private List<Cat> getCats(Long id, String sqlGetChildrenById) throws SQLException {
        PreparedStatement preparedStatement = CONNECTION.prepareStatement(sqlGetChildrenById);
        preparedStatement.setLong(1, id);

        List<Cat> children = new ArrayList<>();
        final ResultSet resultSet = preparedStatement.executeQuery();
        long childId = 0;

        while (resultSet.next()) {
            childId = resultSet.getLong("child_id");
            if (cache.containsKey(childId)) {
                children.add(cache.get(childId));
            } else {
                Cat newCat = getById(childId);
                cache.put(newCat.getId(), newCat);
                children.add(newCat);
            }
        }

        return children;
    }

    private Cat findFatherForCatById(Long id) throws SQLException {
        PreparedStatement preparedStatement = CONNECTION.prepareStatement(SQL_FIND_FATHER_BY_ID);
        preparedStatement.setLong(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();


        if (resultSet.next()) {
            Long fatherId = resultSet.getLong("father_id");
            if (cache.containsKey(fatherId)) {
                return cache.get(fatherId);
            }

            final Cat father = getById(fatherId);
            cache.put(father.getId(), father);
            return father;
        }

        return null;
    }

    private Cat findMotherForCatById(Long id) throws SQLException {
        PreparedStatement preparedStatement = CONNECTION.prepareStatement(SQL_FIND_MOTHER_BY_ID);
        preparedStatement.setLong(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            Long motherId = resultSet.getLong("mother_id");
            if (cache.containsKey(motherId)) {
                return cache.get(motherId);
            }

            final Cat mother = getById(motherId);
            cache.put(mother.getId(), mother);
            return mother;
        }
        return null;
    }

}

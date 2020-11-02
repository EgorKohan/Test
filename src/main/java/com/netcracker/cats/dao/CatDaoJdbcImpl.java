package com.netcracker.cats.dao;

import com.netcracker.cats.model.Cat;
import com.netcracker.cats.model.Gender;
import com.netcracker.cats.util.JdbcConnectionUtil;

import java.sql.*;
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
    private static final String SQL_UPDATE_CAT_BY_NAME =
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
    //language=SQL
    private static final String SQL_GET_LATEST_CAT =
            "SELECT * FROM netcracker.cats ORDER BY id DESC LIMIT 1";
    //language=SQL
    private static final String SQL_DROP_CHILD_FROM_FATHER =
            "DELETE FROM netcracker.fathers WHERE father_id = ?";
    //language=SQL
    private static final String SQL_DROP_CHILD_FROM_MOTHER =
            "DELETE FROM netcracker.mothers WHERE mother_id = ?";


    @Override
    public Cat getById(Long id) throws SQLException {
        if (id == null) {
            return null;
        }

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
        Cat father = cat.getFather();
        Cat mother = cat.getMother();

        PreparedStatement preparedStatementForCat = CONNECTION.prepareStatement(SQL_CREATE_CAT);
        preparedStatementForCat.setString(1, cat.getName());
        preparedStatementForCat.setString(2, cat.getGender().toString());
        preparedStatementForCat.setString(3, cat.getColor());
        preparedStatementForCat.setInt(4, cat.getAge());

        preparedStatementForCat.execute();
        cat = getLatestCat();

        if (cat != null) {
            cat.setFather(father);
            cat.setMother(mother);

            if (father != null) {
                father.getChildren().add(cat);
            }
            if (mother != null) {
                mother.getChildren().add(cat);
            }
            addChildToFather(cat);
            addChildToMother(cat);
        }

        return cat;
    }

    @Override
    public Cat update(Cat cat) throws SQLException {
        PreparedStatement preparedStatement = CONNECTION.prepareStatement(SQL_UPDATE_CAT_BY_NAME);
        preparedStatement.setString(1, cat.getName());
        preparedStatement.setLong(2, cat.getId());
        preparedStatement.executeUpdate();
        return cat;
    }

    @Override
    public boolean deleteById(Long id) throws SQLException {
        PreparedStatement preparedStatement = CONNECTION.prepareStatement(SQL_DELETE_CAT_BY_ID);
        preparedStatement.setLong(1, id);

        PreparedStatement dropFromFather = CONNECTION.prepareStatement(SQL_DROP_CHILD_FROM_FATHER);
        PreparedStatement dropFromMother = CONNECTION.prepareStatement(SQL_DROP_CHILD_FROM_MOTHER);
        dropFromFather.setLong(1, id);
        dropFromMother.setLong(1, id);

        Cat cat = cache.get(id);
        cat = null;
        cache.remove(id);


        return !(preparedStatement.execute()
                && dropFromFather.execute()
                && dropFromFather.execute());
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
        long childId;

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

    private void addChildToFather(Cat cat) throws SQLException {
        if (cat.getFather() != null) {
            PreparedStatement preparedStatement = CONNECTION.prepareStatement(SQL_CREATE_CHILD_TO_FATHER_TABLE);
            preparedStatement.setLong(1, cat.getFather().getId());
            preparedStatement.setLong(2, cat.getId());
            preparedStatement.execute();
        }
    }

    private void addChildToMother(Cat cat) throws SQLException {
        if (cat.getMother() != null) {
            PreparedStatement preparedStatement = CONNECTION.prepareStatement(SQL_CREATE_CHILD_TO_MOTHER_TABLE);
            preparedStatement.setLong(1, cat.getMother().getId());
            preparedStatement.setLong(2, cat.getId());
            preparedStatement.execute();
        }
    }

    private Cat getLatestCat() throws SQLException {
        final Statement statement = CONNECTION.createStatement();
        ResultSet resultSet = statement.executeQuery(SQL_GET_LATEST_CAT);
        if (resultSet.next()) {
            return mapCat(resultSet);
        }
        return null;
    }

}

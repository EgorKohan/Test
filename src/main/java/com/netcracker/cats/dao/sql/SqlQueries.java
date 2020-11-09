package com.netcracker.cats.dao.sql;

public interface SqlQueries {

    //language=SQL
    String SQL_SELECT_ALL_CATS =
            "SELECT * from netcracker.cats";
    //language=SQL
    String SQL_SELECT_CAT_BY_ID =
            "SELECT * FROM netcracker.cats WHERE id = ?";
    //language=SQL
    String SQL_SELECT_CATS_BY_AGE =
            "SELECT * FROM netcracker.cats WHERE age = ?";
    //language=SQL
    String SQL_INSERT_CAT =
            "INSERT INTO netcracker.cats(name, gender, color, age, father_id, mother_id) VALUE (?,?,?,?,?,?)";
    //language=SQL
    String SQL_UPDATE_CAT_BY_ID =
            "UPDATE netcracker.cats SET " +
                    "name = ?, gender = ?, color = ?, age = ?, father_id = ?, mother_id = ? WHERE id = ?";
    //language=SQL
    String SQL_DELETE_CAT_BY_ID =
            "DELETE FROM netcracker.cats WHERE id = ?";
    //language=SQL
    String SQL_SELECT_FATHER_CHILD =
            "SELECT * FROM netcracker.cats WHERE father_id = ?";
    //language=SQL
    String SQL_SELECT_MOTHER_CHILD =
            "SELECT * FROM netcracker.cats WHERE mother_id = ?";
    //language=SQL
    String SQL_SELECT_CATS_BY_NAME_SUBSTRING =
            "SELECT * FROM netcracker.cats WHERE name LIKE ?";

}

package com.netcracker.cats.service;

import com.netcracker.cats.dao.CatDao;
import com.netcracker.cats.dao.CatDaoJdbcImpl;
import com.netcracker.cats.model.Cat;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

public class CatServiceImpl implements CatService {

    private final CatDao catDao = new CatDaoJdbcImpl();
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Override
    public Cat getById(Long id) {
        try {
            final Cat cat = catDao.getById(id);
            if(cat == null){
                LOGGER.error("Can't to map a cat correctly");
            }
            return cat;
        } catch (SQLException e) {
            LOGGER.error("Error in getById method.", e);
        }
        return null;
    }

    @Override
    public List<Cat> getByAge(int age) {
        if (age < 0 || age > 21) {
            LOGGER.error("Incorrect data: {}", age);
            return null;
        }
        try {
            return catDao.getByAge(age);
        } catch (SQLException e) {
            LOGGER.error("Error in getByAge method", e);
        }
        return null;
    }

    @Override
    public List<Cat> getAll() {
        try {
            return catDao.getAll();
        } catch (SQLException e) {
            LOGGER.error("Error in getAll method. Throw SQLException", e);
        }
        return null;
    }

    @Override
    public Cat create(Cat cat) {
        try {
            return catDao.create(cat);
        } catch (SQLException e) {
            LOGGER.error("Error in create method", e);
        }
        return null;
    }

    @Override
    public Cat update(Cat cat) {
        try {
            return catDao.update(cat);
        } catch (SQLException e) {
            LOGGER.error("Error in update method", e);
        }
        return null;
    }

    @Override
    public boolean deleteById(Long id) {
        try {
            return catDao.deleteById(id);
        } catch (SQLException e) {
            LOGGER.error("Error in delete method", e);
        }
        return false;
    }

    @Override
    public List<Cat> findCatsByName(String name) {
        try {
            return catDao.findCatsByName(name);
        } catch (SQLException e) {
            LOGGER.error("Error in findCatsByName method", e);
        }
        return null;
    }

    @Override /*FIXME switch to a cat at the attribute*/
    public String buildFamilyTree(Long id) {
        StringBuilder stringBuilder = new StringBuilder();
        Cat cat = getById(id);
        Cat father = cat.getFather();
        Cat mother = cat.getMother();

        stringBuilder.append("Fathers: ").append(cat.getName());
        while (father != null) {
            stringBuilder.append(" <- ").append(father.getName());
            father = father.getFather();
        }
        stringBuilder.append("\n");

        stringBuilder.append("Mothers: ").append(cat.getName());
        while (mother != null) {
            stringBuilder.append(" <- ").append(mother.getName());
            mother = mother.getMother();
        }
        return stringBuilder.toString();
    }
}

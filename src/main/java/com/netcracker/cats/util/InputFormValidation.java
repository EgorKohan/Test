package com.netcracker.cats.util;

import com.netcracker.cats.model.Cat;
import com.netcracker.cats.service.CatService;
import com.netcracker.cats.service.CatServiceImpl;

import javax.servlet.http.HttpServletRequest;

public class InputFormValidation {

    private final CatService catService = new CatServiceImpl();
    private Cat cat;

    private String checkCatName() {
        String name = cat.getName();
        if (name.matches("\\p{LC}{2,15}")) {
            return "";
        }
        return "Incorrect name! \n";
    }

    private String checkCatAge() {
        Integer age = cat.getAge();
        if (age == null || age < 0 || age > 20) {
            return "Age must be from 0 to 20! \n";
        }
        return "";
    }

    private String checkCatColor() {
        String color = cat.getColor();
        if (color.matches("\\p{LC}{2,15}")) {
            return "";
        }
        return "Incorrect color! \n";
    }

    public String checkCatForValid(Cat cat) {
        this.cat = cat;
        return checkCatName() +
                checkCatAge() +
                checkCatColor() +
                checkFather() +
                checkMother();
    }

    private String checkFather() {
        if(cat.getId() == null || cat.getFather() == null) {
            return "";
        }
        Cat parent = catService.getById(cat.getId()); //ееех костыли
        Cat father = cat.getFather();
        if (father.equals(cat)) {
            return "You can't be a father!";
        }
        for (Cat child : parent.getChildren()) {
            if(father.equals(child)){
                return "Child can't be a father!";
            }
        }
        return "";
    }

    private String checkMother() {
        if(cat.getId() == null || cat.getMother() == null) {
            return "";
        }
        Cat parent = catService.getById(cat.getId()); //ееех костыли
        Cat mother = cat.getMother();
        if (mother.equals(cat)) {
            return "You can't be a mother!";
        }
        for (Cat child : parent.getChildren()) {
            if(mother.equals(child)){
                return "Child can't be a mother!";
            }
        }
        return "";
    }

}

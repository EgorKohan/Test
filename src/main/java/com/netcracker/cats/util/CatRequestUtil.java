package com.netcracker.cats.util;

import com.netcracker.cats.model.Cat;
import com.netcracker.cats.model.Gender;
import com.netcracker.cats.service.CatService;
import com.netcracker.cats.service.CatServiceImpl;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

public class CatRequestUtil {

    private final CatService catService = new CatServiceImpl();

    public Cat createCatByRequest(HttpServletRequest req) {
        String name = req.getParameter("name");
        String color = req.getParameter("color");
        String ageStr = req.getParameter("age");

        Integer age;
        if (ageStr.equals("")) {
            age = null;
        } else {
            age = Integer.parseInt(ageStr);
        }

        String genderStr = req.getParameter("gender");
        Gender gender;
        if (genderStr == null) {
            gender = null;
        } else {
            gender = Gender.valueOf(genderStr.toUpperCase());
            System.out.println(gender.toString().equals("MALE"));
        }

        Long fatherId = Long.valueOf(req.getParameter("father"));
        Long motherId = Long.valueOf(req.getParameter("mother"));
        Cat father = catService.getById(fatherId);
        Cat mother = catService.getById(motherId);

        return Cat.builder()
                .name(name)
                .age(age)
                .gender(gender)
                .color(color)
                .father(father)
                .mother(mother)
                .build();
    }

    public void addCatsAsListByGender(HttpServletRequest req){
        List<Cat> males = catService.getAll().stream()
                .filter(cat -> cat.getGender().equals(Gender.MALE))
                .collect(Collectors.toList());
        List<Cat> females = catService.getAll().stream()
                .filter(cat -> cat.getGender().equals(Gender.FEMALE))
                .collect(Collectors.toList());

        req.setAttribute("males", males);
        req.setAttribute("females", females);
    }

}
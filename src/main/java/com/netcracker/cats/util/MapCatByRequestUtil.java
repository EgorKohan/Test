package com.netcracker.cats.util;

import com.netcracker.cats.model.Cat;
import com.netcracker.cats.model.Gender;
import com.netcracker.cats.service.CatService;
import com.netcracker.cats.service.CatServiceImpl;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MapCatByRequestUtil {

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

    public void addCatsAsListByGender(HttpServletRequest req) {

        final List<Cat> cats = catService.getAll();
        final Map<Gender, List<Cat>> genderListMap =
                cats.stream().collect(Collectors.groupingBy(Cat::getGender));

        req.setAttribute("males", genderListMap.get(Gender.MALE));
        req.setAttribute("females", genderListMap.get(Gender.FEMALE));
    }

}

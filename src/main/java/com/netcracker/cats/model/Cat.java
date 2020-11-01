package com.netcracker.cats.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
@ToString(exclude = {"father", "mother", "children"})
public class Cat {

    private Long id;
    private String name;
    private Cat father;
    private Cat mother;
    private int age;
    private String color;
    private Gender gender;

    private final List<Cat> children = new ArrayList<>();

}

package com.netcracker.cats.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@Builder
@ToString(exclude = {"father", "mother", "children"})
@EqualsAndHashCode(of = "id")
public class Cat {

    private Long id;
    private String name;
    private Cat father;
    private Cat mother;
    private Integer age;
    private String color;
    private Gender gender;

    private final Set<Cat> children = new HashSet<>();

}

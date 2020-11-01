package com.netcracker.cats.model;

import lombok.*;

@Data
@AllArgsConstructor
@Builder
public class Cat {

    private Long id;
    private String name;
    private Long fatherId;
    private Long motherId;

}

package com.netcracker.cats.model;

import lombok.*;

@Data
@AllArgsConstructor
public class Cat {

    @NonNull
    private Long id;
    @NonNull
    private String name;
    private Long fatherId;
    private Long motherId;

}

package com.netcracker.cats.util;

import com.netcracker.cats.model.Cat;

import java.util.List;

public class ConsolePrinter {

    public void printAllCats(List<Cat> cats){
        for (Cat cat : cats) {
            printCatInfo(cat);
        }
    }

    public void printCatInfo(Cat cat){
        String catInfo = "Cat #" + cat.getId() + ". " +
                "Name " + cat.getName() + ".";
        System.out.println(catInfo);
    }

}

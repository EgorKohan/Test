package com.netcracker.cats.controller;

import com.netcracker.cats.model.Cat;
import com.netcracker.cats.service.CatService;
import com.netcracker.cats.service.CatServiceImpl;

import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class ConsoleController {

    private final CatService catService = new CatServiceImpl();
    private static final Scanner SCANNER = new Scanner(System.in);

    public void printMenuItem() {
        System.out.print("Select an action:" +
                "\n1 - Print info about all cats." +
                "\n2 - Add new cat." +
                "\n3 - Search a cat by id." +
                "\n4 - Exit." +
                "\nSelect: "
        );
    }

    public void start() {
        System.out.println("Hello");
        int choice;
        do {
            printMenuItem();
            choice = inputInt();
            switch (choice) {
                case 1: {
                    printAllCats();
                    break;
                }
                case 2: {
                    Cat newCat = createCat();
                    catService.create(newCat);
                    System.out.println(">> Cat created");
                    break;
                }
                case 3: {
                    printCatById();
                    break;
                }
                case 4: {
                    break;          /*exit point*/
                }
                default: {
                    System.out.println("Wrong choice");
                    break;
                }
            }
        } while (choice != 4);
    }

    private void printAllCats() {
        List<Cat> cats = catService.getAll();
        for (Cat cat : cats) {
            printCatInfo(cat);
        }
    }

    private void printCatInfo(Cat cat) {
        final Cat father = catService.getById(cat.getFatherId());
        String fatherInfo = (father == null) ? "unknown" :
                father.getName() + "(id: " + father.getId() + ")";

        final Cat mother = catService.getById(cat.getMotherId());
        String motherInfo = (mother == null) ? "unknown" :
                mother.getName() + "(id: " + mother.getId() + ")";

        String catInfo = ">> Cat #" + cat.getId() + ". " +
                "Name: " + cat.getName() + ". " +
                "Father: " + fatherInfo + ". " +
                "Mother: " + motherInfo + ". ";

        System.out.println(catInfo);
    }

    private void printCatById() {
        System.out.print(">> Input cat id or 'q' for exit:\n<< ");
        String catId = inputStringWithExit("[q[\\d]+]");
        if(catId == null){
            System.out.println("Exit");
            return;
        }
        Cat cat = catService.getById(Long.valueOf(catId));
        if (cat == null) {
            System.out.println(">> A cat with this id doesn't exist");
        } else {
            printCatInfo(cat);
        }
    }

    private Cat createCat() {
        System.out.print(">> Input new cat name:\n<< ");
        String catName = inputString("\\p{LC}{2,}");

        System.out.print(">> Input father id (if known):\n<< ");
        Long fatherId = (long) inputInt();

        System.out.print(">> Input mother id (if known):\n<< ");
        Long motherId = (long) inputInt();

        return Cat.builder()
                .name(catName)
                .fatherId(fatherId)
                .motherId(motherId)
                .build();
    }

    private int inputInt() {
        while (!SCANNER.hasNextInt()) {
            System.out.print(">> Incorrect input! Try again:\n<< ");
            SCANNER.next();
        }
        return SCANNER.nextInt();
    }

    private String inputString(String pattern) {
        String str = SCANNER.next();
        while (!str.matches(pattern)) {
            System.out.print("<< Incorrect regex or too short\n>> ");
            str = SCANNER.next();
        }
        return str;
    }

    private String inputStringWithExit(String pattern){
        String str = SCANNER.next();
        while(!str.matches(pattern)){
            System.out.println(">> Incorrect input! Try again.\\n<< ");
        }
        if(str.equals("q")){
            return null;
        }
        return str;
    }

}

package com.netcracker.cats.controller;

import com.netcracker.cats.dao.CatDao;
import com.netcracker.cats.dao.CatDaoJdbcImpl;
import com.netcracker.cats.model.Cat;
import com.netcracker.cats.model.Gender;
import com.netcracker.cats.service.CatService;
import com.netcracker.cats.service.CatServiceImpl;

import java.util.List;
import java.util.Scanner;

public class ConsoleController {

    private final CatService catService = new CatServiceImpl();
    private static final Scanner SCANNER = new Scanner(System.in);

    public void printMenuItem() {
        System.out.print("Select an action:" +
                "\n1 - Print info about all cats." +
                "\n2 - Add a new cat." +
                "\n3 - Find a cat by id." +
                "\n4 - Find a cat by age." +
                "\n5 - Delete a cat." +
                "\n6 - Exit." +
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
                    printCatByAge();
                    break;
                }
                case 5: {
                    deleteCatById();
                    break;
                }
                case 6: {
                    break;          /*exit point*/
                }
                default: {
                    System.out.println("Wrong choice");
                    break;
                }
            }
        } while (choice != 6);
    }

    private void printAllCats() {
        List<Cat> cats = catService.getAll();
        for (Cat cat : cats) {
            printCatInfo(cat);
        }
    }

    private void printCatInfo(Cat cat) {
        final Cat father = cat.getFather();
        String fatherInfo = (father == null) ? "unknown" :
                father.getName() + "(id: " + father.getId() + ")";

        final Cat mother = cat.getMother();
        String motherInfo = (mother == null) ? "unknown" :
                mother.getName() + "(id: " + mother.getId() + ")";

        String catInfo = ">> Cat #" + cat.getId() + ". " +
                "Name: " + cat.getName() + ". " +
                "Gender: " + cat.getGender().toString().toLowerCase() + ". " +
                "Color: " + cat.getColor() + ". " +
                "Age: " + cat.getAge() + ". " +
                "Father: " + fatherInfo + ". " +
                "Mother: " + motherInfo + ". ";

        StringBuilder childrenInfo = new StringBuilder("Children: ");
        for (Cat child : cat.getChildren()) {
            childrenInfo.append(child.getName()).append("(id: ").append(child.getId()).append(").");
        }

        catInfo += childrenInfo;

        System.out.println(catInfo);
    }

    private void printCatById() {
        System.out.print(">> Input cat id or 'q' for exit:\n<< ");
        String catId = inputStringWithExit("[q[\\d]+]");
        if (catId == null) {
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

    private void printCatByAge() {
        System.out.print(">> Input age:\n<< ");
        int age = inputInt();
        final List<Cat> cats = catService.getByAge(age);
        for (Cat cat : cats) {
            printCatInfo(cat);
        }
    }

    private void deleteCatById() {
        System.out.print(">> Input cat id for deleting:\n<< ");
        Long id = (long) inputInt();
        if (catService.deleteById(id)) {
            System.out.println(">> Cat not found");
        } else {
            System.out.println(">> Cat removed");
        }
    }

    private Cat createCat() {
        System.out.print(">> Input new cat name:\n<< ");
        String catName = inputString("\\p{LC}{2,}");

        System.out.print(">> Input father id (if known):\n<< ");
        Long fatherId = (long) inputInt();/*inputLongWithNull();*/
        Cat father = catService.getById(fatherId);

        //TODO add exit with enter

        System.out.print(">> Input mother id (if known):\n<< ");
        Long motherId = (long) inputInt();/*inputLongWithNull();*/
        Cat mother = catService.getById(motherId);

        System.out.print(">> Input cat gender(male, female):\n<< ");
        Gender gender = Gender.valueOf(inputString("male|female").toUpperCase());

        System.out.print(">> Input cat color: ");
        String color = inputString("\\p{LC}{2,}");

        System.out.print(">> Input cat age(0, 20): ");
        int age = inputInt();

        return Cat.builder()
                .name(catName)
                .father(father)
                .mother(mother)
                .gender(gender)
                .color(color)
                .age(age)
                .build();
    }

    private Integer inputInt() {
        int i;
        while (!SCANNER.hasNextInt()) {
            System.out.print(">> Incorrect input! Try again.\n<< ");
            SCANNER.next();
        }
        i = SCANNER.nextInt();
        return i;
    }


//    private Long inputLongWithNull() {
//        SCANNER.nextLine();
//        String str = SCANNER.nextLine();
//        while (!str.matches("[\\d+]")) {
//            str = SCANNER.nextLine();
//            if (str.equals("")) {
//                return null;
//            }
//            System.out.print(">> Incorrect input! Try again.\n<< ");
//            str = SCANNER.nextLine();
//        }
//        return Long.valueOf(str);
//    }

    private String inputString(String pattern) {
        String str = SCANNER.next();
        while (!str.matches(pattern)) {
            System.out.print("<< Incorrect regex or too short\n>> ");
            str = SCANNER.next();
        }
        return str;
    }

    private String inputStringWithExit(String pattern) {
        String str = SCANNER.next();
        while (!str.matches(pattern)) {

            System.out.print("<< Incorrect input! Try again.\\n>> ");
            str = SCANNER.next();
        }
        if (str.equals("q")) {
            return null;
        }
        return str;
    }

}

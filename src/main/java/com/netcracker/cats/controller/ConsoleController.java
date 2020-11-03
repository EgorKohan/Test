package com.netcracker.cats.controller;

import com.netcracker.cats.model.Cat;
import com.netcracker.cats.model.Gender;
import com.netcracker.cats.service.CatService;
import com.netcracker.cats.service.CatServiceImpl;

import java.util.List;
import java.util.PrimitiveIterator;
import java.util.Scanner;

public class ConsoleController {

    private static final String SIMPLE_STRING_PATTERN = "\\p{LC}{2,}";
    private static final String ID_STRING_PATTERN = "q|\\d+";
    private static final String CAT_GENDER_PATTERN = "male|female";

    private final CatService catService = new CatServiceImpl();
    private static final Scanner SCANNER = new Scanner(System.in);

    public void printMenuItem() {
        System.out.print("Select an action:" +
                "\n1 - Print info about all cats." +
                "\n2 - Add a new cat." +
                "\n3 - Find a cat by id." +
                "\n4 - Find a cat by age." +
                "\n5 - Build a family tree" +
                "\n6 - Find a cat by substring" +
                "\n7 - Delete a cat." +
                "\n8 - Exit." +
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
                    printFamilyTree();
                    break;
                }
                case 6: {
                    printCatsBySubstring();
                    break;
                }
                case 7: {
                    deleteCatById();
                    break;
                }
                case 8: {
                    break;          /*exit point*/
                }
                default: {
                    System.out.println("Wrong choice");
                    break;
                }
            }
        } while (choice != 8);
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
        String catId = inputStringWithExit(ID_STRING_PATTERN, "q");
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
        Long id = inputLong();
        if (catService.deleteById(id)) {
            System.out.println(">> Cat not found");
        } else {
            System.out.println(">> Cat removed");
        }
    }

    private Cat createCat() {
        System.out.print(">> Input new cat name:\n<< ");
        String catName = inputString(SIMPLE_STRING_PATTERN);

        System.out.print(">> Input father id (if known or 0 if unknown):\n<< ");
        Long fatherId = inputLong();
        Cat father = catService.getById(fatherId);
        if (father != null) {
            while (father.getGender() != Gender.MALE) {
                System.out.print(">> This cat is female, input another id\n<< ");
                fatherId = inputLong();
                father = catService.getById(fatherId);
                if (father == null) {
                    break;
                }
            }
        }

        System.out.print(">> Input mother id (if known or 0 if unknown):\n<< ");
        Long motherId = inputLong();
        Cat mother = catService.getById(motherId);
        if (mother != null) {
            while (mother.getGender() != Gender.FEMALE) {
                System.out.print(">> This cat is male, input another id\n<< ");
                motherId = inputLong();
                mother = catService.getById(motherId);
                if (mother == null) {
                    break;
                }
            }
        }

        System.out.print(">> Input cat gender(male, female):\n<< ");
        Gender gender = Gender.valueOf(inputString(CAT_GENDER_PATTERN).toUpperCase());

        System.out.print(">> Input cat color: ");
        String color = inputString(SIMPLE_STRING_PATTERN);

        System.out.print(">> Input cat age(0, 20): ");
        int age = inputInt();
        while (age < 0 || age > 20) {
            System.out.print(">> Incorrect input! Input between 0 and 20\n<<");
            age = inputInt();
        }

        return Cat.builder()
                .name(catName)
                .father(father)
                .mother(mother)
                .gender(gender)
                .color(color)
                .age(age)
                .build();
    }

    private void printFamilyTree() {
        System.out.print(">> Input cat id\n<< ");
        Long id = (long) inputInt();
        final String tree = catService.buildFamilyTree(id);
        System.out.println(tree);
    }

    private void printCatsBySubstring() {
        System.out.print(">> Input a substring\n<< ");
        String substring = inputString(SIMPLE_STRING_PATTERN);
        final List<Cat> catsByName = catService.findCatsByName(substring);
        for (Cat cat : catsByName) {
            printCatInfo(cat);
        }
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

    private Long inputLong() {
        long i;
        while (!SCANNER.hasNextLong()) {
            System.out.print(">> Incorrect input! Try again.\n<< ");
            SCANNER.next();
        }
        i = SCANNER.nextLong();
        return i;
    }

    private String inputString(String pattern) {
        String str = SCANNER.next();
        while (!str.matches(pattern)) {
            System.out.print("<< Incorrect regex or too short\n>> ");
            str = SCANNER.next();
        }
        return str;
    }

    private String inputStringWithExit(String pattern, String exit) {
        String str = SCANNER.next();
        while (!str.matches(pattern)) {
            System.out.print(">> Incorrect input! Try again.\n<< ");
            str = SCANNER.next();
        }
        if (str.equals(exit)) {
            return null;
        }
        return str;
    }

}

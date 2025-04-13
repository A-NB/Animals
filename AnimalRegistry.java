import java.io.*;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

abstract class Animal implements Serializable {
    protected static long idCounter = 0;
    protected final long id;
    protected String name;
    protected LocalDate birthDate;
    protected String[] commands;

    public Animal(String name, LocalDate birthDate, String[] commands) {
        this.id = ++idCounter;
        this.name = name;
        this.birthDate = birthDate;
        this.commands = commands;
    }

    public long getId() {
        return id;
    }

    public abstract String getTypeRu();

    public String getInfo() {
        Period age = Period.between(birthDate, LocalDate.now());
        int years = age.getYears();
        int months = age.getMonths();

        String yearStr = years + " " + getYearWord(years);
        String monthStr = months + " " + getMonthWord(months);

        return id + ". " + getTypeRu() + " " + name + ", рожд. " + birthDate +
                " (" + yearStr + " и " + monthStr + "), команды: " +
                String.join(", ", commands);
    }

    private String getYearWord(int years) {
        if (years % 10 == 1 && years % 100 != 11)
            return "год";
        if (years % 10 >= 2 && years % 10 <= 4 && (years % 100 < 10 || years % 100 >= 20))
            return "года";
        return "лет";
    }

    private String getMonthWord(int months) {
        if (months % 10 == 1 && months % 100 != 11)
            return "месяц";
        if (months % 10 >= 2 && months % 10 <= 4 && (months % 100 < 10 || months % 100 >= 20))
            return "месяца";
        return "месяцев";
    }

    public boolean equals(Animal other) {
        if (this == other)
            return true;
        if (other == null || getClass() != other.getClass())
            return false;
        if (!this.name.equalsIgnoreCase(other.name))
            return false;
        if (!this.birthDate.equals(other.birthDate))
            return false;

        Set<String> thisCommands = new HashSet<>();
        for (String cmd : this.commands)
            thisCommands.add(cmd.trim().toLowerCase());

        Set<String> otherCommands = new HashSet<>();
        for (String cmd : other.commands)
            otherCommands.add(cmd.trim().toLowerCase());

        return thisCommands.equals(otherCommands);
    }

    public static void setIdCounter(long idCounter) {
        Animal.idCounter = idCounter;
    }
}

class Pet extends Animal {
    public Pet(String name, LocalDate birthDate, String[] commands) {
        super(name, birthDate, commands);
    }

    @Override
    public String getTypeRu() {
        return "Домашнее животное";
    }
}

class PackAnimal extends Animal {
    public PackAnimal(String name, LocalDate birthDate, String[] commands) {
        super(name, birthDate, commands);
    }

    @Override
    public String getTypeRu() {
        return "Вьючное животное";
    }
}

class Dog extends Pet {
    public Dog(String name, LocalDate birthDate, String[] commands) {
        super(name, birthDate, commands);
    }

    @Override
    public String getTypeRu() {
        return "Собака";
    }
}

class Cat extends Pet {
    public Cat(String name, LocalDate birthDate, String[] commands) {
        super(name, birthDate, commands);
    }

    @Override
    public String getTypeRu() {
        return "Кот(кошка)";
    }
}

class Hamster extends Pet {
    public Hamster(String name, LocalDate birthDate, String[] commands) {
        super(name, birthDate, commands);
    }

    @Override
    public String getTypeRu() {
        return "Хомяк";
    }
}

class Horse extends PackAnimal {
    public Horse(String name, LocalDate birthDate, String[] commands) {
        super(name, birthDate, commands);
    }

    @Override
    public String getTypeRu() {
        return "Лошадь";
    }
}

class Camel extends PackAnimal {
    public Camel(String name, LocalDate birthDate, String[] commands) {
        super(name, birthDate, commands);
    }

    @Override
    public String getTypeRu() {
        return "Верблюд";
    }
}

class Donkey extends PackAnimal {
    public Donkey(String name, LocalDate birthDate, String[] commands) {
        super(name, birthDate, commands);
    }

    @Override
    public String getTypeRu() {
        return "Осёл";
    }
}

public class AnimalRegistry {
    private static final List<Animal> animals = new ArrayList<>();
    private static final String FILE_NAME = "animals.ser";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in, "CP866");
        boolean running = true;

        while (running) {
            System.out.println("""
                    1. Добавить животное
                    2. Загрузить из файла
                    3. Список команд животного
                    4. Обучить животное новым командам
                    5. Список животных по дате рождения
                    6. Фильтр по типу животных
                    7. Удалить животное
                    8. Редактировать животное
                    9. Показать всех животных
                    10. Сохранить в файл
                    11. Выход
                    """);

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> addAnimal(scanner);
                case 2 -> loadFromFile();
                case 3 -> listCommands(scanner);
                case 4 -> trainAnimal(scanner);
                case 5 -> listByBirthDate();
                case 6 -> filterByType(scanner);
                case 7 -> deleteAnimal(scanner);
                case 8 -> editAnimal(scanner);
                case 9 -> showAllAnimals();
                case 10 -> saveToFile();
                case 11 -> running = false;
            }
        }
    }

    private static void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(animals);
            System.out.println("Сохранение выполнено успешно.\n");
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении: " + e.getMessage() + "\n");
        }
    }

    private static void loadFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            List<Animal> loaded = (List<Animal>) ois.readObject();
            animals.clear();
            animals.addAll(loaded);
            // Устанавливаем правильное значение idCounter
            long maxId = animals.stream().mapToLong(Animal::getId).max().orElse(0);
            Animal.setIdCounter(maxId);
            System.out.println("Загрузка выполнена успешно. Загружено животных: " + animals.size() + "\n");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Ошибка при загрузке: " + e.getMessage() + "\n");
        }
    }

    private static LocalDate readDate(Scanner scanner) {
        while (true) {
            try {
                System.out.print("Введите дату рождения (yyyy-MM-dd): ");
                return LocalDate.parse(scanner.nextLine(), DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.println("Некорректный формат даты. Повторите ввод.");
            }
        }
    }

    private static void addAnimal(Scanner scanner) {
        System.out.println("""
                Выберите тип животного:
                1. Собака
                2. Кошка
                3. Хомяк
                4. Лошадь
                5. Верблюд
                6. Осёл
                """);

        int type = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Введите имя животного: ");
        String name = scanner.nextLine();

        LocalDate birthDate = readDate(scanner);

        System.out.print("Введите команды через запятую: ");
        String[] commands = scanner.nextLine().split(",\\s*");

        Animal animal = switch (type) {
            case 1 -> new Dog(name, birthDate, commands);
            case 2 -> new Cat(name, birthDate, commands);
            case 3 -> new Hamster(name, birthDate, commands);
            case 4 -> new Horse(name, birthDate, commands);
            case 5 -> new Camel(name, birthDate, commands);
            case 6 -> new Donkey(name, birthDate, commands);
            default -> null;
        };

        if (animal != null) {
            boolean duplicate = animals.stream().anyMatch(a -> a.equals(animal));
            if (duplicate) {
                System.out.print("Животное уже существует. Добавить всё равно? (да/нет): ");
                if (scanner.nextLine().equalsIgnoreCase("да")) {
                    animals.add(animal);
                    System.out.println("Животное добавлено.\n");
                }
            } else {
                animals.add(animal);
                System.out.println("Животное добавлено.\n");
            }
        } else {
            System.out.println("Неверный выбор типа.\n");
        }
    }

    private static Animal findAnimalById(long id) {
        return animals.stream().filter(a -> a.getId() == id).findFirst().orElse(null);
    }

    private static void listCommands(Scanner scanner) {
        System.out.print("Введите ID животного: ");
        long id = scanner.nextLong();
        scanner.nextLine();

        Animal a = findAnimalById(id);
        if (a != null) {
            System.out.println("Команды: " + String.join(", ", a.commands) + "\n");
        } else {
            System.out.println("Животное не найдено.\n");
        }
    }

    private static void trainAnimal(Scanner scanner) {
        System.out.print("Введите ID животного: ");
        long id = scanner.nextLong();
        scanner.nextLine();

        Animal a = findAnimalById(id);
        if (a != null) {
            System.out.print("Введите новую команду: ");
            String newCmd = scanner.nextLine();
            Set<String> cmdSet = new HashSet<>(Arrays.asList(a.commands));
            if (cmdSet.add(newCmd)) {
                a.commands = cmdSet.toArray(new String[0]);
                System.out.println("Команда добавлена.\n");
            } else {
                System.out.println("Животное уже знает эту команду.\n");
            }
        } else {
            System.out.println("Животное не найдено.\n");
        }
    }

    private static void listByBirthDate() {
        animals.stream()
                .sorted(Comparator.comparing(a -> a.birthDate))
                .forEach(a -> System.out.println(a.getInfo()));
        System.out.println();
    }

    private static void filterByType(Scanner scanner) {
        System.out.println("""
                Выберите тип:
                1. Собака
                2. Кошка
                3. Хомяк
                4. Лошадь
                5. Верблюд
                6. Осёл
                """);

        int choice = scanner.nextInt();
        scanner.nextLine();

        String className = switch (choice) {
            case 1 -> "Dog";
            case 2 -> "Cat";
            case 3 -> "Hamster";
            case 4 -> "Horse";
            case 5 -> "Camel";
            case 6 -> "Donkey";
            default -> null;
        };

        if (className != null) {
            animals.stream()
                    .filter(a -> a.getClass().getSimpleName().equals(className))
                    .forEach(a -> System.out.println(a.getInfo()));
        } else {
            System.out.println("Неверный выбор типа.\n");
        }
    }

    private static void deleteAnimal(Scanner scanner) {
        System.out.print("Введите ID животного для удаления: ");
        long id = scanner.nextLong();
        scanner.nextLine();

        Animal a = findAnimalById(id);
        if (a != null) {
            animals.remove(a);
            System.out.println("Животное удалено.\n");
        } else {
            System.out.println("Животное не найдено.\n");
        }
    }

    private static void editAnimal(Scanner scanner) {
        System.out.print("Введите ID животного для редактирования: ");
        long id = scanner.nextLong();
        scanner.nextLine();

        Animal a = findAnimalById(id);
        if (a != null) {
            System.out.print("Введите новое имя животного: ");
            a.name = scanner.nextLine();
            a.birthDate = readDate(scanner);
            System.out.print("Введите новые команды через запятую: ");
            a.commands = scanner.nextLine().split(",\\s*");
            System.out.println("Животное обновлено.\n");
        } else {
            System.out.println("Животное не найдено.\n");
        }
    }

    private static void showAllAnimals() {
        if (animals.isEmpty()) {
            System.out.println("Нет зарегистрированных животных.\n");
        } else {
            long totalAnimals = animals.size();
            long totalPets = animals.stream().filter(a -> a instanceof Pet).count();
            long totalPackAnimals = animals.stream().filter(a -> a instanceof PackAnimal).count();

            System.out.println("Всего животных: " + totalAnimals);

            if (totalPets > 0) {
                System.out.println("  Домашних: " + totalPets);
                long totalDogs = animals.stream().filter(a -> a instanceof Dog).count();
                if (totalDogs > 0) {
                    System.out.println("    Собак: " + totalDogs);
                    animals.stream().filter(a -> a instanceof Dog)
                            .forEach(a -> System.out.println("      " + a.getInfo()));
                }

                long totalCats = animals.stream().filter(a -> a instanceof Cat).count();
                if (totalCats > 0) {
                    System.out.println("    Кошек: " + totalCats);
                    animals.stream().filter(a -> a instanceof Cat)
                            .forEach(a -> System.out.println("      " + a.getInfo()));
                }

                long totalHamsters = animals.stream().filter(a -> a instanceof Hamster).count();
                if (totalHamsters > 0) {
                    System.out.println("    Хомяков: " + totalHamsters);
                    animals.stream().filter(a -> a instanceof Hamster)
                            .forEach(a -> System.out.println("      " + a.getInfo()));
                }
            }

            if (totalPackAnimals > 0) {
                System.out.println("  Вьючных: " + totalPackAnimals);
                long totalHorses = animals.stream().filter(a -> a instanceof Horse).count();
                if (totalHorses > 0) {
                    System.out.println("    Лошадей: " + totalHorses);
                    animals.stream().filter(a -> a instanceof Horse)
                            .forEach(a -> System.out.println("      " + a.getInfo()));
                }

                long totalCamels = animals.stream().filter(a -> a instanceof Camel).count();
                if (totalCamels > 0) {
                    System.out.println("    Верблюдов: " + totalCamels);
                    animals.stream().filter(a -> a instanceof Camel)
                            .forEach(a -> System.out.println("      " + a.getInfo()));
                }

                long totalDonkeys = animals.stream().filter(a -> a instanceof Donkey).count();
                if (totalDonkeys > 0) {
                    System.out.println("    Ослов: " + totalDonkeys);
                    animals.stream().filter(a -> a instanceof Donkey)
                            .forEach(a -> System.out.println("      " + a.getInfo()));
                }
            }

            System.out.println();
        }
    }

}

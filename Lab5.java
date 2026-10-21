
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

// Базовий абстрактний клас для всіх записів
abstract class RecordBase implements Comparable<RecordBase> {
    public abstract String show();
    public abstract String sortKey();

    @Override
    public int compareTo(RecordBase other) {
        return sortKey().compareToIgnoreCase(other.sortKey());
    }

    @Override
    public String toString() {
        return show();
    }
}

// Ієрархія працівників
class Person extends RecordBase {
    protected String name;
    protected int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public String show() {
        return "Person: " + name + ", age: " + age;
    }

    @Override
    public String sortKey() {
        return name;
    }
}

class Employee extends Person {
    protected double salary;

    public Employee(String name, int age, double salary) {
        super(name, age);
        this.salary = salary;
    }

    @Override
    public String show() {
        return "Employee: " + name + ", age: " + age
                + ", salary: " + salary;
    }
}

class Worker extends Employee {
    private String position;

    public Worker(String name, int age, double salary, String position) {
        super(name, age, salary);
        this.position = position;
    }

    @Override
    public String show() {
        return "Worker: " + name + ", age: " + age
                + ", salary: " + salary + ", position: " + position;
    }

    @Override
    public String sortKey() {
        return name;
    }

    public static Worker parse(String line) {
        String[] p = line.split(";", -1);
        return new Worker(p[0], Integer.parseInt(p[1]),
                Double.parseDouble(p[2]), p[3]);
    }
}

class Engineer extends Employee {
    private String specialty;

    public Engineer(String name, int age, double salary, String specialty) {
        super(name, age, salary);
        this.specialty = specialty;
    }

    @Override
    public String show() {
        return "Engineer: " + name + ", age: " + age
                + ", salary: " + salary + ", specialty: " + specialty;
    }

    @Override
    public String sortKey() {
        return name;
    }

    public static Engineer parse(String line) {
        String[] p = line.split(";", -1);
        return new Engineer(p[0], Integer.parseInt(p[1]),
                Double.parseDouble(p[2]), p[3]);
    }
}

// Абстрактний клас геометричних тіл
abstract class Body extends RecordBase {
    public abstract double surfaceArea();
    public abstract double volume();

    @Override
    public String show() {
        return String.format(Locale.US,
                "%s: area=%.2f, volume=%.2f",
                getClass().getSimpleName(), surfaceArea(), volume());
    }
}

class Parallelepiped extends Body {
    private double a, b, c;

    public Parallelepiped(double a, double b, double c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    @Override
    public double surfaceArea() {
        return 2 * (a * b + b * c + a * c);
    }

    @Override
    public double volume() {
        return a * b * c;
    }

    @Override
    public String sortKey() {
        return "Parallelepiped-" + String.format(Locale.US, "%010.2f", volume());
    }

    @Override
    public String show() {
        return String.format(Locale.US,
                "Parallelepiped: a=%.2f, b=%.2f, c=%.2f, area=%.2f, volume=%.2f",
                a, b, c, surfaceArea(), volume());
    }

    public static Parallelepiped parse(String line) {
        String[] p = line.split(";", -1);
        return new Parallelepiped(
                Double.parseDouble(p[0]),
                Double.parseDouble(p[1]),
                Double.parseDouble(p[2]));
    }
}

class Ball extends Body {
    private double radius;

    public Ball(double radius) {
        this.radius = radius;
    }

    @Override
    public double surfaceArea() {
        return 4 * Math.PI * radius * radius;
    }

    @Override
    public double volume() {
        return 4.0 / 3.0 * Math.PI * radius * radius * radius;
    }

    @Override
    public String sortKey() {
        return "Ball-" + String.format(Locale.US, "%010.2f", volume());
    }

    @Override
    public String show() {
        return String.format(Locale.US,
                "Ball: radius=%.2f, area=%.2f, volume=%.2f",
                radius, surfaceArea(), volume());
    }

    public static Ball parse(String line) {
        return new Ball(Double.parseDouble(line.trim()));
    }
}

// Компаратор для сортування всіх типів записів
class RecordComparator implements Comparator<RecordBase> {
    @Override
    public int compare(RecordBase a, RecordBase b) {
        int result = a.getClass().getSimpleName()
                .compareToIgnoreCase(b.getClass().getSimpleName());

        if (result == 0) {
            result = a.sortKey().compareToIgnoreCase(b.sortKey());
        }

        return result;
    }
}

public class Lab5 {
    private static final Scanner sc = new Scanner(System.in);

    // Створення демонстраційних файлів, якщо вони відсутні
    static void createFileIfMissing(String file, String content)
            throws IOException {
        Path path = Paths.get(file);

        if (Files.notExists(path)) {
            Files.write(path, content.getBytes(StandardCharsets.UTF_8));
        }
    }

    static List<String> readLines(String file) throws IOException {
        return Files.readAllLines(Paths.get(file), StandardCharsets.UTF_8);
    }

    static void printRecords(String title, List<? extends RecordBase> records) {
        System.out.println("\n" + title);
        if (records.isEmpty()) {
            System.out.println("(немає записів)");
        }

        for (RecordBase record : records) {
            System.out.println(record.show());
        }
    }

    static String ask(String message) {
        System.out.print(message);
        return sc.nextLine().trim();
    }

    static int askInt(String message) {
        while (true) {
            try {
                return Integer.parseInt(ask(message));
            } catch (NumberFormatException e) {
                System.out.println("Введіть ціле число.");
            }
        }
    }

    static double askDouble(String message) {
        while (true) {
            try {
                return Double.parseDouble(ask(message).replace(',', '.'));
            } catch (NumberFormatException e) {
                System.out.println("Введіть число.");
            }
        }
    }

    public static void main(String[] args) {
        try {
            // Кожен тип запису має окремий файл
            createFileIfMissing("workers.txt",
                    "Іван;30;18000;Оператор\nОлег;25;16000;Майстер\n");
            createFileIfMissing("engineers.txt",
                    "Андрій;28;25000;Програміст\nМарія;32;27000;Системний інженер\n");
            createFileIfMissing("parallelepipeds.txt",
                    "2;3;4\n5;2;3\n");
            createFileIfMissing("balls.txt",
                    "3\n5\n");

            // Завдання 1. Читання даних у колекції ArrayList
            List<Worker> workers = new ArrayList<>();
            for (String line : readLines("workers.txt")) {
                if (!line.trim().isEmpty()) workers.add(Worker.parse(line));
            }

            List<Engineer> engineers = new ArrayList<>();
            for (String line : readLines("engineers.txt")) {
                if (!line.trim().isEmpty()) engineers.add(Engineer.parse(line));
            }

            List<Parallelepiped> parallelepipeds = new ArrayList<>();
            for (String line : readLines("parallelepipeds.txt")) {
                if (!line.trim().isEmpty())
                    parallelepipeds.add(Parallelepiped.parse(line));
            }

            List<Ball> balls = new ArrayList<>();
            for (String line : readLines("balls.txt")) {
                if (!line.trim().isEmpty()) balls.add(Ball.parse(line));
            }

            System.out.println("========== ЛАБОРАТОРНА РОБОТА №5 ==========");
            System.out.println("ЗАВДАННЯ 1. КОЛЕКЦІЇ");

            printRecords("Працівники з файлу:", workers);
            printRecords("Інженери з файлу:", engineers);
            printRecords("Паралелепіпеди з файлу:", parallelepipeds);
            printRecords("Кулі з файлу:", balls);

            // Сортування за Comparable
            Collections.sort(workers);
            Collections.sort(engineers);
            Collections.sort(parallelepipeds);
            Collections.sort(balls);

            System.out.println("\n========== ПІСЛЯ СОРТУВАННЯ Comparable ==========");
            printRecords("Працівники:", workers);
            printRecords("Інженери:", engineers);
            printRecords("Паралелепіпеди:", parallelepipeds);
            printRecords("Кулі:", balls);

            // Додавання по одному запису з клавіатури
            System.out.println("\n========== ДОДАВАННЯ ЗАПИСІВ ==========");

            System.out.println("\nНовий працівник:");
            workers.add(new Worker(
                    ask("Ім'я: "),
                    askInt("Вік: "),
                    askDouble("Зарплата: "),
                    ask("Посада: ")));

            System.out.println("\nНовий інженер:");
            engineers.add(new Engineer(
                    ask("Ім'я: "),
                    askInt("Вік: "),
                    askDouble("Зарплата: "),
                    ask("Спеціальність: ")));

            System.out.println("\nНовий паралелепіпед:");
            parallelepipeds.add(new Parallelepiped(
                    askDouble("Сторона a: "),
                    askDouble("Сторона b: "),
                    askDouble("Сторона c: ")));

            System.out.println("\nНова куля:");
            balls.add(new Ball(askDouble("Радіус: ")));

            Collections.sort(workers);
            Collections.sort(engineers);
            Collections.sort(parallelepipeds);
            Collections.sort(balls);

            System.out.println("\n========== ПІСЛЯ ДОДАВАННЯ ТА СОРТУВАННЯ ==========");
            printRecords("Працівники:", workers);
            printRecords("Інженери:", engineers);
            printRecords("Паралелепіпеди:", parallelepipeds);
            printRecords("Кулі:", balls);

            // Спільний ArrayList на основі абстрактного RecordBase
            List<RecordBase> allRecords = new ArrayList<>();
            allRecords.addAll(workers);
            allRecords.addAll(engineers);
            allRecords.addAll(parallelepipeds);
            allRecords.addAll(balls);

            // Сортування через власний Comparator
            Collections.sort(allRecords, new RecordComparator());

            printRecords("========== СПІЛЬНИЙ ВІДСОРТОВАНИЙ СПИСОК ==========",
                    allRecords);

            // Збереження спільного списку у файл
            try (BufferedWriter writer = Files.newBufferedWriter(
                    Paths.get("all_records.txt"), StandardCharsets.UTF_8)) {
                for (RecordBase record : allRecords) {
                    writer.write(record.show());
                    writer.newLine();
                }
            }

            System.out.println("\nУсі записи збережено у файлі all_records.txt");

            // Завдання 2. Перевертання числа за допомогою Stack
            System.out.println("\n========== ЗАВДАННЯ 2. СТЕК ==========");
            String number = ask("Введіть ціле число: ");

            if (!number.matches("[+-]?\\d+")) {
                System.out.println("Помилка: потрібно ввести ціле число.");
                return;
            }

            Stack<Character> stack = new Stack<>();
            boolean negative = number.startsWith("-");
            int start = (number.startsWith("-") || number.startsWith("+")) ? 1 : 0;

            for (int i = start; i < number.length(); i++) {
                stack.push(number.charAt(i));
            }

            StringBuilder reversed = new StringBuilder();
            while (!stack.empty()) {
                reversed.append(stack.pop());
            }

            // Зберігаємо знак, але міняємо порядок цифр
            String result = (negative ? "-" : "") + reversed;
            System.out.println("Число у зворотному порядку: " + result);

            System.out.println("\nПрограму виконано успішно!");

        } catch (IOException e) {
            System.out.println("Помилка роботи з файлами: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Помилка формату вхідних даних: " + e.getMessage());
        }
    }
}

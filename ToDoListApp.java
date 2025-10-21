package org.example;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;


public class ToDoListApp {
    private static final List<Task> tasks = new ArrayList<>();
    private static int nextId = 1;
    private static final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter RUSSIAN_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private static final DateTimeFormatter DISPLAY_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    public static void main(String[] args) {
        System.out.println("Приветик! Готов к задачам? Скорей заполни To-Do List!");

        while (true) {
            printMenu();
            int choice = readIntInput("Выберите пункт меню: ");

            switch (choice) {
                case 1 -> viewAllTasks();
                case 2 -> createTask();
                case 3 -> editTask();
                case 4 -> deleteTask();
                case 5 -> changeTaskPriority();
                case 6 -> sortTasksByDate();
                case 7 -> searchTasks();
                case 8 -> showStatistics();
                case 9 -> sortTasksByPriority();
                case 10 -> searchByDateRange();
                case 0 -> {
                    System.out.println("Пока! Не забывай отдыхать :)!");
                    scanner.close();
                    return;
                }
                default -> System.out.println("Неверный пункт меню. Попробуйте снова.");
            }
        }
    }

    private static void printMenu() {
        System.out.println("\nГЛАВНОЕ МЕНЮ");
        System.out.println("1. Просмотреть все задачи");
        System.out.println("2. Создать задачу");
        System.out.println("3. Редактировать задачу");
        System.out.println("4. Удалить задачу");
        System.out.println("5. Изменить приоритет задачи");
        System.out.println("6. Отсортировать задачи по дате");
        System.out.println("7. Найти задачи");
        System.out.println("8. Статистика задач");
        System.out.println("9. Сортировка по приоритету");
        System.out.println("10. Поиск по диапазону дат");
        System.out.println("0. Выход");
    }

    private static int readIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: Введите целое число!");
            }
        }
    }

    private static void viewAllTasks() {
        if (tasks.isEmpty()) {
            System.out.println("К сожалению, список задач пуст.");
        } else {
            System.out.println("\n================================================");
            System.out.println("              СПИСОК ВСЕХ ЗАДАЧ");
            System.out.println("================================================");

            for (int i = 0; i < tasks.size(); i++) {
                Task task = tasks.get(i);

                System.out.println("ЗАДАЧА #" + task.getId());
                System.out.println("------------------------------------------------");
                System.out.println("Заголовок:  " + task.getTitle());
                System.out.println("Описание:   " + task.getDescription());
                System.out.println("Дедлайн:    " + task.getDeadline().format(DISPLAY_DATE_FORMATTER));
                System.out.println("Приоритет:  " + task.getPriority().getRussianName());
                System.out.println("Статус:     " + task.getStatus().getRussianName());
                System.out.println("Создана:    " + task.getCreatedAt().format(DISPLAY_DATE_FORMATTER));

                if (i < tasks.size() - 1) {
                    System.out.println("\n------------------------------------------------");
                }
            }

            System.out.println("================================================");
            System.out.println("Всего задач: " + tasks.size());
            System.out.println("================================================\n");
        }
    }

    private static void searchByDateRange() {
        if (tasks.isEmpty()) {
            System.out.println("Список пуст.");
            return;
        }

        System.out.println("\nПОИСК ПО ДИАПАЗОНУ ДАТ");
        System.out.println("Введите начальную дату:");
        LocalDateTime startDate = readDateTimeInput();
        System.out.println("Введите конечную дату:");
        LocalDateTime endDate = readDateTimeInput();

        if (startDate.isAfter(endDate)) {
            System.out.println("Ошибка: Ведь начальная дата не может быть позже конечной.");
            return;
        }

        List<Task> results = tasks.stream()
                .filter(t -> (t.getDeadline().isEqual(startDate) || t.getDeadline().isAfter(startDate))
                        && (t.getDeadline().isEqual(endDate) || t.getDeadline().isBefore(endDate)))
                .sorted(Comparator.comparing(Task::getDeadline))
                .toList();

        if (results.isEmpty()) {
            System.out.println("Задачи в указанном диапазоне не найдены");
        } else {
            System.out.println("\n================================================");
            System.out.println("           РЕЗУЛЬТАТЫ ПОИСКА");
            System.out.println("  с " + startDate.format(DISPLAY_DATE_FORMATTER) +
                    " по " + endDate.format(DISPLAY_DATE_FORMATTER));
            System.out.println("================================================");

            for (int i = 0; i < results.size(); i++) {
                Task task = results.get(i);

                System.out.println("ЗАДАЧА #" + task.getId());
                System.out.println("------------------------------------------------");
                System.out.println("Заголовок:  " + task.getTitle());
                System.out.println("Описание:   " + task.getDescription());
                System.out.println("Дедлайн:    " + task.getDeadline().format(DISPLAY_DATE_FORMATTER));
                System.out.println("Приоритет:  " + task.getPriority().getRussianName());
                System.out.println("Статус:     " + task.getStatus().getRussianName());
                System.out.println("Создана:    " + task.getCreatedAt().format(DISPLAY_DATE_FORMATTER));

                if (i < results.size() - 1) {
                    System.out.println("\n------------------------------------------------");
                }
            }

            System.out.println("================================================");
            System.out.println("Найдено задач: " + results.size());
            System.out.println("================================================\n");
        }
    }

    private static void showStatistics() {
        if (tasks.isEmpty()) {
            System.out.println("Список задач пуст.");
            return;
        }

        long totalTasks = tasks.size();
        long completed = tasks.stream().filter(t -> t.getStatus() == Status.DONE).count();
        long inProgress = tasks.stream().filter(t -> t.getStatus() == Status.IN_PROGRESS).count();
        long todo = tasks.stream().filter(t -> t.getStatus() == Status.TO_DO).count();
        long highPriority = tasks.stream().filter(t -> t.getPriority() == Priority.HIGH).count();
        long mediumPriority = tasks.stream().filter(t -> t.getPriority() == Priority.MEDIUM).count();
        long lowPriority = tasks.stream().filter(t -> t.getPriority() == Priority.LOW).count();

        long overdue = tasks.stream()
                .filter(t -> t.getDeadline().isBefore(LocalDateTime.now()) && t.getStatus() != Status.DONE)
                .count();

        System.out.println("\n================================================");
        System.out.println("              СТАТИСТИКА ЗАДАЧ");
        System.out.println("================================================");
        System.out.println("Всего задач: " + totalTasks);
        System.out.println("Выполнено: " + completed);
        System.out.println("В работе: " + inProgress);
        System.out.println("К выполнению: " + todo);
        System.out.println("Высокий приоритет: " + highPriority);
        System.out.println("Средний приоритет: " + mediumPriority);
        System.out.println("Низкий приоритет: " + lowPriority);
        System.out.println("Просрочено: " + overdue);

        double completionRate = (double) completed / totalTasks * 100;
        System.out.printf("Процент выполнения: %.1f%%\n", completionRate);
        System.out.println("================================================\n");
    }


    private static void sortTasksByPriority() {
        if (tasks.isEmpty()) {
            System.out.println("К сожалению, список пуст.");
            return;
        }

        System.out.println("Сортировать по приоритету:");
        System.out.println("1. Сначала высокий приоритет");
        System.out.println("2. Сначала низкий приоритет");
        int choice = readIntInput("Ваш выбор: ");

        switch (choice) {
            case 1:
                tasks.sort((task1, task2) -> {
                    int weight1 = getPriorityWeight(task1.getPriority());
                    int weight2 = getPriorityWeight(task2.getPriority());
                    return Integer.compare(weight2, weight1);
                });
                break;
            case 2:
                tasks.sort((task1, task2) -> {
                    int weight1 = getPriorityWeight(task1.getPriority());
                    int weight2 = getPriorityWeight(task2.getPriority());
                    return Integer.compare(weight1, weight2);
                });
                break;
            default:
                System.out.println("Неверный выбор.");
                return;
        }

        System.out.println("Задачи отсортированы по приоритету.");
        viewAllTasks();
    }
    private static int getPriorityWeight(Priority priority) {
        return switch (priority) {
            case HIGH -> 3;
            case MEDIUM -> 2;
            case LOW -> 1;
        };
    }

    private static void createTask() {
        System.out.println("\nСОЗДАНИЕ НОВОЙ ЗАДАЧИ");
        System.out.print("Введите заголовок: ");
        String title = scanner.nextLine();
        System.out.print("Введите описание: ");
        String description = scanner.nextLine();
        LocalDateTime deadline = readDateTimeInput();
        Priority priority = readPriorityInput();
        Status status = readStatusInput();

        Task newTask = new Task(nextId++, title, description, deadline, priority, status);
        tasks.add(newTask);
        System.out.println("Задача успешно создана!");
    }

    private static LocalDateTime readDateTimeInput() {
        while (true) {
            try {
                System.out.print("Введите срок выполнения (ДД.ММ.ГГГГ ЧЧ:ММ, например 11.12.2025 23:59): ");
                String input = scanner.nextLine();
                return LocalDateTime.parse(input, RUSSIAN_DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.println("Ошибка формата даты. Используйте формат ДД.ММ.ГГГГ ЧЧ:ММ (например 11.12.2025 23:59). Попробуйте снова.");
            }
        }
    }

    private static Priority readPriorityInput() {
        while (true) {
            System.out.println("Выберите приоритет:");
            for (Priority p : Priority.values()) {
                System.out.println((p.ordinal() + 1) + ". " + p.getRussianName());
            }
            int choice = readIntInput("Ваш выбор: ");
            if (choice >= 1 && choice <= Priority.values().length) {
                return Priority.values()[choice - 1];
            }
            System.out.println("Неверный выбор.");
        }
    }

    private static void editTask() {
        viewAllTasks();
        if (tasks.isEmpty()) return;

        int id = readIntInput("Введите ID задачи для редактирования: ");
        Task task = findTaskById(id);

        if (task == null) {
            System.out.println("Задача с ID " + id + " не найдена.");
            return;
        }

        System.out.println("Редактирование задачи: " + task.getTitle());
        System.out.println("Оставьте поле пустым чтобы не менять:");

        System.out.print("Новый заголовок [" + task.getTitle() + "]: ");
        String newTitle = scanner.nextLine();
        if (!newTitle.isEmpty()) task.setTitle(newTitle);

        System.out.print("Новое описание [" + task.getDescription() + "]: ");
        String newDesc = scanner.nextLine();
        if (!newDesc.isEmpty()) task.setDescription(newDesc);

        System.out.println("Хотите изменить срок выполнения? (да/нет)");
        String changeDate = scanner.nextLine().toLowerCase();
        if (changeDate.equals("да") || changeDate.equals("д") || changeDate.equals("yes") || changeDate.equals("y")) {
            LocalDateTime newDeadline = readDateTimeInput();
            task.setDeadline(newDeadline);
        }

        System.out.println("Хотите изменить статус? (да/нет)");
        String changeStatus = scanner.nextLine().toLowerCase();
        if (changeStatus.equals("да") || changeStatus.equals("д") || changeStatus.equals("yes") || changeStatus.equals("y")) {
            Status newStatus = readStatusInput();
            task.setStatus(newStatus);
            System.out.println("Статус обновлен на: " + newStatus.getRussianName());
        }

        System.out.println("Задача обновлена!");
    }

    private static void deleteTask() {
        viewAllTasks();
        if (tasks.isEmpty()) return;

        int id = readIntInput("Введите ID задачи для удаления: ");
        Task task = findTaskById(id);

        if (task == null) {
            System.out.println("Задача с ID " + id + " не найдена.");
        } else {
            tasks.remove(task);
            System.out.println("Задача удалена.");
        }
    }

    private static void changeTaskPriority() {
        viewAllTasks();
        if (tasks.isEmpty()) return;

        int id = readIntInput("Введите ID задачи для смены приоритета: ");
        Task task = findTaskById(id);

        if (task == null) {
            System.out.println("Задача не найдена.");
            return;
        }

        Priority newPriority = readPriorityInput();
        task.setPriority(newPriority);
        System.out.println("Приоритет обновлен!");
    }

    private static void sortTasksByDate() {
        if (tasks.isEmpty()) {
            System.out.println("К сожалению, список пуст.");
            return;
        }

        System.out.println("Сортировать по:");
        System.out.println("1. Дате создания (старые)");
        System.out.println("2. Дате создания (новые)");
        System.out.println("3. Дедлайну (ближайшие)");
        System.out.println("4. Дедлайну (поздние)");
        int choice = readIntInput("Ваш выбор: ");

        Comparator<Task> comparator = switch (choice) {
            case 1 -> Comparator.comparing(Task::getCreatedAt);
            case 2 -> Comparator.comparing(Task::getCreatedAt).reversed();
            case 3 -> Comparator.comparing(task ->
                    Math.abs(java.time.Duration.between(LocalDateTime.now(), task.getDeadline()).getSeconds())
            );
            case 4 -> Comparator.<Task, Long>comparing(task ->
                    Math.abs(java.time.Duration.between(LocalDateTime.now(), task.getDeadline()).getSeconds())
            ).reversed();
            default -> null;
        };

        if (comparator != null) {
            tasks.sort(comparator);
            System.out.println("Задачи отсортированы.");
            viewAllTasks();
        }
    }

    private static void searchTasks() {
        if (tasks.isEmpty()) {
            System.out.println("К сожалению, список пуст.");
            return;
        }

        System.out.println("Поиск по:");
        System.out.println("1. Заголовку");
        System.out.println("2. Статусу");
        int choice = readIntInput("Ваш выбор: ");

        List<Task> results = new ArrayList<>();
        switch (choice) {
            case 1 -> {
                System.out.print("Введите ключевое слово: ");
                String keyword = scanner.nextLine().toLowerCase();
                results = tasks.stream()
                        .filter(task -> task.getTitle().toLowerCase().contains(keyword))
                        .toList();
            }
            case 2 -> {
                Status status = readStatusInput();
                results = tasks.stream()
                        .filter(task -> task.getStatus() == status)
                        .toList();
            }
            default -> System.out.println("Неверный выбор.");
        }

        if (results.isEmpty()) {
            System.out.println("Задачи не найдены.");
        } else {
            System.out.println("\n================================================");
            System.out.println("           РЕЗУЛЬТАТЫ ПОИСКА");
            System.out.println("================================================");

            for (int i = 0; i < results.size(); i++) {
                Task task = results.get(i);

                System.out.println("ЗАДАЧА #" + task.getId());
                System.out.println("------------------------------------------------");
                System.out.println("Заголовок:  " + task.getTitle());
                System.out.println("Описание:   " + task.getDescription());
                System.out.println("Дедлайн:    " + task.getDeadline().format(DISPLAY_DATE_FORMATTER));
                System.out.println("Приоритет:  " + task.getPriority().getRussianName());
                System.out.println("Статус:     " + task.getStatus().getRussianName());
                System.out.println("Создана:    " + task.getCreatedAt().format(DISPLAY_DATE_FORMATTER));

                if (i < results.size() - 1) {
                    System.out.println("\n------------------------------------------------");
                }
            }

            System.out.println("================================================");
            System.out.println("Найдено задач: " + results.size());
            System.out.println("================================================\n");
        }
    }


    private static Status readStatusInput() {
        while (true) {
            System.out.println("Выберите статус:");
            for (Status s : Status.values()) {
                System.out.println((s.ordinal() + 1) + ". " + s.getRussianName());
            }
            int choice = readIntInput("Ваш выбор: ");
            if (choice >= 1 && choice <= Status.values().length) {
                return Status.values()[choice - 1];
            }
            System.out.println("Неверный выбор.");
        }
    }
    private static Task findTaskById(int id) {
        for (Task task : tasks) {
            if (task.getId() == id) {
                return task;
            }
        }
        return null;
    }
}
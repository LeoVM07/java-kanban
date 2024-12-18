public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        //Свои комментарии с замечаниями и непонятками я потру после сдачи работы =)

        TaskManager manager = Managers.getDefault();
        Task task1 = new Task("Пересмотреть Гарри Поттера", "Иначе не будет новогоднего настроения!");
        manager.addTask(task1);
        Task task2 = new Task("Досмотреть Отчаянных домохозяек", "Кто же всё-таки убил Лору Палмер???",
                Task.Status.IN_PROGRESS);
        manager.addTask(task2);
        Task task3 = new Task("В понедельник закончить бегать", "Вот это поворот!", Task.Status.DONE);
        manager.addTask(task3);

        Epic epic1 = new Epic("Устроить светский раут с блэкджеком и мавританками", "Всё будет " +
                "культурно, без лишнего кутежа");
        manager.addEpic(epic1);
        SubTask subTask1 = new SubTask("Купить карты для блэкджека", "А то в блэкджек неудобно играть будет",
                epic1.getId());
        manager.addSubTask(subTask1);
        SubTask subTask2 = new SubTask("Пригласить мавританок", "Но только самых воспитанных! У нас тут не бордель!",
                epic1.getId());
        manager.addSubTask(subTask2);

        Epic epic2 = new Epic("Изобрести вечный двигатель", "А почему бы и нет? Надо ж чем-то на " +
                "каникулах заниматься ¯\\_(ツ)_/¯");
        manager.addEpic(epic2);
        SubTask subTask3 = new SubTask("Завести кота", "Если привязать ему на спину бутерброд и попробовать опустить на " +
                "землю, то получится антигравитация!", Task.Status.DONE, epic2.getId());
        manager.addSubTask(subTask3);
        SubTask subTask4 = new SubTask("Сделать бутерброд", "План надёжный, как швейцарские часы!",
                Task.Status.IN_PROGRESS, epic2.getId());
        manager.addSubTask(subTask4);

        Epic epic3 = new Epic("Закончить первый модуль", "Было интересно, жду второго модуля!");
        manager.addEpic(epic3);
        SubTask subTask5 = new SubTask("Изучить введение в Java Core", "Надо же знать основы",
                Task.Status.DONE, epic3.getId());
        manager.addSubTask(subTask5);
        SubTask subTask6 = new SubTask("Завершить техническое задание 5", "Вопреки всем стараниям того," +
                " кто его составлял", Task.Status.DONE, epic3.getId());
        manager.addSubTask(subTask6);

        printAllTasks(manager);
        System.out.println("В истории ровно 10 задач разного вида, первые две задачи типа Task были перезаписаны");


    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(manager.getTaskById(task.getId()));
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getAllEpics()) {
            System.out.println(manager.getEpicById(epic.getId()));

            for (int id : manager.getExactEpicSubTasks(epic.getId())) {
                System.out.println("ID--> " + id);
            }
        }

        System.out.println("Подзадачи:");
        for (Task subtask : manager.getAllSubTasks()) {
            System.out.println(manager.getSubTaskById(subtask.getId()));
        }

        System.out.println("История:");
        for (Task task : manager.getTaskHistory()) {
            System.out.println(task);
        }
    }


}



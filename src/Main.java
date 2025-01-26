public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager manager = Managers.getDefault();

        //Реализация дополнительного задания

        Task task1 = new Task("Задача 1", "Описание для задачи 1");
        manager.addTask(task1);

        Task task2 = new Task("Задача 2", "Описание для задачи 2", Task.Status.IN_PROGRESS);
        manager.addTask(task2);


        Epic epic1 = new Epic("Эпик 1", "Описание для эпика 1");
        manager.addEpic(epic1);

        SubTask subTask1 = new SubTask("Подзадача 1", "Описание для подзадачи 1", epic1.getId());
        manager.addSubTask(subTask1);
        SubTask subTask2 = new SubTask("Подзадача 2", "Описание для подзадачи 2", epic1.getId());
        manager.addSubTask(subTask2);
        SubTask subTask3 = new SubTask("Подзадача 3", "Описание для подзадачи 3", Task.Status.DONE,
                epic1.getId());
        manager.addSubTask(subTask3);

        Epic epic2 = new Epic("Эпик 2", "Описание для эпика 2");
        manager.addEpic(epic2);

        manager.getTaskById(task2.getId());
        System.out.println(manager.getTaskHistory());
        System.out.println("*".repeat(8));

        manager.getSubTaskById(subTask3.getId());
        manager.getEpicById(epic1.getId());
        manager.getTaskById(task2.getId());
        System.out.println(manager.getTaskHistory());
        System.out.println("*".repeat(8));

        manager.getTaskById(task2.getId());
        manager.getTaskById(task1.getId());
        manager.getEpicById(epic1.getId());
        manager.getSubTaskById(subTask2.getId());
        manager.getEpicById(epic2.getId());
        manager.getSubTaskById(subTask2.getId());
        manager.getSubTaskById(subTask3.getId());
        manager.getSubTaskById(subTask1.getId());
        System.out.println(manager.getTaskHistory());
        System.out.println("*".repeat(8));

        manager.removeTaskById(task2.getId());
        System.out.println(manager.getTaskHistory());
        System.out.println("*".repeat(8));

        manager.removeEpicById(epic1.getId());
        System.out.println(manager.getTaskHistory());
        System.out.println("*".repeat(8));

    }

}



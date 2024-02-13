import java.util.Arrays;
import java.util.Scanner;

public class Parser {
    protected static int current = 0;

    public static boolean parse(String input, TaskList taskList, Ui ui) {
        String command = ui.readCommand();
        Task[] tasks = taskList.getTaskList();

        if (command.equals("bye")) {
            ui.printByeMessage();
            return true;
        } else if (command.equals("list")) {
            StringBuilder listOutput = new StringBuilder();
            for (int i = 0; i < tasks.length; i++) {
                if (tasks[i] == null) {
                    break;
                } else {
                    listOutput.append(i + 1).append(". ").append(tasks[i].toString()).append("\n");
                }
            }
            ui.printTaskList(listOutput.toString());
        } else {
            String[] brokenCommand = command.split("\\s+");
            String advancedCommand = brokenCommand[0];
            String[] details = Arrays.copyOfRange(brokenCommand, 1, brokenCommand.length);
            switch (advancedCommand) {
                case "mark": {
                    if (brokenCommand.length < 2) {
                        ui.printMarkEmptyNumberError();
                    } else {
                        try {
                            int index = Integer.parseInt(brokenCommand[1]) - 1;
                            tasks[index].mark();
                            ui.printTaskMarked(tasks[index].toString());
                        } catch (NumberFormatException e) {
                            ui.printMarkNANError();
                        }
                    }
                    break;
                }
                case "unmark": {
                    if (brokenCommand.length < 2) {
                        ui.printUnmarkEmptyNumberError();
                    } else {
                        try {
                            int index = Integer.parseInt(brokenCommand[1]) - 1;
                            tasks[index].unmark();
                            ui.printTaskUnmarked(tasks[index].toString());
                        } catch (NumberFormatException e) {
                            ui.printUnmarkNANError();
                        }
                    }
                    break;
                }
                case "todo": {
                    String taskDescription = String.join(" ", details);
                    try {
                        tasks[current] = new ToDo(taskDescription);
                        current++;
                        ui.printComplexTask(tasks, current);
                        break;
                    } catch (InvalidInputException e) {
                        System.out.println(e);
                        break;
                    }
                }
                case "deadline": {
                    StringBuilder taskDescription = new StringBuilder();
                    StringBuilder deadline = new StringBuilder();
                    boolean foundDeadline = false;
                    for (String currentString : details) {
                        if (foundDeadline) {
                            deadline.append(currentString).append(" ");
                        } else if (currentString.equals("/by")) {
                            foundDeadline = true;
                        } else {
                            taskDescription.append(currentString).append(" ");
                        }
                    }
                    try {
                        tasks[current] = new Deadline(taskDescription.toString(), deadline.toString());
                        current++;
                        ui.printComplexTask(tasks, current);
                        break;
                    } catch (InvalidInputException | InvalidDateException e) {
                        System.out.println(e);
                        break;
                    }
                }
                case "event": {
                    StringBuilder taskDescription = new StringBuilder();
                    StringBuilder from = new StringBuilder();
                    StringBuilder to = new StringBuilder();
                    boolean foundFrom = false;
                    boolean foundTo = false;
                    for (String currentString : details) {
                        if (foundTo) {
                            to.append(currentString).append(" ");
                        } else if (foundFrom) {
                            if (currentString.equals("/to")) {
                                foundTo = true;
                            } else {
                                from.append(currentString).append(" ");
                            }
                        } else if (currentString.equals("/from")) {
                            foundFrom = true;
                        } else {
                            taskDescription.append(currentString).append(" ");
                        }
                    }
                    try {
                        tasks[current] = new Event(taskDescription.toString(), from.toString(), to.toString());
                        current++;
                        ui.printComplexTask(tasks, current);
                        break;
                    } catch (InvalidInputException | InvalidDateException e) {
                        System.out.println(e);
                        break;
                    }
                }
                case "delete": {
                    if (brokenCommand.length < 2) {
                        System.out.println("OOPS!!! The number for the delete command cannot be empty.");
                    } else {
                        try {
                            Task deletedTask = null;
                            int index = Integer.parseInt(brokenCommand[1]) - 1;
                            for (int i = 0; i < tasks.length; i++) {
                                Task currentTask = tasks[i];
                                if (currentTask == null) {
                                    if (i <= index) {
                                        throw new ArrayIndexOutOfBoundsException();
                                    }
                                    break;
                                } else if (i >= index) {
                                    if (i == index) {
                                        deletedTask = currentTask;
                                    }
                                    tasks[i] = tasks[i + 1];
                                }
                            }
                            current--;
                            assert deletedTask != null;
                            ui.printDeletion(deletedTask, current);
                        } catch (NumberFormatException e) {
                            System.out.println("OOPS!!! The input after the delete command has to be an integer.");
                        } catch (ArrayIndexOutOfBoundsException e) {
                            System.out.println("OOPS!!! The input for delete is out of bounds.");
                        }
                    }
                    break;
                }
                default: {
                    System.out.println("OOPS!!! I'm sorry, but I don't know what that means :-(");
                    break;
                }
            }
        }
        taskList.updateTaskList(new TaskList(tasks));
        return false;
    }

}

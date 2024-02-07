import java.io.*;
import java.util.Arrays;

public class SaveAndLoad {

    enum TaskType {
        ToDo, Deadline, Event
    }

    public static void save(Task[] listOfTasks) {
        StringBuilder textOutput = new StringBuilder();
        for (Task task : listOfTasks) {
            if (task == null) {
                break;
            }
            textOutput.append(task.toString()).append("\n");
        }

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("balkanbot.txt"));
            writer.write(textOutput.toString());
            writer.close();

        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public static void markCheck(Task task, boolean completed) {
        if (completed) {
            task.mark();
        }
    }

    public static void load(Task[] listOfTasks) {
        String[] textInput = new String[100];
        try {
            BufferedReader reader = new BufferedReader(new FileReader("balkanbot.txt"));
            String line;
            int counter = 0;
            while ((line = reader.readLine()) != null) {
                textInput[counter] = line;
                counter++;
            }
        } catch (IOException e) {
            System.out.println(e);
            return;
        }

        int counter = 1;
        int current = 0;
        for (String task : textInput) {
            if (task == null) {
                break;
            } else {
                TaskType taskType = null;
                boolean completed = false;
                boolean proceed = true;

                String taskNature = task.substring(0, 6);
                String[] details = task.substring(6).split("\\s+");
                String type = taskNature.substring(1, 2);
                String complete = taskNature.substring(4, 5);
                switch (type) {
                    case "T": {
                        taskType = TaskType.ToDo;
                        break;
                    }
                    case "D": {
                        taskType = TaskType.Deadline;
                        break;
                    }
                    case "E": {
                        taskType = TaskType.Event;
                        break;
                    }
                    default: {
                        System.out.println("Error with Task: " + counter + "Type Unidentified Task Found Save File. " +
                                "Task will be skipped");
                        proceed = false;
                    }
                }

                if (complete.equals("X")) {
                    completed = true;
                } else if (!complete.equals(" ")) {
                    System.out.println("Completion status of task [" + counter + "] is invalid. Task will be skipped");
                    proceed = false;
                }

                counter++;

                if (!proceed) {
                    continue;
                }

                switch (taskType) {
                    case ToDo: {
                        String taskDescription = String.join(" ", details);
                        try {
                            listOfTasks[current] = new ToDo(taskDescription);
                            markCheck(listOfTasks[current], completed);
                            current++;
                            break;
                        } catch (InvalidInputException e) {
                            System.out.println(e);
                            break;
                        }
                    }
                    case Deadline: {
                        StringBuilder taskDescription = new StringBuilder();
                        StringBuilder deadline = new StringBuilder();
                        boolean foundDeadline = false;
                        for (String currentString : details) {
                            if (foundDeadline) {
                                deadline.append(currentString);
                            } else if (currentString.equals("(by:")) {
                                foundDeadline = true;
                            } else {
                                taskDescription.append(currentString).append(" ");
                            }
                        }

                        String fixedDeadline = deadline.substring(0, deadline.toString().length() - 1);

                        try {
                            listOfTasks[current] = new Deadline(taskDescription.toString(), fixedDeadline);
                            markCheck(listOfTasks[current], completed);
                            current++;
                            break;
                        } catch (InvalidInputException e) {
                            System.out.println(e);
                            break;
                        }
                    }
                    case Event: {
                        StringBuilder taskDescription = new StringBuilder();
                        StringBuilder from = new StringBuilder();
                        StringBuilder to = new StringBuilder();
                        boolean foundFrom = false;
                        boolean foundTo = false;
                        for (String currentString : details) {
                            if (foundTo) {
                                to.append(currentString);
                            } else if (foundFrom) {
                                if (currentString.equals("to:")) {
                                    foundTo = true;
                                } else {
                                    from.append(currentString).append(" ");
                                }
                            } else if (currentString.equals("(from:")) {
                                foundFrom = true;
                            } else {
                                taskDescription.append(currentString).append(" ");
                            }
                        }

                        String fixedTo = to.substring(0, to.toString().length() - 1);
                        try {
                            listOfTasks[current] = new Event(taskDescription.toString(), from.toString(),
                                    fixedTo);
                            markCheck(listOfTasks[current], completed);
                            current++;
                            break;
                        } catch (InvalidInputException e) {
                            System.out.println(e);
                            break;
                        }
                    }
                }
            }
        }
    }
}
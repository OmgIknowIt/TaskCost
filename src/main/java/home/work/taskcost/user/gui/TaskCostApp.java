package home.work.taskcost.user.gui;

import home.work.taskcost.controller.OperationController;
import home.work.taskcost.controller.TaskController;
import home.work.taskcost.database.DbConnection;
import home.work.taskcost.entities.Operation;
import home.work.taskcost.entities.Status;
import home.work.taskcost.entities.Task;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;

public class TaskCostApp extends JFrame {
    private final TaskController taskController;
    private final OperationController operationController;
    private JPanel mainPanel;
    private JTabbedPane tabbedPane1;
    //Tab1
    private JLabel descriptionLabel;
    private JTextArea taskDescriptionArea;
    private JButton saveNewTask;
    private JLabel newTaskIdLabel;
    private JTextPane newTaskId;
    private JLabel taskLabel;
    private JTextField taskIdTextField;
    private JScrollPane taskDescrScPane;
    //
    //Tab2
    private JLabel operationDescLabel;
    private JTextArea newOperDescrTextArea;
    private JLabel plannedQLabel;
    private JTextField plannedQTextField;
    private JLabel operationCostLabel;
    private JTextField operationPrice;
    private JButton saveNewOperation;
    private JLabel newOperLabel;
    private JTextPane newOperId;
    private JScrollPane newOperDescrScPane;
    //
    //Tab3
    private JLabel finOpIdLabel;
    private JTextField finOpId;
    private JLabel actualQLabel;
    private JTextField actualQTextField;
    private JButton finOperation;
    //
    //Tab4
    private JList unfinTasksList;
    private JButton showUnfinTasks;
    private JScrollPane unfinTasksScPane;
    //
    //Tab5
    private JPanel finTasksPanel;
    private JButton showFinTasks;
    private JFormattedTextField fromTaskDate;
    private JFormattedTextField toTaskDate;
    private JScrollPane finTasksPane;
    private JCheckBox completeSumCheckBox;
    private JLabel bigSumLabel;
    private JTextPane bigSumTextPane;

    public TaskCostApp(String title) throws HeadlessException {
        super(title);
        DbConnection.openConnection();
        taskController = new TaskController(DbConnection.getConnection());
        operationController = new OperationController(DbConnection.getConnection());
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setContentPane(mainPanel);
        this.pack();
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                try {
                    DbConnection.getConnection().close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                e.getWindow().dispose();
            }
        });
        tab1();
        tab2();
        tab3();
        tab4();
        tab5();
    }

    private void tab5() {
        String datePattern = "dd/MM/yyyy";
        DateFormatter dateFormatter = new DateFormatter(new SimpleDateFormat(datePattern));

        fromTaskDate.setFormatterFactory(new DefaultFormatterFactory(dateFormatter));
        toTaskDate.setFormatterFactory(new DefaultFormatterFactory(dateFormatter));

        fromTaskDate.setValue(Date.valueOf(LocalDate.now().minusDays(1)));
        toTaskDate.setValue(Date.valueOf(LocalDate.now()));

        showFinTasks.addActionListener(e -> {
            String[] columnNames = {"Номер задания", "Описание задания", "Стоимость", "Дата завершения"};
            DefaultTableModel taskTable = new DefaultTableModel();
            JTable table = new JTable();
            Boolean checkSum = completeSumCheckBox.isSelected();
            BigDecimal bigSum = BigDecimal.valueOf(0);

            taskTable.setColumnIdentifiers(columnNames);
            table.setModel(taskTable);
            table.setFillsViewportHeight(true);
            finTasksPane.getViewport().add(table);

            Set<Task> tasks = taskController.getAll();
            LocalDate start = LocalDate.parse(fromTaskDate.getText(), DateTimeFormatter.ofPattern(datePattern));
            LocalDate end = LocalDate.parse(toTaskDate.getText(), DateTimeFormatter.ofPattern(datePattern));

            for (Task task : tasks) {
                if (task.getTaskFinished() != null) {
                    if (!task.getTaskFinished().isBefore(start) && !task.getTaskFinished().isAfter(end) && task.getTaskStatus().equals(Status.Done)) {
                        Object[] data = {task.getId(), task.getDescription(), task.getCost(), task.getTaskFinished()};
                        taskTable.addRow(data);
                        if (checkSum) {
                            bigSum = bigSum.add(task.getCost());
                        }
                    }
                }
            }

            if (checkSum) {
                bigSumTextPane.setText(bigSum.toString());
                bigSumLabel.setVisible(true);
                bigSumTextPane.setVisible(true);
            }
        });
    }

    private void tab4() {
        showUnfinTasks.addActionListener(e -> {
            Set<Task> tasks = taskController.getAll();
            DefaultTableModel defaultTableModel = new DefaultTableModel();
            JTable table = new JTable();
            String[] columnNames = {"Номер задания", "Описание задания", "Стоимость", "Статус"};

            defaultTableModel.setColumnIdentifiers(columnNames);
            table.setModel(defaultTableModel);
            table.setFillsViewportHeight(true);
            unfinTasksScPane.getViewport().add(table);

            for (Task task : tasks) {
                if (!task.getTaskStatus().equals(Status.Done)) {
                    String statusRus;
                    if (task.getTaskStatus().equals(Status.Project)) {
                        statusRus = "Проект";
                    } else {
                        statusRus = "Выполняется";
                    }
                    Object[] data = {task.getId(), task.getDescription(), task.getCost(), statusRus};
                    defaultTableModel.addRow(data);
                }
            }
        });
    }

    private void tab3() {
        onlyNums(finOpId);
        onlyNums(actualQTextField);
        blockIfEmpty(finOpId, finOperation);
        blockIfEmpty(actualQTextField, finOperation);
        finOperation.addActionListener(e -> {
            try {
                Operation o = operationController.getById(Long.valueOf(finOpId.getText()));
                o.setOperationStatus(Status.Done);
                o.setActualQuantity(Integer.valueOf(actualQTextField.getText()));
                operationController.update(o);
                Set<Operation> operations = operationController.getAll();
                BigDecimal newSum = BigDecimal.valueOf(0);
                for (Operation op : operations) {
                    if (op.getTaskId().equals(o.getTaskId())) {
                        newSum = newSum.add(op.getCost());
                    }
                }
                Task t = taskController.getById(o.getTaskId());
                t.setCost(newSum);
                Status currStatus = taskController.checkOverallStatus(t.getId());
                t.setTaskStatus(currStatus);
                if (currStatus.equals(Status.Done)) {
                    t.setTaskFinished(LocalDate.now());
                }
                taskController.update(t);
                infoMessage("Сообщение","Операция под номером " + o.getId() + " помечена как \"Выполнена\"!", JOptionPane.INFORMATION_MESSAGE);
            } catch (NullPointerException | NumberFormatException | SQLException ex) {
                errorMessage();
                ex.printStackTrace();
            }
        });
    }

    private void tab2() {
        newOperDescrTextArea = new JTextArea();
        newOperDescrTextArea.setLineWrap(true);
        newOperDescrScPane.getViewport().add(newOperDescrTextArea);
        newOperDescrScPane.setViewportView(newOperDescrTextArea);
        newOperDescrScPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        blockIfEmpty(taskDescriptionArea, saveNewTask);
        onlyNums(taskIdTextField);
        onlyNums(plannedQTextField);
        onlyNums(operationPrice);
        blockIfEmpty(taskIdTextField, saveNewOperation);
        blockIfEmpty(plannedQTextField, saveNewOperation);
        blockIfEmpty(operationPrice, saveNewOperation);
        blockIfEmpty(newOperDescrTextArea, saveNewOperation);
        saveNewOperation.addActionListener(e -> {
            try {
                Operation o = new Operation();
                o.setTaskId(Long.valueOf(taskIdTextField.getText()));
                o.setDescription(newOperDescrTextArea.getText());
                o.setPlannedQuantity(Integer.valueOf(plannedQTextField.getText()));
                o.setActualQuantity(0);
                o.setOperationStatus(Status.Project);
                o.setPrice(new BigDecimal(operationPrice.getText().replace(",", ".")));
                o.setCost(BigDecimal.valueOf(o.getPlannedQuantity()).multiply(o.getPrice()));
                Long id = operationController.createNew(o);
                o.setId(id);
                newOperLabel.setVisible(true);
                newOperId.setVisible(true);
                newOperId.setText(String.valueOf(o.getId()));

                Task t = taskController.getById(o.getTaskId());
                t.setCost(t.getCost().add(o.getCost()));
                Status currentStatus = taskController.checkOverallStatus(t.getId());
                t.setTaskStatus(currentStatus);
                taskController.update(t);
            } catch (NumberFormatException | SQLException ex) {
                errorMessage();
                ex.printStackTrace();
            }
        });
    }

    private void tab1() {
        taskDescriptionArea = new JTextArea();
        taskDescriptionArea.setLineWrap(true);
        taskDescrScPane.getViewport().add(taskDescriptionArea);
        taskDescrScPane.setViewportView(taskDescriptionArea);
        taskDescrScPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        blockIfEmpty(taskDescriptionArea, saveNewTask);
        saveNewTask.addActionListener(e -> {
            Task t = new Task();
            t.setCost(BigDecimal.valueOf(0));
            t.setTaskStatus(Status.Project);
            t.setDescription(taskDescriptionArea.getText());
            Long id = taskController.createNew(t);
            t.setId(id);
            newTaskIdLabel.setVisible(true);
            newTaskId.setVisible(true);
            newTaskId.setText(String.valueOf(t.getId()));
        });
    }

    private void blockIfEmpty(JTextComponent field, JButton button) {
        field.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                changed();
            }

            public void removeUpdate(DocumentEvent e) {
                changed();
            }

            public void insertUpdate(DocumentEvent e) {
                changed();
            }

            private void changed() {
                if (field.getText().length() == 0) {
                    button.setEnabled(false);
                } else {
                    button.setEnabled(true);
                }
            }
        });
    }

    private void onlyNums(JTextField textField) {
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                if (Character.isAlphabetic(e.getKeyChar())) {
                    e.consume();
                }
            }
        });
    }

    private void errorMessage() {
        infoMessage("Ошибка", "Введены неверные данные", JOptionPane.ERROR_MESSAGE);
    }

    private void infoMessage(String title, String message, int messageType) {
        JOptionPane.showMessageDialog(new JFrame(), message, title, messageType);
    }

    public static void main(String[] args) {
        JFrame qq = new TaskCostApp("Система подсчёта стоимости выполненных заданий");
        qq.setVisible(true);
    }
}

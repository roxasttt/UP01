package org.example;

import java.sql.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class RecruitmentAgencyApp extends JFrame {

    private JTable employerTable;
    private JTable jobTable;
    private JTable candidateTable;
    private JTable applicationTable;
    private JTable skillTable;
    private JTable candidateSkillTable;

    private DefaultTableModel employerTableModel;
    private DefaultTableModel jobTableModel;
    private DefaultTableModel candidateTableModel;
    private DefaultTableModel applicationTableModel;
    private DefaultTableModel skillTableModel;
    private DefaultTableModel candidateSkillTableModel;

    private Connection connection;

    public RecruitmentAgencyApp() {
        super("База данных рекрутингового агентства");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Создаем таблицы
        employerTable = new JTable();
        jobTable = new JTable();
        candidateTable = new JTable();
        applicationTable = new JTable();
        skillTable = new JTable();
        candidateSkillTable = new JTable();

        // Создаем модели таблиц
        employerTableModel = new DefaultTableModel();
        jobTableModel = new DefaultTableModel();
        candidateTableModel = new DefaultTableModel();
        applicationTableModel = new DefaultTableModel();
        skillTableModel = new DefaultTableModel();
        candidateSkillTableModel = new DefaultTableModel();

        // Устанавливаем модели для таблиц
        employerTable.setModel(employerTableModel);
        jobTable.setModel(jobTableModel);
        candidateTable.setModel(candidateTableModel);
        applicationTable.setModel(applicationTableModel);
        skillTable.setModel(skillTableModel);
        candidateSkillTable.setModel(candidateSkillTableModel);

        // Создаем вкладки
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Работодатели", new JScrollPane(employerTable));
        tabbedPane.addTab("Вакансии", new JScrollPane(jobTable));
        tabbedPane.addTab("Кандидаты", new JScrollPane(candidateTable));
        tabbedPane.addTab("Заявки", new JScrollPane(applicationTable));
        tabbedPane.addTab("Навыки", new JScrollPane(skillTable));
        tabbedPane.addTab("Навыки кандидатов", new JScrollPane(candidateSkillTable));

        // Создаем кнопку "Обновить"
        JButton updateButton = new JButton("Обновить");
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateTables();
            }
        });

        // Добавляем кнопку в панель инструментов
        JToolBar toolBar = new JToolBar();
        toolBar.add(updateButton);

        // Добавляем вкладки и кнопку в фрейм
        getContentPane().add(toolBar, BorderLayout.NORTH);
        getContentPane().add(tabbedPane, BorderLayout.CENTER);

        // Устанавливаем соединение с базой данных
        connectToDatabase();

        // Заполняем таблицы
        updateTables();

        setVisible(true);
    }

    private void connectToDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/recruitment_agency?useUnicode=true&characterEncoding=UTF-8", "root", "Bublikya123");
            System.out.println("Соединение с базой данных установлено.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Ошибка при подключении к базе данных: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTables() {
        try {
            // Обновляем данные в таблицах
            updateEmployerTable();
            updateJobTable();
            updateCandidateTable();
            updateApplicationTable();
            updateSkillTable();
            updateCandidateSkillTable();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Ошибка при обновлении данных: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Методы для обновления таблиц
    private void updateEmployerTable() throws SQLException {
        Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        ResultSet rs = stmt.executeQuery("SELECT * FROM employers");
        employerTableModel.setDataVector(getResultSetData(rs), new String[]{"ID", "Компания", "Email"});
        rs.close();
        stmt.close();
    }

    private void updateJobTable() throws SQLException {
        Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        ResultSet rs = stmt.executeQuery("SELECT * FROM jobs");
        jobTableModel.setDataVector(getResultSetData(rs), new String[]{"ID вакансии", "Название", "Описание", "Местоположение", "Работодатель ID"});
        rs.close();
        stmt.close();
    }

    private void updateCandidateTable() throws SQLException {
        Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        ResultSet rs = stmt.executeQuery("SELECT * FROM candidates");
        candidateTableModel.setDataVector(getResultSetData(rs), new String[]{"ID кандидата", "Имя", "Email"});
        rs.close();
        stmt.close();
    }

    private void updateApplicationTable() throws SQLException {
        Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        ResultSet rs = stmt.executeQuery("SELECT * FROM applications");
        applicationTableModel.setDataVector(getResultSetData(rs), new String[]{"ID заявки", "Кандидат ID", "Вакансия ID", "Дата", "Статус"});
        rs.close();
        stmt.close();
    }

    private void updateSkillTable() throws SQLException {
        Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        ResultSet rs = stmt.executeQuery("SELECT * FROM skills");
        skillTableModel.setDataVector(getResultSetData(rs), new String[]{"ID навыка", "Название"});
        rs.close();
        stmt.close();
    }

    private void updateCandidateSkillTable() throws SQLException {
        Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        ResultSet rs = stmt.executeQuery("SELECT * FROM candidate_skills");
        candidateSkillTableModel.setDataVector(getResultSetData(rs), new String[]{"Кандидат ID", "Навык ID"});
        rs.close();
        stmt.close();
    }

    private Object[][] getResultSetData(ResultSet rs) throws SQLException {
        // Получаем количество столбцов
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        // Получаем количество строк
        rs.last();
        int rowCount = rs.getRow();
        rs.beforeFirst();

        // Создаем двумерный массив для хранения данных
        Object[][] data = new Object[rowCount][columnCount];

        // Заполняем массив данными
        int row = 0;
        while (rs.next()) {
            for (int col = 0; col < columnCount; col++) {
                data[row][col] = rs.getObject(col + 1);
            }
            row++;
        }
        return data;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new RecruitmentAgencyApp();
            }
        });
    }
}

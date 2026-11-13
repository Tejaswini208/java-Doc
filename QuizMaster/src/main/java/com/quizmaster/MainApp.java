package com.quizmaster;

import com.quizmaster.dao.AdminFileHandler;
import com.quizmaster.dao.StudentFileHandler;
import com.quizmaster.model.Admin;
import com.quizmaster.model.Student;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainApp extends Application {

    private Stage stage;
    private StudentFileHandler studentHandler = new StudentFileHandler();
    private AdminFileHandler adminHandler = new AdminFileHandler();

    // High-Contrast Enterprise Color Palette
    private final String darkNavy = "#0F172A";       // Header / Accent Navy
    private final String primaryIndigo = "#6366F1";  // Vibrant Accent Indigo
    private final String primaryHover = "#4F46E5";   // Darker Indigo
    private final String successGreen = "#10B981";  // Emerald Green Action
    private final String lightBg = "#F8FAFC";        // Soft Slate Background
    private final String borderGray = "#CBD5E1";     // Visible Slate Border
    private final String textDark = "#0F172A";       // Deep Text Color
    private final String textMuted = "#475569";      // Subtitle Slate

    // Data Structures
    public static class QuestionData {
        private String questionText;
        private String optionA, optionB, optionC, optionD;
        private int correctOptionIndex;
        private String difficulty;
        private int points;

        public QuestionData(String questionText, String optionA, String optionB, String optionC, String optionD, int correctOptionIndex, String difficulty, int points) {
            this.questionText = questionText;
            this.optionA = optionA;
            this.optionB = optionB;
            this.optionC = optionC;
            this.optionD = optionD;
            this.correctOptionIndex = correctOptionIndex;
            this.difficulty = difficulty;
            this.points = points;
        }

        public String getQuestionText() { return questionText; }
        public String getOptionA() { return optionA; }
        public String getOptionB() { return optionB; }
        public String getOptionC() { return optionC; }
        public String getOptionD() { return optionD; }
        public int getCorrectOptionIndex() { return correctOptionIndex; }
        public String getDifficulty() { return difficulty; }
        public int getPoints() { return points; }
    }

    public static class QuizSummary {
        private String quizId;
        private String quizName;
        private int questionsCount;
        private int participants;
        private String schedule;
        private boolean isActive;
        private int durationMinutes;
        private int easyMarks;
        private int medMarks;
        private int hardMarks;
        private List<QuestionData> questions;

        public QuizSummary(String quizId, String quizName, int questionsCount, int participants, String schedule, boolean isActive, int durationMinutes, int easyMarks, int medMarks, int hardMarks, List<QuestionData> questions) {
            this.quizId = quizId;
            this.quizName = quizName;
            this.questionsCount = questionsCount;
            this.participants = participants;
            this.schedule = schedule;
            this.isActive = isActive;
            this.durationMinutes = durationMinutes;
            this.easyMarks = easyMarks;
            this.medMarks = medMarks;
            this.hardMarks = hardMarks;
            this.questions = questions;
        }

        public String getQuizId() { return quizId; }
        public String getQuizName() { return quizName; }
        public int getQuestions() { return questionsCount; }
        public int getParticipants() { return participants; }
        public String getSchedule() { return schedule; }
        public boolean isActive() { return isActive; }
        public int getDurationMinutes() { return durationMinutes; }
        public int getEasyMarks() { return easyMarks; }
        public int getMedMarks() { return medMarks; }
        public int getHardMarks() { return hardMarks; }
        public List<QuestionData> getQuestionsList() { return questions; }
        public void incrementParticipants() { this.participants++; }

        public int getTotalPossibleMarks() {
            int total = 0;
            for (QuestionData q : questions) {
                total += q.getPoints();
            }
            return total;
        }
    }

    public static class StudentScore {
        private String regNo;
        private String name;
        private String quizId;
        private String quizName;
        private String status;
        private String score;
        private int rawScore;

        public StudentScore(String regNo, String name, String quizId, String quizName, String status, String score, int rawScore) {
            this.regNo = regNo;
            this.name = name;
            this.quizId = quizId;
            this.quizName = quizName;
            this.status = status;
            this.score = score;
            this.rawScore = rawScore;
        }

        public String getRegNo() { return regNo; }
        public String getName() { return name; }
        public String getQuizId() { return quizId; }
        public String getQuizName() { return quizName; }
        public String getStatus() { return status; }
        public String getScore() { return score; }
        public int getRawScore() { return rawScore; }
    }

    // Application State
    private ObservableList<QuizSummary> quizList = FXCollections.observableArrayList();
    private ObservableList<StudentScore> leaderboardList = FXCollections.observableArrayList();

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("QuizMaster | Automated Examination Suite");
        showHome();
        stage.setMinWidth(1100);
        stage.setMinHeight(720);
        stage.show();
    }

    // ---------------- UI BUTTON & INPUT COMPONENT BUILDERS ----------------
    private Button createPrimaryButton(String text) {
        Button b = new Button(text);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setPrefHeight(44);
        b.setStyle(
            "-fx-background-color: " + primaryIndigo + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-family: 'Segoe UI', sans-serif;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8px;" +
            "-fx-cursor: hand;"
        );
        return b;
    }

    private Button createSecondaryButton(String text) {
        Button b = new Button(text);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setPrefHeight(42);
        b.setStyle(
            "-fx-background-color: #EEF2FF;" +
            "-fx-text-fill: " + primaryHover + ";" +
            "-fx-font-family: 'Segoe UI', sans-serif;" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8px;" +
            "-fx-border-color: #C7D2FE;" +
            "-fx-border-radius: 8px;" +
            "-fx-cursor: hand;"
        );
        return b;
    }

    private Button createSuccessButton(String text) {
        Button b = new Button(text);
        b.setStyle(
            "-fx-background-color: " + successGreen + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-family: 'Segoe UI', sans-serif;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 13px;" +
            "-fx-padding: 9 20;" +
            "-fx-background-radius: 8px;" +
            "-fx-cursor: hand;"
        );
        return b;
    }

    private TextField createStyledTextField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setPrefHeight(40);
        tf.setStyle(
            "-fx-background-color: #FFFFFF;" +
            "-fx-border-color: " + borderGray + ";" +
            "-fx-border-radius: 6px;" +
            "-fx-background-radius: 6px;" +
            "-fx-padding: 0 12px;" +
            "-fx-font-size: 13px;" +
            "-fx-text-fill: " + textDark + ";"
        );
        return tf;
    }

    private PasswordField createStyledPasswordField(String prompt) {
        PasswordField pf = new PasswordField();
        pf.setPromptText(prompt);
        pf.setPrefHeight(40);
        pf.setStyle(
            "-fx-background-color: #FFFFFF;" +
            "-fx-border-color: " + borderGray + ";" +
            "-fx-border-radius: 6px;" +
            "-fx-background-radius: 6px;" +
            "-fx-padding: 0 12px;" +
            "-fx-font-size: 13px;" +
            "-fx-text-fill: " + textDark + ";"
        );
        return pf;
    }

    // ---------------- HOME PAGE ----------------
    private void showHome() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + lightBg + ";");

        Label logo = new Label("✦ QuizMaster");
        logo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 26));
        logo.setTextFill(Color.web(darkNavy));

        Label navText = new Label("COMPETITIVE ASSESSMENT SYSTEM");
        navText.setStyle("-fx-text-fill: " + textMuted + "; -fx-font-size: 11px; -fx-font-weight: bold; -fx-letter-spacing: 1.5px;");

        HBox navbar = new HBox(logo, navText);
        navbar.setAlignment(Pos.CENTER_LEFT);
        navbar.setSpacing(25);
        navbar.setPadding(new Insets(20, 50, 20, 50));
        navbar.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: " + borderGray + "; -fx-border-width: 0 0 1 0;");
        root.setTop(navbar);

        Label welcome = new Label("Automated Examination Platform");
        welcome.setFont(Font.font("Segoe UI", FontWeight.BOLD, 36));
        welcome.setTextFill(Color.web(darkNavy));

        Label description = new Label("Seamless online assessments, automated weighted scoring, real-time leaderboards, and instant analytics.");
        description.setFont(Font.font("Segoe UI", 15));
        description.setTextFill(Color.web(textMuted));
        description.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        VBox adminCard = createPortalCard("🛡", "ADMINISTRATION PORTAL", "Design custom multi-question quizzes, assign weightages,\nschedule timers, and inspect submission statistics.");
        Button adminButton = createPrimaryButton("Admin Sign In");
        Button adminRegister = createSecondaryButton("Create Admin Account");
        adminButton.setOnAction(e -> showLogin("Admin"));
        adminRegister.setOnAction(e -> showRegister("Admin"));
        adminCard.getChildren().addAll(adminButton, adminRegister);

        VBox studentCard = createPortalCard("🎓", "STUDENT PORTAL", "Attempt live scheduled quizzes, complete challenges within time,\nand review detailed answer key explanations.");
        Button studentButton = createPrimaryButton("Student Sign In");
        Button studentRegister = createSecondaryButton("Register as Student");
        studentButton.setOnAction(e -> showLogin("Student"));
        studentRegister.setOnAction(e -> showRegister("Student"));
        studentCard.getChildren().addAll(studentButton, studentRegister);

        HBox cards = new HBox(35, adminCard, studentCard);
        cards.setAlignment(Pos.CENTER);

        VBox center = new VBox(25, welcome, description, cards);
        center.setAlignment(Pos.CENTER);
        center.setPadding(new Insets(40));
        root.setCenter(center);

        Label footer = new Label("QUIZMASTER ENGINE © 2026 | Powered by JavaFX & Modern UI Architecture");
        footer.setTextFill(Color.web("#94A3B8"));
        footer.setFont(Font.font("Segoe UI", 12));
        HBox footerBox = new HBox(footer);
        footerBox.setAlignment(Pos.CENTER);
        footerBox.setPadding(new Insets(15));
        root.setBottom(footerBox);

        stage.setScene(new Scene(root, 1150, 750));
    }

    private VBox createPortalCard(String iconText, String heading, String description) {
        Label icon = new Label(iconText);
        icon.setFont(Font.font("Segoe UI", 42));

        Label title = new Label(heading);
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 17));
        title.setTextFill(Color.web(darkNavy));

        Label info = new Label(description);
        info.setFont(Font.font("Segoe UI", 13));
        info.setTextFill(Color.web(textMuted));
        info.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        VBox card = new VBox(16, icon, title, info);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(35));
        card.setPrefWidth(380);
        card.setStyle(
            "-fx-background-color: #FFFFFF;" +
            "-fx-background-radius: 14px;" +
            "-fx-border-color: " + borderGray + ";" +
            "-fx-border-radius: 14px;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(15, 23, 42, 0.06), 15, 0, 0, 5);"
        );
        return card;
    }

    // ---------------- LOGIN & REGISTER PAGES ----------------
    private void showLogin(String role) {
        VBox form = new VBox(18);

        Label heading = new Label(role + " Login");
        heading.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        heading.setTextFill(Color.web(darkNavy));

        Label subtitle = new Label("Enter your College Register Number to continue");
        subtitle.setFont(Font.font("Segoe UI", 13));
        subtitle.setTextFill(Color.web(textMuted));

        TextField userId = createStyledTextField(role + " Register Number");
        PasswordField password = createStyledPasswordField("Password");

        Button login = createPrimaryButton("Sign In");
        Button register = createSecondaryButton("Create New Account");
        Button back = new Button("← Back to Home");
        back.setStyle("-fx-background-color: transparent; -fx-text-fill: " + textMuted + "; -fx-font-weight: bold; -fx-cursor: hand;");

        Label message = new Label();
        message.setTextFill(Color.web("#EF4444"));

        back.setOnAction(e -> showHome());
        register.setOnAction(e -> showRegister(role));

        login.setOnAction(e -> {
            if (userId.getText().trim().isEmpty() || password.getText().isEmpty()) {
                message.setText("Please enter Register Number and password.");
                return;
            }

            if (role.equals("Student")) {
                Student student = studentHandler.login(userId.getText().trim(), password.getText());
                if (student != null) showStudentDashboard(student);
                else message.setText("Invalid Register Number or Password!");
            } else {
                Admin admin = adminHandler.login(userId.getText().trim(), password.getText());
                if (admin != null) showAdminDashboard(admin);
                else message.setText("Invalid Admin Register Number or Password!");
            }
        });

        form.getChildren().addAll(heading, subtitle, userId, password, login, register, message, back);
        form.setAlignment(Pos.CENTER);
        form.setMaxWidth(400);
        form.setPadding(new Insets(40));
        form.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 16px; -fx-border-color: " + borderGray + "; -fx-border-radius: 16px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.06), 20, 0, 0, 8);");

        showForm(form);
    }

    private void showRegister(String role) {
        VBox form = new VBox(12);

        Label heading = new Label(role + " Registration");
        heading.setFont(Font.font("Segoe UI", FontWeight.BOLD, 26));
        heading.setTextFill(Color.web(darkNavy));

        TextField name = createStyledTextField("Full Name");
        TextField registerNumber = createStyledTextField("College Register Number");
        TextField email = createStyledTextField("Email Address");
        TextField department = createStyledTextField("Department");
        TextField subject = createStyledTextField(role.equals("Admin") ? "Subject" : "Class / Section");
        PasswordField password = createStyledPasswordField("Create Password");
        PasswordField confirm = createStyledPasswordField("Confirm Password");

        Button submit = createPrimaryButton("Register Account");
        Button back = new Button("← Back to Home");
        back.setStyle("-fx-background-color: transparent; -fx-text-fill: " + textMuted + "; -fx-font-weight: bold; -fx-cursor: hand;");
        Label message = new Label();
        message.setWrapText(true);

        submit.setOnAction(e -> {
            if (name.getText().trim().isEmpty() || registerNumber.getText().trim().isEmpty() ||
                email.getText().trim().isEmpty() || department.getText().trim().isEmpty() ||
                subject.getText().trim().isEmpty() || password.getText().isEmpty() || confirm.getText().isEmpty()) {
                message.setText("Please fill all fields.");
                return;
            }

            if (!password.getText().equals(confirm.getText())) {
                message.setText("Passwords do not match.");
                return;
            }

            String id = registerNumber.getText().trim();
            if (role.equals("Admin")) {
                Admin admin = new Admin(id, name.getText().trim(), email.getText().trim(), department.getText().trim(), subject.getText().trim(), password.getText());
                if (adminHandler.registerAdmin(admin)) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Admin account registered!\nLogin ID: " + id);
                    alert.showAndWait();
                    showLogin("Admin");
                } else message.setText("Register number or email already exists!");
            } else {
                Student student = new Student(id, name.getText().trim(), email.getText().trim(), department.getText().trim(), subject.getText().trim(), password.getText());
                if (studentHandler.registerStudent(student)) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Student registered successfully!\nLogin ID: " + id);
                    alert.showAndWait();
                    showLogin("Student");
                } else message.setText("Register number or email already exists!");
            }
        });

        back.setOnAction(e -> showHome());
        form.getChildren().addAll(heading, name, registerNumber, email, department, subject, password, confirm, submit, message, back);
        form.setAlignment(Pos.CENTER);
        form.setMaxWidth(420);
        form.setPadding(new Insets(30));
        form.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 16px; -fx-border-color: " + borderGray + "; -fx-border-radius: 16px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.06), 20, 0, 0, 8);");

        showForm(form);
    }

    private void showForm(VBox form) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + lightBg + ";");
        StackPane center = new StackPane(form);
        center.setPadding(new Insets(30));
        root.setCenter(center);
        stage.setScene(new Scene(root, 1150, 750));
    }

    // ---------------- STUDENT DASHBOARD ----------------
    private void showStudentDashboard(Student student) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + lightBg + ";");

        Label logo = new Label("QuizMaster | Student Workspace");
        logo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        logo.setTextFill(Color.WHITE);

        Button logoutBtn = createSecondaryButton("Logout");
        logoutBtn.setPrefWidth(90);
        logoutBtn.setOnAction(e -> showHome());

        Region navSpacer = new Region();
        HBox.setHgrow(navSpacer, Priority.ALWAYS);

        HBox topBar = new HBox(15, logo, navSpacer, logoutBtn);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(15, 30, 15, 30));
        topBar.setStyle("-fx-background-color: " + darkNavy + ";");
        root.setTop(topBar);

        VBox content = new VBox(20);
        content.setPadding(new Insets(30));

        Label welcome = new Label("Welcome back, " + student.getName());
        welcome.setFont(Font.font("Segoe UI", FontWeight.BOLD, 26));
        welcome.setTextFill(Color.web(darkNavy));

        Label details = new Label("Register No: " + student.getStudentId() + "   |   Dept: " + student.getDepartment() + "   |   Section: " + student.getSection());
        details.setFont(Font.font("Segoe UI", 13));
        details.setTextFill(Color.web(textMuted));

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14px;");

        Set<String> completedQuizIds = new HashSet<>();
        for (StudentScore score : leaderboardList) {
            if (score.getRegNo().equalsIgnoreCase(student.getStudentId()) && score.getStatus().equals("Completed")) {
                completedQuizIds.add(score.getQuizId());
            }
        }

        Tab todoTab = new Tab("To-Do Quizzes");
        VBox todoBox = new VBox(15);
        todoBox.setPadding(new Insets(20));

        boolean activeFound = false;
        for (QuizSummary quiz : quizList) {
            if (quiz.isActive() && !completedQuizIds.contains(quiz.getQuizId())) {
                activeFound = true;
                HBox card = new HBox(20);
                card.setPadding(new Insets(20));
                card.setAlignment(Pos.CENTER_LEFT);
                card.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: " + borderGray + "; -fx-border-radius: 12px; -fx-background-radius: 12px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.03), 8, 0, 0, 3);");

                VBox infoBox = new VBox(5);
                Label qTitle = new Label(quiz.getQuizName());
                qTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 17));
                Label qMeta = new Label("Questions: " + quiz.getQuestions() + "  •  Duration: " + quiz.getDurationMinutes() + " Mins  •  Schedule: " + quiz.getSchedule());
                qMeta.setTextFill(Color.web(textMuted));
                infoBox.getChildren().addAll(qTitle, qMeta);

                Region sp = new Region();
                HBox.setHgrow(sp, Priority.ALWAYS);

                Button startBtn = createSuccessButton("Start Quiz →");
                startBtn.setOnAction(e -> startQuizExecution(student, quiz));

                card.getChildren().addAll(infoBox, sp, startBtn);
                todoBox.getChildren().add(card);
            }
        }
        if (!activeFound) {
            Label emptyLbl = new Label("No pending quizzes available right now.");
            emptyLbl.setTextFill(Color.web("#94A3B8"));
            emptyLbl.setFont(Font.font("Segoe UI", 14));
            todoBox.getChildren().add(emptyLbl);
        }
        todoTab.setContent(new ScrollPane(todoBox));

        Tab upcomingTab = new Tab("Upcoming Quizzes");
        VBox upcomingBox = new VBox(15);
        upcomingBox.setPadding(new Insets(20));

        boolean upcomingFound = false;
        for (QuizSummary quiz : quizList) {
            if (!quiz.isActive()) {
                upcomingFound = true;
                VBox card = new VBox(6);
                card.setPadding(new Insets(18));
                card.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: " + borderGray + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");

                Label qTitle = new Label(quiz.getQuizName());
                qTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
                Label qMeta = new Label("Questions: " + quiz.getQuestions() + "  •  Scheduled Time: " + quiz.getSchedule() + " (Not Started Yet)");
                qMeta.setTextFill(Color.web(textMuted));

                card.getChildren().addAll(qTitle, qMeta);
                upcomingBox.getChildren().add(card);
            }
        }
        if (!upcomingFound) {
            Label emptyLbl = new Label("No upcoming quizzes scheduled.");
            emptyLbl.setTextFill(Color.web("#94A3B8"));
            upcomingBox.getChildren().add(emptyLbl);
        }
        upcomingTab.setContent(new ScrollPane(upcomingBox));

        Tab historyTab = new Tab("My History");
        VBox historyBox = new VBox(15);
        historyBox.setPadding(new Insets(20));

        TableView<StudentScore> historyTable = new TableView<>();
        ObservableList<StudentScore> myScores = FXCollections.observableArrayList();
        for (StudentScore score : leaderboardList) {
            if (score.getRegNo().equalsIgnoreCase(student.getStudentId())) {
                myScores.add(score);
            }
        }
        historyTable.setItems(myScores);
        historyTable.setPlaceholder(new Label("You have not attempted any quizzes yet."));

        TableColumn<StudentScore, String> colQName = new TableColumn<>("Quiz Title");
        colQName.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getQuizName()));
        TableColumn<StudentScore, String> colStatus = new TableColumn<>("Submission Status");
        colStatus.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatus()));
        TableColumn<StudentScore, String> colScore = new TableColumn<>("Score Obtained");
        colScore.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getScore()));

        historyTable.getColumns().addAll(colQName, colStatus, colScore);
        historyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        historyBox.getChildren().add(historyTable);
        historyTab.setContent(historyBox);

        tabPane.getTabs().addAll(todoTab, upcomingTab, historyTab);

        content.getChildren().addAll(welcome, details, tabPane);
        root.setCenter(content);

        stage.setScene(new Scene(root, 1150, 750));
    }

    // ---------------- LIVE QUIZ EXECUTION ENGINE ----------------
    private int currentQIndex = 0;
    private List<Integer> selectedAnswers = new ArrayList<>();
    private Timeline overallDurationTimer;
    private int totalSecondsRemaining;

    private void startQuizExecution(Student student, QuizSummary quiz) {
        currentQIndex = 0;
        selectedAnswers.clear();
        for (int i = 0; i < quiz.getQuestionsList().size(); i++) {
            selectedAnswers.add(-1);
        }

        leaderboardList.removeIf(s -> s.getRegNo().equalsIgnoreCase(student.getStudentId()) && s.getQuizId().equals(quiz.getQuizId()));
        leaderboardList.add(new StudentScore(student.getStudentId(), student.getName(), quiz.getQuizId(), quiz.getQuizName(), "In Progress", "N/A", 0));

        totalSecondsRemaining = quiz.getDurationMinutes() * 60;
        renderQuestionScreen(student, quiz, true);
    }

    private void renderQuestionScreen(Student student, QuizSummary quiz, boolean startTimer) {
        List<QuestionData> qList = quiz.getQuestionsList();
        if (currentQIndex >= qList.size()) {
            if (overallDurationTimer != null) overallDurationTimer.stop();
            calculateAndDisplayResults(student, quiz);
            return;
        }

        QuestionData currentQ = qList.get(currentQIndex);

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + lightBg + ";");

        Label quizHeader = new Label(quiz.getQuizName() + " (Question " + (currentQIndex + 1) + " of " + qList.size() + ")");
        quizHeader.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        quizHeader.setTextFill(Color.WHITE);

        Label timerLabel = new Label("Time Remaining: " + formatTime(totalSecondsRemaining));
        timerLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        timerLabel.setTextFill(Color.web("#EF4444"));
        timerLabel.setStyle("-fx-background-color: #FEF2F2; -fx-padding: 6 16; -fx-background-radius: 20px; -fx-border-color: #FCA5A5; -fx-border-radius: 20px;");

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        HBox topBar = new HBox(15, quizHeader, sp, timerLabel);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(15, 30, 15, 30));
        topBar.setStyle("-fx-background-color: " + darkNavy + ";");
        root.setTop(topBar);

        VBox mainBox = new VBox(16);
        mainBox.setPadding(new Insets(35));
        mainBox.setMaxWidth(780);
        mainBox.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: " + borderGray + "; -fx-border-radius: 14px; -fx-background-radius: 14px; -fx-effect: dropshadow(three-pass-box, rgba(15,23,42,0.04), 15, 0, 0, 5);");

        Label diffTag = new Label(currentQ.getDifficulty().toUpperCase() + "  •  " + currentQ.getPoints() + " MARKS");
        diffTag.setStyle("-fx-background-color: #EEF2FF; -fx-text-fill: " + primaryIndigo + "; -fx-font-weight: bold; -fx-padding: 5 12; -fx-background-radius: 6px;");

        Label qTextLabel = new Label("Q" + (currentQIndex + 1) + ". " + currentQ.getQuestionText());
        qTextLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 19));
        qTextLabel.setWrapText(true);

        ToggleGroup optionsGroup = new ToggleGroup();
        RadioButton rbA = new RadioButton("A) " + currentQ.getOptionA());
        RadioButton rbB = new RadioButton("B) " + currentQ.getOptionB());
        RadioButton rbC = new RadioButton("C) " + currentQ.getOptionC());
        RadioButton rbD = new RadioButton("D) " + currentQ.getOptionD());

        rbA.setFont(Font.font("Segoe UI", 15));
        rbB.setFont(Font.font("Segoe UI", 15));
        rbC.setFont(Font.font("Segoe UI", 15));
        rbD.setFont(Font.font("Segoe UI", 15));

        rbA.setToggleGroup(optionsGroup);
        rbB.setToggleGroup(optionsGroup);
        rbC.setToggleGroup(optionsGroup);
        rbD.setToggleGroup(optionsGroup);

        int prevSelected = selectedAnswers.get(currentQIndex);
        if (prevSelected == 0) rbA.setSelected(true);
        else if (prevSelected == 1) rbB.setSelected(true);
        else if (prevSelected == 2) rbC.setSelected(true);
        else if (prevSelected == 3) rbD.setSelected(true);

        VBox optsBox = new VBox(14, rbA, rbB, rbC, rbD);
        optsBox.setPadding(new Insets(10, 0, 10, 0));

        mainBox.getChildren().addAll(diffTag, qTextLabel, optsBox);

        StackPane centerPane = new StackPane(mainBox);
        centerPane.setPadding(new Insets(30));
        root.setCenter(centerPane);

        Button prevBtn = new Button("← Previous");
        prevBtn.setStyle("-fx-background-color: #E2E8F0; -fx-text-fill: " + darkNavy + "; -fx-font-weight: bold; -fx-padding: 10 22; -fx-background-radius: 8px; -fx-cursor: hand;");
        prevBtn.setDisable(currentQIndex == 0);
        prevBtn.setOnAction(e -> {
            saveAnswer(optionsGroup);
            currentQIndex--;
            renderQuestionScreen(student, quiz, false);
        });

        Button actionBtn;
        if (currentQIndex == qList.size() - 1) {
            actionBtn = new Button("Submit Quiz");
            actionBtn.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 22; -fx-background-radius: 8px; -fx-cursor: hand;");
            actionBtn.setOnAction(e -> {
                saveAnswer(optionsGroup);
                promptSubmitConfirmation(student, quiz);
            });
        } else {
            actionBtn = new Button("Next Question →");
            actionBtn.setStyle("-fx-background-color: " + primaryIndigo + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 22; -fx-background-radius: 8px; -fx-cursor: hand;");
            actionBtn.setOnAction(e -> {
                saveAnswer(optionsGroup);
                currentQIndex++;
                renderQuestionScreen(student, quiz, false);
            });
        }

        Region bottomSpacer = new Region();
        HBox.setHgrow(bottomSpacer, Priority.ALWAYS);

        HBox bottomBar = new HBox(15, prevBtn, bottomSpacer, actionBtn);
        bottomBar.setAlignment(Pos.CENTER_RIGHT);
        bottomBar.setPadding(new Insets(20, 50, 20, 50));
        root.setBottom(bottomBar);

        if (startTimer) {
            if (overallDurationTimer != null) overallDurationTimer.stop();
            overallDurationTimer = new Timeline(new KeyFrame(javafx.util.Duration.seconds(1), ev -> {
                totalSecondsRemaining--;
                timerLabel.setText("Time Remaining: " + formatTime(totalSecondsRemaining));
                if (totalSecondsRemaining <= 0) {
                    overallDurationTimer.stop();
                    Alert timeUpAlert = new Alert(Alert.AlertType.INFORMATION, "Time's up! Your answers are being auto-submitted.");
                    timeUpAlert.setHeaderText("Duration Expired");
                    timeUpAlert.showAndWait();
                    calculateAndDisplayResults(student, quiz);
                }
            }));
            overallDurationTimer.setCycleCount(Timeline.INDEFINITE);
            overallDurationTimer.play();
        }

        stage.setScene(new Scene(root, 1150, 750));
    }

    private String formatTime(int totalSecs) {
        int mins = totalSecs / 60;
        int secs = totalSecs % 60;
        return String.format("%02d:%02d", mins, secs);
    }

    private void saveAnswer(ToggleGroup optionsGroup) {
        RadioButton selected = (RadioButton) optionsGroup.getSelectedToggle();
        if (selected != null) {
            String txt = selected.getText();
            if (txt.startsWith("A)")) selectedAnswers.set(currentQIndex, 0);
            else if (txt.startsWith("B)")) selectedAnswers.set(currentQIndex, 1);
            else if (txt.startsWith("C)")) selectedAnswers.set(currentQIndex, 2);
            else if (txt.startsWith("D)")) selectedAnswers.set(currentQIndex, 3);
        }
    }

    private void promptSubmitConfirmation(Student student, QuizSummary quiz) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Submit Quiz Confirmation");
        confirmAlert.setHeaderText("Are you sure you want to submit?");
        confirmAlert.setContentText("Your responses will be recorded and scored immediately.");

        ButtonType stayButton = new ButtonType("Stay", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType submitButton = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        confirmAlert.getButtonTypes().setAll(submitButton, stayButton);

        confirmAlert.showAndWait().ifPresent(type -> {
            if (type == submitButton) {
                if (overallDurationTimer != null) overallDurationTimer.stop();
                calculateAndDisplayResults(student, quiz);
            }
        });
    }

    private void calculateAndDisplayResults(Student student, QuizSummary quiz) {
        if (overallDurationTimer != null) overallDurationTimer.stop();

        int userObtainedMarks = 0;
        int totalPossibleMarks = quiz.getTotalPossibleMarks();

        List<QuestionData> questions = quiz.getQuestionsList();
        for (int i = 0; i < questions.size(); i++) {
            QuestionData q = questions.get(i);
            if (selectedAnswers.get(i) == q.getCorrectOptionIndex()) {
                userObtainedMarks += q.getPoints();
            }
        }

        double scorePercentage = ((double) userObtainedMarks / totalPossibleMarks) * 100.0;
        String formattedScore = String.format("%.1f%% (%d/%d Marks)", scorePercentage, userObtainedMarks, totalPossibleMarks);

        leaderboardList.removeIf(s -> s.getRegNo().equalsIgnoreCase(student.getStudentId()) && s.getQuizId().equals(quiz.getQuizId()));
        leaderboardList.add(new StudentScore(student.getStudentId(), student.getName(), quiz.getQuizId(), quiz.getQuizName(), "Completed", formattedScore, userObtainedMarks));
        quiz.incrementParticipants();

        VBox layout = new VBox(22);
        layout.setPadding(new Insets(35));
        layout.setStyle("-fx-background-color: " + lightBg + ";");

        Label heading = new Label("Quiz Submitted Successfully!");
        heading.setFont(Font.font("Segoe UI", FontWeight.BOLD, 26));
        heading.setTextFill(Color.web(darkNavy));

        Label scoreLbl = new Label("Final Score: " + formattedScore);
        scoreLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        scoreLbl.setTextFill(Color.web(primaryIndigo));

        Label reviewTitle = new Label("Detailed Solution & Question Breakdown:");
        reviewTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));

        VBox reviewContainer = new VBox(15);
        for (int i = 0; i < questions.size(); i++) {
            QuestionData q = questions.get(i);
            int userAns = selectedAnswers.get(i);
            int correctAns = q.getCorrectOptionIndex();

            VBox card = new VBox(8);
            card.setPadding(new Insets(18));
            card.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: " + borderGray + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");

            Label qLbl = new Label("Q" + (i + 1) + " [" + q.getDifficulty() + "  •  " + q.getPoints() + " Marks]: " + q.getQuestionText());
            qLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));

            String[] opts = {q.getOptionA(), q.getOptionB(), q.getOptionC(), q.getOptionD()};
            Label userLbl = new Label("Your Response: " + (userAns == -1 ? "Not Attempted" : opts[userAns]));
            userLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
            userLbl.setTextFill(userAns == correctAns ? Color.web("#10B981") : Color.web("#EF4444"));

            Label correctLbl = new Label("Correct Answer: " + opts[correctAns]);
            correctLbl.setFont(Font.font("Segoe UI", 14));
            correctLbl.setTextFill(Color.web(primaryIndigo));

            card.getChildren().addAll(qLbl, userLbl, correctLbl);
            reviewContainer.getChildren().add(card);
        }

        Button backDashBtn = createPrimaryButton("Return to Dashboard");
        backDashBtn.setMaxWidth(260);
        backDashBtn.setOnAction(e -> showStudentDashboard(student));

        layout.getChildren().addAll(heading, scoreLbl, reviewTitle, reviewContainer, backDashBtn);

        ScrollPane scrollPane = new ScrollPane(layout);
        scrollPane.setFitToWidth(true);

        stage.setScene(new Scene(scrollPane, 1150, 750));
    }

    // ---------------- FULL ADMIN DASHBOARD ----------------
    private void showAdminDashboard(Admin admin) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + lightBg + ";");

        Label logo = new Label("QuizMaster | Admin Workspace");
        logo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        logo.setTextFill(Color.WHITE);

        Button createQuizBtn = createSuccessButton("+ Create New Quiz");
        createQuizBtn.setOnAction(e -> showCreateQuizWindow(admin));

        Button logoutBtn = createSecondaryButton("Logout");
        logoutBtn.setPrefWidth(90);
        logoutBtn.setOnAction(e -> showHome());

        Region navSpacer = new Region();
        HBox.setHgrow(navSpacer, Priority.ALWAYS);

        HBox topBar = new HBox(15, logo, navSpacer, createQuizBtn, logoutBtn);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(15, 30, 15, 30));
        topBar.setStyle("-fx-background-color: " + darkNavy + ";");
        root.setTop(topBar);

        Label adminDetails = new Label("Admin: " + admin.getName() + " (ID: " + admin.getAdminId() + ")   •   Subject: " + admin.getSubject() + "   •   Dept: " + admin.getDepartment());
        adminDetails.setFont(Font.font("Segoe UI", 13));
        adminDetails.setTextFill(Color.web(textMuted));

        VBox metricCard = new VBox(6);
        metricCard.setPadding(new Insets(20));
        metricCard.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 12px; -fx-border-color: " + borderGray + "; -fx-border-radius: 12px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.03), 10, 0, 0, 4);");
        Label metricTitle = new Label("Total Quizzes Conducted");
        metricTitle.setTextFill(Color.web(textMuted));
        metricTitle.setFont(Font.font("Segoe UI", 13));
        Label metricValue = new Label(String.valueOf(quizList.size()));
        metricValue.setFont(Font.font("Segoe UI", FontWeight.BOLD, 34));
        metricValue.setTextFill(Color.web(darkNavy));
        metricCard.getChildren().addAll(metricTitle, metricValue);

        Label qhTitle = new Label("Conducted Quizzes (Click 'View Results' to open student leaderboard)");
        qhTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        qhTitle.setTextFill(Color.web(darkNavy));

        TableView<QuizSummary> qhTable = new TableView<>(quizList);
        qhTable.setPlaceholder(new Label("No quizzes created yet. Click '+ Create New Quiz' to begin."));
        qhTable.setPrefHeight(250);

        TableColumn<QuizSummary, String> colHistQName = new TableColumn<>("Quiz Name");
        colHistQName.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getQuizName()));

        TableColumn<QuizSummary, String> colNumQ = new TableColumn<>("Questions");
        colNumQ.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getQuestions())));

        TableColumn<QuizSummary, String> colPart = new TableColumn<>("Submissions");
        colPart.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getParticipants())));

        TableColumn<QuizSummary, String> colTime = new TableColumn<>("Schedule");
        colTime.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getSchedule()));

        TableColumn<QuizSummary, Void> colAction = new TableColumn<>("Action");
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("View Results");
            {
                btn.setStyle("-fx-background-color: " + primaryIndigo + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px; -fx-background-radius: 6px; -fx-cursor: hand;");
                btn.setOnAction(event -> {
                    QuizSummary selectedQuiz = getTableView().getItems().get(getIndex());
                    showQuizBreakdownModal(selectedQuiz);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setGraphic(null);
                else setGraphic(btn);
            }
        });

        qhTable.getColumns().addAll(colHistQName, colNumQ, colPart, colTime, colAction);
        qhTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        VBox leftSection = new VBox(15, adminDetails, metricCard, qhTitle, qhTable);
        leftSection.setPrefWidth(640);

        Label chartTitle = new Label("Quiz Submissions Analytics");
        chartTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        chartTitle.setTextFill(Color.web(darkNavy));

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Quizzes");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Submissions");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Participation Rates");
        barChart.setLegendVisible(false);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (QuizSummary q : quizList) {
            series.getData().add(new XYChart.Data<>(q.getQuizName(), q.getParticipants()));
        }
        barChart.getData().add(series);

        VBox rightSection = new VBox(15, chartTitle, barChart);
        rightSection.setPadding(new Insets(18));
        rightSection.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 12px; -fx-border-color: " + borderGray + "; -fx-border-radius: 12px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.03), 10, 0, 0, 4);");
        HBox.setHgrow(rightSection, Priority.ALWAYS);

        HBox mainLayout = new HBox(25, leftSection, rightSection);
        mainLayout.setPadding(new Insets(25));

        ScrollPane scrollPane = new ScrollPane(mainLayout);
        scrollPane.setFitToWidth(true);
        root.setCenter(scrollPane);

        stage.setScene(new Scene(root, 1150, 750));
    }

    // Modal Results Breakdown View
    private void showQuizBreakdownModal(QuizSummary quiz) {
        Stage modal = new Stage();
        modal.setTitle("Quiz Analytics - " + quiz.getQuizName());

        VBox layout = new VBox(16);
        layout.setPadding(new Insets(25));
        layout.setStyle("-fx-background-color: #FFFFFF;");

        Label title = new Label("Participation Breakdown: " + quiz.getQuizName());
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        title.setTextFill(Color.web(darkNavy));

        int startedCount = 0;
        int completedCount = 0;
        int notStartedCount = 0;

        ObservableList<StudentScore> quizScores = FXCollections.observableArrayList();
        for (StudentScore s : leaderboardList) {
            if (s.getQuizId().equals(quiz.getQuizId())) {
                quizScores.add(s);
                if (s.getStatus().equals("Completed")) completedCount++;
                else if (s.getStatus().equals("In Progress")) startedCount++;
                else notStartedCount++;
            }
        }

        HBox statCards = new HBox(15);
        statCards.getChildren().addAll(
            createStatBadge("In Progress", String.valueOf(startedCount), "#EAB308"),
            createStatBadge("Submitted", String.valueOf(completedCount), "#10B981"),
            createStatBadge("Not Attempted", String.valueOf(notStartedCount), "#EF4444")
        );

        Label tableHeader = new Label("Student Rankings & Response Status");
        tableHeader.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));

        TableView<StudentScore> modalTable = new TableView<>(quizScores);
        modalTable.setPlaceholder(new Label("No student responses registered yet."));

        TableColumn<StudentScore, String> colReg = new TableColumn<>("Register No");
        colReg.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getRegNo()));

        TableColumn<StudentScore, String> colName = new TableColumn<>("Student Name");
        colName.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getName()));

        TableColumn<StudentScore, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatus()));

        TableColumn<StudentScore, String> colScore = new TableColumn<>("Score");
        colScore.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getScore()));

        modalTable.getColumns().addAll(colReg, colName, colStatus, colScore);
        modalTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        Button closeBtn = createSecondaryButton("Close");
        closeBtn.setOnAction(e -> modal.close());

        layout.getChildren().addAll(title, statCards, tableHeader, modalTable, closeBtn);

        Scene scene = new Scene(layout, 680, 540);
        modal.setScene(scene);
        modal.showAndWait();
    }

    private VBox createStatBadge(String label, String value, String colorHex) {
        VBox badge = new VBox(4);
        badge.setPadding(new Insets(14));
        badge.setPrefWidth(190);
        badge.setStyle("-fx-background-color: " + lightBg + "; -fx-border-color: " + colorHex + "; -fx-border-width: 0 0 0 4px; -fx-background-radius: 6px;");

        Label v = new Label(value);
        v.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        v.setTextFill(Color.web(darkNavy));

        Label l = new Label(label);
        l.setFont(Font.font("Segoe UI", 12));
        l.setTextFill(Color.web(textMuted));

        badge.getChildren().addAll(v, l);
        return badge;
    }

    // ---------------- CREATE QUIZ MODAL FORM ----------------
    private void showCreateQuizWindow(Admin currentAdmin) {
        Stage modal = new Stage();
        modal.setTitle("Create Quiz");

        VBox mainLayout = new VBox(18);
        mainLayout.setPadding(new Insets(25));
        mainLayout.setStyle("-fx-background-color: #FFFFFF;");

        Label heading = new Label("Create & Schedule Quiz");
        heading.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        heading.setTextFill(Color.web(darkNavy));

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(14);

        TextField nameFx = createStyledTextField("e.g. Java Fundamentals Quiz");
        TextField totalQFx = createStyledTextField("Number of questions (e.g. 5)");

        DatePicker startDateFx = new DatePicker();
        TextField startTimeFx = createStyledTextField("e.g. 10:00 AM");

        DatePicker endDateFx = new DatePicker();
        TextField endTimeFx = createStyledTextField("e.g. 11:30 AM");

        TextField durationFx = createStyledTextField("Overall Duration in Mins (e.g. 30)");

        // Explicitly Labeled Marks Fields per Difficulty Level
        TextField easyMarksFx = createStyledTextField("2");
        easyMarksFx.setPrefWidth(55);
        
        TextField medMarksFx = createStyledTextField("4");
        medMarksFx.setPrefWidth(55);
        
        TextField hardMarksFx = createStyledTextField("5");
        hardMarksFx.setPrefWidth(55);

        Label easyLbl = new Label("Easy Marks:");
        easyLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #0F172A;");
        Label medLbl = new Label("Medium Marks:");
        medLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #0F172A;");
        Label hardLbl = new Label("Hard Marks:");
        hardLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #0F172A;");

        HBox marksConfigBox = new HBox(12,
            easyLbl, easyMarksFx,
            medLbl, medMarksFx,
            hardLbl, hardMarksFx
        );
        marksConfigBox.setAlignment(Pos.CENTER_LEFT);

        CheckBox activeFx = new CheckBox("Publish to Active To-Do List Immediately");
        activeFx.setSelected(true);
        activeFx.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 13px;");

        grid.add(new Label("Quiz Title:"), 0, 0); grid.add(nameFx, 1, 0, 3, 1);
        grid.add(new Label("Total Questions:"), 0, 1); grid.add(totalQFx, 1, 1, 3, 1);
        grid.add(new Label("Start Date:"), 0, 2); grid.add(startDateFx, 1, 2);
        grid.add(new Label("Start Time:"), 2, 2); grid.add(startTimeFx, 3, 2);
        grid.add(new Label("End Date:"), 0, 3); grid.add(endDateFx, 1, 3);
        grid.add(new Label("End Time:"), 2, 3); grid.add(endTimeFx, 3, 3);
        grid.add(new Label("Duration (Mins):"), 0, 4); grid.add(durationFx, 1, 4, 3, 1);
        grid.add(new Label("Mark Scheme:"), 0, 5); grid.add(marksConfigBox, 1, 5, 3, 1);
        grid.add(activeFx, 1, 6, 3, 1);

        VBox questionsContainer = new VBox(15);
        Button generateQuestionsBtn = createSecondaryButton("Set / Generate Questions");

        List<TextField> questionFields = new ArrayList<>();
        List<List<TextField>> optionFieldsList = new ArrayList<>();
        List<ComboBox<String>> correctOptionsList = new ArrayList<>();
        List<ComboBox<String>> difficultyList = new ArrayList<>();

        generateQuestionsBtn.setOnAction(e -> {
            questionsContainer.getChildren().clear();
            questionFields.clear();
            optionFieldsList.clear();
            correctOptionsList.clear();
            difficultyList.clear();

            String qCountStr = totalQFx.getText().trim();
            if (qCountStr.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please enter total questions count first!");
                alert.showAndWait();
                return;
            }

            try {
                int count = Integer.parseInt(qCountStr);
                if (count <= 0) throw new NumberFormatException();

                for (int i = 1; i <= count; i++) {
                    VBox qBox = new VBox(8);
                    qBox.setPadding(new Insets(14));
                    qBox.setStyle("-fx-background-color: " + lightBg + "; -fx-border-color: " + borderGray + "; -fx-border-radius: 8px;");

                    Label qLabel = new Label("Question " + i);
                    qLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

                    TextField qText = createStyledTextField("Enter Question " + i + " Text");
                    questionFields.add(qText);

                    TextField optA = createStyledTextField("Option A");
                    TextField optB = createStyledTextField("Option B");
                    TextField optC = createStyledTextField("Option C");
                    TextField optD = createStyledTextField("Option D");

                    List<TextField> opts = new ArrayList<>();
                    opts.add(optA); opts.add(optB); opts.add(optC); opts.add(optD);
                    optionFieldsList.add(opts);

                    ComboBox<String> diffBox = new ComboBox<>();
                    diffBox.getItems().addAll("Easy", "Medium", "Hard");
                    diffBox.setValue("Easy");
                    diffBox.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: " + borderGray + "; -fx-border-radius: 4px;");
                    difficultyList.add(diffBox);

                    ComboBox<String> correctBox = new ComboBox<>();
                    correctBox.getItems().addAll("Option A", "Option B", "Option C", "Option D");
                    correctBox.setValue("Option A");
                    correctBox.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: " + borderGray + "; -fx-border-radius: 4px;");
                    correctOptionsList.add(correctBox);

                    HBox controlsRow = new HBox(15,
                        new Label("Difficulty:"), diffBox,
                        new Label("Correct Option:"), correctBox
                    );
                    controlsRow.setAlignment(Pos.CENTER_LEFT);

                    qBox.getChildren().addAll(qLabel, qText, optA, optB, optC, optD, controlsRow);
                    questionsContainer.getChildren().add(qBox);
                }
            } catch (NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please enter a valid positive number for total questions.");
                alert.showAndWait();
            }
        });

        Button saveQuizBtn = createPrimaryButton("Publish Quiz");
        saveQuizBtn.setOnAction(e -> {
            if (nameFx.getText().trim().isEmpty() || totalQFx.getText().trim().isEmpty() ||
                startDateFx.getValue() == null || endDateFx.getValue() == null ||
                startTimeFx.getText().trim().isEmpty() || endTimeFx.getText().trim().isEmpty() ||
                durationFx.getText().trim().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please fill in all quiz schedule and timing details.");
                alert.showAndWait();
                return;
            }

            if (questionFields.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Click 'Set / Generate Questions' to build questions before publishing.");
                alert.showAndWait();
                return;
            }

            try {
                int qCount = Integer.parseInt(totalQFx.getText().trim());
                int durationMins = Integer.parseInt(durationFx.getText().trim());
                int easyMarks = Integer.parseInt(easyMarksFx.getText().trim());
                int medMarks = Integer.parseInt(medMarksFx.getText().trim());
                int hardMarks = Integer.parseInt(hardMarksFx.getText().trim());

                String fullSchedule = startDateFx.getValue() + " " + startTimeFx.getText().trim() + " - " + endDateFx.getValue() + " " + endTimeFx.getText().trim();

                List<QuestionData> constructedQList = new ArrayList<>();
                for (int i = 0; i < qCount; i++) {
                    String qText = questionFields.get(i).getText().trim();
                    List<TextField> opts = optionFieldsList.get(i);
                    int correctIdx = correctOptionsList.get(i).getSelectionModel().getSelectedIndex();
                    String diff = difficultyList.get(i).getValue();

                    int points = easyMarks;
                    if (diff.equals("Medium")) points = medMarks;
                    else if (diff.equals("Hard")) points = hardMarks;

                    constructedQList.add(new QuestionData(qText, opts.get(0).getText(), opts.get(1).getText(), opts.get(2).getText(), opts.get(3).getText(), correctIdx, diff, points));
                }

                String uniqueQuizId = "Q_" + System.currentTimeMillis();
                quizList.add(new QuizSummary(uniqueQuizId, nameFx.getText().trim(), qCount, 0, fullSchedule, activeFx.isSelected(), durationMins, easyMarks, medMarks, hardMarks, constructedQList));

                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Quiz published successfully!");
                alert.showAndWait();
                modal.close();

                showAdminDashboard(currentAdmin);
            } catch (NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Questions count, duration, and marks must be valid numbers!");
                alert.showAndWait();
            }
        });

        mainLayout.getChildren().addAll(heading, grid, generateQuestionsBtn, questionsContainer, saveQuizBtn);

        ScrollPane scrollPane = new ScrollPane(mainLayout);
        scrollPane.setFitToWidth(true);

        Scene scene = new Scene(scrollPane, 680, 720);
        modal.setScene(scene);
        modal.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
package Controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import java.time.*;
import java.util.*;
import java.time.format.TextStyle;
import Model.DBConnection;
import java.net.URL;
import java.sql.*;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class CalendarioController implements Initializable {
    private ZonedDateTime dateFocus;
    private ZonedDateTime today;
    
    @FXML private Text year;
    @FXML private Text month;
    @FXML private FlowPane calendar;
    
    private final List<Consumer<?>> eventHandlers = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dateFocus = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo"));
        today = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo"));
        drawCalendar();
        
        // Subscribe to events
        Consumer<AppointmentEvents.CalendarRefreshNeeded> refreshHandler = this::handleRefreshEvent;
        Consumer<AppointmentEvents.AppointmentDeleted> deleteHandler = this::handleAppointmentDeleted;
        
        EventBus.getInstance().subscribe(AppointmentEvents.CalendarRefreshNeeded.class, refreshHandler);
        EventBus.getInstance().subscribe(AppointmentEvents.AppointmentDeleted.class, deleteHandler);
        
        eventHandlers.add(refreshHandler);
        eventHandlers.add(deleteHandler);
    }
    
    private void handleRefreshEvent(AppointmentEvents.CalendarRefreshNeeded event) {
        Platform.runLater(() -> {
            if (event.getForDate() != null && !dateFocus.toLocalDate().equals(event.getForDate())) {
                dateFocus = ZonedDateTime.of(event.getForDate(), 
                    dateFocus.toLocalTime(), dateFocus.getZone());
            }
            refreshCalendar();
        });
    }
    
    private void handleAppointmentDeleted(AppointmentEvents.AppointmentDeleted event) {
        Platform.runLater(this::refreshCalendar);
    }
    
    public void refreshCalendar() {
        calendar.getChildren().clear();
        drawCalendar();
    }
    
    public void cleanup() {
        EventBus.getInstance().unsubscribe(AppointmentEvents.CalendarRefreshNeeded.class, 
            (Consumer<AppointmentEvents.CalendarRefreshNeeded>)eventHandlers.get(0));
        EventBus.getInstance().unsubscribe(AppointmentEvents.AppointmentDeleted.class, 
            (Consumer<AppointmentEvents.AppointmentDeleted>)eventHandlers.get(1));
    }

    @FXML
    void backOneMonth(ActionEvent event) {
        dateFocus = dateFocus.minusMonths(1);
        calendar.getChildren().clear();
        drawCalendar();
    }

    @FXML
    void forwardOneMonth(ActionEvent event) {
        dateFocus = dateFocus.plusMonths(1);
        calendar.getChildren().clear();
        drawCalendar();
    }

    private void drawCalendar() {
        year.setText(String.valueOf(dateFocus.getYear()));
        
        // Get month name in Portuguese (Brazil)
        String monthName = dateFocus.getMonth().getDisplayName(TextStyle.FULL, new Locale("pt", "BR"));
        // Capitalize first letter
        monthName = monthName.substring(0, 1).toUpperCase() + monthName.substring(1);
        month.setText(monthName);

        double calendarWidth = calendar.getPrefWidth();
        double calendarHeight = calendar.getPrefHeight();
        double strokeWidth = 1;
        double spacingH = calendar.getHgap();
        double spacingV = calendar.getVgap();

        //List of activities for a given month
        Map<Integer, List<CalendarioActivity>> calendarActivityMap = getCalendarActivitiesMonth(dateFocus);

        int monthMaxDate = dateFocus.getMonth().maxLength();
        //Check for leap year
        if(dateFocus.getYear() % 4 != 0 && monthMaxDate == 29){
            monthMaxDate = 28;
        }
        int dateOffset = ZonedDateTime.of(dateFocus.getYear(), dateFocus.getMonthValue(), 1,0,0,0,0,dateFocus.getZone()).getDayOfWeek().getValue();

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                StackPane stackPane = new StackPane();
                Rectangle rectangle = new Rectangle();
                rectangle.setFill(Color.TRANSPARENT);
                rectangle.setStroke(Color.GRAY);
                rectangle.setStrokeWidth(strokeWidth);
                double rectangleWidth =(calendarWidth/7) - strokeWidth - spacingH;
                rectangle.setWidth(rectangleWidth);
                double rectangleHeight = (calendarHeight/6) - strokeWidth - spacingV;
                rectangle.setHeight(rectangleHeight);
                stackPane.getChildren().add(rectangle);

                int calculatedDate = (j+1)+(7*i);
                if(calculatedDate > dateOffset){
                    int currentDate = calculatedDate - dateOffset;
                    if(currentDate <= monthMaxDate){
                        Text date = new Text(String.valueOf(currentDate));
                        date.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
                        
                        // Adjust position to top right corner
                        double padding = 5;
                        date.setTranslateX((-rectangleWidth / 2.2) + padding);
                        date.setTranslateY((-rectangleHeight / 2.4) + padding);
                        
                        stackPane.getChildren().add(date);
                        
                        List<CalendarioActivity> calendarActivities = calendarActivityMap.get(currentDate);
                        if(calendarActivities != null){
                            createCalendarActivity(calendarActivities, rectangleHeight, rectangleWidth, stackPane);
                        }
                    }
                    if(today.getYear() == dateFocus.getYear() && today.getMonth() == dateFocus.getMonth() && today.getDayOfMonth() == currentDate){
                        rectangle.setStroke(Color.MEDIUMVIOLETRED);
                    }
                }
                calendar.getChildren().add(stackPane);
            }
        }
    }

    private void createCalendarActivity(List<CalendarioActivity> calendarActivities, double cellHeight, double cellWidth, StackPane stackPane) {
        VBox calendarActivityBox = new VBox();
        calendarActivityBox.setMaxWidth(cellWidth - 10);
        calendarActivityBox.setMaxHeight(cellHeight * 0.5);
        calendarActivityBox.setStyle("-fx-background-color:#ebebfb; -fx-opacity: 0.8; -fx-padding: 2px; -fx-alignment: center;");

        if (!calendarActivities.isEmpty()) {
            String activityText = calendarActivities.get(0).getClientName() + ", " + 
                                 calendarActivities.get(0).getDate().toLocalTime().toString().substring(0, 5); // Show only hours and minutes

            if (calendarActivities.size() > 1) {
                activityText += " ...";
            }

            Text text = new Text(activityText);
            text.setStyle("-fx-font-size: 12px; -fx-wrap-text: true;");
            text.setWrappingWidth(cellWidth - 10);
            calendarActivityBox.getChildren().add(text);
        }

        StackPane.setAlignment(calendarActivityBox, Pos.CENTER);
        StackPane.setMargin(calendarActivityBox, new Insets(cellHeight * 0.2, 0, 0, 0));
        stackPane.getChildren().add(calendarActivityBox);
    }

    private Map<Integer, List<CalendarioActivity>> getCalendarActivitiesMonth(ZonedDateTime dateFocus) {
        Map<Integer, List<CalendarioActivity>> calendarActivityMap = new HashMap<>();

        String query = "SELECT a.data_agendamento, a.hora, t.nome " +
                       "FROM agendamentos a " +
                       "JOIN cad_tutor t ON a.id_tutor = t.id_tutor " +
                       "WHERE YEAR(a.data_agendamento) = ? " +
                       "AND MONTH(a.data_agendamento) = ? " +
                       "AND a.isDeleted = 0";

        DBConnection dbConnection = new DBConnection();
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, dateFocus.getYear());
            stmt.setInt(2, dateFocus.getMonthValue());

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                LocalDate localDate = rs.getDate("data_agendamento").toLocalDate();
                LocalTime localTime = rs.getTime("hora").toLocalTime();
                ZonedDateTime zonedDateTime = ZonedDateTime.of(localDate, localTime, dateFocus.getZone());

                String clientName = rs.getString("nome");

                CalendarioActivity activity = new CalendarioActivity(zonedDateTime, clientName, 0);
                int day = localDate.getDayOfMonth();

                calendarActivityMap.computeIfAbsent(day, k -> new ArrayList<>()).add(activity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return calendarActivityMap;
    }
}
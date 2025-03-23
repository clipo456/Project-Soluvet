/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package Controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.*;

import Model.DBConnection; 
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

public class CalendarioController implements Initializable {

    ZonedDateTime dateFocus;
    ZonedDateTime today;

    @FXML
    private Text year;

    @FXML
    private Text month;

    @FXML
    private FlowPane calendar;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dateFocus = ZonedDateTime.now();
        today = ZonedDateTime.now();
        drawCalendar();
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

    private void drawCalendar(){
        year.setText(String.valueOf(dateFocus.getYear()));
        month.setText(String.valueOf(dateFocus.getMonth()));

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
                //quadradinho de fora
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
                        
                        // Ajustar posição no canto superior direito
                        double padding = 5; // Espaço do canto
                        date.setTranslateX((-rectangleWidth / 2.2) + padding);
                        date.setTranslateY((-rectangleHeight / 2.4) + padding);
                        
                        stackPane.getChildren().add(date);
                        
                        List<CalendarioActivity> calendarActivities = calendarActivityMap.get(currentDate);
                        if(calendarActivities != null){
                            createCalendarActivity(calendarActivities, rectangleHeight, rectangleWidth, stackPane);
                        }
                    }
                    if(today.getYear() == dateFocus.getYear() && today.getMonth() == dateFocus.getMonth() && today.getDayOfMonth() == currentDate){
                        rectangle.setStroke(Color.BLUE);
                    }
                }
                calendar.getChildren().add(stackPane);
            }
        }
    }

   private void createCalendarActivity(List<CalendarioActivity> calendarActivities, double cellHeight, double cellWidth, StackPane stackPane) {
        VBox calendarActivityBox = new VBox();
        calendarActivityBox.setMaxWidth(cellWidth - 10); // Ensure the VBox does not exceed the cell width
        calendarActivityBox.setMaxHeight(cellHeight * 0.5); // Reduce the height of the activity box (50% of cell height)
        calendarActivityBox.setStyle("-fx-background-color:#EBEBFB; -fx-padding: 2px; -fx-alignment: center;");

        if (!calendarActivities.isEmpty()) {
            // Create the text for the first activity
            String activityText = calendarActivities.get(0).getClientName() + ", " + calendarActivities.get(0).getDate().toLocalTime();

            // Add "..." after the first activity if there are multiple activities
            if (calendarActivities.size() > 1) {
                activityText += " ..."; // Add "..." after the first activity
            }

            // Create a Text node with the combined text
            Text text = new Text(activityText);
            text.setStyle("-fx-font-size: 12px; -fx-wrap-text: true;");
            text.setWrappingWidth(cellWidth - 10); // Set the wrapping width to fit within the cell
            calendarActivityBox.getChildren().add(text);
        }

        // Position the activity box in the center of the StackPane, leaving space for the date at the top
        StackPane.setAlignment(calendarActivityBox, Pos.CENTER);
        StackPane.setMargin(calendarActivityBox, new Insets(cellHeight * 0.2, 0, 0, 0)); // Add top margin to leave space for the date
        stackPane.getChildren().add(calendarActivityBox);
    }

    private Map<Integer, List<CalendarioActivity>> createCalendarMap(List<CalendarioActivity> calendarActivities) {
        Map<Integer, List<CalendarioActivity>> calendarActivityMap = new HashMap<>();

        for (CalendarioActivity activity: calendarActivities) {
            int activityDate = activity.getDate().getDayOfMonth();
            if(!calendarActivityMap.containsKey(activityDate)){
                calendarActivityMap.put(activityDate, List.of(activity));
            } else {
                List<CalendarioActivity> OldListByDate = calendarActivityMap.get(activityDate);

                List<CalendarioActivity> newList = new ArrayList<>(OldListByDate);
                newList.add(activity);
                calendarActivityMap.put(activityDate, newList);
            }
        }
        return  calendarActivityMap;
    }

    private Map<Integer, List<CalendarioActivity>> getCalendarActivitiesMonth(ZonedDateTime dateFocus) {
        Map<Integer, List<CalendarioActivity>> calendarActivityMap = new HashMap<>();

        // Consulta SQL para buscar agendamentos do mês atual, incluindo o horário
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
                LocalTime localTime = rs.getTime("hora").toLocalTime();  // Obtendo o horário
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


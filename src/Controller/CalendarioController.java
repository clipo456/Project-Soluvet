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

    private void createCalendarActivity(List<CalendarioActivity> calendarActivities, double rectangleHeight, double rectangleWidth, StackPane stackPane) {
        VBox calendarActivityBox = new VBox();
        for (int k = 0; k < calendarActivities.size(); k++) {
            if(k >= 1) {
                Text moreActivities = new Text("...");
                calendarActivityBox.getChildren().add(moreActivities);
                moreActivities.setOnMouseClicked(mouseEvent -> {
                    //On ... click print all activities for given date
                    System.out.println(calendarActivities);
                });
                break;
            }
            //Estilo do texto
            Text text = new Text(calendarActivities.get(k).getClientName() + ", " + calendarActivities.get(k).getDate().toLocalTime()); text.setStyle("-fx-font-size: 12px; -fx-font-family: 'Segoe UI Semibold'; -fx-fill: black;");

            calendarActivityBox.getChildren().add(text);
            text.setOnMouseClicked(mouseEvent -> {
                //On Text clicked
                System.out.println(text.getText());
            });
        }
        // Ajustando posição do box de atividades
        calendarActivityBox.setTranslateY((rectangleHeight / 2) * 0.005); // Ajuste vertical
        calendarActivityBox.setTranslateX(10); // Ajuste horizontal (mover para a direita)
        
        //quadradinhos agendamento
        calendarActivityBox.setTranslateY((rectangleHeight / 2) * 0.005);
        calendarActivityBox.setMaxWidth(rectangleWidth * 0.02);
        calendarActivityBox.setMaxHeight(rectangleHeight * 0.01);
        calendarActivityBox.setStyle("-fx-background-color:#EBEBFB");
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
        List<CalendarioActivity> calendarActivities = new ArrayList<>();
        int year = dateFocus.getYear();
        int month = dateFocus.getMonth().getValue();

        Random random = new Random();
        for (int i = 0; i < 50; i++) {
            ZonedDateTime time = ZonedDateTime.of(year, month, random.nextInt(27)+1, 16,0,0,0,dateFocus.getZone());
            calendarActivities.add(new CalendarioActivity(time, "Hans", 111111));
        }

        return createCalendarMap(calendarActivities);
    }
}


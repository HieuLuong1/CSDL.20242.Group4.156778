package hust.soict.hedspi.market.csdl_20242_oanhnt;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class Employee_ScheduleController {

    @FXML
    private TableView<WorkSchedule> scheduleTable;

    @FXML
    private TableColumn<WorkSchedule, String> colDate;

    @FXML
    private TableColumn<WorkSchedule, String> colShift;

    @FXML
    private TableColumn<WorkSchedule, String> colStartTime;

    @FXML
    private TableColumn<WorkSchedule, String> colEndTime;

    @FXML
    private TableColumn<WorkSchedule, String> colStatus;

    @FXML
    private TableColumn<WorkSchedule, String> colNote;

    @FXML
    public void initialize() {
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colShift.setCellValueFactory(new PropertyValueFactory<>("shift"));
        colStartTime.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        colEndTime.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colNote.setCellValueFactory(new PropertyValueFactory<>("note"));

        ObservableList<WorkSchedule> schedules = FXCollections.observableArrayList(
                new WorkSchedule("2025-05-20", "Sáng", "08:00", "12:00", "Có mặt", ""),
                new WorkSchedule("2025-05-21", "Chiều", "13:00", "17:00", "Đi muộn", "Tắc đường"),
                new WorkSchedule("2025-05-22", "Sáng", "08:00", "12:00", "Vắng", "Bị ốm"),
                new WorkSchedule("2025-05-23", "Tối", "18:00", "22:00", "Có mặt", "")
        );

        scheduleTable.setItems(schedules);
    }
}
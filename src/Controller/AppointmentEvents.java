package Controller;

import Model.Agendamento;
import java.time.LocalDate;

public class AppointmentEvents {
    public static class AppointmentDeleted {
        private final int appointmentId;
        
        public AppointmentDeleted(int appointmentId) {
            this.appointmentId = appointmentId;
        }
        
        public int getAppointmentId() {
            return appointmentId;
        }
    }

    public static class AppointmentAdded {
        private final Agendamento appointment;
        
        public AppointmentAdded(Agendamento appointment) {
            this.appointment = appointment;
        }
        
        public Agendamento getAppointment() {
            return appointment;
        }
    }

    public static class CalendarRefreshNeeded {
        private final LocalDate forDate;
        
        public CalendarRefreshNeeded() {
            this.forDate = null;
        }
        
        public CalendarRefreshNeeded(LocalDate forDate) {
            this.forDate = forDate;
        }
        
        public LocalDate getForDate() {
            return forDate;
        }
    }
}
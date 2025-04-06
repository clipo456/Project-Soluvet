package Model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Agendamento {
    private int idAgenda;
    private LocalDate dataAgendamento;
    private LocalTime hora;
    private int idPlano;
    private int idAnimal;
    private int idTutor;
    private boolean isDeleted;

    public Agendamento() {
    }

    public Agendamento(int idAgenda, LocalDate dataAgendamento, LocalTime hora, int idPlano, int idAnimal, int idTutor, boolean isDeleted) {
        this.idAgenda = idAgenda;
        this.dataAgendamento = dataAgendamento;
        this.hora = hora;
        this.idPlano = idPlano;
        this.idAnimal = idAnimal;
        this.idTutor = idTutor;
        this.isDeleted = isDeleted;
    }

    // Getters and Setters
    public int getIdAgenda() {
        return idAgenda;
    }

    public void setIdAgenda(int idAgenda) {
        this.idAgenda = idAgenda;
    }

    public LocalDate getDataAgendamento() {
        return dataAgendamento;
    }

    public void setDataAgendamento(LocalDate dataAgendamento) {
        this.dataAgendamento = dataAgendamento;
    }

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public int getIdPlano() {
        return idPlano;
    }

    public void setIdPlano(int idPlano) {
        this.idPlano = idPlano;
    }

    public int getIdAnimal() {
        return idAnimal;
    }

    public void setIdAnimal(int idAnimal) {
        this.idAnimal = idAnimal;
    }

    public int getIdTutor() {
        return idTutor;
    }

    public void setIdTutor(int idTutor) {
        this.idTutor = idTutor;
    }

    public boolean isIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}
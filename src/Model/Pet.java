package Model;

import java.time.LocalDate;

public class Pet {
    private int id;
    private String nome;
    private LocalDate dataNasc;
    private int id_tutor;
    private String raca;
    private String especie;
    private char sexo;
    private String cor;
    private String obs;
    private String id_plano;
    private int idade; 

    public Pet(int id, String nome, LocalDate dataNasc, int id_tutor, String raca, 
              String especie, char sexo, String cor, String obs, String id_plano, int idade) {
        this.id = id;
        this.nome = nome;
        this.dataNasc = dataNasc;
        this.id_tutor = id_tutor;
        this.raca = raca;
        this.especie = especie;
        this.sexo = sexo;
        this.cor = cor;
        this.obs = obs;
        this.id_plano = id_plano;
        this.idade = idade;
    }

    // Getters and setters
    public int getId() { return id; }
    public String getNome() { return nome; }
    public LocalDate getDataNasc() { return dataNasc; }
    public int getId_tutor() { return id_tutor; }
    public String getRaca() { return raca; }
    public String getEspecie() { return especie; }
    public char getSexo() { return sexo; }
    public String getCor() { return cor; }
    public String getObs() { return obs; }
    public String getId_plano() { return id_plano; }
    public int getIdade() { return idade; }

    public void setId(int id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setDataNasc(LocalDate dataNasc) { this.dataNasc = dataNasc; }
    public void setId_tutor(int id_tutor) { this.id_tutor = id_tutor; }
    public void setRaca(String raca) { this.raca = raca; }
    public void setEspecie(String especie) { this.especie = especie; }
    public void setSexo(char sexo) { this.sexo = sexo; }
    public void setCor(String cor) { this.cor = cor; }
    public void setObs(String obs) { this.obs = obs; }
    public void setId_plano(String id_plano) { this.id_plano = id_plano; }
    public void setIdade(int idade) { this.idade = idade; }
}
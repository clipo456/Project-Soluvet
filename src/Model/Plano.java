package Model;

public class Plano {
    private int id_plano;
    private String nome;
    private double valor;
    private boolean corte_unhas;
    private boolean limpeza_dentes;
    private int banho_mes;
    private int tosa_mes;
    private int banhos_realizados;
    private int tosas_realizadas;
    private boolean isDeleted;

    public Plano(int id_plano, String nome, double valor, boolean corte_unhas, 
                boolean limpeza_dentes, int banho_mes, int tosa_mes, 
                int banhos_realizados, int tosas_realizadas, boolean isDeleted) {
        this.id_plano = id_plano;
        this.nome = nome;
        this.valor = valor;
        this.corte_unhas = corte_unhas;
        this.limpeza_dentes = limpeza_dentes;
        this.banho_mes = banho_mes;
        this.tosa_mes = tosa_mes;
        this.banhos_realizados = banhos_realizados;
        this.tosas_realizadas = tosas_realizadas;
        this.isDeleted = isDeleted;
    }

    // Getters and Setters
    public int getId_plano() {
        return id_plano;
    }

    public void setId_plano(int id_plano) {
        this.id_plano = id_plano;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public boolean isCorte_unhas() {
        return corte_unhas;
    }

    public void setCorte_unhas(boolean corte_unhas) {
        this.corte_unhas = corte_unhas;
    }

    public boolean isLimpeza_dentes() {
        return limpeza_dentes;
    }

    public void setLimpeza_dentes(boolean limpeza_dentes) {
        this.limpeza_dentes = limpeza_dentes;
    }

    public int getBanho_mes() {
        return banho_mes;
    }

    public void setBanho_mes(int banho_mes) {
        this.banho_mes = banho_mes;
    }

    public int getTosa_mes() {
        return tosa_mes;
    }

    public void setTosa_mes(int tosa_mes) {
        this.tosa_mes = tosa_mes;
    }

    public int getBanhos_realizados() {
        return banhos_realizados;
    }

    public void setBanhos_realizados(int banhos_realizados) {
        this.banhos_realizados = banhos_realizados;
    }

    public int getTosas_realizadas() {
        return tosas_realizadas;
    }

    public void setTosas_realizadas(int tosas_realizadas) {
        this.tosas_realizadas = tosas_realizadas;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    @Override
    public String toString() {
        return nome;
    }
}
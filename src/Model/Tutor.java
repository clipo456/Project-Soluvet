package Model;

public class Tutor {
    private int id;
    private String nome;
    private String cpf;
    private String dataNasc;
    private String telefone;
    private String rua;
    private String cidade;
    private String bairro;
    private String cep;
    private String complemento;
    private String numero;
    
    public Tutor(int id, String nome, String cpf, String telefone, String dataNasc, 
                String rua, String cidade, String bairro, String cep, 
                String complemento, String numero) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.telefone = telefone;
        this.dataNasc = dataNasc;
        this.rua = rua;
        this.cidade = cidade;
        this.bairro = bairro;
        this.cep = cep;
        this.complemento = complemento;
        this.numero = numero;
    }
    
    // Getters
    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getCpf() { return cpf; }
    public String getTelefone() { return telefone; }
    public String getDataNasc() { return dataNasc; }
    public String getRua() { return rua; }
    public String getCidade() { return cidade; }
    public String getBairro() { return bairro; }
    public String getCep() { return cep; }
    public String getComplemento() { return complemento; }
    public String getNumero() { return numero; }
    
    // Setters
    public void setId(int id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public void setDataNasc(String dataNasc) { this.dataNasc = dataNasc; }
    public void setRua(String rua) { this.rua = rua; }
    public void setCidade(String cidade) { this.cidade = cidade; }
    public void setBairro(String bairro) { this.bairro = bairro; }
    public void setCep(String cep) { this.cep = cep; }
    public void setComplemento(String complemento) { this.complemento = complemento; }
    public void setNumero(String numero) { this.numero = numero; }
}
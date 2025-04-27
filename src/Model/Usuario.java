
package Model;

public class Usuario {
    private int id;
    private String nome;
    private String usuario;
    private String cpf;
    private String senha;
    private boolean admin;

    public Usuario(int id, String nome, String usuario, String cpf, String senha, boolean admin) {
        this.id = id;
        this.nome = nome;
        this.usuario = usuario;
        this.cpf = cpf;
        this.senha = senha;
        this.admin = admin;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCPF() {
        return cpf;
    }

    public void setCPF(String email) {
        this.cpf = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
    
    
}


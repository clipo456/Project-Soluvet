package Model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {
    private DBConnection dbConnection = new DBConnection();
    private Connection connection = dbConnection.getConnection();
    
    // Create - Adicionar novo usuário
    public boolean adicionarUsuario(Usuario usuario) {
        String sql = "INSERT INTO cad_usuarios (nome, usuario, cpf, senha, permissao_admin) VALUES (?, ?, ?, ?, ?)";
        
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getUsuario());
            stmt.setString(3, usuario.getCPF());
            stmt.setString(4, usuario.getSenha());
            stmt.setBoolean(5, usuario.isAdmin());
            
            stmt.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Read - Listar todos os usuários
    public List<Usuario> listarUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM cad_usuarios";
        
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Usuario usuario = new Usuario(
                    rs.getInt("id_usuario"),
                    rs.getString("nome"),
                    rs.getString("usuario"),
                    rs.getString("cpf"),
                    rs.getString("senha"),
                    rs.getBoolean("permissao_admin")
                );
                usuarios.add(usuario);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return usuarios;
    }
    
    // Update - Atualizar usuário
    public boolean atualizarUsuario(Usuario usuario) {
        String sql = "UPDATE cad_usuarios SET nome = ?, usuario = ?, cpf = ?, senha = ?, permissao_admin = ? WHERE id_usuario = ?";
        
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getUsuario());
            stmt.setString(3, usuario.getCPF());
            stmt.setString(4, usuario.getSenha());
            stmt.setBoolean(5, usuario.isAdmin());
            stmt.setInt(6, usuario.getId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Delete - Remover usuário
    public boolean removerUsuario(int id) {
        String sql = "DELETE FROM cad_usuarios WHERE id_usuario = ?";
        
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, id);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Buscar usuário por ID
    public Usuario buscarUsuarioPorId(int id) {
        String sql = "SELECT * FROM cad_usuarios WHERE id_usuario = ?";
        
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Usuario(
                    rs.getInt("id_usuario"),
                    rs.getString("nome"),
                    rs.getString("usuario"),
                    rs.getString("cpf"),
                    rs.getString("senha"),
                    rs.getBoolean("permissao_admin")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
}
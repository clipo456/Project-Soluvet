package Controller;

import Model.Usuario;
import Model.UsuarioDAO;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class CadastroUsuarioController extends Navigation implements Initializable {
    @FXML private TableView<Usuario> tableView;
    @FXML private TableColumn<Usuario, Integer> idTbUsuario;
    @FXML private TableColumn<Usuario, String> userTbUsuario;
    @FXML private TableColumn<Usuario, String> nomeTbUsuario;
    @FXML private TableColumn<Usuario, String> cpfTbUsuario;
    @FXML private TableColumn<Usuario, String> senhaTbUsuario;
    @FXML private TableColumn<Usuario, String> tipoTbUsuario;
    
    @FXML private TextField userUsuario;
    @FXML private TextField nomeUsuario;
    @FXML private TextField cpfUsuario;
    @FXML private TextField senhaUsuario;
    @FXML private MenuButton tipoUsuario;
    @FXML private TextField pesquisarPlanos;
    
    @FXML private Button btnCadastrar;
    @FXML private Button btnAlterar;
    @FXML private Button btnExcluir;
    @FXML private Button btnLimpar;
    @FXML private Button btnPesquisar;
    
    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    private ObservableList<Usuario> usuarios = FXCollections.observableArrayList();
    private Usuario usuarioSelecionado = null;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Configurar colunas da tabela
        idTbUsuario.setCellValueFactory(new PropertyValueFactory<>("id"));
        userTbUsuario.setCellValueFactory(new PropertyValueFactory<>("usuario"));
        nomeTbUsuario.setCellValueFactory(new PropertyValueFactory<>("nome"));
        cpfTbUsuario.setCellValueFactory(new PropertyValueFactory<>("CPF"));
        senhaTbUsuario.setCellValueFactory(new PropertyValueFactory<>("senha"));
        tipoTbUsuario.setCellValueFactory(cellData -> {
            boolean isAdmin = cellData.getValue().isAdmin();
            return new javafx.beans.property.SimpleStringProperty(isAdmin ? "Admin" : "Usuário");
        });
        
        // Configurar itens do MenuButton
        MenuItem adminItem = new MenuItem("Admin");
        MenuItem usuarioItem = new MenuItem("Usuário");
        
        adminItem.setOnAction(e -> tipoUsuario.setText("Admin"));
        usuarioItem.setOnAction(e -> tipoUsuario.setText("Usuário"));
        
        tipoUsuario.getItems().addAll(adminItem, usuarioItem);
        
        // Carregar dados
        carregarUsuarios();
        
        // Configurar seleção na tabela
        tableView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> selecionarUsuario(newValue));
    }
    
    private void carregarUsuarios() {
        usuarios.clear();
        usuarios.addAll(usuarioDAO.listarUsuarios());
        tableView.setItems(usuarios);
    }
    
    private void selecionarUsuario(Usuario usuario) {
        usuarioSelecionado = usuario;
        
        if (usuario != null) {
            userUsuario.setText(usuario.getUsuario());
            nomeUsuario.setText(usuario.getNome());
            cpfUsuario.setText(usuario.getCPF());
            senhaUsuario.setText(usuario.getSenha());
            tipoUsuario.setText(usuario.isAdmin() ? "Admin" : "Usuário");
            
            btnCadastrar.setDisable(true);
            btnAlterar.setDisable(false);
            btnExcluir.setDisable(false);
        } else {
            limparCampos();
        }
    }
    
    private void limparCampos() {
        userUsuario.clear();
        nomeUsuario.clear();
        cpfUsuario.clear();
        senhaUsuario.clear();
        tipoUsuario.setText("Tipo");
        
        usuarioSelecionado = null;
        tableView.getSelectionModel().clearSelection();
        
        btnCadastrar.setDisable(false);
        btnAlterar.setDisable(true);
        btnExcluir.setDisable(true);
    }
    
    @FXML
    private void pesquisarUsuario() {
        String termo = pesquisarPlanos.getText().toLowerCase();
        
        if (termo.isEmpty()) {
            tableView.setItems(usuarios);
            return;
        }
        
        ObservableList<Usuario> resultados = FXCollections.observableArrayList();
        
        for (Usuario usuario : usuarios) {
            if (String.valueOf(usuario.getId()).contains(termo) ||
                usuario.getNome().toLowerCase().contains(termo) ||
                usuario.getUsuario().toLowerCase().contains(termo) ||
                usuario.getCPF().toLowerCase().contains(termo) ||
                usuario.getSenha().toLowerCase().contains(termo) ||
                (usuario.isAdmin() ? "admin" : "usuário").contains(termo)) {
                resultados.add(usuario);
            }
        }
        
        tableView.setItems(resultados);
    }
    
    @FXML
    private void handleCadastrar() {
        if (validarCampos()) {
            Usuario novoUsuario = new Usuario(
                0,
                nomeUsuario.getText(),
                userUsuario.getText(),
                cpfUsuario.getText(),
                senhaUsuario.getText(),
                tipoUsuario.getText().equals("Admin")
            );
            
            if (usuarioDAO.adicionarUsuario(novoUsuario)) {
                mostrarAlerta("Sucesso", "Usuário cadastrado com sucesso!", Alert.AlertType.INFORMATION);
                carregarUsuarios();
                limparCampos();
            } else {
                mostrarErro("Erro ao cadastrar usuário.");
            }
        }
    }
    
    @FXML
    private void handleAlterar() {
        if (usuarioSelecionado != null && validarCampos()) {
            usuarioSelecionado.setNome(nomeUsuario.getText());
            usuarioSelecionado.setUsuario(userUsuario.getText());
            usuarioSelecionado.setCPF(cpfUsuario.getText());
            usuarioSelecionado.setSenha(senhaUsuario.getText());
            usuarioSelecionado.setAdmin(tipoUsuario.getText().equals("Admin"));
            
            if (usuarioDAO.atualizarUsuario(usuarioSelecionado)) {
                mostrarAlerta("Sucesso", "Usuário atualizado com sucesso!", Alert.AlertType.INFORMATION);
                carregarUsuarios();
                limparCampos();
            } else {
                mostrarErro("Erro ao atualizar usuário.");
            }
        }
    }
    
    @FXML
    private void handleExcluir() {
        if (usuarioSelecionado != null) {
            Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacao.setTitle("Confirmação");
            confirmacao.setHeaderText("Excluir Usuário");
            confirmacao.setContentText("Tem certeza que deseja excluir este usuário?");
            
            if (confirmacao.showAndWait().get() == ButtonType.OK) {
                if (usuarioDAO.removerUsuario(usuarioSelecionado.getId())) {
                    mostrarAlerta("Sucesso", "Usuário excluído com sucesso!", Alert.AlertType.INFORMATION);
                    carregarUsuarios();
                    limparCampos();
                } else {
                    mostrarErro("Erro ao excluir usuário.");
                }
            }
        }
    }
    
    @FXML
    private void handleLimpar() {
        limparCampos();
        pesquisarPlanos.clear();
        tableView.setItems(usuarios);
    }
    
    private boolean validarCampos() {
        if (userUsuario.getText().isEmpty() || nomeUsuario.getText().isEmpty() || 
            cpfUsuario.getText().isEmpty() || senhaUsuario.getText().isEmpty() || 
            tipoUsuario.getText().equals("Tipo")) {
            
            mostrarErro("Por favor, preencha todos os campos.");
            return false;
        }
        
        if (!cpfUsuario.getText().matches("\\d{11}")) {
            mostrarErro("CPF inválido. Deve conter 11 dígitos.");
            return false;
        }
        
        return true;
    }
    
    private void mostrarErro(String mensagem) {
        mostrarAlerta("Erro", mensagem, Alert.AlertType.ERROR);
    }
    
    private void mostrarAlerta(String titulo, String mensagem, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
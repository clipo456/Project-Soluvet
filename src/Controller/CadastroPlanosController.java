package Controller;

import Model.Plano;
import Model.PlanoDAO;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class CadastroPlanosController extends Navigation implements Initializable {

    @FXML private Button btnCadastrar;
    @FXML private Button btnExcluir;
    @FXML private Button btnLimpar;
    @FXML private Button btnAlterar;
    @FXML private Button btnPesquisar;
    @FXML private TextField nomePlano;
    @FXML private TextField tosasPlano;
    @FXML private TextField valorPlano;
    @FXML private TextField banhosPlano;
    @FXML private TextField pesquisarPlanos;
    @FXML private TableView<Plano> tableView;
    @FXML private TableColumn<Plano, Integer> idTbPlanos;
    @FXML private TableColumn<Plano, String> nomeTbPlanos;
    @FXML private TableColumn<Plano, Double> valorTbPlanos;
    @FXML private TableColumn<Plano, String> cortesTbPlanos;
    @FXML private TableColumn<Plano, Integer> banhosTbPlanos;
    @FXML private TableColumn<Plano, Integer> tosasTbPlanos;
    @FXML private TableColumn<Plano, String> limpezasTbPlanos;
    @FXML private TableColumn<Plano, Integer> countAssinaturaTbPlanos;
    @FXML private MenuButton menuCortesPlano;
    @FXML private MenuButton menuLimpezasPlano;

    private ObservableList<Plano> planosList = FXCollections.observableArrayList();
    private PlanoDAO planoDAO = new PlanoDAO();
    private Plano planoSelecionado = null;
    private boolean corteUnhasSelecionado = false;
    private boolean limpezaDentesSelecionado = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarMenuButtons();
        configurarTableView();
        carregarPlanos();
        configurarSelecaoTabela();
    }

    private void configurarMenuButtons() {
        // Configuração do MenuButton para cortes de unha
        MenuItem cortesSim = new MenuItem("Sim");
        MenuItem cortesNao = new MenuItem("Não");
        
        cortesSim.setOnAction(e -> {
            menuCortesPlano.setText("Sim");
            corteUnhasSelecionado = true;
        });
        
        cortesNao.setOnAction(e -> {
            menuCortesPlano.setText("Não");
            corteUnhasSelecionado = false;
        });
        
        menuCortesPlano.getItems().setAll(cortesSim, cortesNao);
        menuCortesPlano.setText("Não"); // Valor padrão

        // Configuração do MenuButton para limpeza de dentes
        MenuItem limpezaSim = new MenuItem("Sim");
        MenuItem limpezaNao = new MenuItem("Não");
        
        limpezaSim.setOnAction(e -> {
            menuLimpezasPlano.setText("Sim");
            limpezaDentesSelecionado = true;
        });
        
        limpezaNao.setOnAction(e -> {
            menuLimpezasPlano.setText("Não");
            limpezaDentesSelecionado = false;
        });
        
        menuLimpezasPlano.getItems().setAll(limpezaSim, limpezaNao);
        menuLimpezasPlano.setText("Não"); // Valor padrão
    }

    private void configurarTableView() {
        idTbPlanos.setCellValueFactory(new PropertyValueFactory<>("id_plano"));
        nomeTbPlanos.setCellValueFactory(new PropertyValueFactory<>("nome"));
        valorTbPlanos.setCellValueFactory(new PropertyValueFactory<>("valor"));
        
        // Configuração para exibir "Sim" ou "Não" para campos booleanos
        cortesTbPlanos.setCellValueFactory(cellData -> 
            cellData.getValue().isCorte_unhas() ? 
            javafx.beans.binding.Bindings.createStringBinding(() -> "Sim") : 
            javafx.beans.binding.Bindings.createStringBinding(() -> "Não"));
            
        banhosTbPlanos.setCellValueFactory(new PropertyValueFactory<>("banho_mes"));
        tosasTbPlanos.setCellValueFactory(new PropertyValueFactory<>("tosa_mes"));
        
        limpezasTbPlanos.setCellValueFactory(cellData -> 
            cellData.getValue().isLimpeza_dentes() ? 
            javafx.beans.binding.Bindings.createStringBinding(() -> "Sim") : 
            javafx.beans.binding.Bindings.createStringBinding(() -> "Não"));
            
        countAssinaturaTbPlanos.setCellValueFactory(new PropertyValueFactory<>("banhos_realizados"));
    }

    private void carregarPlanos() {
        planosList.clear();
        planosList.addAll(planoDAO.getPlanos());
        tableView.setItems(planosList);
    }

    private void configurarSelecaoTabela() {
        tableView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                planoSelecionado = newValue;
                if (newValue != null) {
                    preencherCampos(newValue);
                }
            });
    }

    private void preencherCampos(Plano plano) {
        nomePlano.setText(plano.getNome());
        // Formata o valor substituindo vírgula por ponto se necessário
        String valorFormatado = String.format("%.2f", plano.getValor()).replace(",", ".");
        valorPlano.setText(valorFormatado);
        banhosPlano.setText(String.valueOf(plano.getBanho_mes()));
        tosasPlano.setText(String.valueOf(plano.getTosa_mes()));
        
        if (plano.isCorte_unhas()) {
            menuCortesPlano.setText("Sim");
            corteUnhasSelecionado = true;
        } else {
            menuCortesPlano.setText("Não");
            corteUnhasSelecionado = false;
        }
        
        if (plano.isLimpeza_dentes()) {
            menuLimpezasPlano.setText("Sim");
            limpezaDentesSelecionado = true;
        } else {
            menuLimpezasPlano.setText("Não");
            limpezaDentesSelecionado = false;
        }
    }

    @FXML
    private void cadastrarPlano() {
        if (validarCampos()) {
            Plano novoPlano = criarPlanoAPartirCampos();
            if (novoPlano != null) {
                if (planoDAO.inserirPlano(novoPlano)) {
                    mostrarAlerta("Sucesso", "Plano cadastrado com sucesso!", Alert.AlertType.INFORMATION);
                    limparCampos();
                    carregarPlanos();
                } else {
                    mostrarAlerta("Erro", 
                        "Já existe um plano ATIVO com este nome. " +
                        "Delete o plano existente antes de cadastrar um novo com mesmo nome.",
                        Alert.AlertType.ERROR);
                }
            }
        }
    }

    @FXML
    private void alterarPlano() {
        if (planoSelecionado == null) {
            mostrarAlerta("Aviso", "Selecione um plano para alterar.", Alert.AlertType.WARNING);
            return;
        }

        if (validarCampos()) {
            Plano planoAtualizado = criarPlanoAPartirCampos();
            if (planoAtualizado != null) { // Só continua se a conversão do valor foi bem sucedida
                planoAtualizado.setId_plano(planoSelecionado.getId_plano());
                
                if (planoDAO.atualizarPlano(planoAtualizado)) {
                    mostrarAlerta("Sucesso", "Plano atualizado com sucesso!", Alert.AlertType.INFORMATION);
                    limparCampos();
                } else {
                    mostrarAlerta("Erro", "Falha ao atualizar plano.", Alert.AlertType.ERROR);
                }
            }
        }
    }

    @FXML
    private void excluirPlano() {
        if (planoSelecionado == null) {
            mostrarAlerta("Aviso", "Selecione um plano para excluir.", Alert.AlertType.WARNING);
            return;
        }

        if (planoDAO.excluirPlano(planoSelecionado.getId_plano())) {
            mostrarAlerta("Sucesso", "Plano excluído com sucesso!", Alert.AlertType.INFORMATION);
            limparCampos();
            carregarPlanos();
            planoSelecionado = null;
        } else {
            mostrarAlerta("Erro", "Falha ao excluir plano. O plano pode estar em uso por algum animal.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void limparCampos() {
        nomePlano.clear();
        valorPlano.clear();
        banhosPlano.clear();
        tosasPlano.clear();
        pesquisarPlanos.clear(); // Limpa o campo de pesquisa
        menuCortesPlano.setText("Não");
        menuLimpezasPlano.setText("Não");
        corteUnhasSelecionado = false;
        limpezaDentesSelecionado = false;
        planoSelecionado = null;
        tableView.getSelectionModel().clearSelection();
        carregarPlanos(); // Recarrega a tabela completa
    }

    @FXML
    private void pesquisarPlano() {
        String termo = pesquisarPlanos.getText().trim();
        if (!termo.isEmpty()) {
            ObservableList<Plano> resultado = FXCollections.observableArrayList();
            
            // Busca em todas as colunas
            for (Plano plano : planoDAO.getPlanos()) {
                if (contemTermo(plano, termo)) {
                    resultado.add(plano);
                }
            }
            
            if (resultado.isEmpty()) {
                mostrarAlerta("Informação", "Nenhum plano encontrado com o termo: " + termo, Alert.AlertType.INFORMATION);
            } else {
                tableView.setItems(resultado);
            }
        } else {
            carregarPlanos();
        }
    }
    
    private boolean contemTermo(Plano plano, String termo) {
        // Converte tudo para String e para minúsculas para busca case insensitive
        String termoLower = termo.toLowerCase();
        
        return String.valueOf(plano.getId_plano()).toLowerCase().contains(termoLower) ||
               plano.getNome().toLowerCase().contains(termoLower) ||
               String.format("%.2f", plano.getValor()).toLowerCase().contains(termoLower) ||
               (plano.isCorte_unhas() ? "sim" : "não").contains(termoLower) ||
               String.valueOf(plano.getBanho_mes()).toLowerCase().contains(termoLower) ||
               String.valueOf(plano.getTosa_mes()).toLowerCase().contains(termoLower) ||
               (plano.isLimpeza_dentes() ? "sim" : "não").contains(termoLower) ||
               String.valueOf(plano.getBanhos_realizados()).toLowerCase().contains(termoLower);
    }

    private Plano criarPlanoAPartirCampos() {
        // Garante que o valor decimal use ponto como separador
        String valorStr = valorPlano.getText().replace(",", ".");
        
        try {
            return new Plano(
                0, // ID será gerado no banco
                nomePlano.getText(),
                Double.parseDouble(valorStr), // Usa valor com ponto decimal
                corteUnhasSelecionado,
                limpezaDentesSelecionado,
                Integer.parseInt(banhosPlano.getText()),
                Integer.parseInt(tosasPlano.getText()),
                0, // banhos_realizados inicia com 0
                0, // tosas_realizadas inicia com 0
                false // isDeleted inicia como false
            );
        } catch (NumberFormatException e) {
            mostrarAlerta("Erro", "Valor inválido. Use números com ponto ou vírgula como separador decimal.", Alert.AlertType.ERROR);
            return null;
        }
    }
    

    private boolean validarCampos() {
        if (nomePlano.getText().isEmpty() || valorPlano.getText().isEmpty() ||
            banhosPlano.getText().isEmpty() || tosasPlano.getText().isEmpty()) {
            mostrarAlerta("Aviso", "Preencha todos os campos!", Alert.AlertType.WARNING);
            return false;
        }

        try {
            // Valida valor - aceita tanto ponto quanto vírgula
            String valorStr = valorPlano.getText().replace(",", ".");
            double valor = Double.parseDouble(valorStr);
            if (valor <= 0) {
                mostrarAlerta("Aviso", "O valor deve ser maior que zero!", Alert.AlertType.WARNING);
                return false;
            }

            // Valida quantidades
            int banhos = Integer.parseInt(banhosPlano.getText());
            int tosas = Integer.parseInt(tosasPlano.getText());
            if (banhos < 0 || tosas < 0) {
                mostrarAlerta("Aviso", "Quantidades não podem ser negativas!", Alert.AlertType.WARNING);
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Erro", "Valor inválido. Use números com ponto ou vírgula como separador decimal.", Alert.AlertType.ERROR);
            return false;
        }

        return true;
    }

    private void mostrarAlerta(String titulo, String mensagem, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
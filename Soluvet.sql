/* TABELA DE CADASTRO DE TUTORES DOS PETS */
CREATE TABLE cad_tutor (
    id_tutor INT AUTO_INCREMENT PRIMARY KEY UNIQUE,
    nome VARCHAR(255) NOT NULL,
    cpf VARCHAR(14) UNIQUE NOT NULL,
    data_nascimento DATE NOT NULL,
    telefone VARCHAR(20) NOT NULL,
    rua VARCHAR(100) NOT NULL,
    cidade VARCHAR(50) NOT NULL,
    bairro VARCHAR(255) NOT NULL,
    cep VARCHAR(8) NOT NULL,
    complemento VARCHAR(50),
    numero VARCHAR(4) NOT NULL
);

/* TABELA DE CADASTRO DOS PETS */
CREATE TABLE cad_animal (
    id_animal INT AUTO_INCREMENT PRIMARY KEY UNIQUE,
    nome VARCHAR(255) NOT NULL,
    data_nascimento DATE NOT NULL,
    id_tutor INT NOT NULL,
    raca VARCHAR(100),
    especie VARCHAR(100) NOT NULL,
    sexo ENUM('M', 'F', 'Indefinido') NOT NULL,
    cor VARCHAR(100),
    obs_geral VARCHAR(255),
    plano iNT NOT NULL,
    FOREIGN KEY (id_plano) REFERENCES planos(id_plano) ON DELETE CASCADE,
    FOREIGN KEY (id_tutor) REFERENCES cad_tutor(id_tutor) ON DELETE CASCADE
);

/* TABELA DE CADASTRO DE USUÁRIOS DO SISTEMA */
CREATE TABLE cad_usuarios (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY UNIQUE,
    nome VARCHAR(255) NOT NULL,
    usuario VARCHAR(100) UNIQUE NOT NULL,
    cpf VARCHAR(14) UNIQUE NOT NULL,
    senha VARCHAR(255) NOT NULL
);

/* TABELA DE CADASTRO DE PLANOS OFERECIDOS */
CREATE TABLE planos (
    id_plano INT AUTO_INCREMENT PRIMARY KEY UNIQUE,
    nome VARCHAR(255) UNIQUE NOT NULL,
    valor DECIMAL(10,2) NOT NULL,
    corte_unhas BOOLEAN NOT NULL,
    limpeza_dentes BOOLEAN NOT NULL,
    banho_mes INT NOT NULL,
    tosa_mes INT NOT NULL
);


CREATE TABLE cad_responsavel (
    num_registro INT AUTO_INCREMENT PRIMARY KEY UNIQUE,
    nome VARCHAR(255) NOT NULL,
    cpf VARCHAR(14) UNIQUE NOT NULL,
    data_nascimento DATE NOT NULL,
    telefone VARCHAR(20) NOT NULL,
    rua VARCHAR(100) NOT NULL,
    cidade VARCHAR(50) NOT NULL,
    bairro VARCHAR(255) NOT NULL,
    cep VARCHAR(50) NOT NULL,
    complemento VARCHAR(50),
    numero varchar(4) NOT NULL
);
CREATE TABLE cad_animal (
    num_registro INT AUTO_INCREMENT PRIMARY KEY UNIQUE,
    nome VARCHAR(255) NOT NULL,
    data_nascimento DATE NOT NULL,
    responsavel INT NOT NULL,
    raca VARCHAR(100),
    especie VARCHAR(100) NOT NULL,
    sexo ENUM('M', 'F', 'Indefinido') NOT NULL,
    cor VARCHAR(100),
    obs_geral VARCHAR(255),
    FOREIGN KEY (responsavel) REFERENCES cad_responsavel(num_registro) ON DELETE CASCADE
);

CREATE TABLE cad_usuarios (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY UNIQUE,
    nome VARCHAR(255) NOT NULL,
    usuario VARCHAR(100) UNIQUE NOT NULL,
    cpf VARCHAR(14) UNIQUE NOT NULL,
    senha VARCHAR(255) NOT NULL
);

CREATE TABLE servicos (
    cod_serv INT AUTO_INCREMENT PRIMARY KEY UNIQUE,
    nome VARCHAR(255) UNIQUE NOT NULL,
    valor DECIMAL(10,2) NOT NULL
);


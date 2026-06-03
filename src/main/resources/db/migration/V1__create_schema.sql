-- =============================================================
--  Dendê Eventos – Migration V1 – Criação do schema (MySQL)
-- =============================================================

CREATE TABLE IF NOT EXISTS usuario
(
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    nome            VARCHAR(255) NOT NULL,
    data_nascimento DATE         NOT NULL,
    sexo            CHAR(1)      NOT NULL,
    email           VARCHAR(255) NOT NULL UNIQUE,
    senha           VARCHAR(255) NOT NULL,
    tipo_usuario    VARCHAR(20)  NOT NULL,
    ativo           TINYINT(1)   NOT NULL DEFAULT 1,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS empresa
(
    id             BIGINT       NOT NULL AUTO_INCREMENT,
    organizador_id BIGINT       NOT NULL UNIQUE,
    cnpj           VARCHAR(18)  NOT NULL UNIQUE,
    razao_social   VARCHAR(255) NOT NULL,
    nome_fantasia  VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_empresa_organizador
        FOREIGN KEY (organizador_id) REFERENCES usuario (id)
            ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS evento
(
    id                  BIGINT         NOT NULL AUTO_INCREMENT,
    organizador_id      BIGINT         NOT NULL,
    evento_principal_id BIGINT         NULL,
    nome                VARCHAR(255)   NOT NULL,
    descricao           TEXT           NULL,
    pagina_web          VARCHAR(500)   NULL,
    tipo_evento         VARCHAR(30)    NOT NULL,
    modalidade          VARCHAR(10)    NOT NULL,
    local_evento        VARCHAR(500)   NOT NULL,
    data_inicio         DATETIME       NOT NULL,
    data_fim            DATETIME       NOT NULL,
    capacidade_maxima   INT            NOT NULL,
    preco_ingresso      DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    estorna_ingresso    TINYINT(1)     NOT NULL DEFAULT 0,
    taxa_estorno        DECIMAL(5, 2)  NOT NULL DEFAULT 0.00,
    ativo               TINYINT(1)     NOT NULL DEFAULT 0,
    data_cadastro       DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_evento_organizador
        FOREIGN KEY (organizador_id) REFERENCES usuario (id)
            ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_evento_principal
        FOREIGN KEY (evento_principal_id) REFERENCES evento (id)
            ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT chk_evento_datas
        CHECK (data_fim > data_inicio),
    CONSTRAINT chk_evento_duracao_minima
        CHECK (TIMESTAMPDIFF(MINUTE, data_inicio, data_fim) >= 30),
    CONSTRAINT chk_taxa_estorno
        CHECK (taxa_estorno >= 0 AND taxa_estorno <= 100)
);

CREATE TABLE IF NOT EXISTS ingresso
(
    id                BIGINT         NOT NULL AUTO_INCREMENT,
    usuario_id        BIGINT         NOT NULL,
    evento_id         BIGINT         NOT NULL,
    codigo            VARCHAR(20)    NOT NULL,
    valor_pago        DECIMAL(10, 2) NOT NULL,
    status            VARCHAR(15)    NOT NULL DEFAULT 'PENDENTE',
    valor_estornado   DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    ingresso_principal TINYINT(1)   NOT NULL DEFAULT 1,
    data_compra       DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_cancelamento DATETIME       NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_ingresso_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuario (id)
            ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_ingresso_evento
        FOREIGN KEY (evento_id) REFERENCES evento (id)
            ON DELETE RESTRICT ON UPDATE CASCADE
);

-- Índices
CREATE INDEX idx_usuario_ativo  ON usuario (ativo);
CREATE INDEX idx_usuario_tipo   ON usuario (tipo_usuario);
CREATE INDEX idx_evento_org     ON evento  (organizador_id);
CREATE INDEX idx_evento_ativo   ON evento  (ativo);
CREATE INDEX idx_evento_inicio  ON evento  (data_inicio);
CREATE INDEX idx_evento_nome    ON evento  (nome);
CREATE INDEX idx_ingresso_usr   ON ingresso(usuario_id);
CREATE INDEX idx_ingresso_evt   ON ingresso(evento_id);
CREATE INDEX idx_ingresso_sta   ON ingresso(status);

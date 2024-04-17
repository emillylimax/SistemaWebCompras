CREATE TABLE IF NOT EXISTS Clientes (
	id SERIAL,
	nome TEXT NOT NULL,
	email TEXT NOT NULL,
	senha TEXT NOT NULL,
	PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS Lojistas (
	id SERIAL,
	nome TEXT NOT NULL,
	email TEXT NOT NULL,
	senha TEXT NOT NULL,
	PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS Produtos (
	id SERIAL,
	nome TEXT NOT NULL,
	descricao TEXT,
	preco FLOAT NOT NULL,
	quantidade INTEGER NOT NULL,
	PRIMARY KEY (id)
);

INSERT INTO clientes (nome, email, senha)
VALUES ('João Pedro', 'jp2017@uol.com.br' , '12345jaum'),
('Amara Silva', 'amarasil@bol.com.br', 'amara82'),
('Maria Pereira', 'mariape@terra.com.br', '145aektm');

INSERT INTO lojistas (nome, email, senha)
VALUES ('Taniro Rodrigues', 'tanirocr@gmail.com', '123456abc'),
('Lorena Silva', 'lore_sil@yahoo.com.br','12uhuuu@');

INSERT INTO produtos (nome, descricao, preco, quantidade)
VALUES ('Mesa', 'Uma mesa de computador.', 500.00 , 10),
('Lapis', 'Lapis B2 grafite.', 2.00, 50),
('Computador', 'Computador I5 16Gb de RAM.', 1500.00,  2);

select * from produtos


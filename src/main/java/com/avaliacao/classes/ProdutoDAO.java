package com.avaliacao.classes;

import java.net.URISyntaxException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public class ProdutoDAO {

    public boolean salvar(Produto produto) {
        try (Connection con = Conexao.getConnection()) {
            String sql = "INSERT INTO produtos (nome, descricao, preco, quantidade) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, produto.getNome());
                ps.setString(2, produto.getDescricao());
                ps.setDouble(3, produto.getPreco());
                ps.setInt(4, produto.getQuantidade());
                ps.executeUpdate();
            }
        } catch (SQLException | URISyntaxException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public List<Produto> listarTodos() {
        List<Produto> produtos = new ArrayList<>();
        try (Connection con = Conexao.getConnection()) {
            String sql = "SELECT * FROM produtos";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Produto produto = new Produto(
                                rs.getInt("id"),
                                rs.getString("nome"),
                                rs.getString("descricao"),
                                rs.getDouble("preco"),
                                rs.getInt("quantidade")
                        );
                        produtos.add(produto);
                    }
                }
            }
        } catch (SQLException | URISyntaxException e) {
            e.printStackTrace();
        }
        return produtos;
    }

    public Produto buscarPorId(int id) {
        Produto produto = null;
        try (Connection con = Conexao.getConnection()) {
            String sql = "SELECT * FROM produtos WHERE id = ?";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        produto = new Produto(
                                rs.getInt("id"),
                                rs.getString("nome"),
                                rs.getString("descricao"),
                                rs.getDouble("preco"),
                                rs.getInt("quantidade")
                        );
                    }
                }
            }
        } catch (SQLException | URISyntaxException e) {
            e.printStackTrace();
        }
        return produto;
    }

    public Produto buscarPorNome(String nome) {
        Produto produto = null;
        try (Connection con = Conexao.getConnection()) {
            String sql = "SELECT * FROM produtos WHERE LOWER(nome) = LOWER(?)";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, nome);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        produto = new Produto(
                                rs.getInt("id"),
                                rs.getString("nome"),
                                rs.getString("descricao"),
                                rs.getDouble("preco"),
                                rs.getInt("quantidade")
                        );
                    }
                }
            }
        } catch (SQLException | URISyntaxException e) {
            e.printStackTrace();
        }
        return produto;
    }

    public boolean atualizar(Produto produto) {
        try (Connection con = Conexao.getConnection()) {
            String sql = "UPDATE produtos SET descricao = ?, preco = ?, quantidade = ? WHERE id = ?";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, produto.getDescricao());
                ps.setDouble(2, produto.getPreco());
                ps.setInt(3, produto.getQuantidade());
                ps.setInt(4, produto.getId());
                ps.executeUpdate();
            }
        } catch (SQLException | URISyntaxException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
  
    public int verificarQuantidadeDisponivel(int idProduto) {
        int quantidadeDisponivel = 0;
        try (Connection con = Conexao.getConnection()) {
            String sql = "SELECT quantidade FROM produtos WHERE id = ?";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, idProduto);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        quantidadeDisponivel = rs.getInt("quantidade");
                    }
                }
            }
        } catch (SQLException | URISyntaxException e) {
            e.printStackTrace();
            quantidadeDisponivel = -1; 
        }
        return quantidadeDisponivel;
    }

    public boolean atualizarQuantidade(int idProduto, int novaQuantidade) {
        try (Connection con = Conexao.getConnection()) {
            String sql = "UPDATE produtos SET quantidade = ? WHERE id = ?";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, novaQuantidade);
                ps.setInt(2, idProduto);
                ps.executeUpdate();
            }
        } catch (SQLException | URISyntaxException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
 
}

package com.avaliacao.classes;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class LojistaDAO {


    public boolean autenticar(String email, String senha) {
        try (Connection con = Conexao.getConnection()) {
            PreparedStatement stmt = con.prepareStatement("SELECT * FROM Lojistas WHERE email = ? AND senha = ?");
            stmt.setString(1, email);
            stmt.setString(2, senha);
            ResultSet rs = stmt.executeQuery();

            return rs.next();
        } catch (SQLException | URISyntaxException e) {
            System.out.println("Erro ao autenticar logista: " + e.getMessage());
            return false;
        }
    }

    public void salvar(Lojista lojista) {
        try (Connection con = Conexao.getConnection()) {
            PreparedStatement instrucao = con.prepareStatement("INSERT INTO lojistas (nome, email, senha) VALUES (?, ?, ?)");
            instrucao.setString(1, lojista.getNome());
            instrucao.setString(2, lojista.getEmail());
            instrucao.setString(3, lojista.getSenha());
            instrucao.executeUpdate();
        } catch (SQLException | URISyntaxException e) {
            System.out.println("Erro ao salvar lojista: " + e.getMessage());
        }
    }

    public void excluir(int id) {
        try (Connection con = Conexao.getConnection()) {
            PreparedStatement instrucao = con.prepareStatement("DELETE FROM lojistas WHERE id = ?");
            instrucao.setInt(1, id);
            instrucao.executeUpdate();
        } catch (SQLException | URISyntaxException e) {
            System.out.println("Erro ao excluir lojista: " + e.getMessage());
        }
    }

    public void atualizar(Lojista lojista) {
        try (Connection con = Conexao.getConnection()) {
            PreparedStatement instrucao = con.prepareStatement("UPDATE lojistas SET nome=?, email=?, senha=? WHERE id=?");
            instrucao.setString(1, lojista.getNome());
            instrucao.setString(2, lojista.getEmail());
            instrucao.setString(3, lojista.getSenha());
            instrucao.setInt(4, lojista.getId());
            instrucao.executeUpdate();
        } catch (SQLException | URISyntaxException e) {
            System.out.println("Erro ao atualizar lojista: " + e.getMessage());
        }
    }

    public Lojista buscarPorId(int id) {
        Lojista lojista = null;
        try (Connection con = Conexao.getConnection()) {
            PreparedStatement instrucao = con.prepareStatement("SELECT * FROM lojistas WHERE id = ?");
            instrucao.setInt(1, id);
            ResultSet resultado = instrucao.executeQuery();
            if (resultado.next()) {
                lojista = new Lojista(resultado.getInt("id"), resultado.getString("nome"), resultado.getString("email"), resultado.getString("senha"));
            }
        } catch (SQLException | URISyntaxException e) {
            System.out.println("Erro ao buscar lojista por ID: " + e.getMessage());
        }
        return lojista;
    }

    public List<Lojista> listarTodos() {
        List<Lojista> lojistas = new ArrayList<>();
        try (Connection con = Conexao.getConnection()) {
            PreparedStatement instrucao = con.prepareStatement("SELECT * FROM lojistas");
            ResultSet resultado = instrucao.executeQuery();
            while (resultado.next()) {
                Lojista lojista = new Lojista(resultado.getInt("id"), resultado.getString("nome"), resultado.getString("email"), resultado.getString("senha"));
                lojistas.add(lojista);
            }
        } catch (SQLException | URISyntaxException e) {
            System.out.println("Erro ao listar lojistas: " + e.getMessage());
        }
        return lojistas;
    }
}

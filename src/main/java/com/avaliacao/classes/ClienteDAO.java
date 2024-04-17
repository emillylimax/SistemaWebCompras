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
public class ClienteDAO {

    public boolean autenticar(String email, String senha) {
        try (Connection con = Conexao.getConnection()) {
            PreparedStatement stmt = con.prepareStatement("SELECT * FROM Clientes WHERE email = ? AND senha = ?");
            stmt.setString(1, email);
            stmt.setString(2, senha);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException | URISyntaxException e) {
            System.out.println("Erro ao autenticar cliente: " + e.getMessage());
            return false;
        }
    }

    public boolean salvar(Cliente cliente) {
        try (Connection con = Conexao.getConnection()) {
            if (clienteExistente(cliente.getEmail())) {
                System.out.println("Erro ao salvar cliente: Cliente já existe");
                return false;
            }
            
            PreparedStatement instrucao = con.prepareStatement("INSERT INTO Clientes (nome, email, senha) VALUES (?, ?, ?)");
            instrucao.setString(1, cliente.getNome());
            instrucao.setString(2, cliente.getEmail());
            instrucao.setString(3, cliente.getSenha());
            instrucao.executeUpdate();
            return true;
        } catch (SQLException | URISyntaxException e) {
            System.out.println("Erro ao salvar cliente: " + e.getMessage());
            return false;
        }
    }
    
    private boolean clienteExistente(String email) throws SQLException, URISyntaxException {
        try (Connection con = Conexao.getConnection()) {
            PreparedStatement stmt = con.prepareStatement("SELECT * FROM Clientes WHERE email = ?");
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    public void excluir(int id) {
        try (Connection con = Conexao.getConnection()) {
            PreparedStatement instrucao = con.prepareStatement("DELETE FROM Clientes WHERE id = ?");
            instrucao.setInt(1, id);
            instrucao.executeUpdate();
        } catch (SQLException | URISyntaxException e) {
            System.out.println("Erro ao excluir cliente: " + e.getMessage());
        }
    }

    public void atualizar(Cliente cliente) {
        try (Connection con = Conexao.getConnection()) {
            PreparedStatement instrucao = con.prepareStatement("UPDATE Clientes SET nome=?, email=?, senha=? WHERE id=?");
            instrucao.setString(1, cliente.getNome());
            instrucao.setString(2, cliente.getEmail());
            instrucao.setString(3, cliente.getSenha());
            instrucao.setInt(4, cliente.getId());
            instrucao.executeUpdate();
        } catch (SQLException | URISyntaxException e) {
            System.out.println("Erro ao atualizar cliente: " + e.getMessage());
        }
    }

    public Cliente buscarPorId(int id) {
        Cliente cliente = null;
        try (Connection con = Conexao.getConnection()) {
            PreparedStatement instrucao = con.prepareStatement("SELECT * FROM Clientes WHERE id = ?");
            instrucao.setInt(1, id);
            ResultSet resultado = instrucao.executeQuery();
            if (resultado.next()) {
                cliente = new Cliente(resultado.getInt("id"), resultado.getString("nome"), resultado.getString("email"), resultado.getString("senha"));
            }
        } catch (SQLException | URISyntaxException e) {
            System.out.println("Erro ao buscar cliente por ID: " + e.getMessage());
        }
        return cliente;
    }

    public List<Cliente> listarTodos() {
        List<Cliente> clientes = new ArrayList<>();
        try (Connection con = Conexao.getConnection()) {
            PreparedStatement instrucao = con.prepareStatement("SELECT * FROM Clientes");
            ResultSet resultado = instrucao.executeQuery();
            while (resultado.next()) {
                Cliente cliente = new Cliente(resultado.getInt("id"), resultado.getString("nome"), resultado.getString("email"), resultado.getString("senha"));
                clientes.add(cliente);
            }
        } catch (SQLException | URISyntaxException e) {
            System.out.println("Erro ao listar clientes: " + e.getMessage());
        }
        return clientes;
    }
}
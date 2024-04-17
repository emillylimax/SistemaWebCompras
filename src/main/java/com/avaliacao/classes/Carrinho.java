package com.avaliacao.classes;

import java.util.ArrayList;

public class Carrinho {
    private ProdutoDAO produtoDAO; 

    private ArrayList<Produto> produtos;

    public Carrinho(ProdutoDAO produtoDAO) {
        this.produtoDAO = produtoDAO;
        this.produtos = new ArrayList<>();
    }

    public ArrayList<Produto> getProdutos() {
        return produtos;
    }

    public Produto getProduto(int id) {
        for (Produto p : produtos) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    public void removeProduto(int id) {
        Produto produto = getProduto(id);
        if (produto != null) {
            if (produto.getQuantidade() > 1) {
                produto.setQuantidade(produto.getQuantidade() - 1);
            } else {
                produtos.remove(produto);
            }
        }
    }

    public void addProduto(Produto p) {
        int quantidadeDisponivel = produtoDAO.verificarQuantidadeDisponivel(p.getId());
        
        if (quantidadeDisponivel > 0) {
            boolean produtoJaNoCarrinho = false;
            for (Produto produto : produtos) {
                if (produto.getId() == p.getId()) {
                    if (produto.getQuantidade() + 1 <= quantidadeDisponivel) {
                        produto.setQuantidade(produto.getQuantidade() + 1);
                    } else {
                        System.out.println("Quantidade máxima atingida para o produto " + p.getNome());
                    }
                    produtoJaNoCarrinho = true;
                    break;
                }
            }
            if (!produtoJaNoCarrinho) {
                p.setQuantidade(1);
                produtos.add(p);
            }
        } else {
            System.out.println("Produto " + p.getNome() + " fora de estoque.");
        }
    }

    public double calcularTotal() {
        double total = 0.0;
        for (Produto produto : produtos) {
            total += produto.getPreco() * produto.getQuantidade();
        }
        return total;
    }

    public void removerProduto(Produto produto) {
        produtos.remove(produto);
    }

    public void limparCarrinho() {
        produtos.clear();
    }

    public void setProdutoDAO(ProdutoDAO produtoDAO) {
        this.produtoDAO = produtoDAO;
    }

    public boolean isEmpty() {
        return produtos.isEmpty();
    }

    public String gerarTextoConteudo() {
        StringBuilder conteudo = new StringBuilder();
    
        for (Produto produto : produtos) {
            int quantidade = obterQuantidadeProduto(produto);
            conteudo.append(produto.getId()).append("!").append(quantidade).append("-");
        }
    
        if (conteudo.length() > 0 && conteudo.charAt(conteudo.length() - 1) == '-') {
            conteudo.deleteCharAt(conteudo.length() - 1);
        }
    
        return conteudo.toString();
    }
    
    public int obterQuantidadeProduto(Produto produto) {
        int quantidade = 0;
        for (Produto p : produtos) {
            if (p.equals(produto)) {
                quantidade++;
            }
        }
        return quantidade;
    }
    
    public int getQuantidadeProduto(Produto produto) {
        return obterQuantidadeProduto(produto);
    }
}

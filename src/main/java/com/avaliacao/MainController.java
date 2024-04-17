package com.avaliacao;

import com.avaliacao.classes.Carrinho;
import com.avaliacao.classes.Cliente;
import com.avaliacao.classes.ClienteDAO;
import com.avaliacao.classes.LojistaDAO;
import com.avaliacao.classes.Produto;
import com.avaliacao.classes.ProdutoDAO;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import jakarta.servlet.http.Cookie;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@SpringBootApplication
@ServletComponentScan 
@Controller
public class MainController {
    private final ClienteDAO clienteDAO;
    private final LojistaDAO lojistaDAO;
    private final ProdutoDAO produtoDAO;


    public MainController(ClienteDAO clienteDAO, LojistaDAO lojistaDAO, ProdutoDAO produtoDAO) {
        this.clienteDAO = clienteDAO;
        this.lojistaDAO = lojistaDAO;
        this.produtoDAO = produtoDAO;
    }


    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public void doLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        var email = request.getParameter("email");
        var senha = request.getParameter("senha");
    
        if (clienteDAO.autenticar(email, senha)) {
            HttpSession session = request.getSession();
            session.setAttribute("logado", true);
            session.setAttribute("tipo", "cliente");
    
            reconstruirCarrinho(request, session);
    
            response.sendRedirect(request.getContextPath() + "/dolistar");
        } else if (lojistaDAO.autenticar(email, senha)) {
            HttpSession session = request.getSession();
            session.setAttribute("logado", true);
            session.setAttribute("tipo", "lojista");
    
            reconstruirCarrinho(request, session);
    
            if ("lojista".equals(session.getAttribute("tipo"))) {
                response.sendRedirect("cadastrarProduto.html");
            } else {
                response.sendRedirect("index.html?msg=Você não tem permissão para acessar esta página");
            }
        } else {
            response.sendRedirect("index.html?msg=Login falhou");
        }
    }

    private void reconstruirCarrinho(HttpServletRequest request, HttpSession session) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("carrinho")) {
                    String carrinhoData = cookie.getValue();
                    if (!carrinhoData.isEmpty()) {
                        Carrinho carrinho = new Carrinho(produtoDAO);
                        carrinho.setProdutoDAO(produtoDAO);
                        String[] produtos = carrinhoData.split("-");
                        for (String produtoInfo : produtos) {
                            String[] info = produtoInfo.split("!");
                            int idProduto = Integer.parseInt(info[0]);
                            int quantidade = Integer.parseInt(info[1]);
                            Produto produto = produtoDAO.buscarPorId(idProduto);
                            if (produto != null) {
                                for (int i = 0; i < quantidade; i++) {
                                    carrinho.addProduto(produto);
                                }
                            }
                        }
                        session.setAttribute("carrinho", carrinho);
                    }
                    break;
                }
            }
        }
    }
    
    @RequestMapping("/logout")
    public void doLogout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        session.invalidate();


        Cookie carrinhoCookie = new Cookie("carrinho", "");
        carrinhoCookie.setMaxAge(0); 
        response.addCookie(carrinhoCookie);

        response.sendRedirect("index.html?msg=Usuario deslogado");
    }
 
    @RequestMapping(value = "/acaoCarrinho", method = RequestMethod.GET)
    public void acaoCarrinho(HttpServletRequest request, HttpServletResponse response) throws IOException {
      HttpSession session = request.getSession();
      Carrinho carrinho = inicializarCarrinho(session);
    
      String command = request.getParameter("comando");
      String prodId = request.getParameter("id");
    
      if (command != null && prodId != null) {
        int idProduto = Integer.parseInt(prodId);
    
        if (carrinho == null) {
          carrinho = new Carrinho(produtoDAO);
          carrinho.setProdutoDAO(produtoDAO);
          session.setAttribute("carrinho", carrinho);
        }
    
        Produto produto = produtoDAO.buscarPorId(idProduto);
    
        if (produto != null) {
          if (command.equals("add")) {
            carrinho.addProduto(produto);
          } else if (command.equals("remove")) {
            carrinho.removeProduto(idProduto);
          }
    
          StringBuilder carrinhoIds = new StringBuilder();
          for (Produto prod : carrinho.getProdutos()) {
            carrinhoIds.append(prod.getId()).append("!")
                       .append(carrinho.obterQuantidadeProduto(prod)).append("-");
          }
    
          if (carrinhoIds.length() > 0) {
            carrinhoIds.deleteCharAt(carrinhoIds.length() - 1);
          }
    
          Cookie carrinhoCookie = new Cookie("carrinho", carrinhoIds.toString());
          carrinhoCookie.setMaxAge(24 * 60 * 60);
          response.addCookie(carrinhoCookie);
        } else {
          response.getWriter().println("Produto não encontrado");
        }
      } else {
        response.getWriter().println("Erro ao adicionar produto ao carrinho");
      }
    
      response.sendRedirect(request.getContextPath() + "/dolistar");
    }

    @RequestMapping(value = "/verCarrinho", method = RequestMethod.GET)
    public void verCarrinho(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        Carrinho carrinho = (Carrinho) session.getAttribute("carrinho");

            if (carrinho != null && !carrinho.getProdutos().isEmpty()) {
                response.setContentType("text/html");
                PrintWriter writer = response.getWriter();
                writer.println("<html>");
                writer.println("<head><title>Carrinho de Compras</title></head>");
                writer.println("<body>");
                writer.println("<h1>Carrinho de Compras</h1>");
                writer.println("<table>");
                writer.println("<tr><th>Nome</th><th>Descrição</th><th>Preço</th><th>Quantidade</th><th>Ações</th></tr>");
        
                for (Produto produto : carrinho.getProdutos()) {
                    writer.println("<tr>");
                    writer.println("<td>" + produto.getNome() + "</td>");
                    writer.println("<td>" + produto.getDescricao() + "</td>");
                    writer.println("<td>" + produto.getPreco() + "</td>");
                    writer.println("<td>" + produto.getQuantidade() + "</td>");
                    writer.println("<td><form action='/acaoCarrinho' method='get'><input type='hidden' name='comando' value='remove'><input type='hidden' name='id' value='" + produto.getId() + "'><button type='submit'>Remover</button></form></td>");
                    writer.println("</tr>");
                }
                writer.println("</table>");
        
                writer.println("<form action=\"/dolistar\" method=\"get\">");
                writer.println("<button type=\"submit\">Voltar para Carrinho</button>");
                writer.println("</form>");
        
                writer.println("<form action=\"/finalizarCompra\" method=\"post\">");
                writer.println("<button type=\"submit\">Finalizar Compra</button>");
                writer.println("</form>");
        
                writer.println("</body>");
                writer.println("</html>");
            } else {
                response.sendRedirect(request.getContextPath() + "/dolistar?carrinho=vazio");
            }
    }

    @RequestMapping(value = "/finalizarCompra", method = RequestMethod.POST)
    public void finalizarCompra(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        Carrinho carrinho = (Carrinho) session.getAttribute("carrinho");

        if (carrinho != null && !carrinho.getProdutos().isEmpty()) {
            double total = carrinho.calcularTotal();
            for (Produto produto : carrinho.getProdutos()) {
                int quantidadeDisponivel = produtoDAO.verificarQuantidadeDisponivel(produto.getId());
                if (quantidadeDisponivel >= produto.getQuantidade()) {
                    produtoDAO.atualizarQuantidade(produto.getId(), quantidadeDisponivel - produto.getQuantidade());
                } else {
                }
            }

            carrinho.limparCarrinho();

            response.setContentType("text/html");
            PrintWriter writer = response.getWriter();
            writer.println("<html>");
            writer.println("<head><title>Total da Compra</title></head>");
            writer.println("<body>");
            writer.println("<h1>Total da Compra</h1>");
            writer.println("<p>Total da compra: R$" + total + "</p>");

            writer.println("<form action=\"/dolistar\" method=\"get\">");
            writer.println("<button type=\"submit\">Voltar para Carrinho</button>");
            writer.println("</form>");

            writer.println("</body>");
            writer.println("</html>");

        } else {
            response.sendRedirect(request.getContextPath() + "/dolistar?carrinho=vazio");
        }
    }

    @GetMapping("/listarProdutosLojista")
    @ResponseBody
    public List<Produto> listarProdutosLojista() {
        return produtoDAO.listarTodos();
    } 

    @RequestMapping(value = "/dolistar")
    public void listaProduto(HttpServletRequest request, HttpServletResponse response) throws IOException {

            var writer = response.getWriter();
    
            List<Produto> listarMercadoria = produtoDAO.listarTodos();
    
            response.setContentType("text/html");
    
            writer.println("<html>");
            writer.println("<head>");
            writer.println("<title>Lista de Produtos</title>");
            writer.println("</head>");
            writer.println("<body>");
    
            writer.println("<table>");
            writer.println("<thead>");
            writer.println("<tr>");
            writer.println("<th>Nome</th>");
            writer.println("<th>Descrição</th>");
            writer.println("<th>Preço</th>");
            writer.println("<th>Quantidade</th>");
            writer.println("<th>Ações</th>");
            writer.println("</tr>");
            writer.println("</thead>");
            writer.println("<tbody>");
    
            for (Produto produto : listarMercadoria) {
                writer.println("<tr>");
                writer.println("<td>" + produto.getNome() + "</td>");
                writer.println("<td>" + produto.getDescricao() + "</td>");
                writer.println("<td align='right'>" + produto.getPreco() + "</td>");
                writer.println("<td>" + produto.getQuantidade() + "</td>");
                writer.println("<td><form action='/acaoCarrinho' method='get'><input type='hidden' name='comando' value='add'><input type='hidden' name='id' value='" + produto.getId() + "'><button type='submit'>Adicionar no carrinho</button></form></td>");
                writer.println("</tr>");
            }
    
            writer.println("</tbody>");
            writer.println("</table>");
    
            writer.println("<form action='/verCarrinho' method='get'><button type='submit'>Ver Carrinho</button></form>");
    
            writer.println("<form action='/logout' method='get'><button type='submit'>Logout</button></form>");
    
            writer.println("</body>");
            writer.println("</html>");
    }

    @RequestMapping(value = "/cadastroCliente", method = RequestMethod.POST)
    public void cadastrarCliente(HttpServletRequest request, HttpServletResponse response) throws IOException {
        var nome = request.getParameter("nome");
        var email = request.getParameter("email");
        var senha = request.getParameter("senha");

        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
            response.sendRedirect("index.html?msg=Preencha todos os campos!");
            return;
        }

        Cliente novoCliente = new Cliente(0, nome, email, senha);
        
        if (clienteDAO.salvar(novoCliente)) {
            response.sendRedirect("index.html?msg=Cadastro concluído com sucesso!");
        } else {
            response.sendRedirect("index.html?msg=Erro ao cadastrar cliente");
        }
    }

    @RequestMapping(value = "/cadastroProduto", method = RequestMethod.POST)
    public void cadastrarProduto(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
    
        if (session != null) {
                String nome = request.getParameter("nome");
                String descricao = request.getParameter("descricao");
                String precoParam = request.getParameter("preco");
                String quantidadeParam = request.getParameter("quantidade");
        
                if (nome != null && !nome.isEmpty() && precoParam != null && !precoParam.isEmpty() && quantidadeParam != null && !quantidadeParam.isEmpty()) {
                    double preco = Double.parseDouble(precoParam);
                    int quantidade = Integer.parseInt(quantidadeParam);
        
                    Produto produtoExistente = produtoDAO.buscarPorNome(nome);
                    if (produtoExistente != null) {
                        produtoExistente.setQuantidade(produtoExistente.getQuantidade() + quantidade);
                        produtoDAO.atualizar(produtoExistente);
                        response.sendRedirect("cadastrarProduto.html?msg=Quantidade do produto atualizada com sucesso!");
                    } else {
                        Produto novoProduto = new Produto(0, nome, descricao, preco, quantidade);
                        if (produtoDAO.salvar(novoProduto)) {
                            response.sendRedirect("cadastrarProduto.html?msg=Produto cadastrado com sucesso!");
                        } else {
                            response.sendRedirect("cadastrarProduto.html?msg=Erro ao cadastrar produto");
                        }
                    }
                } else {
                    response.sendRedirect("index.html?msg=Preencha todos os campos obrigatórios");
                }

        } else {
            response.sendRedirect("index.html?msg=Faça login para acessar esta página");
        }
    }
    
    private Carrinho inicializarCarrinho(HttpSession session) {
        Object carrinhoObj = session.getAttribute("carrinho");
        
        if (carrinhoObj instanceof Carrinho) {
            return (Carrinho) carrinhoObj;
        } else {
            Carrinho carrinho = new Carrinho(produtoDAO);
            carrinho.setProdutoDAO(produtoDAO);
            session.setAttribute("carrinho", carrinho);
            return carrinho;
        }
    }
}
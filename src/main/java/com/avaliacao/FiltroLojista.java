package com.avaliacao;

import java.io.IOException;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebFilter("/cadastrarProduto.html")
public class FiltroLojista implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        HttpSession session = request.getSession();

        Boolean logado = (Boolean) session.getAttribute("logado");
        String tipo = (String) session.getAttribute("tipo");

        if (logado == null || !logado || !"lojista".equals(tipo)) {
            response.sendRedirect(request.getContextPath() + "/index.html?msg=Faça login como lojista para acessar esta página");
            return;
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}

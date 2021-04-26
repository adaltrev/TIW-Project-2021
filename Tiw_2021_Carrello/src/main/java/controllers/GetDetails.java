package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import beans.Product;
import beans.Seller;
import dao.ProductDao;
import dao.SellerDao;
import utils.ConnectionHandler;


@WebServlet("/GetDetails")
public class GetDetails extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

       
    public GetDetails() {
        super();
    }

    public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
	}
    
    
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Integer id= Integer.parseInt(request.getParameter("Id"));
		ProductDao productDao= new ProductDao(connection);
		SellerDao sellerDao= new SellerDao(connection);
		Product product;
		List<Seller> sellers;
		
		try {
		product=productDao.findProductById(id);
		}catch(SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Server unavailable, not possible to show search results");
			return;
		}
		if(product==null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND,
					"Page not found");
		}
		
		try {
		sellers=sellerDao.findSellersByProduct(product.getId());
		}catch(SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Server unavailable, not possible to show search results");
			return;
		}
		
		
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		String path="/WEB-INF/Details.html";

		if(sellers==null||sellers.size()==0)
			ctx.setVariable("errorMsg", "No sellers for this product");
		ctx.setVariable("product", product);
		ctx.setVariable("sellers", sellers);
		templateEngine.process(path, ctx, response.getWriter());
		
	}



}

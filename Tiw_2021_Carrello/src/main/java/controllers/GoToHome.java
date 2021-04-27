package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import beans.Product;
import dao.ProductDao;
import utils.ConnectionHandler;

@WebServlet("/Home")
public class GoToHome extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

	public GoToHome() {
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

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// the cookies have saved the last visualized products

		int toShow = 5;// maximum number of products to show in the home page
		int num = 0;
		List<Product> recentProducts = new ArrayList<>();
		List<Product> suggestedProducts = new ArrayList<>();
		Cookie cookies[] = request.getCookies();
		ProductDao productDao = new ProductDao(connection);

		if (cookies != null) {
			for (int i = cookies.length - 1; i >= 0 && toShow > 0; i--) {// it starts from the most recent cookie
				Cookie c = cookies[i];
				int id = 0;
				try {
					id = Integer.parseInt(c.getValue());
					try {
						Product product = productDao.findProductById(id);
						if (product != null) {// it means that the cookie value was correct and we found a correspondent
												// product
							recentProducts.add(product);
							toShow--;
						}

					} catch (SQLException e) {
						response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
								"Server unavailable, not possible to show products ");
						return;
					}
				} catch (NumberFormatException e) {// if the cookie was modified we simply ignore it
				}
			}

		}
		num = toShow;// we add the remaining product to show
		if (num > 0) {
			try {
				suggestedProducts = productDao.getSuggestedProducts(num, "Food");
			} catch (SQLException e) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
						"Server unavailable, not possible to show products ");
				return;
			}
		}
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		String path = "/WEB-INF/Home.html";
		String viewedMessage = "Check your recent viewed products :";
		String defaultMessage = "Polizon suggests :";
		if (recentProducts.size() == 0)
			ctx.setVariable("viewedMessage", null);

		else
			ctx.setVariable("viewedMessage", viewedMessage);

		if (suggestedProducts.size() == 0)
			ctx.setVariable("defaultMessage", null);
		else
			ctx.setVariable("defaultMessage", defaultMessage);
		String eMsg = request.getParameter("eMsg");// error parameter valid if it is equal to "1"
		if (eMsg != null && eMsg.equals("1"))
			ctx.setVariable("errorMsg", "You inserted an invaled parameter into the search form, try again");
		ctx.setVariable("recentProducts", recentProducts);
		ctx.setVariable("suggestedProducts", suggestedProducts);
		templateEngine.process(path, ctx, response.getWriter());

	}

}

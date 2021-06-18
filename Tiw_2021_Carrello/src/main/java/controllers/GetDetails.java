package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import beans.CartProduct;
import beans.Product;
import beans.Seller;
import dao.ProductDao;
import dao.SellerDao;
import utils.ConnectionHandler;

@WebServlet("/GetProductDetails")
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

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		Integer id = null;
		ProductDao productDao = new ProductDao(connection);
		SellerDao sellerDao = new SellerDao(connection);
		Product product;
		Map<Seller, CartProduct> sellersMap = new HashMap<>();
		List<Seller> sellers;
		try {
			id = Integer.parseInt(request.getParameter("id"));
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid product ID");
			return;
		}
		try {
			product = productDao.findProductById(id);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Server unavailable, not possible to show search results");
			return;
		}
		if (product == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Page not found");
		} else {

			try {
				sellers = sellerDao.findSellersByProduct(product.getId());
			} catch (SQLException e) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
						"Server unavailable, not possible to show search results");
				return;
			}

			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			String path = "/WEB-INF/Details.html";

			String cookieId = Integer.toString(product.getId());
			Cookie ck = new Cookie("visualized_product_" + cookieId, "");// reset of the eventual old cookie
			ck.setMaxAge(0);
			response.addCookie(ck);
			ck = new Cookie("visualized_product_" + cookieId, cookieId);
			ck.setMaxAge(604800);
			response.addCookie(ck);

			if (sellers == null || sellers.size() == 0)
				ctx.setVariable("errorMsg", "No sellers for this product");
			else {
				ctx.setVariable("found", "Sellers for this product: ");

				HttpSession session = request.getSession();
				@SuppressWarnings("unchecked")
				Map<Integer, List<CartProduct>> cart = (Map<Integer, List<CartProduct>>) session.getAttribute("cart");
				for (Seller s : sellers) {
					CartProduct cp = new CartProduct();
					cp.setAmount(0);
					cp.setPrice(0);
					if (cart.size() > 0 && cart.keySet().contains(s.getId())) {
						for (CartProduct p : cart.get(s.getId())) {
							cp.setAmount(cp.getAmount() + p.getAmount());
							cp.setPrice(cp.getPrice() + (p.getPrice() * p.getAmount()));
						}
					}
					sellersMap.put(s, cp);
				}
			}
			ctx.setVariable("product", product);
			ctx.setVariable("sellersMap", sellersMap);
			templateEngine.process(path, ctx, response.getWriter());

		}
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}

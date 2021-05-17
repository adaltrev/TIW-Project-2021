package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;

import dao.ProductDao;
import dao.SellerDao;
import utils.ConnectionHandler;
import beans.CartProduct;
import beans.Product;

@WebServlet("/AddToCart")
public class AddToCart extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public AddToCart() {
		super();
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Integer Pid = null;
		Integer Sid = null;
		Integer amount = null;
		Float price;
		boolean found = false;

		// Get and check input parameters
		try {
			Pid = Integer.parseInt(request.getParameter("product"));
			Sid = Integer.parseInt(request.getParameter("seller"));
			amount = Integer.parseInt(StringEscapeUtils.escapeJava(request.getParameter("addToCart")));
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid parameters");
			return;
		}
		if (Pid == null || Sid == null || amount == null || amount <= 0) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid parameters");
			return;
		}

		SellerDao sellerDao = new SellerDao(connection);
		try {
			price = sellerDao.checkSell(Pid, Sid);
			if (price == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "Couldn't add product to cart");
				return;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Server unavailable, not possible to add to cart");
			return;
		}

		// Get current cart from session
		Map<Integer, List<CartProduct>> cart;
		HttpSession session = request.getSession(true);
		try {
			cart = (Map<Integer, List<CartProduct>>) session.getAttribute("cart");
			if (cart == null)// TODO non so se è necessario
				cart = new HashMap<>();
		} catch (Exception e) {
			cart = new HashMap<>();
		}

		// Add product (and seller) to cart
		if (cart.size() > 0 && cart.keySet().contains(Sid)) {
			for (CartProduct p : cart.get(Sid)) {
				if (p.getId() == Pid) {
					p.setAmount(p.getAmount() + amount);
					found = true;
					break;
				}
			}
		} else {
			cart.put(Sid, new ArrayList<CartProduct>());
		}

		if (!found) {
			CartProduct newProduct = new CartProduct();
			Product product;
			try {
				ProductDao productDao = new ProductDao(connection);
				product = productDao.findProductById(Pid);
			} catch (Exception e) {
				e.printStackTrace();
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
						"Server unavailable, not possible to add to cart");
				return;
			}
			newProduct.setName(product.getName());
			newProduct.setId(Pid);
			newProduct.setAmount(amount);
			newProduct.setPrice(price);
			cart.get(Sid).add(newProduct);
		}

		// Update session and redirect
		session.setAttribute("cart", cart);
		String path = getServletContext().getContextPath() + "/Cart";
		response.sendRedirect(path);
	}
}
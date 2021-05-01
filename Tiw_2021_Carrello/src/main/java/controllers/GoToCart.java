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
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import beans.CartProduct;
import beans.Seller;
import dao.SellerDao;
import utils.ConnectionHandler;

/**
 * Servlet implementation class GoToCart
 */
@WebServlet("/Cart")
public class GoToCart extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

	public GoToCart() {
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

	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession session= request.getSession();
		Map<Integer,List<CartProduct>> cart = (Map<Integer, List<CartProduct>>) session.getAttribute("cart");//TODO vedere se la sessione può essere modificata dal client
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		String path = "/WEB-INF/Cart.html";
		if(cart==null||cart.keySet().size()==0) {
			ctx.setVariable("errorMsg", "There aren't products in your cart");
		}
		else {
			SellerDao sellerDao= new SellerDao(connection);
			Map<Seller, List<CartProduct>> sellerMap=new HashMap<>();
			for(Integer id :cart.keySet()) {
				if(id!=null) {
					Seller seller= new Seller();
					try {
						seller= sellerDao.findSellerById(id);
					} catch (SQLException e) {
						e.printStackTrace();
						response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
								"Server unavailable, not possible to show cart products ");
						return;
					}
					float cartPrice=0;
					for(CartProduct cartProduct:cart.get(id)) {
						cartPrice+=cartProduct.getPrice()*cartProduct.getAmount();
					}
					seller.setCartPrice(cartPrice);
					float shippingPrice=0;
					if(cartPrice<seller.getFreeShipping()){//no freeShipping, we have to find the shipping price
						int numProducts=cart.get(id).size();
						try {
							shippingPrice= sellerDao.findShippingPrice(seller.getId(), numProducts);
						} catch (SQLException e) {
							response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
									"Server unavailable, not possible to show cart products ");
							return;
						}
					}
					seller.setShippingPrice(shippingPrice);
					sellerMap.put(seller, cart.get(id));//TODO verificare se l'utente può modificare i prodotti nella sessione
				}
			}
			if(sellerMap.keySet().size()>0) {//checks if there is at least one seller
				ctx.setVariable("sellerMap", sellerMap);
			}
			else {
				ctx.setVariable("errorMsg", "There aren't products in your cart");
			}
		}
		templateEngine.process(path, ctx, response.getWriter());
			
		
	}

}

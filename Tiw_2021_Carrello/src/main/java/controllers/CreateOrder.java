package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import beans.CartProduct;
import beans.User;
import dao.OrderDao;
import utils.ConnectionHandler;


@WebServlet("/CreateOrder")
public class CreateOrder extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
       

    public CreateOrder() {
        super();
      
    }
    
    public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

    
    
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Integer Sid;
		User user;
		Sid = Integer.parseInt(request.getParameter("sId"));
		
		Map<Integer, List<CartProduct>> cart;
		HttpSession session = request.getSession(true);
		try {
			user = (User) session.getAttribute("user");
			cart = (Map<Integer, List<CartProduct>>) session.getAttribute("cart");
			if(Sid<=0 || cart==null || cart.size()==0 || !cart.keySet().contains(Sid) ) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST,
						"No products from this seller in your cart.");
				return;
			}				
		} catch (Exception e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Server unavailable, not possible to create order");
			return;
		}
		
		
		OrderDao orderDao = new OrderDao(connection);
		try {
			orderDao.createOrder(user.getId(), Sid, cart.get(Sid));
		} catch (Exception e) {
			System.out.println("2");
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Server unavailable, not possible to create order");
			return;
		}
		
		cart.remove(Sid);
		session.setAttribute("cart", cart);
		String path = getServletContext().getContextPath() + "/Orders";
		response.sendRedirect(path);		
	}

}

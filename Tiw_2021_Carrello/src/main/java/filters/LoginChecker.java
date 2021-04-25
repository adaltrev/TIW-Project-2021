package filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginChecker implements Filter {
	public LoginChecker() {
		
	}

	public void destroy() {
		// TODO Auto-generated method stub
	}


	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {


		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		String loginpath = req.getServletContext().getContextPath() + "/FailedLogin";

		HttpSession s = req.getSession();
		if (s==null||s.isNew() || s.getAttribute("user") == null) {
			HttpSession session= req.getSession(true);
			session.setAttribute("errorMsg", "You cannot do that before login, please login");
			res.sendRedirect(loginpath);
			return;
		}

		// pass the request along the filter chain
		chain.doFilter(request, response);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}

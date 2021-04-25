package controllers;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;


@WebServlet("/SubmitSearch")
public class SubmitSearch extends HttpServlet {
	private static final long serialVersionUID = 1L;
  
    public SubmitSearch() {
        super();
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String search;
		String path; 
		search= StringEscapeUtils.escapeJava(request.getParameter("search"));
		if(search==null || search.isEmpty()) {
			path = getServletContext().getContextPath() + "/Home?eMsg=1";
			response.sendRedirect(path);
		}
		
		
		else {
			path = getServletContext().getContextPath() + "/GetResults?search="+search;
			response.sendRedirect(path);
			
		}
	}

}

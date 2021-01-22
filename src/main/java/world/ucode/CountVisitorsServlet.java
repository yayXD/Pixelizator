/*package world.ucode;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/index.jsp")
public class CountVisitorsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Integer visitCounter = (Integer)session.getAttribute("visitCounter");
        if (visitCounter == null) {
            visitCounter = 1;
        } else {
            visitCounter++;
        }
        session.setAttribute("visitCounter", visitCounter);

    }
}
*/
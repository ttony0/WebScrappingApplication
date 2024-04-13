/**
 * Author: Tongren Chen
 * Andrew ID: tongrenc
 * Date: 02/08/2024
 *
 * This Servlet that handles the requests from the client and sends the response back to the client.
 * The servlet receives the state name and the type of information requested from the client. It then uses the
 * Project1Task3Model class to get the information and sends the response back to the client.
 * The servlet uses the RequestDispatcher to forward the request to the appropriate JSP page.
 * The servlet also contains helper methods to handle the state name and the Wikipedia URL.
 */

package ds.project1task3;

// Import necessary Java and Jakarta Servlet API classes
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;

// Annotation to declare servlet's name and URL pattern it responds to
@WebServlet(name = "Project1Task3Servlet", urlPatterns = {"/Information"})
public class Project1Task3Servlet extends HttpServlet {

    /**
     * This method handles the GET request from the client. It receives the state name and the type of information
     * requested from the client. It then uses the Project1Task3Model class to get the information and sends the
     * response back to the client. The servlet uses the RequestDispatcher to forward the request to the appropriate
     * JSP page.
     *
     * @param request HttpServletRequest object that contains the request the client has made of the servlet
     * @param response HttpServletResponse object that contains the response the servlet sends to the client
     * @throws IOException if an input or output error is detected when the servlet handles the request
     * @throws ServletException if the request for the GET could not be handled
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        // Create a new Project1Task3Model object
        Project1Task3Model model = new Project1Task3Model();
        // Get the state name and the type of information requested from the client
        String state = request.getParameter("states");
        // Create a RequestDispatcher object
        RequestDispatcher dispatcher = null;
        // Check the type of information requested
        if (request.getParameter("InformationType").equals("type1")) {
            // Get the population and symbols information
            dispatcher = getPopulationAndSymbol(request, response, model, state);
        } else {
            // Get the facts information
            dispatcher = getFacts(request, response, model, state);
        }
        // Forward the request to the appropriate JSP page
        dispatcher.forward(request, response);
    }

    /**
     * This method gets the population and symbols information of the state and sets the attributes in the request
     * object. It then returns the RequestDispatcher object to forward the request to the populationAndSymbols.jsp page.
     *
     * @param request HttpServletRequest object that contains the request the client has made of the servlet
     * @param response HttpServletResponse object that contains the response the servlet sends to the client
     * @param model Project1Task3Model object that contains the methods to get the population and symbols information
     * @param state String object that contains the state name
     * @return RequestDispatcher object to forward the request to the populationAndSymbols.jsp page
     * @throws IOException if an input or output error is detected when the servlet handles the request
     * @throws ServletException if the request for the GET could not be handled
     */
    private RequestDispatcher getPopulationAndSymbol(HttpServletRequest request, HttpServletResponse response,
                                        Project1Task3Model model, String state) throws IOException, ServletException {
        // Get the state population
        String statePopulation = model.getStatePopulation(state);
        // change the state name to the format that can be used in the Wikipedia URL
        state = stateName(state);
        // Get the flag and seal information
        String wikipediaURL = "https://en.wikipedia.org/wiki/" + state;
        String wikipediaResponse = model.getWikipediaResponse(wikipediaURL);
        String flagURL = model.getFlagURL(wikipediaResponse);
        String flagCredit = model.getFlagCredit(state);
        String sealURL = model.getSealURL(wikipediaResponse);
        String sealCredit = model.getSealCredit(state);
        // change the state name back to the original format
        state = recoverStateName(state);
        // Set the attributes in the request object
        request.setAttribute("state", state);
        request.setAttribute("statePopulation", statePopulation);
        request.setAttribute("stateFlag", flagURL);
        request.setAttribute("stateFlagCredit", flagCredit);
        request.setAttribute("stateSeal", sealURL);
        request.setAttribute("stateSealCredit", sealCredit);
        // Return the RequestDispatcher object to forward the request to the populationAndSymbols.jsp page
        return request.getRequestDispatcher("populationAndSymbols.jsp");
    }

    /**
     * This method gets the facts information of the state and sets the attributes in the request object. It then returns
     * the RequestDispatcher object to forward the request to the facts.jsp page.
     *
     * @param request HttpServletRequest object that contains the request the client has made of the servlet
     * @param response HttpServletResponse object that contains the response the servlet sends to the client
     * @param model Project1Task3Model object that contains the methods to get the facts information
     * @param state String object that contains the state name
     * @return RequestDispatcher object to forward the request to the facts.jsp page
     * @throws IOException if an input or output error is detected when the servlet handles the request
     * @throws ServletException if the request for the GET could not be handled
     */
    private RequestDispatcher getFacts(HttpServletRequest request, HttpServletResponse response,
                                        Project1Task3Model model, String state) throws IOException, ServletException {
        // change the state name to the format that can be used in the Wikipedia URL
        state = stateName(state);
        // Get the capital and governor information
        String wikipediaURL = "https://en.wikipedia.org/wiki/" + state;
        String capital = model.getCapital(wikipediaURL);
        String governor = model.getGovernor(wikipediaURL);
        // change the state name back to the original format
        state = recoverStateName(state);
        // Set the attributes in the request object
        request.setAttribute("state", state);
        request.setAttribute("stateCapital", capital);
        request.setAttribute("stateGovernor", governor);
        // Return the RequestDispatcher object to forward the request to the facts.jsp page
        return request.getRequestDispatcher("facts.jsp");
    }

    /**
     * This method changes the state name to the format that can be used in the Wikipedia URL.
     * @param state String object that contains the state name
     * @return String object that contains the state name in the format that can be used in the Wikipedia URL
     */
    private String stateName(String state) {
        // change the blank space to underscore
        if (state.contains(" ")) {
            state = state.replace(" ", "_");
        }
        // change specific state name to the format that can be used in the Wikipedia URL
        switch (state) {
            case "Georgia":
                state = "Georgia_(U.S._state)";
                break;
            case "New_York":
                state = "New_York_(state)";
                break;
            case "Washington":
                state = "Washington_(state)";
                break;
        }
        // return the state name
        return state;
    }

    /**
     * This method changes the state name back to the original format.
     * @param state String object that contains the state name
     * @return String object that contains the state name in the original format
     */
    private String recoverStateName(String state) {
        // change specific state name back to the original format
        switch (state) {
            case "Georgia_(U.S._state)":
                state = "Georgia";
                break;
            case "New_York_(state)":
                state = "New York";
                break;
            case "Washington_(state)":
                state = "Washington";
                break;
        }
        // change underscore to blank space
        if (state.contains("_")) {
            state = state.replace("_", " ");
        }
        // return the state name
        return state;
    }
}

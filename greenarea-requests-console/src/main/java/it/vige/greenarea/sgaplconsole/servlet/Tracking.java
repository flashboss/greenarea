/******************************************************************************
 * Vige, Home of Professional Open Source Copyright 2010, Vige, and           *
 * individual contributors by the @authors tag. See the copyright.txt in the  *
 * distribution for a full listing of individual contributors.                *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may    *
 * not use this file except in compliance with the License. You may obtain    *
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0        *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 ******************************************************************************/
package it.vige.greenarea.sgaplconsole.servlet;

import it.vige.greenarea.sgapl.sgot.webservice.LocateShippingResponseData;
import it.vige.greenarea.sgapl.sgot.webservice.ResultStatus;
import it.vige.greenarea.sgapl.sgot.webservice.ShippingOrderManager;
import it.vige.greenarea.sgapl.sgot.webservice.ShippingOrderManager_Service;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.WebServiceRef;

public class Tracking extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2243943845449233153L;
	@WebServiceRef(wsdlLocation = "http://localhost:8080/greenarea-service/ShippingOrderManager?wsdl")
	// @WebServiceRef(wsdlLocation =
	// "WEB-INF/wsdl/163.162.24.76/SGOTserver/ShippingOrderManager.wsdl")
	private ShippingOrderManager_Service service;

	/**
	 * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
	 * methods.
	 * 
	 * @param request
	 *            servlet request
	 * @param response
	 *            servlet response
	 * @throws ServletException
	 *             if a servlet-specific error occurs
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	protected void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		try {
			/* TODO output your page here. You may use following sample code. */
			String shippingID = request.getParameter("shippingID");
			// trackingOrderBean.locateOrder(shippingID);
			out.println("<html>");
			out.println("<head>");
			out.println("<title>Servlet tracking</title>");
			out.println("</head>");
			out.println("<body>");
			out.println("<h1>Servlet tracking</h1>");
			LocateShippingResponseData res = locateShipping(shippingID);
			out.println("<table border=\"1\">");
			/*
			 * out.println("<thead>"); out.println("<tr>");
			 * out.println("<th></th>"); out.println("<th></th>");
			 * out.println("</tr>"); out.println("</thead>");
			 */

			out.println("</br> ");
			out.println("<tbody>");
			out.println("<tr>");
			out.println("<td>shippingID</td>");
			out.println("<td>" + shippingID + "</td>");
			out.println("</tr>");
			out.println("<tbody>");
			out.println("<tr>");
			out.println("<td>Response</td>");
			out.println("<td>" + res.getResult().getStatus().name() + "</td>");
			out.println("</tr>");
			if (res.getResult().getStatus().equals(ResultStatus.NOK)) {
				out.println("<tr>");
				out.println("<td>ErrorCode</td>");
				out.println("<td>" + res.getResult().getErrorCode() + "</td>");
				out.println("</tr>");
				out.println("<tr>");
				out.println("<td>ErrorDescription</td>");
				out.println("<td>" + res.getResult().getErrorDescription()
						+ "</td>");
				out.println("</tr>");

			} else {

				out.println("<tr>");
				out.println("<td>Transport State</td>");
				out.println("<td>" + res.getTransportState() + "</td>");
				out.println("</tr>");

				if (res.getExchangeSiteName() != null) {
					out.println("<tr>");
					out.println("<td>ExchangeSite name</td>");
					out.println("<td>" + res.getExchangeSiteName() + "</td>");
					out.println("</tr>");

				}
				out.println("<tr>");
				out.println("<td>Address:</td>");
				out.println("<td>" + res.getAddress() + "</td>");
				out.println("</tr>");

			}
			out.println("</tbody>");
			out.println("</table>");
			out.println("</body>");
			out.println("</html>");
		} finally {
			out.close();
		}
	}

	// <editor-fold defaultstate="collapsed"
	// desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
	/**
	 * Handles the HTTP <code>GET</code> method.
	 * 
	 * @param request
	 *            servlet request
	 * @param response
	 *            servlet response
	 * @throws ServletException
	 *             if a servlet-specific error occurs
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Handles the HTTP <code>POST</code> method.
	 * 
	 * @param request
	 *            servlet request
	 * @param response
	 *            servlet response
	 * @throws ServletException
	 *             if a servlet-specific error occurs
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Returns a short description of the servlet.
	 * 
	 * @return a String containing servlet description
	 */
	@Override
	public String getServletInfo() {
		return "Short description";
	}// </editor-fold>

	private LocateShippingResponseData locateShipping(String shippingOrderID) {
		ShippingOrderManager shippingOrderManager = service
				.getShippingOrderManagerPort();
		return shippingOrderManager.locateShipping(shippingOrderID);
	}
}

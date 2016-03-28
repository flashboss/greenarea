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
package it.vige.greenarea.sgrl.servlet;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;

@WebServlet(name = "CommonsFileUploadServlet", urlPatterns = { "/CommonsFileUploadServlet" })
public class CommonsFileUploadServlet extends HttpServlet {
	private static final long serialVersionUID = -5477895704391093993L;

	private Logger logger = getLogger(getClass());

	private static final String TMP_DIR_PATH = "c:\\tmp";
	private File tmpDir;
	private static final String DESTINATION_DIR_PATH = "";
	private File destinationDir;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		tmpDir = new File(TMP_DIR_PATH);
		if (!tmpDir.isDirectory()) {
			throw new ServletException(TMP_DIR_PATH + " is not a directory");
		}
		String realPath = getServletContext().getRealPath(DESTINATION_DIR_PATH);
		destinationDir = new File(realPath);
		if (!destinationDir.isDirectory()) {
			throw new ServletException(DESTINATION_DIR_PATH + " is not a directory");
		}
		logger.info("realpath=" + realPath);

	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		response.setContentType("text/plain");
		out.println("<h1>Servlet File Upload Example using Commons File Upload</h1>");
		out.println();
		int fileSize = request.getContentLength();
		byte[] buf = new byte[fileSize];
		try {
			ServletInputStream is = request.getInputStream();
			is.read(buf);
			File file = new File(destinationDir, "LogisticNetwork.mxe");
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(buf);
			fos.close();
			out.close();
		} catch (Exception ex) {
			log("Error encountered while uploading file", ex);
		}

	}

	protected void workingdoPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		response.setContentType("text/plain");
		out.println("<h1>Servlet File Upload Example using Commons File Upload</h1>");
		out.println();

		DiskFileItemFactory fileItemFactory = new DiskFileItemFactory();
		/*
		 * Set the size threshold, above which content will be stored on disk.
		 */
		fileItemFactory.setSizeThreshold(1 * 1024 * 1024); // 1 MB
		/*
		 * Set the temporary directory to store the uploaded files of size above
		 * threshold.
		 */
		fileItemFactory.setRepository(tmpDir);

		ServletFileUpload uploadHandler = new ServletFileUpload(fileItemFactory);
		try {
			/*
			 * Parse the request
			 */
			List<FileItem> items = uploadHandler.parseRequest(request);
			Iterator<FileItem> itr = items.iterator();
			while (itr.hasNext()) {
				FileItem item = (FileItem) itr.next();
				/*
				 * Handle Form Fields.
				 */
				if (item.isFormField()) {
					out.println("File Name = " + item.getFieldName() + ", Value = " + item.getString());
				} else {
					// Handle Uploaded files.
					out.println("Field Name = " + item.getFieldName() + ", File Name = " + item.getName()
							+ ", Content type = " + item.getContentType() + ", File Size = " + item.getSize());
					/*
					 * Write file to the ultimate location.
					 */
					File file = new File(destinationDir, "LogisticNetwork.mxe");
					item.write(file);
				}
				out.close();
			}
		} catch (FileUploadException ex) {
			log("Error encountered while parsing the request", ex);
		} catch (Exception ex) {
			log("Error encountered while uploading file", ex);
		}

	}

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
	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		try {
			/*
			 * TODO output your page here. You may use following sample code.
			 */
			out.println("<html>");
			out.println("<head>");
			out.println("<title>Servlet CommonsFileUploadServlet</title>");
			out.println("</head>");
			out.println("<body>");
			out.println("<h1>Servlet CommonsFileUploadServlet at " + request.getContextPath() + "</h1>");
			out.println("</body>");
			out.println("</html>");
		} finally {
			out.close();
		}
	}

	// <editor-fold defaultstate="collapsed"
	// desc="HttpServlet methods. Click on the + sign on the left to edit the
	// code.">
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
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
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
}

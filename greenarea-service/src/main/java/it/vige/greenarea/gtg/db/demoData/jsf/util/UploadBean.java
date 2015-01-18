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
package it.vige.greenarea.gtg.db.demoData.jsf.util;

import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.sgapl.sgot.business.SGOTbean;

import java.io.IOException;
import java.io.InputStream;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.Part;

import org.slf4j.Logger;

@ManagedBean
@RequestScoped
public class UploadBean {

	private Logger logger = getLogger(getClass());

	private Part file;

	@Inject
	private SGOTbean sgotBean;

	public Part getFile() {
		return file;
	}

	public void setFile(Part file) {
		this.file = file;
	}

	public void upload() {
		try {
			InputStream inputStream = file.getInputStream();
			sgotBean.caricaTrasportiDaFile(inputStream);
		} catch (IOException e) {
			logger.error("errore su upload", e);
		}
	}

}

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

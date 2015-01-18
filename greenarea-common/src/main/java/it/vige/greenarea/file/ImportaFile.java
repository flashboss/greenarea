package it.vige.greenarea.file;

import it.vige.greenarea.dto.Filtro;
import it.vige.greenarea.dto.OperatoreLogistico;
import it.vige.greenarea.dto.Richiesta;
import it.vige.greenarea.vo.RichiestaXML;

import java.io.File;
import java.io.InputStream;
import java.util.List;

public interface ImportaFile {

	public File recuperaFile();

	public List<Richiesta> convertiARichieste(List<RichiestaXML> richiesteXML,
			OperatoreLogistico operatoreLogistico);

	public List<RichiestaXML> prelevaDati(InputStream inputStream,
			List<Filtro> filtri) throws Exception;

	public boolean acceptRoundCode(String roundCode);

	public OperatoreLogistico getOperatoreLogistico();

	public void setOperatoreLogistico(OperatoreLogistico operatoreLogistico);

	public File getDirectory();
}

package it.vige.greenarea.bpm.tempo.importaconsegneeritiri;

import static it.vige.greenarea.Constants.BASE_URI_ADMINISTRATOR;
import static it.vige.greenarea.Conversioni.convertiFiltersToFiltri;
import static it.vige.greenarea.bpm.risultato.Categoria.ERROREGRAVE;
import static it.vige.greenarea.bpm.risultato.Categoria.ERRORELIEVE;
import static it.vige.greenarea.bpm.risultato.Tipo.ERROREDATIMANCANTI;
import static it.vige.greenarea.bpm.risultato.Tipo.ERROREDATINONCORRETTI;
import static it.vige.greenarea.bpm.risultato.Tipo.ERRORESISTEMA;
import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.bpm.risultato.Messaggio;
import it.vige.greenarea.cl.library.entities.Filter;
import it.vige.greenarea.dto.Filtro;
import it.vige.greenarea.dto.OperatoreLogistico;
import it.vige.greenarea.dto.Richiesta;
import it.vige.greenarea.file.ImportaCSVFile;
import it.vige.greenarea.file.ImportaFile;
import it.vige.greenarea.vo.RichiestaXML;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.GenericType;

import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;

public class VerificaDatiConsegneERitiri extends
		EmptyVerificaDatiConsegneERitiri {

	private Logger logger = getLogger(getClass());

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		try {
			super.execute(execution);
			OperatoreLogistico operatoreLogistico = (OperatoreLogistico) execution
					.getVariable("operatorelogistico");
			logger.info("CDI Verifica Dati Consegne e Ritiri");
			ImportaFile importaFile = new ImportaCSVFile(operatoreLogistico,
					null);
			File fileDaImportare = importaFile.recuperaFile();
			if (fileDaImportare == null) {
				Messaggio messaggio = (Messaggio) execution
						.getVariable("messaggio");
				messaggio.setCategoria(ERRORELIEVE);
				messaggio.setTipo(ERROREDATIMANCANTI);
			} else {
				InputStream inpuStream = new FileInputStream(fileDaImportare);
				try {
					Client client = newClient();
					Builder bldr = client.target(
							BASE_URI_ADMINISTRATOR + "/getFiltersForOP/"
									+ operatoreLogistico.getId()).request(
							APPLICATION_JSON);
					List<Filter> filters = bldr
							.get(new GenericType<List<Filter>>() {
							});
					@SuppressWarnings("unchecked")
					List<Filtro> filtri = (List<Filtro>) execution
							.getVariable("filtri");
					filtri.addAll(convertiFiltersToFiltri(filters));

					List<RichiestaXML> richiesteXML = importaFile.prelevaDati(
							inpuStream, filtri);
					List<Richiesta> richieste = importaFile.convertiARichieste(
							richiesteXML, operatoreLogistico);
					execution.setVariableLocal("richieste", richieste);
				} catch (Exception ex) {
					Messaggio messaggio = (Messaggio) execution
							.getVariable("messaggio");
					messaggio.setCategoria(ERRORELIEVE);
					messaggio.setTipo(ERROREDATINONCORRETTI);
				} finally {
					inpuStream.close();
				}
			}
		} catch (Exception ex) {
			Messaggio messaggio = (Messaggio) execution
					.getVariable("messaggio");
			messaggio.setCategoria(ERROREGRAVE);
			messaggio.setTipo(ERRORESISTEMA);
		}
	}
}

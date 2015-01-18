package it.vige.greenarea.bpm.tempo.importaconsegneeritiri;

import static it.vige.greenarea.sgapl.sgot.webservice.ResultStatus.NOK;
import static it.vige.greenarea.bpm.ConversioniBPM.convertiRichiesta;
import static it.vige.greenarea.bpm.risultato.Categoria.ERROREGRAVE;
import static it.vige.greenarea.bpm.risultato.Categoria.ERRORELIEVE;
import static it.vige.greenarea.bpm.risultato.Tipo.ERRORESISTEMA;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.sgapl.sgot.webservice.ResultOperationResponse;
import it.vige.greenarea.sgapl.sgot.webservice.ShippingOrderData;
import it.vige.greenarea.sgapl.sgot.webservice.ShippingOrderManager;
import it.vige.greenarea.sgapl.sgot.webservice.ShippingOrderManager_Service;
import it.vige.greenarea.bpm.risultato.Messaggio;
import it.vige.greenarea.dto.OperatoreLogistico;
import it.vige.greenarea.dto.Richiesta;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Named;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceRef;

import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;

@Named
@Stateless
public class AggiornamentoConsegneERitiri extends
		EmptyAggiornamentoConsegneERitiri {

	private Logger logger = getLogger(getClass());

	@WebServiceRef(wsdlLocation = "http://localhost:8080/greenarea-service/ShippingOrderManager?wsdl")
	private ShippingOrderManager_Service sgotService;

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		try {
			super.execute(execution);
			logger.info("CDI Aggiornamento Consegne e Ritiri");
			@SuppressWarnings("unchecked")
			List<Richiesta> richieste = (List<Richiesta>) execution
					.getVariableLocal("richieste");
			ShippingOrderManager port = sgotService
					.getShippingOrderManagerPort();
			List<ShippingOrderData> richiesteConvertite = new ArrayList<ShippingOrderData>();
			OperatoreLogistico operatoreLogistico = (OperatoreLogistico) execution
					.getVariable("operatorelogistico");
			for (Richiesta richiesta : richieste) {
				richiesteConvertite.add(convertiRichiesta(richiesta,
						operatoreLogistico.getId()));
			}
			// Set timeout until a connection is established
			((BindingProvider) port).getRequestContext().put(
					"javax.xml.ws.client.connectionTimeout", "600000");
			// Set timeout until the response is received
			((BindingProvider) port).getRequestContext().put(
					"javax.xml.ws.client.receiveTimeout", "600000");
			ResultOperationResponse resultOperationResponse = port
					.requestShippings(richiesteConvertite);
			if (resultOperationResponse.getStatus().equals(NOK)) {
				Messaggio messaggio = (Messaggio) execution
						.getVariable("messaggio");
				messaggio.setCategoria(ERRORELIEVE);
				messaggio.setTipo(ERRORESISTEMA);
			}
		} catch (Exception ex) {
			Messaggio messaggio = (Messaggio) execution
					.getVariable("messaggio");
			messaggio.setCategoria(ERROREGRAVE);
			messaggio.setTipo(ERRORESISTEMA);
		}
	}
}

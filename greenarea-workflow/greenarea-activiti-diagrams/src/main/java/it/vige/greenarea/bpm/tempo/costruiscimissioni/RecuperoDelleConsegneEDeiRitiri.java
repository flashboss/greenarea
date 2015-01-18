package it.vige.greenarea.bpm.tempo.costruiscimissioni;

import static it.vige.greenarea.bpm.ConversioniBPM.convertiRichiesta;
import static it.vige.greenarea.bpm.risultato.Categoria.ERROREGRAVE;
import static it.vige.greenarea.bpm.risultato.Categoria.ERRORELIEVE;
import static it.vige.greenarea.bpm.risultato.Tipo.ERROREDATIMANCANTI;
import static it.vige.greenarea.bpm.risultato.Tipo.ERRORESISTEMA;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.sgapl.sgot.webservice.RequestShippingsResponseData;
import it.vige.greenarea.sgapl.sgot.webservice.ShippingOrderData;
import it.vige.greenarea.sgapl.sgot.webservice.ShippingOrderManager;
import it.vige.greenarea.sgapl.sgot.webservice.ShippingOrderManager_Service;
import it.vige.greenarea.bpm.risultato.Messaggio;
import it.vige.greenarea.dto.OperatoreLogistico;
import it.vige.greenarea.dto.Richiesta;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Named;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceRef;

import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;

@Named
@Stateless
public class RecuperoDelleConsegneEDeiRitiri extends
		EmptyRecuperoDelleConsegneEDeiRitiri {

	private Logger logger = getLogger(getClass());

	@WebServiceRef(wsdlLocation = "http://localhost:8080/greenarea-service/ShippingOrderManager?wsdl")
	private ShippingOrderManager_Service sgotService;

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		try {
			super.execute(execution);
			logger.info("CDI Recupero delle Consegne e dei Ritiri");
			OperatoreLogistico operatoreLogistico = (OperatoreLogistico) execution
					.getVariable("operatorelogistico");
			@SuppressWarnings("unchecked")
			List<Richiesta> richieste = (List<Richiesta>) execution
					.getVariableLocal("richieste");
			try {
				ShippingOrderManager port = sgotService
						.getShippingOrderManagerPort();
				// Set timeout until a connection is established
				((BindingProvider) port).getRequestContext().put(
						"javax.xml.ws.client.connectionTimeout", "600000");
				// Set timeout until the response is received
				((BindingProvider) port).getRequestContext().put(
						"javax.xml.ws.client.receiveTimeout", "600000");
				RequestShippingsResponseData shippingOrdersResponse = port
						.getShippings(operatoreLogistico.getId());
				List<ShippingOrderData> shippingOrders = shippingOrdersResponse
						.getShippings();
				if (shippingOrders == null || shippingOrders.size() == 0) {
					Messaggio messaggio = (Messaggio) execution
							.getVariable("messaggio");
					messaggio.setCategoria(ERRORELIEVE);
					messaggio.setTipo(ERROREDATIMANCANTI);
				} else
					for (ShippingOrderData shippingOrderDetails : shippingOrders) {
						richieste.add(convertiRichiesta(shippingOrderDetails));
					}
			} catch (Exception ex) {
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
			throw new BpmnError("erroregraverecuperoconsegneritiri");
		}
	}

}

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

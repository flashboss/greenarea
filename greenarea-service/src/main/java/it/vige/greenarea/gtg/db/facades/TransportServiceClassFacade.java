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
package it.vige.greenarea.gtg.db.facades;

import it.vige.greenarea.cl.library.entities.TransportServiceClass;
import it.vige.greenarea.cl.library.entities.TransportServiceClass_;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@Stateless
public class TransportServiceClassFacade extends AbstractFacade<TransportServiceClass, Long> {
    @PersistenceContext(unitName = "GTGwebPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TransportServiceClassFacade() {
        super(TransportServiceClass.class);
    }
    
    @Override
    public Long getId(TransportServiceClass entity) {
        return  entity.getId();
    }

    @Override
    public void setId(TransportServiceClass entity, Long id) {
        entity.setId(id);
    }
    
      public List<TransportServiceClass> findBySelection( String serviceClassType ) {
         
 
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        Root<TransportServiceClass> trRoot = cq.from(TransportServiceClass.class);
        
        Predicate wherePredicate = cb.and( cb.like(trRoot.get(TransportServiceClass_.description), serviceClassType));
        cq = cq.select(trRoot ).where(wherePredicate);
        Query q = getEntityManager().createQuery(cq);
        return q.getResultList();
    }
}

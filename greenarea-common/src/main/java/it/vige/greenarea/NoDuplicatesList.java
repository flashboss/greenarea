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
package it.vige.greenarea;

import java.util.Collection;
import java.util.LinkedList;

public class NoDuplicatesList<E> extends LinkedList<E> {

	private static final long serialVersionUID = -2103425506554297525L;

	public NoDuplicatesList() {
		super();
	}

	public NoDuplicatesList(Collection<? extends E> c) {
		super(c);
	}

	@Override
	public boolean add(E e) {
		if (this.contains(e)) {
			return false;
		} else {
			return super.add(e);
		}
	}

	@Override
	public boolean addAll(Collection<? extends E> collection) {
		Collection<E> copy = new LinkedList<E>(collection);
		copy.removeAll(this);
		return super.addAll(copy);
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> collection) {
		Collection<E> copy = new LinkedList<E>(collection);
		copy.removeAll(this);
		return super.addAll(index, copy);
	}

	@Override
	public void add(int index, E element) {
		if (this.contains(element)) {
			return;
		} else {
			super.add(index, element);
		}
	}
}

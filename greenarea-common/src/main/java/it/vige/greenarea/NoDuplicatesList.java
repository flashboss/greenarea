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

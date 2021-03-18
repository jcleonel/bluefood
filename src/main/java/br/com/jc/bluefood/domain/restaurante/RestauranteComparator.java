package br.com.jc.bluefood.domain.restaurante;

import java.util.Comparator;

import br.com.jc.bluefood.domain.restaurante.SearchFilter.Order;

public class RestauranteComparator implements Comparator<Restaurante> {

	private SearchFilter filter;
	private String cep;

	public RestauranteComparator(SearchFilter filter, String cep) {
		super();
		this.filter = filter;
		this.cep = cep;
	}

	@Override
	public int compare(Restaurante r1, Restaurante r2) {
		int result;
		
		if (filter.getOrder() == Order.Taxa) {
			result = r1.getTaxaEntrega().compareTo(r2.getTaxaEntrega());
		} else if (filter.getOrder() == Order.Tempo) {
			result = r1.calucularTempoEntregaBase(cep).compareTo(r2.calucularTempoEntregaBase(cep));
		} else {
			throw new IllegalStateException("O valor de ordenaçãoo " + filter.getOrder() + " não é válido");
		}
		
		return filter.isAsc() ? result : -result;
	}

}

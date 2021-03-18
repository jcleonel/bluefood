package br.com.jc.bluefood.infrastructure.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import br.com.jc.bluefood.domain.pedido.Carrinho;
import br.com.jc.bluefood.domain.pedido.ItemPedido;
import br.com.jc.bluefood.domain.pedido.Pedido;
import br.com.jc.bluefood.domain.pedido.PedidoRepository;
import br.com.jc.bluefood.domain.pedido.RestauranteDiferenteException;
import br.com.jc.bluefood.domain.restaurante.ItemCardapio;
import br.com.jc.bluefood.domain.restaurante.ItemCardapioRepository;

@Controller
@RequestMapping("/cliente/carrinho")
@SessionAttributes("carrinho")
public class CarrinhoController {
	
	@Autowired
	private ItemCardapioRepository itemCardapioRepository;
	
	@Autowired
	private PedidoRepository pedidoRepository;
	
	@ModelAttribute("carrinho")
	public Carrinho Carrinho() {
		return new Carrinho();
	}

	@GetMapping("/adicionar")
	public String adicionarItem(
			@RequestParam("itemId") Integer ItemId,
			@RequestParam("quantidade") Integer quantidade,
			@RequestParam("observacoes") String observacoes,
			@ModelAttribute("carrinho") Carrinho carrinho,
			Model model) {
		
		ItemCardapio itemCardapio = itemCardapioRepository.findById(ItemId).orElseThrow();
		try {
			carrinho.adicionarItem(itemCardapio, quantidade, observacoes);
		} catch (RestauranteDiferenteException e) {
			model.addAttribute("msg", "Não é possível misturar pedidos de restaurantes diferentes.");
		}
		
		return "cliente-carrinho";
	}
	
	@GetMapping("/remover")
	public String removerItem(
			@RequestParam("itemId") Integer ItemId,
			@ModelAttribute("carrinho") Carrinho carrinho,
			SessionStatus sessionStatus,
			Model model) {
		
		ItemCardapio itemCardapio = itemCardapioRepository.findById(ItemId).orElseThrow();
		
		carrinho.removerItem(itemCardapio);
		
		if (carrinho.vazio()) {
			sessionStatus.setComplete();
		}
		
		return "cliente-carrinho";
	}
	
	@GetMapping("/visualizar")
	public String viewCarrinho() {
		return "cliente-carrinho";
	}
	
	@GetMapping("/refazerPedido")
	public String refazerPedido(
			@RequestParam("pedidoId") Integer pedidoId,
			@ModelAttribute("carrinho") Carrinho carrinho,
			Model model) {
		
		Pedido pedido = pedidoRepository.findById(pedidoId).orElseThrow();
		
		for (ItemPedido itemPedido : pedido.getItens()) {
			carrinho.adicionarItem(itemPedido);
		}
		
		return "cliente-carrinho";
	}
}

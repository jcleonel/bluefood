package br.com.jc.bluefood.infrastructure.web.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.jc.bluefood.application.service.RelatorioService;
import br.com.jc.bluefood.application.service.RestauranteService;
import br.com.jc.bluefood.application.service.ValidationException;
import br.com.jc.bluefood.domain.pedido.Pedido;
import br.com.jc.bluefood.domain.pedido.PedidoRepository;
import br.com.jc.bluefood.domain.pedido.RelatorioItemFaturamento;
import br.com.jc.bluefood.domain.pedido.RelatorioItemFilter;
import br.com.jc.bluefood.domain.pedido.RelatorioPedidoFilter;
import br.com.jc.bluefood.domain.restaurante.CategoriaRestauranteRepository;
import br.com.jc.bluefood.domain.restaurante.ItemCardapio;
import br.com.jc.bluefood.domain.restaurante.ItemCardapioRepository;
import br.com.jc.bluefood.domain.restaurante.Restaurante;
import br.com.jc.bluefood.domain.restaurante.RestauranteRepository;
import br.com.jc.bluefood.util.SecurityUtils;

@Controller
@RequestMapping(path = "/restaurante")
public class RestauranteController {
	
	@Autowired
	private RestauranteService restauranteService;
	
	@Autowired
	private RelatorioService relatorioService;
	
	@Autowired
	private RestauranteRepository restauranteRepository;
	
	@Autowired
	private CategoriaRestauranteRepository categoriaRestauranteRepository;
	
	@Autowired
	private ItemCardapioRepository itemCardapioRepository;
	
	@Autowired
	private PedidoRepository pedidoRepository;

	@GetMapping("/home")
	public String home(Model model) {
		Integer restautanteId = SecurityUtils.loggedRestaurante().getId();
		List<Pedido> pedidos = pedidoRepository.findByRestaurante_IdOrderByDataDesc(restautanteId);
		model.addAttribute("pedidos", pedidos);
				
		return "restaurante-home";
	}
	
	@GetMapping("/edit")
	public String edit(Model model) {
		Integer restautanteId = SecurityUtils.loggedRestaurante().getId();
		Restaurante restaurante = restauranteRepository.findById(restautanteId).orElseThrow();

		model.addAttribute("restaurante", restaurante);
		ControllerHelper.setEditMode(model, true);
		
		ControllerHelper.addCategoriasToRequest(categoriaRestauranteRepository, model);

		return "restaurante-cadastro";
	}
	
	@PostMapping("/save")
	public String save(@ModelAttribute("restaurante") @Valid Restaurante restaurante, Errors errors, Model model) {
		
		if (!errors.hasErrors()) {
			try {
				restauranteService.saveRestaurante(restaurante);
				model.addAttribute("msg", "Restaurante alterado com sucesso!");
			} catch (ValidationException e) {
				errors.rejectValue("email", null, e.getMessage());
			}
		}		
		
		ControllerHelper.setEditMode(model, true);
		
		ControllerHelper.addCategoriasToRequest(categoriaRestauranteRepository, model);
		
		return "restaurante-cadastro";
	}
	
	@GetMapping("/comidas")
	public String viewComidas(Model model) {
		Integer restautanteId = SecurityUtils.loggedRestaurante().getId();
		Restaurante restaurante = restauranteRepository.findById(restautanteId).orElseThrow();
		model.addAttribute("restaurante", restaurante);
		
		List<ItemCardapio> itensCardapio = itemCardapioRepository.findByRestaurante_IdOrderByNome(restautanteId);
		model.addAttribute("itensCardapio", itensCardapio);
		
		model.addAttribute("itemCardapio", new ItemCardapio());
		
		return "restaurante-comidas";
	}
	
	@GetMapping("/comidas/remover")
	public String remover(
			@RequestParam("itemId") Integer itemId,
			Model model) {
		
		itemCardapioRepository.deleteById(itemId);
		
		return "redirect:/restaurante/comidas";
	}
	
	@PostMapping("/comidas/cadastrar")
	public String cadastrar(
			@Valid @ModelAttribute("itemCardapio") ItemCardapio itemCardapio,
			Errors errors,
			Model model) {
		
		if (errors.hasErrors()) {
			Integer restautanteId = SecurityUtils.loggedRestaurante().getId();
			Restaurante restaurante = restauranteRepository.findById(restautanteId).orElseThrow();
			model.addAttribute("restaurante", restaurante);
			
			List<ItemCardapio> itensCardapio = itemCardapioRepository.findByRestaurante_IdOrderByNome(restautanteId);
			model.addAttribute("itensCardapio", itensCardapio);
			
			return "restaurante-comidas";
		}
		
		restauranteService.saveItemCardapio(itemCardapio);
		
		return "redirect:/restaurante/comidas";
		
	}
	
	@GetMapping("pedido")
	public String viewPedido(
			@RequestParam("pedidoId") Integer pedidoId,
			Model model) {
		
		Pedido pedido = pedidoRepository.findById(pedidoId).orElseThrow();
		model.addAttribute("pedido", pedido);
		
		return "restaurante-pedido";
		
	}
	
	@PostMapping("/pedido/proximoStatus")
	public String proximoStatus(
			@RequestParam("pedidoId") Integer pedidoId,
			Model model) {
		
		Pedido pedido = pedidoRepository.findById(pedidoId).orElseThrow();
		model.addAttribute("pedido", pedido);
		model.addAttribute("msg", "Status alterado com sucesso");
		pedido.definirProximoStatus();
		pedidoRepository.save(pedido);
		
		return "restaurante-pedido";
	}
	
	@GetMapping("/relatorios/pedidos")
	public String relatorioPedidos(
			@ModelAttribute("relatorioPedidoFilter") RelatorioPedidoFilter filter,
			Model model) {
		
		Integer restautanteId = SecurityUtils.loggedRestaurante().getId();
		List<Pedido> pedidos = relatorioService.listPedidos(restautanteId, filter);  
		model.addAttribute("pedidos", pedidos);
		
		model.addAttribute("filter", filter);
		
		return "restaurante-relatorio-pedidos";
	}
	
	@GetMapping("/relatorios/itens")
	public String relatorioItens(
			@ModelAttribute("relatorioItemFilter") RelatorioItemFilter filter,
			Model model) {

		Integer restautanteId = SecurityUtils.loggedRestaurante().getId();
		
		List<ItemCardapio> itensCardapio = itemCardapioRepository.findByRestaurante_IdOrderByNome(restautanteId);
		model.addAttribute("itensCardapio", itensCardapio);
		
		List<RelatorioItemFaturamento> itensCalculados = relatorioService.calcularFaturamentoItens(restautanteId, filter);
		model.addAttribute("itensCalculados", itensCalculados);
		
		model.addAttribute("relatorioItemFilter", filter);
		
		return "restaurante-relatorio-itens";
	}
}

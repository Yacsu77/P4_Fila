package com.example.P4_Fila.controller;

import com.example.P4_Fila.model.Cliente;
import com.example.P4_Fila.model.Colaborador;
import com.example.P4_Fila.model.Secao;
import com.example.P4_Fila.repository.ClienteRepository;
import com.example.P4_Fila.repository.ColaboradorRepository;
import com.example.P4_Fila.repository.SecaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ViewController {
    
    private final ClienteRepository clienteRepository;
    private final SecaoRepository secaoRepository;
    private final ColaboradorRepository colaboradorRepository;
    
    // Variável para armazenar colaborador logado (em produção use sessão)
    private Colaborador colaboradorLogado = null;
    
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("totalClientes", clienteRepository.count());
        model.addAttribute("senhasAtivas", secaoRepository.findByAtivoTrue().size());
        model.addAttribute("senhasEspera", secaoRepository.findAll().size());
        return "index";
    }
    
    // ===== CLIENTES =====
    @GetMapping("/clientes")
    public String clientes(Model model) {
        model.addAttribute("clientes", clienteRepository.findAll());
        model.addAttribute("editando", false);
        model.addAttribute("cliente", new Cliente());
        return "clientes";
    }
    
    @PostMapping("/clientes")
    public String salvarCliente(@ModelAttribute Cliente cliente, RedirectAttributes redirectAttributes) {
        try {
            if (cliente.getId() != null) {
                // Atualização
                clienteRepository.save(cliente);
                redirectAttributes.addFlashAttribute("success", "Cliente atualizado com sucesso!");
            } else {
                // Novo cliente
                clienteRepository.save(cliente);
                redirectAttributes.addFlashAttribute("success", "Cliente cadastrado com sucesso!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao salvar cliente: " + e.getMessage());
        }
        return "redirect:/clientes";
    }
    
    @GetMapping("/clientes/editar/{id}")
    public String editarCliente(@PathVariable Long id, Model model) {
        Optional<Cliente> clienteOpt = clienteRepository.findById(id);
        if (clienteOpt.isPresent()) {
            model.addAttribute("cliente", clienteOpt.get());
            model.addAttribute("editando", true);
            model.addAttribute("clientes", clienteRepository.findAll());
            return "clientes";
        }
        return "redirect:/clientes";
    }
    
    @GetMapping("/clientes/deletar/{id}")
    public String deletarCliente(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            clienteRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Cliente deletado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao deletar cliente: " + e.getMessage());
        }
        return "redirect:/clientes";
    }
    
    // ===== CRIAR SEÇÃO/SENHA =====
    @GetMapping("/criar-secao")
    public String criarSecao(Model model) {
        model.addAttribute("clientes", clienteRepository.findAll());
        return "criar-secao";
    }
    
    @PostMapping("/criar-secao")
    public String gerarSenha(@RequestParam Long idCliente, 
                             @RequestParam String tipoSecao, 
                             Model model, 
                             RedirectAttributes redirectAttributes) {
        try {
            Optional<Cliente> cliente = clienteRepository.findById(idCliente);
            if (cliente.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Cliente não encontrado!");
                return "redirect:/criar-secao";
            }
            
            com.example.P4_Fila.service.SecaoService secaoService = 
                new com.example.P4_Fila.service.SecaoService(
                    secaoRepository,
                    new com.example.P4_Fila.factory.EntityFactory()
                );
            
            Secao secao = secaoService.criarSecao(idCliente, tipoSecao);
            model.addAttribute("senhaGerada", secao.getSenha());
            model.addAttribute("tipoSecao", secao.getTipoSecao());
            model.addAttribute("clientes", clienteRepository.findAll());
            redirectAttributes.addFlashAttribute("success", "Senha gerada com sucesso: " + secao.getSenha());
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao gerar senha: " + e.getMessage());
            model.addAttribute("clientes", clienteRepository.findAll());
        }
        return "criar-secao";
    }
    
    // ===== VISUALIZAR SENHAS =====
    @GetMapping("/senhas")
    public String visualizarSenhas(Model model,
                                   @RequestParam(required = false) Boolean ativo,
                                   @RequestParam(required = false) String tipo) {
        List<Secao> secoes;
        
        if (ativo != null && ativo) {
            secoes = secaoRepository.findByAtivoTrue();
        } else if (tipo != null) {
            secoes = secaoRepository.findAll().stream()
                .filter(s -> tipo.equals(s.getTipoSecao()))
                .toList();
        } else {
            secoes = secaoRepository.findAll();
        }
        
        model.addAttribute("secoes", secoes);
        model.addAttribute("totalSenhas", secaoRepository.count());
        model.addAttribute("senhasAtivas", secaoRepository.findByAtivoTrue().size());
        model.addAttribute("senhasPrioritarias", 
            secaoRepository.findAll().stream().filter(s -> "PRIORITARIA".equals(s.getTipoSecao())).count());
        
        return "senhas";
    }
    
    @GetMapping("/senhas/atender/{id}")
    public String atenderSenha(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Optional<Secao> secao = secaoRepository.findById(id);
            if (secao.isPresent()) {
                secao.get().setAtivo(false);
                secaoRepository.save(secao.get());
                redirectAttributes.addFlashAttribute("success", "Atendimento finalizado!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao finalizar atendimento: " + e.getMessage());
        }
        return "redirect:/senhas";
    }
    
    // ===== LOGIN COLABORADOR =====
    @GetMapping("/login")
    public String login(Model model) {
        return "login";
    }
    
    @PostMapping("/login")
    public String fazerLogin(@RequestParam String usuario, 
                           @RequestParam String senha, 
                           Model model,
                           RedirectAttributes redirectAttributes) {
        try {
            Optional<Colaborador> colaborador = colaboradorRepository.findByUsuario(usuario);
            
            if (colaborador.isPresent() && colaborador.get().getSenha().equals(senha)) {
                colaboradorLogado = colaborador.get();
                redirectAttributes.addFlashAttribute("success", "Login realizado com sucesso!");
                return "redirect:/atendimento";
            } else {
                model.addAttribute("error", "Usuário ou senha inválidos!");
                return "login";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Erro ao fazer login: " + e.getMessage());
            return "login";
        }
    }
    
    // ===== ATENDIMENTO COLABORADOR =====
    @GetMapping("/atendimento")
    public String atendimento(Model model) {
        if (colaboradorLogado == null) {
            return "redirect:/login";
        }
        
        // Buscar senhas em diferentes estados
        List<Secao> senhasAguardando = secaoRepository.findAll().stream()
            .filter(s -> "AGUARDANDO".equals(s.getStatus()) && s.getAtivo())
            .sorted((s1, s2) -> {
                // Priorizar prioritárias
                if ("PRIORITARIA".equals(s1.getTipoSecao()) && "NORMAL".equals(s2.getTipoSecao())) return -1;
                if ("NORMAL".equals(s1.getTipoSecao()) && "PRIORITARIA".equals(s2.getTipoSecao())) return 1;
                return 0;
            })
            .toList();
        
        // Buscar senha em atendimento por este colaborador (se houver)
        Secao senhaEmAtendimento = secaoRepository.findAll().stream()
            .filter(s -> "EM_ATENDIMENTO".equals(s.getStatus()) && s.getAtivo())
            .findFirst()
            .orElse(null);
        
        model.addAttribute("colaboradorLogado", colaboradorLogado);
        model.addAttribute("senhasAguardando", senhasAguardando);
        model.addAttribute("senhaEmAtendimento", senhaEmAtendimento);
        model.addAttribute("meusAtendimentos", List.of()); // TODO: implementar busca de atendimentos
        
        return "atendimento";
    }
    
    @PostMapping("/atendimento/chamar")
    public String chamarSenha(@RequestParam Long idSecao, RedirectAttributes redirectAttributes) {
        if (colaboradorLogado == null) {
            return "redirect:/login";
        }
        
        try {
            Optional<Secao> secao = secaoRepository.findById(idSecao);
            if (secao.isPresent()) {
                secao.get().setStatus("CHAMADA");
                secaoRepository.save(secao.get());
                redirectAttributes.addFlashAttribute("success", "Senha chamada! Aguardando confirmação do cliente.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao chamar senha: " + e.getMessage());
        }
        
        return "redirect:/atendimento";
    }
    
    @PostMapping("/atendimento/iniciar")
    public String iniciarAtendimento(@RequestParam Long idSecao, RedirectAttributes redirectAttributes) {
        if (colaboradorLogado == null) {
            return "redirect:/login";
        }
        
        try {
            Optional<Secao> secao = secaoRepository.findById(idSecao);
            if (secao.isPresent()) {
                secao.get().setStatus("EM_ATENDIMENTO");
                secaoRepository.save(secao.get());
                redirectAttributes.addFlashAttribute("success", "Atendimento iniciado! Cliente confirmado.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao iniciar atendimento: " + e.getMessage());
        }
        
        return "redirect:/atendimento";
    }
    
    @PostMapping("/atendimento/finalizar")
    public String finalizarAtendimento(@RequestParam Long idSecao,
                                      @RequestParam String descricao,
                                      RedirectAttributes redirectAttributes) {
        if (colaboradorLogado == null) {
            return "redirect:/login";
        }
        
        try {
            Optional<Secao> secao = secaoRepository.findById(idSecao);
            if (secao.isPresent()) {
                secao.get().setStatus("FINALIZADA");
                secao.get().setAtivo(false);
                secaoRepository.save(secao.get());
                
                // TODO: Criar registro de atendimento com descrição
                redirectAttributes.addFlashAttribute("success", "Atendimento finalizado com sucesso!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao finalizar atendimento: " + e.getMessage());
        }
        
        return "redirect:/atendimento";
    }
    
    @PostMapping("/atendimento/transferir")
    public String transferirAtendimento(@RequestParam Long idSecao,
                                       @RequestParam Long departamentoDestinoId,
                                       @RequestParam String descricao,
                                       RedirectAttributes redirectAttributes) {
        if (colaboradorLogado == null) {
            return "redirect:/login";
        }
        
        try {
            Optional<Secao> secao = secaoRepository.findById(idSecao);
            if (secao.isPresent()) {
                secao.get().setStatus("TRANSFERIDA");
                secaoRepository.save(secao.get());
                
                // TODO: Criar registro de atendimento com transferência
                redirectAttributes.addFlashAttribute("success", "Atendimento transferido para outro departamento!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao transferir atendimento: " + e.getMessage());
        }
        
        return "redirect:/atendimento";
    }
}


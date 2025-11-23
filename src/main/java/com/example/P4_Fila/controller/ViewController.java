package com.example.P4_Fila.controller;

import com.example.P4_Fila.factory.EntityFactory;
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
    private final EntityFactory entityFactory;
    
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
        // Buscar cliente anônimo
        Optional<Cliente> clienteAnonimo = clienteRepository.findByCpf("00000000000");
        model.addAttribute("clienteAnonimoId", clienteAnonimo.map(Cliente::getId).orElse(null));
        return "criar-secao";
    }
    
    @PostMapping("/criar-secao")
    public String gerarSenha(@RequestParam(required = false) Long idCliente,
                             @RequestParam(required = false) String clienteAnonimo,
                             @RequestParam(required = false) String tipoSecao, 
                             Model model, 
                             RedirectAttributes redirectAttributes) {
        try {
            Long clienteIdFinal = null;
            boolean isAnonimo = "anonimo".equals(clienteAnonimo);
            
            // Se cliente anônimo foi selecionado, buscar ou criar
            if (isAnonimo) {
                Optional<Cliente> clienteAnonimoOpt = clienteRepository.findByCpf("00000000000");
                if (clienteAnonimoOpt.isEmpty()) {
                    // Criar cliente anônimo se não existir
                    Cliente novoAnonimo = entityFactory.createCliente(
                        "Cliente Anônimo",
                        0,
                        "00000000000",
                        "anonimo@sistema.com",
                        "anonimo123"
                    );
                    clienteIdFinal = clienteRepository.save(novoAnonimo).getId();
                } else {
                    clienteIdFinal = clienteAnonimoOpt.get().getId();
                }
            } else {
                // Cliente cadastrado
                if (idCliente == null) {
                    redirectAttributes.addFlashAttribute("error", "Selecione um cliente ou escolha Cliente Anônimo!");
                    model.addAttribute("clientes", clienteRepository.findAll());
                    Optional<Cliente> clienteAnonimoOpt = clienteRepository.findByCpf("00000000000");
                    model.addAttribute("clienteAnonimoId", clienteAnonimoOpt.map(Cliente::getId).orElse(null));
                    return "criar-secao";
                }
                clienteIdFinal = idCliente;
            }
            
            Optional<Cliente> cliente = clienteRepository.findById(clienteIdFinal);
            if (cliente.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Cliente não encontrado!");
                return "redirect:/criar-secao";
            }
            
            com.example.P4_Fila.service.SecaoService secaoService = 
                new com.example.P4_Fila.service.SecaoService(
                    secaoRepository,
                    entityFactory
                );
            
            // Se for cliente anônimo, sempre criar como prioritária
            String tipoFinal = isAnonimo ? "PRIORITARIA" : (tipoSecao != null ? tipoSecao : "NORMAL");
            Secao secao = secaoService.criarSecao(clienteIdFinal, tipoFinal);
            
            model.addAttribute("senhaGerada", secao.getSenha());
            model.addAttribute("tipoSecao", secao.getTipoSecao());
            model.addAttribute("clientes", clienteRepository.findAll());
            Optional<Cliente> clienteAnonimoOpt = clienteRepository.findByCpf("00000000000");
            model.addAttribute("clienteAnonimoId", clienteAnonimoOpt.map(Cliente::getId).orElse(null));
            
            if (isAnonimo) {
                redirectAttributes.addFlashAttribute("success", "✅ Senha prioritária gerada para Cliente Anônimo: " + secao.getSenha() + " - Atendimento rápido!");
            } else {
                redirectAttributes.addFlashAttribute("success", "Senha gerada com sucesso: " + secao.getSenha());
            }
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao gerar senha: " + e.getMessage());
            model.addAttribute("clientes", clienteRepository.findAll());
            Optional<Cliente> clienteAnonimoOpt = clienteRepository.findByCpf("00000000000");
            model.addAttribute("clienteAnonimoId", clienteAnonimoOpt.map(Cliente::getId).orElse(null));
        }
        return "criar-secao";
    }
    
    // ===== VISUALIZAR SENHAS =====
    @GetMapping("/senhas")
    public String visualizarSenhas(Model model,
                                   @RequestParam(required = false) Boolean ativo,
                                   @RequestParam(required = false) String tipo) {
        // Buscar senha atual (mais recente em CHAMADA ou EM_ATENDIMENTO)
        Secao senhaAtual = secaoRepository.findAll().stream()
            .filter(s -> ("CHAMADA".equals(s.getStatus()) || "EM_ATENDIMENTO".equals(s.getStatus())) && s.getAtivo())
            .sorted((s1, s2) -> s2.getId().compareTo(s1.getId())) // Mais recente primeiro
            .findFirst()
            .orElse(null);
        
        // Buscar últimas 3 senhas chamadas (excluindo a senha atual se houver)
        List<Secao> senhasChamadas = secaoRepository.findAll().stream()
            .filter(s -> ("CHAMADA".equals(s.getStatus()) || "EM_ATENDIMENTO".equals(s.getStatus())) && s.getAtivo() && 
                        (senhaAtual == null || !s.getId().equals(senhaAtual.getId())))
            .sorted((s1, s2) -> s2.getId().compareTo(s1.getId())) // Mais recente primeiro
            .limit(3)
            .toList();
        
        // Lista completa para filtros
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
        
        model.addAttribute("senhaAtual", senhaAtual);
        model.addAttribute("senhasChamadas", senhasChamadas);
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
        
        // Buscar senhas aguardando (AGUARDANDO)
        List<Secao> senhasAguardando = secaoRepository.findAll().stream()
            .filter(s -> "AGUARDANDO".equals(s.getStatus()) && s.getAtivo())
            .sorted((s1, s2) -> {
                // Priorizar prioritárias
                if ("PRIORITARIA".equals(s1.getTipoSecao()) && "NORMAL".equals(s2.getTipoSecao())) return -1;
                if ("NORMAL".equals(s1.getTipoSecao()) && "PRIORITARIA".equals(s2.getTipoSecao())) return 1;
                return 0;
            })
            .toList();
        
        // Buscar senha chamada por este colaborador (aguardando confirmação)
        Secao senhaChamada = secaoRepository.findAll().stream()
            .filter(s -> "CHAMADA".equals(s.getStatus()) && s.getAtivo() && 
                        colaboradorLogado.getNome().equals(s.getColaboradorChamada()))
            .findFirst()
            .orElse(null);
        
        // Buscar senha em atendimento por este colaborador
        Secao senhaEmAtendimento = secaoRepository.findAll().stream()
            .filter(s -> "EM_ATENDIMENTO".equals(s.getStatus()) && s.getAtivo())
            .findFirst()
            .orElse(null);
        
        model.addAttribute("colaboradorLogado", colaboradorLogado);
        model.addAttribute("senhasAguardando", senhasAguardando);
        model.addAttribute("senhaChamada", senhaChamada);
        model.addAttribute("senhaEmAtendimento", senhaEmAtendimento);
        model.addAttribute("temAtendimentos", !senhasAguardando.isEmpty() || senhaChamada != null || senhaEmAtendimento != null);
        
        return "atendimento";
    }
    
    @PostMapping("/atendimento/chamar")
    public String chamarSenha(@RequestParam Long idSecao, 
                             @RequestParam(required = false) String guiche,
                             RedirectAttributes redirectAttributes) {
        if (colaboradorLogado == null) {
            return "redirect:/login";
        }
        
        try {
            Optional<Secao> secao = secaoRepository.findById(idSecao);
            if (secao.isPresent()) {
                secao.get().setStatus("CHAMADA");
                // Registrar onde foi chamada
                String guicheInfo = guiche != null && !guiche.isEmpty() 
                    ? guiche 
                    : "Guichê " + colaboradorLogado.getUsuario();
                secao.get().setGuicheChamada(guicheInfo);
                secao.get().setColaboradorChamada(colaboradorLogado.getNome());
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
    
    @PostMapping("/atendimento/cancelar-chamada")
    public String cancelarChamada(@RequestParam Long idSecao, RedirectAttributes redirectAttributes) {
        if (colaboradorLogado == null) {
            return "redirect:/login";
        }
        
        try {
            Optional<Secao> secao = secaoRepository.findById(idSecao);
            if (secao.isPresent()) {
                // Volta para AGUARDANDO se o cliente não compareceu
                secao.get().setStatus("AGUARDANDO");
                secao.get().setGuicheChamada(null);
                secao.get().setColaboradorChamada(null);
                secaoRepository.save(secao.get());
                redirectAttributes.addFlashAttribute("success", "Chamada cancelada. Senha voltou para a fila de espera.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao cancelar chamada: " + e.getMessage());
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


package br.com.mini_erp.bi;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

// Imports do Swagger
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/bi")
@Tag(name = "4. Inteligência (BI)", description = "Relatórios e métricas gerenciais")
public class BiController {

    private final BiService service;

    public BiController(BiService service) {
        this.service = service;
    }

    @Operation(summary = "Total de Clientes", description = "Retorna a contagem total de clientes ativos.")
    @GetMapping("/clientes/total")
    public long totalClientes() {
        return service.totalClientes();
    }

    // --- ENDPOINT DE RELATÓRIO CSV COM SWAGGER ---
    @Operation(summary = "Exportar Vendas (CSV)", description = "Gera e baixa um arquivo CSV com o detalhamento de vendas no período selecionado (Requer ADMIN).")
    @GetMapping("/relatorios/vendas/csv")
    @PreAuthorize("hasRole('ADMIN')")
    public void exportarVendasCsv(
            @Parameter(description = "Data inicial (AAAA-MM-DD)", example = "2024-01-01", required = true)
            @RequestParam("dataInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,

            @Parameter(description = "Data final (AAAA-MM-DD)", example = "2024-01-31", required = true)
            @RequestParam("dataFim") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,

            HttpServletResponse response) throws IOException {

        // 1. Configurar o cabeçalho da resposta HTTP para download de arquivo
        response.setContentType("text/csv");
        String nomeArquivo = String.format("relatorio_vendas_%s_a_%s.csv",
                dataInicio.format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                dataFim.format(DateTimeFormatter.ofPattern("yyyyMMdd")));

        response.setHeader("Content-Disposition", "attachment; filename=\"" + nomeArquivo + "\"");

        // 2. Chamar o serviço para escrever o CSV diretamente no response
        service.escreverVendasCsv(response.getWriter(), dataInicio, dataFim);
    }
}
package br.com.mini_erp.bi;

import jakarta.servlet.http.HttpServletResponse; // Importe
import org.springframework.format.annotation.DateTimeFormat; // Importe
import org.springframework.web.bind.annotation.*;

import java.io.IOException; // Importe
import java.math.BigDecimal;
import java.time.LocalDate; // Importe
import java.time.format.DateTimeFormatter; // Importe

@RestController
@RequestMapping("/bi")
public class BiController {

    private final BiService service;

    public BiController(BiService service) {
        this.service = service;
    }

    @GetMapping("/clientes/total")
    public long totalClientes() {
        return service.totalClientes();
    }

    // ... (seus outros endpoints)

    // --- NOVO ENDPOINT DE RELATÓRIO ---
    @GetMapping("/relatorios/vendas/csv")
    public void exportarVendasCsv(
            @RequestParam("dataInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam("dataFim") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            HttpServletResponse response) throws IOException {

        // 1. Configurar o cabeçalho da resposta HTTP
        response.setContentType("text/csv");
        String nomeArquivo = String.format("relatorio_vendas_%s_a_%s.csv",
                dataInicio.format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                dataFim.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        response.setHeader("Content-Disposition", "attachment; filename=\"" + nomeArquivo + "\"");

        // 2. Chamar o serviço para escrever o CSV diretamente no response
        service.escreverVendasCsv(response.getWriter(), dataInicio, dataFim);
    }
}

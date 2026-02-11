package br.com.mini_erp.cadastro;

import br.com.mini_erp.cadastro.dto.EmpresaSetupDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/empresas")
public class EmpresaController {

    private final EmpresaService service;

    public EmpresaController(EmpresaService service) {
        this.service = service;
    }

    // Endpoint público para cadastro inicial (Sign Up)
    @PostMapping("/setup")
    public ResponseEntity<String> setup(@RequestBody @Valid EmpresaSetupDTO dto) {
        Empresa empresa = service.setupEmpresa(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Empresa '" + empresa.getNome() + "' criada com sucesso. Admin: " + dto.emailAdmin());
    }
}
package valida.movimentacao.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MensagemSqs {

    private String agencia;

    private String numero_conta;

    private String digito_conta;

    private BigDecimal valor_movimento;
}


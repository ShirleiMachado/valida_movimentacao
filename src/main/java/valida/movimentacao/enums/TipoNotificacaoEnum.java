package valida.movimentacao.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum TipoNotificacaoEnum {
    SMS(0, "sms"),
    WHATSAPP(1, "whatsapp"),
    EMAIL(2, "email");

    private Integer value;
    private String nome;

}
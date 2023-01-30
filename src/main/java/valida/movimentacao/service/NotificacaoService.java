package valida.movimentacao.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import valida.movimentacao.client.ConsultarLimiteClient;
import valida.movimentacao.dto.MensagemSqs;
import valida.movimentacao.dto.ResponseLimite;
import valida.movimentacao.enums.TipoNotificacaoEnum;
import valida.movimentacao.model.LoggerNotificacao;
import valida.movimentacao.repository.NotificacaoRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificacaoService {


    @Autowired
    private NotificacaoRepository notificacaoRepository;

    @Autowired
    private ConsultarLimiteClient consultarLimiteClient;

    @Autowired
    private ObjectMapper objectMapper;

    //Obj mensagemSqs será enviado via SQS Amazon
    public void validaMovimentacao(MensagemSqs mensagemSqs) throws JsonProcessingException {

        //Poderia usar um StringBuilder
        String id_conta = mensagemSqs.getAgencia().concat(mensagemSqs.getNumero_conta().concat(mensagemSqs.getDigito_conta()));
        ResponseLimite responseLimite = consultarLimiteClient.consultar(id_conta);

        if (mensagemSqs.getValor_movimento().compareTo(responseLimite.getLimite()) > 0) {
            //chamar um Microserviço que retornará dados parametrizados de quais tipos de notificação o cliente espera receber.
            enviaNotificacaoLimiteExcedido(mensagemSqs);
        }
    }

    private void enviaNotificacaoLimiteExcedido(MensagemSqs sqsMovimentacaoConta) throws JsonProcessingException {
        try {
            montarNotificacao(sqsMovimentacaoConta, null);
            /*O registro da notificação pode ser gravado diretamente nesse microserviço ou pode chamar outro que ficará responsavel por
             * fazer toda parte de persistencia.*/
            notificacaoRepository.save(LoggerNotificacao.builder().dadosNotificacao(objectMapper.writeValueAsString(sqsMovimentacaoConta)).horaEnvioNotificacaoLimiteExcedido(LocalDateTime.now()).build());
        } catch (RuntimeException ex) {
            throw new RuntimeException("Erro no fluxo enviar notificação de limite excedido.", ex);
        }
    }


    /*Aqui seria feito uma consulta em uma tabela com dados informando quais os tipos de notificação o cliente deseja receber,
     * seria retornado uma, pois o cliente pode querer receber todos os 3 tipos de notificações(SMS,EMAIL,ZAP)*/
    private String montarNotificacao(MensagemSqs objSqs, String canalNotificacao) {

        String msgNotificacao = "";

        if(TipoNotificacaoEnum.EMAIL.getNome().equals(canalNotificacao)) {
            msgNotificacao = "Prezado cliente o limite da conta: ".concat(objSqs.getNumero_conta()).concat("-").concat(objSqs.getDigito_conta()).concat("foi excedido para movimentação de valor").concat(objSqs.getValor_movimento().toString());
        } else if(TipoNotificacaoEnum.SMS.getNome().equals(canalNotificacao)){
            msgNotificacao = "Prezado cliente o limite da conta: "
                    .concat(objSqs.getNumero_conta()).concat("-")
                    .concat(objSqs.getDigito_conta()).concat("foi excedido para movimentação de valor")
                    .concat(objSqs.getValor_movimento().toString().concat(" ").concat("para cancelar o recebimento basta enviar SAIR para numero XXX."));
        } else {
            msgNotificacao = "Olá, tudo bem? Passando aqui para informar que o limite da conta: "
                    .concat(objSqs.getNumero_conta()).concat("-")
                    .concat(objSqs.getDigito_conta())
                    .concat("foi excedido para movimentação de valor")
                    .concat(objSqs.getValor_movimento().toString());
        }

        return msgNotificacao;
    }


    public List<LoggerNotificacao> listaNotificacoesLimiteExcedido(String id_conta){
        return notificacaoRepository.findByIdConta(id_conta);
    }

}

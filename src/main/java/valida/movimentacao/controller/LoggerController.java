package valida.movimentacao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import valida.movimentacao.model.LoggerNotificacao;
import valida.movimentacao.service.NotificacaoService;

import javax.websocket.server.PathParam;
import java.util.List;

@RestController
@RequestMapping("api/conta/registro/notificacao")
public class LoggerController {

    @Autowired
    private NotificacaoService notificacaoService;

    @GetMapping
    public ResponseEntity<List<LoggerNotificacao>> getByIdConta(@PathParam(value = "idConta") String idConta){
        return ResponseEntity.ok(notificacaoService.listaNotificacoesLimiteExcedido(idConta));
    }
}

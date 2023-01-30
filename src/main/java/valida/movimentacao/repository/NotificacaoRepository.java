package valida.movimentacao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import valida.movimentacao.model.LoggerNotificacao;

import java.util.List;

public interface NotificacaoRepository extends JpaRepository<LoggerNotificacao, Long> {

    List<LoggerNotificacao> findByIdConta(String idConta);
}

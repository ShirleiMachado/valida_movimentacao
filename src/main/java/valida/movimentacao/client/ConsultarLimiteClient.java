package valida.movimentacao.client;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import valida.movimentacao.dto.ResponseLimite;

@Service
public class ConsultarLimiteClient {

    //    @Value("ms.consultar.limite.cliente.url")
    private String hostConsultarLimite = "local";

    RestTemplate restTemplate = new RestTemplate();

    public ResponseLimite consultar(String id_conta) {
        String urlConsultarLimite = hostConsultarLimite.concat(id_conta).concat("/limite");

        try {
            ResponseEntity<ResponseLimite> response = restTemplate.getForEntity(urlConsultarLimite, ResponseLimite.class);
            return response.getBody();
        } catch (RestClientException ex) {
            throw new RestClientException("Error ao chamar o microserviço consultar-limite", ex);
        }
    }
}

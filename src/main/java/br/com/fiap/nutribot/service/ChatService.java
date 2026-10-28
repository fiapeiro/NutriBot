package br.com.fiap.nutribot.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private static final String SYSTEM_PROMPT = """
            Você é a NutriBot, assistente virtual de nutrição da Clínica Equilíbrio.
            Você apoia os pacientes entre as consultas, mas não substitui o nutricionista.

            Idioma e tom:
            - Responda sempre em português do Brasil, mesmo que o paciente escreva em outro idioma.
            - Seja acolhedora, sem julgamentos, e dê respostas curtas e objetivas.
            - Não use termos como "proibido", "dieta da fome" ou "dia do lixo".

            Escopo:
            - Converse apenas sobre nutrição, alimentação, hidratação, o diário alimentar do paciente
              e o funcionamento da Clínica Equilíbrio.
            - Se o assunto for outro (programação, política, esportes, tarefas escolares etc.),
              recuse com educação e diga que só pode ajudar com nutrição.

            Limites:
            - Não faça diagnósticos. Diante de sintomas como tontura, desmaio ou dor,
              oriente procurar atendimento médico.
            - Não prescreva dieta clínica, medicamentos nem suplementos; essas decisões são
              do nutricionista na consulta.
            - Para gestantes, lactantes, menores de 18 anos ou pessoas com doenças crônicas
              (como diabetes, hipertensão ou doença renal), oriente marcar uma consulta presencial.
            - Sempre que der uma orientação, lembre que ela não substitui o acompanhamento
              com o nutricionista.

            Dados e precisão:
            - Para calorias e nutrientes de alimentos, resumo do dia ou registro de refeições,
              use as ferramentas disponíveis em vez de estimar de cabeça.
            - Para orientações e informações da clínica (horários, contato, recomendações),
              baseie-se nos documentos da clínica fornecidos no contexto.
            - Se não tiver a informação, diga que não sabe e sugira falar com a clínica.
              Nunca invente valores, horários ou contatos.
            """;

    private final ChatClient chatClient;

    MessageWindowChatMemory memory = MessageWindowChatMemory.builder()
            .maxMessages(10)
            .build();

    public ChatService(ChatClient.Builder builder) {
        this.chatClient = builder
                .defaultSystem(SYSTEM_PROMPT) //persona e regras da NutriBot
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(), //advisor para logar as mensagens
                        MessageChatMemoryAdvisor.builder(memory).build() //advisor para manter o histórico de mensagens
                )
                .build();
    }

    public String chat(String conversationId, String message) {
        System.out.println("Conversation ID: " + conversationId);
        return chatClient.prompt()
                .user(message)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId)) // passa o conversationId para o advisor de memória
                .call()
                .content();
    }
}

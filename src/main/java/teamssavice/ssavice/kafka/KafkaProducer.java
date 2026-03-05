package teamssavice.ssavice.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import teamssavice.ssavice.global.property.KafkaProperties;
import teamssavice.ssavice.kafka.event.KafkaEvent;

@Component
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaProperties kafkaProperties;
    private final KafkaTemplate<String, KafkaEvent.Join> kafkaTemplate;

    public void publish(String key, KafkaEvent.Join payload) {
//        System.out.println("publish: " + key + " " + payload.messageType());
        kafkaTemplate.send(kafkaProperties.joinTopic(), key, payload);
    }
}
